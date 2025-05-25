package plagiatchecker.statservice.Domain.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredBytes {
    private String fileName;
    private byte[] fileContent;

    public StoredBytes(String fileName, byte[] fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }
}