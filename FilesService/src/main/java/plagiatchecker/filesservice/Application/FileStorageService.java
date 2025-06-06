package plagiatchecker.filesservice.Application;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import plagiatchecker.filesservice.Domain.Entities.FileInfo;
import plagiatchecker.filesservice.Domain.Entities.StoredFile;
import plagiatchecker.filesservice.Domain.Interfaces.Repositories.FilesRepositoryI;
import plagiatchecker.filesservice.Domain.Interfaces.Repositories.FilesStorageI;
import plagiatchecker.filesservice.Domain.Interfaces.Services.FileStorageServiceI;
import lombok.RequiredArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageService implements FileStorageServiceI {
    @Autowired
    private final FilesRepositoryI fileRepository;
    @Autowired
    private final FilesStorageI fileStorage;

    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        return HexFormat.of().formatHex(hash);
    }

    @Override
    @Transactional
    public int uploadFile(StoredFile file) {
        try {
            String curHash = sha256(file.getFileContent());

            if (!fileRepository.checkFileExists(curHash)) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setHash(curHash);
                fileInfo.setName(file.getFileName());

                FileInfo savedInfo = fileRepository.save(fileInfo);

                boolean r = fileStorage.saveFile(String.format("%d", savedInfo.getId()),  file.getFileContent());

                if (r) {
                    fileRepository.updateFilepath(savedInfo.getId(), String.format("%d", savedInfo.getId()));
                    System.out.println("File uploaded. New id: " + savedInfo.getId());
                    return savedInfo.getId();
                } else {
                    return -1;
                }

            } else {
                return -1;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    @Override
    public StoredFile getFileById(int id) {
        Optional<FileInfo> info = fileRepository.findById(id);

        if (info.isPresent()) {
            FileInfo infoR = info.get();
            StoredFile file = fileStorage.getFile(infoR.getPath());
            return file;
        } else {
            return null;
        }
    }

    @Override
    public List<StoredFile> fetchFiles() {
        return fileStorage.fetchFiles();
    }
}
