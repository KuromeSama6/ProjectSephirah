package moe.protasis.sephirah.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IJsonSerializable {
    default ObjectNode Serialize() {
        JsonWrapper ret = new JsonWrapper();
        SerializeInternal(ret);
        return ret.getJson();
    }

    void SerializeInternal(JsonWrapper json);
}
