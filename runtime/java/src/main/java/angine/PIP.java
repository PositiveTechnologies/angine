package angine;

import angine.generated.Decoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
     * @param factory объект, способный восстанавливать AST из json
     * @return PIP
     */
    public static PIP fromJson(String jsonSchema, String JsonText, @Nonnull Decoder factory){

        JsonNode data;
        try {
            JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
            data = JsonLoader.fromString(JsonText);
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
            JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
            ProcessingReport report = schema.validate(data);
            if (report.isSuccess()){
                return new PIP(factory.fromJson(JsonText));
            } else {
                System.out.println("NOO");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



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
