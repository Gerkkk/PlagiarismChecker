package plagiatchecker.filesservice.Domain.Interfaces.Repositories;

import plagiatchecker.filesservice.Domain.Entities.StoredFile;

import java.util.List;

public interface FilesStorageI {
    boolean saveFile(String filepath, String fileContent);
    StoredFile getFile(String filepath);
    List<StoredFile> fetchFiles();
}
