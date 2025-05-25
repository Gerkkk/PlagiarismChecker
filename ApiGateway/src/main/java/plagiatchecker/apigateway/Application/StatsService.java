package plagiatchecker.apigateway.Application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import plagiatchecker.apigateway.Domain.Entities.FileStats;
import plagiatchecker.apigateway.Domain.Entities.StoredFile;
import plagiatchecker.apigateway.Domain.Interfaces.Repositories.StatsGrpcServiceI;
import plagiatchecker.apigateway.Domain.Interfaces.Services.StatsServiceI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatsService implements StatsServiceI {
    private final StatsGrpcServiceI statsGrpcService;

    @Override
    public FileStats getFileStats(int id) {
        FileStats fileStats = statsGrpcService.getFileStats(id);

        if (fileStats == null) return null;

        return fileStats;
    }

    @Override
    public File getWordMap(int id) {
        byte[] wordMap = statsGrpcService.getWordMap(id);

        try {
            File tempFile = File.createTempFile("file_", "_" + String.valueOf(id) + ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(wordMap);
            }

            return tempFile;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error getting word map", e);
        }
    }
}
