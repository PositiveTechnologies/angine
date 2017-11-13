package angine;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PIP {

    public Map<String, IIdentifiable> attrs;


    /**
     * creates angine.PIP from attributes.
     * @param attrs Map from object.id to object
     */
    public PIP(Map<String, IIdentifiable> attrs){
        this.attrs = attrs;
    }
    public List<EvaluationContext> createContext( RequestContext requestContext){
        List<EvaluationContext> result = new ArrayList<EvaluationContext>();
        for(IIdentifiable e :  requestContext.entities){
            result.add(
                    new EvaluationContext(
                            attrs.get(requestContext.subject.id()),
                            attrs.get(e.id()),
                            requestContext.action
                    )
            );
        }
        return result;
    }



    /**
     * Creates angine.PIP. Attributes are loaded from jsonText, wich must match jsonSchema
     * @param factory object, extended from generated Decoder
     * @return angine.PIP
     */
    @Nullable
    public static PIP fromJson(@Nonnull String jsonSchema,@Nonnull String jsonText, @Nonnull INodesDecoder factory)
            throws Exception {
        JsonNode data;
        JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
        data = JsonLoader.fromString(jsonText);
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();
        JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
        ProcessingReport report = schema.validate(data);
        if (report.isSuccess()) {
            Map map = factory.fromJson(jsonText);
            List<IIdentifiable> objects = (List<IIdentifiable>) map.get("entities");
            Map<String, IIdentifiable> attrs = new HashMap<String, IIdentifiable>();
            for(IIdentifiable identifiable : objects){
                attrs.put(identifiable.id(),identifiable);
            }
            return new PIP(attrs);
        } else {
            return null;
        }
    }


    public static class RequestContext{

        public IIdentifiable subject;
        public List<IIdentifiable> entities;
        public Object action;

        public RequestContext(IIdentifiable subject, List<IIdentifiable> entities, Object action) {
            this.subject = subject;
            this.entities = entities;
            this.action = action;
        }
    }


    public static class EvaluationContext{
        public IIdentifiable subject;
        public IIdentifiable entity;
        public Object action;

        public EvaluationContext(IIdentifiable subject, IIdentifiable entity, Object action){
            this.subject = subject;
            this.entity = entity;
            this.action = action;
        }
    }


}
