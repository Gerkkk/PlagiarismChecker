package plagiatchecker.statservice.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import plagiatchecker.statservice.Domain.Entities.FileStats;
import plagiatchecker.statservice.Domain.Entities.StoredFile;
import plagiatchecker.statservice.Domain.Interfaces.Repositories.WordMapRepositoryI;
import plagiatchecker.statservice.Domain.Interfaces.Services.WordStatsServiceI;
import plagiatchecker.statservice.Domain.Interfaces.Transport.FileProviderI;
import plagiatchecker.statservice.Domain.Interfaces.Transport.WordMapProviderI;
import plagiatchecker.statservice.Repository.WordMapStorage;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WordStatsService implements WordStatsServiceI {
    @Autowired
    private final WordMapStorage wordMapStorage;

    @Autowired
    private final WordMapRepositoryI wordMapRepository;

    @Autowired
    private final FileProviderI fileProvider;

    @Autowired
    private final WordMapProviderI wordMapProvider;

    @Override
    public FileStats getFileStats(int id) {
        Optional<FileStats> fs = wordMapRepository.findById(id);

        if (fs.isPresent()) {
            return fs.get();
        }

        StoredFile file = fileProvider.getFile(id);

        if (file == null) return null;

        FileStats fileStats = new FileStats(file.getFileContent());
        var ret = wordMapRepository.save(fileStats);

        String newPath = String.format("%s", ret.getId());
        byte[] wordMap = wordMapProvider.getWordMap(file.getFileContent());
        wordMapStorage.saveFile(newPath, wordMap);

        return fileStats;
    }

    @Override
    public byte[] getWordMap(int id) {
        StoredFile file = wordMapStorage.getFile(String.format("%s", id));
        return file.getFileContent().getBytes(StandardCharsets.UTF_8);
    }
}
