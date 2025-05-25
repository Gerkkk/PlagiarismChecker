package plagiatchecker.statservice.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import plagiatchecker.statservice.Domain.Entities.StoredFile;
import plagiatchecker.statservice.Domain.Entities.StoredBytes;
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
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(dirName, filepath);
        try {
            Files.write(file.toPath(), fileContent);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    @Override
    public StoredBytes getFile(String filepath) {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            byte[] content = Files.readAllBytes(Paths.get(dirName, filepath));
            return new StoredBytes(filepath, content);
        } catch (Exception e) {
            return null;
        }
    }

}

