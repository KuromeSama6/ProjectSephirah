package moe.protasis.sephirah.data.manga;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class ChapterDetails extends ChapterInfo {
    private MangaInfo manga;
    private List<String> images;
    private String nextChapter;
    private String prevChapter;
}
