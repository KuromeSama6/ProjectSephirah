package moe.protasis.sephirah.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@JsonDeserialize(using = JsonWrapper.JsonWrapperDeserializer.class)
@JsonSerialize(using = JsonWrapper.JsonWrapperSerializer.class)
public class JsonWrapper implements IJsonSerializable {
    @Getter
    private final ObjectNode json;
    private final ObjectMapper objectMapper;
    private JsonWrapper parent;
    private JsonWrapper child;

    public JsonWrapper(JsonNode node) {
        if (!node.isObject()) {
            throw new IllegalArgumentException("Argument must be an ObjectNode!");
        }

        this.json = (ObjectNode)node;
        this.objectMapper = new ObjectMapper();
    }

    public JsonWrapper() {
        this(new ObjectNode(JsonNodeFactory.instance));
    }

    public JsonWrapper(Object obj) {
        this(new ObjectMapper().valueToTree(obj));
    }

    public JsonWrapper(String str) throws JsonProcessingException {
        this(new ObjectMapper().readTree(str));
    }

    public JsonWrapper EnterObject() {
        JsonWrapper ret = new JsonWrapper();
        ret.parent = this;
        child = ret;
        return ret;
    }

    public JsonWrapper ExitObject(String name) {
        if (parent == null) {
            throw new IllegalStateException("Object not part of builder.");
        }
        JsonWrapper ret = parent;
        parent.Set(name, json);

        return ret;
    }

    public String GetString(String path, String def) {
        JsonNode ret = ResolvePath(path);
        return (ret != null && ret.isTextual()) ? ret.asText() : def;
    }

    public String GetString(String path) {
        return GetString(path, null);
    }

    public <T extends Enum<T>> T GetEnum(Class<T> clazz, String path, T def) {
        String str = GetString(path, def.toString());
        if (str == null) return null;

        return Enum.valueOf(clazz, str);
    }

    public int GetInt(String path, int def) {
        JsonNode ret = ResolvePath(path);
        return (ret != null && ret.isInt()) ? ret.intValue() : def;
    }

    public int GetInt(String path) {
        return GetInt(path, 0);
    }

    public long GetLong(String path, long def) {
        JsonNode ret = ResolvePath(path);
        return (ret != null && ret.isLong()) ? ret.longValue() : def;
    }

    public long GetLong(String path) {
        return GetLong(path, 0);
    }

    public double GetDouble(String path, double def) {
        JsonNode ret = ResolvePath(path);
        return (ret != null && ret.isDouble()) ? ret.doubleValue() : def;
    }

    public double GetDouble(String path) {
        return GetDouble(path, 0);
    }

    public boolean GetBool(String path, boolean def) {
        JsonNode ret = ResolvePath(path);
        return (ret != null && ret.isBoolean()) ? ret.booleanValue() : def;
    }

    public boolean GetBool(String path) {
        return GetBool(path, false);
    }

    public UUID GetUuid(String path) {
        String ret = GetString(path);
        return (ret == null) ? null : UUID.fromString(ret);
    }

    public <T> List<T> GetList(String path, Class<T> clazz) {
        List<T> ret = new ArrayList<>();
        JsonNode node = ResolvePath(path);
        if (node == null || !node.isArray()) return ret;

        for (JsonNode jsonElement : node) {
            try {
                ret.add(objectMapper.treeToValue(jsonElement, clazz));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    public ArrayNode GetList(String path) {
        JsonNode node = ResolvePath(path);
        return (node != null && node.isArray()) ? (ArrayNode)node : null;
    }

    public <T> T GetObject(String path, Class<T> clazz) {
        JsonNode node = ResolvePath(path);
        if (node == null || node.isNull()) return null;

        try {
            return objectMapper.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonWrapper GetObject(String path) {
        JsonNode node = ResolvePath(path);
        if (node == null || !node.isObject()) return new JsonWrapper();
        return new JsonWrapper(node);
    }

    public JsonNode Get(String path) {
        return ResolvePath(path);
    }

    public List<String> GetKeys() {
        List<String> keys = new ArrayList<>();
        json.fieldNames().forEachRemaining(keys::add);
        return keys;
    }

    public boolean Has(String path) {
        return ResolvePath(path) != null;
    }

    public boolean IsEmpty() {
        return json.isEmpty();
    }

    public JsonWrapper Set(String path, Object obj) {
        ObjectNode parentNode = json;
        String[] args = path.split("\\.");
        if (args.length == 0) throw new IllegalArgumentException("Setting parent node not permitted.");
        String property = args[args.length - 1];

        for (int i = 0; i < args.length - 1; i++) {
            String name = args[i];
            if (!parentNode.has(name) || !parentNode.get(name).isObject()) {
                parentNode.set(name, new ObjectNode(JsonNodeFactory.instance));
            }
            parentNode = (ObjectNode)parentNode.get(name);
        }

        Set(property, parentNode, obj);
        return this;
    }

    public JsonWrapper SetObject(String path, Object obj) {
        return Set(path, objectMapper.valueToTree(obj));
    }

    public void Save(File file) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void Set(String name, ObjectNode parentNode, Object obj) {
        if (obj == null) {
            parentNode.remove(name);
        } else if (obj instanceof JsonNode) {
            parentNode.set(name, (JsonNode)obj);
        } else if (obj instanceof Map) {
            ObjectNode data = objectMapper.convertValue(obj, ObjectNode.class);
            parentNode.set(name, data);
        } else if (obj instanceof List || obj.getClass().isArray()) {
            ArrayNode arrayNode = objectMapper.valueToTree(obj);
            parentNode.set(name, arrayNode);
        } else if (obj instanceof Enum<?>) {
            parentNode.put(name, obj.toString());
        } else if (obj instanceof Number) {
            parentNode.putPOJO(name, obj);
        } else if (obj instanceof Boolean) {
            parentNode.put(name, (Boolean)obj);

        } else if (obj instanceof IJsonSerializable) {
            parentNode.set(name, ((IJsonSerializable)obj).Serialize());

        } else {
            parentNode.put(name, obj.toString());
        }
    }

    private JsonNode ResolvePath(String path) {
        String[] args = path.split("\\.");
        if (args.length == 0) return json;

        JsonNode currentNode = json;
        for (String name : args) {
            if (currentNode.has(name)) {
                currentNode = currentNode.get(name);
            } else {
                return null;
            }
        }

        return currentNode;
    }

    @Override
    public String toString() {
        return json.toString();
    }

    @Override
    public ObjectNode Serialize() {
        return json;
    }

    @Override
    public void SerializeInternal(JsonWrapper obj) {

    }

    public static class JsonWrapperSerializer extends JsonSerializer<JsonWrapper> {
        @Override
        public void serialize(JsonWrapper obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(obj.getJson());
        }
    }

    public static class JsonWrapperDeserializer extends JsonDeserializer<JsonWrapper> {
        @Override
        public JsonWrapper deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return new JsonWrapper(jsonParser.readValueAs(ObjectNode.class));
        }
    }

    public static class JsonWrapperReadConverter implements Converter<Document, JsonWrapper> {
        private final ObjectMapper objectMapper = new ObjectMapper();
        @Override
        public JsonWrapper convert(Document obj) {
            try {
                var ret = objectMapper.readTree(obj.toJson());
                return new JsonWrapper(ret);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class JsonWrapperWriteConverter implements Converter<JsonWrapper, Document> {
        @Override
        public Document convert(JsonWrapper jsonWrapper) {
            return Document.parse(jsonWrapper.toString());
        }
    }
}
