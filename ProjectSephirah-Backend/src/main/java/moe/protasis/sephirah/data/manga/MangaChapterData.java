package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
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
