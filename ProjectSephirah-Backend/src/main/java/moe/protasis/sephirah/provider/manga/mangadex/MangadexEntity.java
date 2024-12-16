package moe.protasis.sephirah.provider.manga.mangadex;

import lombok.Data;
import lombok.Getter;
import moe.protasis.sephirah.util.JsonWrapper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MangadexEntity {
    private final String id;
    private final String type;
    private final JsonWrapper attributes;
    private final List<MangadexEntity> relationships = new ArrayList<>();

    public MangadexEntity(JsonWrapper data) {
        id = data.GetString("id");
        type = data.GetString("type");
        attributes = data.GetObject("attributes");

        var relationships = data.GetList("relationships");
        if (relationships != null) {
            for (var relationship : relationships) {
                this.relationships.add(new MangadexEntity(new JsonWrapper(relationship)));
            }
        }
    }

    public MangadexEntity GetRelationship(String type) {
        return relationships.stream().filter(x -> x.type.equals(type)).findFirst().orElse(null);
    }
}
