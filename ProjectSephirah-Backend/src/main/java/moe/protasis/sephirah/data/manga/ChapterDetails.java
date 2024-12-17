package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDetails extends ChapterInfo {
    private MangaInfo manga;
    private List<String> images;
    private String nextChapter;
    private String prevChapter;
}
