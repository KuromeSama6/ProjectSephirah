package moe.protasis.sephirah.data.manga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moe.protasis.sephirah.util.JsonWrapper;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterImages {
    private List<String> links;
    private JsonWrapper extraData;

    public static ChapterImages From(List<String> links) {
        return ChapterImages.builder()
                .links(links)
                .extraData(new JsonWrapper())
                .build();
    }
}
