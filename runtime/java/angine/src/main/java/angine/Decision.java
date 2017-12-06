package angine;


public enum Decision{
    PERMIT(0),
    DENY(1),
    NOT_APPLICABLE(2),
    INDETERMINATE(3);


    public final int value;

    Decision(int i){
        this.value = i;
    }


    public static Decision fromInt(int i) throws IllegalArgumentException{
        for (Decision decision : Decision.values()){
            if (decision.value == i){
                return decision;
            }
        }
        throw new IllegalArgumentException("argument of function must be one of PDP's decision");
    }
}
