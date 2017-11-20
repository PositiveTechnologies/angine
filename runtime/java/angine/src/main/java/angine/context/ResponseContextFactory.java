package angine.context;


import angine.result.AbstractResult;
import angine.Decision;
import angine.result.Result;
import angine.Status;

import java.util.ArrayList;
import java.util.List;

public class ResponseContextFactory {

    public static ResponseContext indeterminate(Status.Code statusCode, String message){
        Status status = new Status(statusCode, message);
        return fromResult( new Result(Decision.Indeterminate, status));
    }

    public static ResponseContext fromResult(AbstractResult result){
        List<AbstractResult> results = new ArrayList<AbstractResult>(1);
        results.add(result);
        return fromResults(results);
    }

    public static ResponseContext fromResults(List<AbstractResult> results){
        return new ResponseContext(results);
    }
}
