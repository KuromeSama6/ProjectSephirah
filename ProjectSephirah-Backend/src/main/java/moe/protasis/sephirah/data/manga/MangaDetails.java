package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MangaDetails extends MangaInfo {
    //    description:string;
//    status:MangaStatus;
//
//    latestUpdate:Date;
//    latestChapter:ChapterInfo |string;
//
//    chapters:LazyGet<MangaChapters>;
    private MangaStatus status;
    private String description;
    private String latestChapter;
    private String latestUpdate;
    private MangaChapterData chapters;
}
