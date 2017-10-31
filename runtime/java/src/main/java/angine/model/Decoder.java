package angine.model;

import angine.generated.Entities;
import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Decoder implements JsonDeserializer<Map<String, Object>> {

    private Map<String, Type> nameToType;

    public Decoder(){
        nameToType = new HashMap<String, Type>();
        nameToType.put("entity", Entities.Entity.class);
        nameToType.put("subject", Entities.Subject.class);
        nameToType.put("urlentity", Entities.UrlEntity.class);
    }

    public Map<String,Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonObject().get("entities").getAsJsonArray();
        Map<String, Object> attrs = new HashMap<String ,Object>();
        for(JsonElement element : array){
            String objectName = element.getAsJsonObject().get("type").getAsString();
            Object obj = jsonDeserializationContext.deserialize(element,nameToType.get(objectName));
            attrs.put(getId(obj), obj);
        }
        return attrs;
    }


    private String getId(Object o){
        try {
            Field field = o.getClass().getField("id");
            return field.get(o).toString();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
