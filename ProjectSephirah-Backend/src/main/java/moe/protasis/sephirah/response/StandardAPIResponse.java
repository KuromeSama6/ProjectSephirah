package moe.protasis.sephirah.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import moe.protasis.sephirah.util.IJsonSerializable;
import moe.protasis.sephirah.util.JsonWrapper;

@Data
public class StandardAPIResponse {
    private int code;
    private String message;
    private ObjectNode data = new ObjectMapper().createObjectNode();
    private ObjectNode error = new ObjectMapper().createObjectNode();

    public StandardAPIResponse() {
        code = 0;
        message = "ok";
    }

    public StandardAPIResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public StandardAPIResponse(int code, String message, JsonWrapper errorData) {
        this.code = code;
        this.message = message;
        this.error = errorData.getJson();
    }

    public StandardAPIResponse data(String key, JsonNode data) {
        this.data.set(key, data);
        return this;
    }

    public StandardAPIResponse data(String key, String data) {
        this.data.put(key, data);
        return this;
    }

    public StandardAPIResponse data(String key, Integer data) {
        this.data.put(key, data);
        return this;
    }

    public StandardAPIResponse data(String key, Long data) {
        this.data.put(key, data);
        return this;
    }

    public StandardAPIResponse data(String key, IJsonSerializable data) {
        this.data.set(key, data.Serialize());
        return this;
    }

    public StandardAPIResponse data(ObjectNode node) {
        this.data = node;
        return this;
    }

    public StandardAPIResponse data(IJsonSerializable data) {
        this.data = data.Serialize();
        return this;
    }

    public StandardAPIResponse data(String key, Float data) {
        this.data.put(key, data);
        return this;
    }

    public StandardAPIResponse data(String key, Double data) {
        this.data.put(key, data);
        return this;
    }

    public StandardAPIResponse data(String key, Boolean data) {
        this.data.put(key, data);
        return this;
    }


    public static StandardAPIResponse ok() {
        return new StandardAPIResponse(0, "ok");
    }
}