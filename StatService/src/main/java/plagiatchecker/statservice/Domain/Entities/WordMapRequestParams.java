package plagiatchecker.statservice.Domain.Entities;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordMapRequestParams {
    private String text;
    private int width;
    private int height;
    private int fontSize;
    private String format;
    private String fontFamily;
    private String letterCase;
    private boolean removeStopWords;
}
