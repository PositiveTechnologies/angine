package angine.context;


import angine.result.AbstractResult;
import angine.Decision;
import angine.result.Result;
import angine.Status;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ResponseContext {

    @Nonnull
    public List<AbstractResult> results;

    public ResponseContext(@Nonnull  List<AbstractResult> results){
        this.results = results;
    }

    public void addResult(AbstractResult result){
        results.add(result);
    }

    public static AbstractResult getCombinedDecision(@Nonnull List<AbstractResult> abstractResults){

        boolean atLeaftOneDeny = false;
        for(AbstractResult result : abstractResults){
            if(result.decision.equals(Decision.Indeterminate)
                    || result.decision.equals(Decision.NotApplicable)){
                return new AbstractResult(Decision.Indeterminate);
            } else {
                if ( result.decision.equals(Decision.DENY) ){
                    atLeaftOneDeny = true;
                }
            }
        }
        if(atLeaftOneDeny){
            return new AbstractResult(Decision.DENY);
        } else {
            return new AbstractResult(Decision.PERMIT);
        }
    }

    @Override
    public String toString() {
        Map res;
        if(results.size() == 1){
            res = results.get(0).encode();
        } else if (results.size() > 1){
            res = getCombinedDecision(results).encode();
        } else {
            res = new Result(
                    Decision.Indeterminate,
                    new Status(
                            Status.Code.PROCESSING_ERROR,
                            "No results in response"))
                    .encode();
        }
        Gson gson = new Gson();
        return gson.toJson(res);
    }
}
