package plagiatchecker.statservice.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import plagiatchecker.statservice.Domain.Entities.StoredFile;
import plagiatchecker.statservice.Domain.Interfaces.Repositories.WordMapStorageI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class WordMapStorage implements WordMapStorageI {
    private static final String dirName = "wordmaps";

    @Override
    public boolean saveFile(String filepath, byte[] fileContent) {
        File file = new File(dirName + filepath);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new String(fileContent));
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public StoredFile getFile(String filepath) {
        try {
            String content = Files.readString(Paths.get(dirName + filepath));
            StoredFile storedFile = new StoredFile(filepath, content);
            return storedFile;
        } catch (Exception e) {
            return null;
        }
    }
}

