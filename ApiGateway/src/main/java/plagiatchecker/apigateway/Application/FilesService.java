package plagiatchecker.apigateway.Application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import plagiatchecker.apigateway.Domain.Entities.StoredFile;
import plagiatchecker.apigateway.Domain.Interfaces.Repositories.FilesGrpcServiceI;
import plagiatchecker.apigateway.Domain.Interfaces.Services.FilesServiceI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilesService implements FilesServiceI {
    private final FilesGrpcServiceI filesGrpcService;

    public long uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        byte[] fileBytes = multipartFile.getBytes();
        long id = filesGrpcService.uploadFile(fileName, fileBytes);
        return id;
    }

    @Override
    public File getFile(int id) {
        StoredFile storedFile = filesGrpcService.getFile(id);

        try {
            File tempFile = File.createTempFile("file_", "_" + storedFile.getFileName());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(storedFile.getFileContent().getBytes());
            }

            return tempFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error creating file", e);
        }
    }
}
