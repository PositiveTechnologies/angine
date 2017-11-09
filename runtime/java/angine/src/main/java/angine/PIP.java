package angine;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PIP {

    public Map<String, Object> attrs;


    /**
     * creates PIP from attributes.
     * @param attrs Map from object.id to object
     */
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
     * Creates PIP. Attributes are loaded from jsonText, wich must match jsonSchema
     * @param factory object, extended from generated Decoder
     * @return PIP
     */
    @Nullable
    public static PIP fromJson(@Nonnull String jsonSchema,@Nonnull String jsonText, @Nonnull Object factory){
        try {
            JsonNode data;
            JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
            data = JsonLoader.fromString(jsonText);
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
            JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
            ProcessingReport report = schema.validate(data);
            if (report.isSuccess()) {
                return new PIP(callFromJson(factory,jsonText));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * calls o.fromJson(jsonText)
     */
    private static Map<String, Object> callFromJson(Object o, String jsonText){
        try {
            Method method = o.getClass().getMethod("fromJson",String.class);
            return (Map<String, Object>) method.invoke(o, jsonText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Calls object.id() method
     */
    private String getId(Object o){
        try {
            Method method = o.getClass().getMethod("id");
            return (String) method.invoke(o);
        } catch (Exception e) {
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
