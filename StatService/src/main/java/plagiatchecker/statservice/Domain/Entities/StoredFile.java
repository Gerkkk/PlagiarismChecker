package plagiatchecker.statservice.Domain.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class StoredFile {
    private String fileName;
    private String fileContent;
    private Map<String, Integer> wordFrequency;

    public StoredFile(String fileName, String fileContent) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.wordFrequency = buildWordFrequency(fileContent);
    }

    private Map<String, Integer> buildWordFrequency(String text) {
        Map<String, Integer> freq = new HashMap<>();
        String[] words = text.toLowerCase().split("[^\\p{L}\\p{Nd}]+");
        for (String word : words) {
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }

        return freq;
    }
}