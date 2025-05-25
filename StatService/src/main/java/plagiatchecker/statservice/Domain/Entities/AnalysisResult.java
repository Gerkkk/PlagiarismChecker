package plagiatchecker.statservice.Domain.Entities;

import lombok.Builder;
import org.springframework.boot.context.properties.bind.DefaultValue;

public class AnalysisResult {
    public String closestFileName;
    public double closestDistance;
    public double averageDistanceBelowThreshold;

    public AnalysisResult(String closestFileName, double closestDistance, double averageDistanceBelowThreshold) {
        this.closestFileName = closestFileName;
        this.closestDistance = closestDistance;
        this.averageDistanceBelowThreshold = averageDistanceBelowThreshold;
    }
}
