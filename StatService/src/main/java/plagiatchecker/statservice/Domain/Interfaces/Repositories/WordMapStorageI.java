package plagiatchecker.statservice.Domain.Interfaces.Repositories;

import plagiatchecker.statservice.Domain.Entities.StoredBytes;
import plagiatchecker.statservice.Domain.Entities.StoredFile;

import java.util.List;

public interface WordMapStorageI {
    boolean saveFile(String filepath, byte[] fileContent);
    StoredBytes getFile(String filepath);
}
