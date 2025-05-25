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
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(dirName, filepath);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(fileContent);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public StoredFile getFile(String filepath) {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String content = Files.readString(Paths.get(dirName, filepath));
            return new StoredFile(filepath, content);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<StoredFile> fetchFiles() {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        ArrayList<StoredFile> storedFiles = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            try {
                String content = Files.readString(file.toPath());
                storedFiles.add(new StoredFile(file.getName(), content));
            } catch (IOException e) {}
        }

        return storedFiles;
    }
}
