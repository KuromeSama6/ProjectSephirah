package moe.protasis.sephirah.data.manga;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MangaChapterData {
    private List<ChapterGroup> groups = new ArrayList<>();

    public ChapterInfo GetChapter(String id) {
//        groups.forEach(ChapterGroup::AutoAssignRelations);

        return groups.stream()
                .flatMap(group -> group.getChapters().stream())
                .filter(chapter -> chapter.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
