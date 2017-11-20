package angine.result;


import angine.Decision;
import angine.Status;

import java.util.HashMap;
import java.util.Map;

public class Result extends AbstractResult {

    public Result(Decision decision){
        this(decision, new Status());
    }

    public Result(Decision decision, Status status) {
        super(decision, status);
    }

    @Override
    public Map<String, Object> encode() {
        Map<String, Object> superMap = super.encode();
        Map<String, String> map = new HashMap<String, String>();
        map.put(DECISION, this.decision.name().toLowerCase());
        superMap.putAll(map);
        return superMap;
    }
}
