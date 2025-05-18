package plagiatchecker.apigateway.Domain.Entities;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStats {
    long id;
    long numWords;
    long numSentences;
    long numArticles;
    double maxSimilarity;
    long closestFileId;
    double averageSimilarity;
}