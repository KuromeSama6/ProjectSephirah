package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ChapterGroup {
    private String id;
    private String name;
    private int count;
    private List<ChapterInfo> chapters = new ArrayList<>();
}
