package plagiatchecker.filesservice.Domain.Interfaces.Services;

import org.springframework.web.multipart.MultipartFile;
import plagiatchecker.filesservice.Domain.Entities.StoredFile;

import java.util.List;

public interface FileStorageServiceI {
    int uploadFile(StoredFile file);
    StoredFile getFileById(int id);
    List<StoredFile> fetchFiles();
}
