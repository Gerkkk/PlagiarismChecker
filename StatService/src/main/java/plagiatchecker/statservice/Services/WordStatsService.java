package plagiatchecker.statservice.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import plagiatchecker.statservice.Domain.Entities.*;
import plagiatchecker.statservice.Domain.Interfaces.Repositories.WordMapRepositoryI;
import plagiatchecker.statservice.Domain.Interfaces.Services.WordStatsServiceI;
import plagiatchecker.statservice.Domain.Interfaces.Transport.FileProviderI;
import plagiatchecker.statservice.Domain.Interfaces.Transport.WordMapProviderI;
import plagiatchecker.statservice.Repository.WordMapStorage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WordStatsService implements WordStatsServiceI {
    @Autowired
    private final WordMapStorage wordMapStorage;

    @Autowired
    private final PlagiarismChecker plagiarismChecker;

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

        AnalysisResult res = plagiarismChecker.analyzeFile(file, 1);
        fileStats.setClosestFileId(Integer.parseInt(res.closestFileName));
        fileStats.setAverageSimilarity(res.averageDistanceBelowThreshold);
        fileStats.setMaxSimilarity(res.closestDistance);

        var ret = wordMapRepository.save(fileStats);

        String newPath = String.format("%s", ret.getId());
        byte[] wordMap = wordMapProvider.getWordMap(file.getFileContent());
        wordMapStorage.saveFile(newPath, wordMap);

        return fileStats;
    }

    @Override
    public byte[] getWordMap(int id) {
        StoredBytes file = wordMapStorage.getFile(String.format("%s", id));
        return file.getFileContent();
    }

    //@Scheduled(fixedRate = 300000)
    @Scheduled(fixedRate = 20000)
    public void refreshList() {
        plagiarismChecker.updateFileStorage(fileProvider.getAllFiles());
    }
}
