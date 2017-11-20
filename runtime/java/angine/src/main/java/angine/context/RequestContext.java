package angine.context;


import angine.util.IIdentifiable;

import java.util.List;

public class RequestContext{

    public IIdentifiable subject;
    public List<IIdentifiable> entities;
    public Object action;

    public RequestContext(IIdentifiable subject, List<IIdentifiable> entities, Object action) {
        this.subject = subject;
        this.entities = entities;
        this.action = action;
    }
}
