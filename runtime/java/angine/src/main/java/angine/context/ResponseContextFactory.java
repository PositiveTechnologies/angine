package angine.context;


import angine.result.Result;
import angine.Decision;
import angine.Status;

import java.util.ArrayList;
import java.util.List;

public class ResponseContextFactory {

    public static ResponseContext indeterminate(Status.Code statusCode, String message){
        Status status = new Status(statusCode, message);
        return fromResult( new Result(Decision.Indeterminate, status));
    }

    public static ResponseContext fromResult(Result result){
        List<Result> results = new ArrayList<Result>(1);
        results.add(result);
        return fromResults(results);
    }

    public static ResponseContext fromResults(List<Result> results){
        return new ResponseContext(results);
    }
}
