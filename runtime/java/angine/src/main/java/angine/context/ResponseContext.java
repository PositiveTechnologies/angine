package angine.context;


import angine.result.Result;
import angine.Decision;
import angine.Status;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseContext {

    public List<Result> results;

    public ResponseContext(@Nonnull  List<Result> results){
        this.results = results;
    }

    public void addResult(Result result){
        results.add(result);
    }


    @Override
    public String toString() {
        Map res;
        if(this.results != null && this.results.size() > 0){
            res =  new HashMap<String, Object>();
            List<Map> encodedItems = new ArrayList<Map>(this.results.size());
            for(Result result : this.results){
                encodedItems.add(result.encode());
            }
        } else {
            res = new Result(
                    Decision.Indeterminate,
                    new Status(
                            Status.Code.PROCESSING_ERROR,
                            "Response must contain at least one result"))
                    .encode();
        }
        Gson gson = new Gson();
        return gson.toJson(res);
    }
}
