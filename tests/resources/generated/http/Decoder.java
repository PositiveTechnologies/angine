from .AST import *

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class Decoder{

    private Gson gson;

    public Decoder(){
        gson = new Gson();
    }

    public List<Object> fromJson(String jsonString){
        JsonArray jsonArray = new JsonParser()
                .parse(jsonString)
                .getAsJsonObject()
                .get("entities")
                .getAsJsonArray();
        List<Object> objects = new ArrayList<Object>();
        for(JsonElement element : jsonArray){
            String str = element.getAsJsonObject().get("type").getAsString();
            Object currentObject = null;
            if(str.equals("entity")){
            	currentObject = createEntity(element);
            }
            
            if(str.equals("urlentity")){
            	currentObject = createUrlEntity(element);
            }
            
            if(str.equals("subject")){
            	currentObject = createSubject(element);
            }
            
            objects.add(currentObject);
        }
        return objects;
    }

    public abstract Object createEntity(JsonElement je);
    
    public abstract Object createUrlEntity(JsonElement je);
    
    public abstract Object createSubject(JsonElement je);
    
    public Object create(JsonElement je, Type type){
        return gson.fromJson(je, type);
    }
}