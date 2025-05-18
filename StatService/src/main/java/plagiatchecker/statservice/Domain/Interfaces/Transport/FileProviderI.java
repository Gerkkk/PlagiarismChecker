package plagiatchecker.statservice.Domain.Interfaces.Transport;

import plagiatchecker.statservice.Domain.Entities.StoredFile;

public interface FileProviderI {
    StoredFile getFile(int fileId);
}

