package plagiatchecker.apigateway.Domain.Interfaces.Services;

import org.springframework.web.multipart.MultipartFile;
import plagiatchecker.apigateway.Domain.Entities.FileStats;

import java.io.File;
import java.io.IOException;

public interface StatsServiceI {
    FileStats getFileStats(int id);
    File getWordMap(int id);
}
