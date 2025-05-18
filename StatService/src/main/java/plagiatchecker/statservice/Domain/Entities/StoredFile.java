package plagiatchecker.statservice.Domain.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredFile {
    private String fileName;
    private String fileContent;

    public StoredFile(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
    }
}