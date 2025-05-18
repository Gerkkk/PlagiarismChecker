package plagiatchecker.statservice.Domain.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "filestats")
@ToString
@NoArgsConstructor
public class FileStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int numWords;
    int numSentences;
    int numArticles;
    double maxSimilarity;
    int closestFileId;
    double averageSimilarity;
    String path;

    public FileStats(String text) {
        closestFileId = -1;
        averageSimilarity = 0.0;
        maxSimilarity = 0.0;

        String[] words = text.split("[\s|\n]+");
        numWords = words.length;

        String[] sentences = text.split("[.|?|!]+[\s|\n]*");
        numSentences = sentences.length;

        String[] articles = text.split("[.|?|!]+[\n]+");
        numArticles = articles.length;
    }
}

