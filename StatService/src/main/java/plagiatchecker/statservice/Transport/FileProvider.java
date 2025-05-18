package plagiatchecker.statservice.Transport;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
                    System.out.println("Prom name: " + chunk.getFilename());
                    fileName[0] = chunk.getFilename();
                }
                chunks.add(chunk.getData().toByteArray());
            }

            @Override
            public void onError(Throwable t) {
                log.error("Ошибка при получении файла: {}", t.getMessage(), t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("Получение файла завершено");
                latch.countDown();
            }
        });

        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout while receiving file");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for file", e);
        }

        int totalSize = chunks.stream().mapToInt(arr -> arr.length).sum();
        byte[] fileData = new byte[totalSize];
        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, fileData, offset, chunk.length);
            offset += chunk.length;
        }

        String text = new String(fileData, StandardCharsets.UTF_8);

        System.out.println("File bytes: " + text + "; FileName " + fileName[0]);
        return new StoredFile(fileName[0], text);
    }
}
