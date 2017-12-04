package angine;

import angine.context.AccessRequest;
import angine.context.EvaluationContext;
import angine.context.RequestContext;
import angine.util.IIdentifiable;
import angine.util.INodesDecoder;
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


    /**
     * creates EvaluationContext, which can be passed to PDP to evaluate, from RequestContext
     * @throws IllegalArgumentException if PIPs attributes don't contain subject, entity or action from @requestContext
     */
    public EvaluationContext createContext( RequestContext requestContext) throws IllegalArgumentException{
        List<AccessRequest> accessRequests = new ArrayList<AccessRequest>(requestContext.entities.size());
        for(IIdentifiable e : requestContext.entities){
            IIdentifiable subject = attrs.get(requestContext.subject.id());
            IIdentifiable entity = attrs.get(e.id());
            Object action = requestContext.action;
            if(subject == null || entity == null || action == null){
                throw new IllegalArgumentException("PIP cant create proper context from this request");
            }
            accessRequests.add(
                    new AccessRequest(
                            subject,
                            entity,
                            action
                    )
            );
        }
        return new EvaluationContext(accessRequests, requestContext.combinedDecision);
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

}