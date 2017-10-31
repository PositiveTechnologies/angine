package angine;

import angine.model.Factory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PIP {

    public Map<String, Object> attrs;


    public PIP(Map<String, Object> attrs){
        this.attrs = attrs;
    }
    public List<EvaluationContext> createContext( RequestContext requestContext){
        List<EvaluationContext> result = new ArrayList<EvaluationContext>();
        for(Object e :  requestContext.entities){
            result.add(
                    new EvaluationContext(
                            attrs.get(getId(requestContext.subject)) ,
                            attrs.get(getId(e)),
                            requestContext.action
                    )
            );
        }
        return result;
    }



    /**
     * Создает PIP. Аттрибуты загружаются из JsonText, предварительно проверяются на соответствие jsonSchema
     * @param jsonSchema
     * @param JsonText
     * @param factory объект, способный восстанавливать Entities из json
     * @return PIP
     */
    @Deprecated
    public PIP fromJson(String jsonSchema, String JsonText, Factory factory){
        System.out.println("PIP.fromJson is incomplete");
        return null;
        /*
        JsonNode data;
        try {
            JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
            data = JsonLoader.fromString(JsonText);
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
            JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
            ProcessingReport report = schema.validate(data);
        } catch (IOException e){
            e.printStackTrace();
        } catch (ProcessingException e) {
            e.printStackTrace();
        }
        attrs = new HashMap<String, Identifiable>();

        return new PIP(attrs);
        */

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




    public static class RequestContext{

        public Object subject;
        public List<Object> entities;
        public Object action;

        public RequestContext(Object subject, List<Object> entities, Object action) {
            this.subject = subject;
            this.entities = entities;
            this.action = action;
        }
    }


    public static class EvaluationContext{
        public Object subject;
        public Object entity;
        public Object action;

        public EvaluationContext(Object subject, Object entity, Object action){
            this.subject = subject;
            this.entity = entity;
            this.action = action;
        }
    }


}
