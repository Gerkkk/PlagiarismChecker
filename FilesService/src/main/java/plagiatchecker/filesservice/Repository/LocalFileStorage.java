package plagiatchecker.filesservice.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import plagiatchecker.filesservice.Domain.Entities.StoredFile;
import plagiatchecker.filesservice.Domain.Interfaces.Repositories.FilesStorageI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LocalFileStorage implements FilesStorageI {
    private static final String dirName = "files";

    @Override
    public boolean saveFile(String filepath, String fileContent) {
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

    @Override
    public List<StoredFile> fetchFiles() {
        ArrayList<StoredFile> storedFiles = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(dirName).listFiles())) {
            storedFiles.add(new StoredFile(file.getName(), file.toString()));
        }

        return storedFiles;
    }
}
