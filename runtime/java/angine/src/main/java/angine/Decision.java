package angine;


public enum Decision{
    PERMIT(PDP.PERMIT),
    DENY(PDP.DENY),
    NotApplicable(PDP.NOT_APPLICABLE),
    Indeterminate(PDP.INDETERMINATE);


    private final int value;

    Decision(int i){
        this.value = i;
    }

    public static Decision fromInt(int i){
        for (Decision decision : Decision.values()){
            if (decision.value == i){
                return decision;
            }
        }
        return Decision.Indeterminate;
    }
}
