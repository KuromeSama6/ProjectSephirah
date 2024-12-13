package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChapterInfo {
    private String id;
    private Integer index;
    private String title;
    private String date;
}
