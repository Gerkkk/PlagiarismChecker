package plagiatchecker.statservice.Transport;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import plagiatchecker.filesservice.proto.FileServiceGrpc;
import plagiatchecker.filesservice.proto.FileServiceProto;
import plagiatchecker.statservice.Domain.Entities.StoredFile;
import plagiatchecker.statservice.Domain.Interfaces.Transport.FileProviderI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
public class FileProvider implements FileProviderI {
    CompletableFuture<Long> fileIdFuture = new CompletableFuture<>();
    private FileServiceGrpc.FileServiceStub stub;

    @PostConstruct
    public void init() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("file-service", 9090)
                .usePlaintext()
                .build();
        this.stub = FileServiceGrpc.newStub(channel);
    }

    public StoredFile getFile(int fileId) {
        FileServiceProto.GetFileRequest request = FileServiceProto.GetFileRequest.newBuilder()
                .setId(fileId)
                .build();

        List<byte[]> chunks = new ArrayList<>();
        final String[] fileName = {null};
        CountDownLatch latch = new CountDownLatch(1);

        stub.getFileById(request, new StreamObserver<FileServiceProto.FileChunk>() {
            @Override
            public void onNext(FileServiceProto.FileChunk chunk) {
                if (fileName[0] == null) {
                    fileName[0] = chunk.getFilename();
                }
                chunks.add(chunk.getData().toByteArray());
            }

            @Override
            public void onError(Throwable t) {
                if (t instanceof io.grpc.StatusRuntimeException) {
                    io.grpc.StatusRuntimeException sre = (io.grpc.StatusRuntimeException) t;
                    io.grpc.Status.Code code = sre.getStatus().getCode();
                    if (code == io.grpc.Status.Code.UNAVAILABLE) {
                        log.error("File service UNAVAILABLE: {}", t.getMessage());
                    } else {
                        log.error("gRPC error with code {}: {}", code, t.getMessage());
                    }
                } else {
                    log.error("Error: {}", t.getMessage(), t);
                }

                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                log.error("File service did not complete within 10 seconds");
                //throw new RuntimeException("Timeout while receiving file");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error: {}", e.getMessage(), e);
            //throw new RuntimeException("Interrupted while waiting for file", e);
        }

        int totalSize = chunks.stream().mapToInt(arr -> arr.length).sum();
        byte[] fileData = new byte[totalSize];
        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, fileData, offset, chunk.length);
            offset += chunk.length;
        }

        String text = new String(fileData, StandardCharsets.UTF_8);

        return new StoredFile(fileName[0], text);
    }

    @Override
    public List<StoredFile> getAllFiles() {
        List<StoredFile> files = new ArrayList<>();
        CountDownLatch finishLatch = new CountDownLatch(1);

        final StringBuilder currentFileName = new StringBuilder();
        final ByteArrayOutputStream currentFileContent = new ByteArrayOutputStream();

        stub.getAllFiles(FileServiceProto.Empty.newBuilder().build(), new StreamObserver<FileServiceProto.FileChunkWithInfo>() {
            @Override
            public void onNext(FileServiceProto.FileChunkWithInfo chunk) {
                try {
                    if (chunk.getIsFirstChunk()) {
                        if (currentFileName.length() > 0) {
                            files.add(new StoredFile(currentFileName.toString(), currentFileContent.toString()));
                            currentFileContent.reset();
                        }
                        currentFileName.setLength(0);
                        currentFileName.append(chunk.getFilename());
                    }

                    currentFileContent.write(chunk.getData().toByteArray());
                } catch (Exception t) {
                    if (t instanceof io.grpc.StatusRuntimeException) {
                        io.grpc.StatusRuntimeException sre = (io.grpc.StatusRuntimeException) t;
                        io.grpc.Status.Code code = sre.getStatus().getCode();
                        if (code == io.grpc.Status.Code.UNAVAILABLE) {
                            log.error("File service UNAVAILABLE: {}", t.getMessage());
                        } else {
                            log.error("gRPC error with code {}: {}", code, t.getMessage());
                        }
                    } else {
                        log.error("Error: {}", t.getMessage(), t);
                    }

                    //throw new RuntimeException("Ошибка при сборке файла из чанков", e);
                }
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();

                if (t instanceof io.grpc.StatusRuntimeException) {
                    io.grpc.StatusRuntimeException sre = (io.grpc.StatusRuntimeException) t;
                    io.grpc.Status.Code code = sre.getStatus().getCode();
                    if (code == io.grpc.Status.Code.UNAVAILABLE) {
                        log.error("File service UNAVAILABLE: {}", t.getMessage());
                    } else {
                        log.error("gRPC error with code {}: {}", code, t.getMessage());
                    }
                } else {
                    log.error("Error: {}", t.getMessage(), t);
                }

                //throw new RuntimeException("Ошибка при получении файлов", t);
            }

            @Override
            public void onCompleted() {
                if (currentFileName.length() > 0) {
                    files.add(new StoredFile(currentFileName.toString(), currentFileContent.toString()));
                }
                finishLatch.countDown();
            }
        });

        try {
            finishLatch.await();
        } catch (InterruptedException t) {
            Thread.currentThread().interrupt();
            log.error("Error: {}", t.getMessage(), t);
            //throw new RuntimeException("Ожидание потока было прервано", e);
        }

        return files;
    }

}
