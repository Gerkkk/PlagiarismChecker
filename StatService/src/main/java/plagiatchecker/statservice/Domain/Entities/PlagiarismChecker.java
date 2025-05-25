package plagiatchecker.statservice.Domain.Entities;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PlagiarismChecker {
    private List<StoredFile> storedFiles = new ArrayList<>();

    public void updateFileStorage(List<StoredFile> newFiles) {
        this.storedFiles = new ArrayList<>(newFiles);
    }

    private double cosineDistance(Map<String, Integer> freq1, Map<String, Integer> freq2) {
        Set<String> allWords = new HashSet<>(freq1.keySet());
        allWords.addAll(freq2.keySet());

        double[] vector1 = new double[allWords.size()];
        double[] vector2 = new double[allWords.size()];

        int i = 0;
        for (String word : allWords) {
            vector1[i] = freq1.getOrDefault(word, 0);
            vector2[i] = freq2.getOrDefault(word, 0);
            i++;
        }

        RealVector v1 = new ArrayRealVector(vector1);
        RealVector v2 = new ArrayRealVector(vector2);

        double normProduct = v1.getNorm() * v2.getNorm();
        if (normProduct == 0) return 1.0;

        double similarity = v1.dotProduct(v2) / normProduct;
        return 1.0 - similarity;
    }

    public AnalysisResult analyzeFile(StoredFile inputFile, double threshold) {
        double closestDistance = Double.MAX_VALUE;
        String closestFileName = null;
        double sum = 0;
        int count = 0;

        for (StoredFile stored : storedFiles) {

            System.out.println(stored.getFileName());
            if (inputFile.getFileName().equals(stored.getFileName())) {
                continue;
            }

            double dist = cosineDistance(inputFile.getWordFrequency(), stored.getWordFrequency());
            System.out.println(dist);

            if (dist < closestDistance) {
                closestDistance = dist;
                closestFileName = stored.getFileName();
            }

            if (dist < threshold) {
                sum += dist;
                count++;
            }
        }

        double average = count > 0 ? sum / count : 0.0;

        if (closestFileName == null) {
            closestFileName = "";
            average = 0.0;
        }

        return new AnalysisResult(closestFileName, closestDistance, average);
    }

}
