package plagiatchecker.apigateway.Domain.Interfaces.Repositories;

import plagiatchecker.apigateway.Domain.Entities.FileStats;
import plagiatchecker.apigateway.Domain.Entities.StoredFile;

public interface StatsGrpcServiceI {
    FileStats getFileStats(int fileId);
    byte[] getWordMap(int fileId);
}
