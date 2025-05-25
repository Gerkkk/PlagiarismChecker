package plagiatchecker.statservice.Domain.Interfaces.Transport;

import plagiatchecker.statservice.Domain.Entities.StoredFile;

import java.util.List;

public interface FileProviderI {
    StoredFile getFile(int fileId);
    List<StoredFile> getAllFiles();
}

