package plagiatchecker.statservice.Domain.Interfaces.Services;

import plagiatchecker.statservice.Domain.Entities.FileStats;

public interface WordStatsServiceI {
    FileStats getFileStats(int id);
    byte[] getWordMap(int id);
}
