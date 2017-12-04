package angine.context;


import java.util.List;

public class EvaluationContext{

    public List<AccessRequest> accessRequests;
    public boolean combinedDecision;

    public EvaluationContext(List<AccessRequest> accessRequests, boolean combinedDecision) {
        this.accessRequests = accessRequests;
        this.combinedDecision = combinedDecision;
    }
}
