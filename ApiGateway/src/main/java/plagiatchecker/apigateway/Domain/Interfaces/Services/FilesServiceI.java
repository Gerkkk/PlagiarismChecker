package plagiatchecker.apigateway.Domain.Interfaces.Services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface FilesServiceI {
    long uploadFile(MultipartFile multipartFile) throws IOException;
    File getFile(int id);
}
