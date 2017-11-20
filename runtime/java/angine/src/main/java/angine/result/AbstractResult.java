package angine.result;


import angine.Decision;
import angine.Status;

import java.util.HashMap;
import java.util.Map;

public class AbstractResult {

    public final static String DECISION = "decision";

    public Decision decision;

    public Status status;

    public AbstractResult(Decision decision){
        this.decision = decision;
        this.status = new Status();
    }

    public AbstractResult(Decision decision, Status status){
        this.decision = decision;
        this.status = status;
    }

    public Map<String, Object> encode(){
        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put(DECISION,this.decision.name().toLowerCase());
        Map<String, String> statusMap = new HashMap<String, String>();
        statusMap.put("code",this.status.code.name().toLowerCase());
        statusMap.put("message", this.status.message);
        resultMap.put("status", statusMap);
        return resultMap;
    }



}
