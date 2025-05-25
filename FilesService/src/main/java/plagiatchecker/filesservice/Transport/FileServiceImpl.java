package plagiatchecker.filesservice.Transport;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import plagiatchecker.filesservice.Domain.Entities.StoredFile;
import plagiatchecker.filesservice.Domain.Interfaces.Services.FileStorageServiceI;
import plagiatchecker.filesservice.proto.FileServiceGrpc;
import plagiatchecker.filesservice.proto.FileServiceProto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@GrpcService
@Component
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {
    @Autowired
    private final FileStorageServiceI fileStorageService;

    @Override
    public StreamObserver<FileServiceProto.FileChunk> postNewFile(
            StreamObserver<FileServiceProto.FileUploadResponse> responseObserver) {

        return new StreamObserver<>() {
            private String filename;
            private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            @Override
            public void onNext(FileServiceProto.FileChunk chunk) {
                if (filename == null) {
                    filename = chunk.getFilename();
                }
                try {
                    outputStream.write(chunk.getData().toByteArray());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage());
            }

            @Override
            public void onCompleted() {
                byte[] fullFileBytes = outputStream.toByteArray();
                StoredFile file = new StoredFile(filename, new String(fullFileBytes));

                var id = fileStorageService.uploadFile(file);

                FileServiceProto.FileUploadResponse response = FileServiceProto.FileUploadResponse
                        .newBuilder()
                        .setId(id)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void getFileById(FileServiceProto.GetFileRequest request,
                            StreamObserver<FileServiceProto.FileChunk> responseObserver) {
        long fileId = request.getId();
        try {
            StoredFile storedFile = fileStorageService.getFileById((int)fileId);
            if (storedFile == null) {
                responseObserver.onError(new RuntimeException("File not found with id: " + fileId));
                return;
            }

            String filename = storedFile.getFileName();
            byte[] data = storedFile.getFileContent().getBytes(StandardCharsets.UTF_8);

            int chunkSize = 64 * 1024;
            for (int i = 0; i < data.length; i += chunkSize) {
                int end = Math.min(data.length, i + chunkSize);
                byte[] chunkBytes = new byte[end - i];
                System.arraycopy(data, i, chunkBytes, 0, end - i);

                FileServiceProto.FileChunk chunk = FileServiceProto.FileChunk.newBuilder()
                        .setFilename(filename)
                        .setData(ByteString.copyFrom(chunkBytes))
                        .build();

                responseObserver.onNext(chunk);
            }

            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllFiles(FileServiceProto.Empty request,
                            StreamObserver<FileServiceProto.FileChunkWithInfo> responseObserver) {
        try {
            List<StoredFile> storedFiles = fileStorageService.fetchFiles();

            for (StoredFile file : storedFiles) {
                byte[] fileBytes = file.getFileContent().getBytes();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
                byte[] buffer = new byte[1024 * 64];
                int bytesRead;
                boolean isFirstChunk = true;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    FileServiceProto.FileChunkWithInfo.Builder chunkBuilder = FileServiceProto.FileChunkWithInfo.newBuilder()
                            .setFilename(file.getFileName())
                            .setIsFirstChunk(isFirstChunk)
                            .setData(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead));

                    responseObserver.onNext(chunkBuilder.build());
                    isFirstChunk = false;
                }
            }

            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(e);
        }
    }
}