package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class ChapterInfo {
    private String id;
    private Integer index;
    private String title;
    private String date;
}
