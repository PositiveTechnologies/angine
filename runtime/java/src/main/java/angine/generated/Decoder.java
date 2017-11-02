package angine.generated;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class Decoder{

    private Gson gson;

    public Decoder(){
        gson = new Gson();
    }

    public Map<String, Object> fromJson(String jsonString) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JsonArray jsonArray = new JsonParser()
                .parse(jsonString)
                .getAsJsonObject()
                .get("entities")
                .getAsJsonArray();
        Map<String, Object> objects = new HashMap<String, Object>();
        for(JsonElement element : jsonArray){
            String str = element.getAsJsonObject().get("type").getAsString();
            Object currentObject = null;
            if(str.equals("entity")){
                currentObject = createEntity(element);
            }
            if(str.equals("subject")){
                currentObject = createSubject(element);
            }
            if (str.equals("urlentity")){
                currentObject = createUrlEntity(element);
            }
            String id = (String) currentObject.getClass().getMethod("id").invoke(currentObject);
            objects.put(id, currentObject);
        }
        return objects;
    }


    public abstract Object createEntity(JsonElement je);
    public abstract Object createSubject(JsonElement je);
    public abstract Object createUrlEntity(JsonElement je);

    public Object create(JsonElement je, Type type){
        return gson.fromJson(je, type);
    }
}
