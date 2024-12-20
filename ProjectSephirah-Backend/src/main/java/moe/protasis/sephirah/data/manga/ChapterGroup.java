package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterGroup {
    private String id;
    private String name;
    private int count;
    private List<ChapterInfo> chapters = new ArrayList<>();

//    public void AutoAssignRelations() {
//        for (int i = 0; i < chapters.size(); i++) {
//            var c = chapters.get(i);
//            if (i > 0) {
//                c.setPrevChapter(chapters.get(i - 1).getId());
//            }
//            if (i < chapters.size() - 1) {
//                c.setNextChapter(chapters.get(i + 1).getId());
//            }
//        }
//    }

    public static ChapterGroup Single(MangaDetails manga) {
        return ChapterGroup.builder()
                .id("0")
                .count(1)
                .name("Default")
                .chapters(List.of(
                        ChapterInfo.builder()
                                .id("0")
                                .index(0)
                                .date(manga.getLatestUpdate())
                                .build()
                ))
                .build();
    }
}
