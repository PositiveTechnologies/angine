package angine.context;


import angine.util.IIdentifiable;

import java.util.List;

public class RequestContext{

    public IIdentifiable subject;
    public List<IIdentifiable> entities;
    public Object action;
    public boolean combinedDecision;


    public RequestContext(IIdentifiable subject, List<IIdentifiable> entities, Object action, boolean combinedDecision) {
        this.subject = subject;
        this.entities = entities;
        this.action = action;
        this.combinedDecision = combinedDecision;
    }
}
