package angine.result;

import angine.Decision;
import angine.Status;

/**
 * Currently out of use
 */
public class ResultFactory {

    public static AbstractResult create(Decision decision, Status status){
        return new Result(decision, status);
    }
}
