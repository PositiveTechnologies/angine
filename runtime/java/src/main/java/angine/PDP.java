package angine;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class PDP {

    public static int PERMIT = 0;
    public static int DENY = 1;
    public static int NOT_APPLICABLE = 2;
    public static int INDETERMINATE = 3;



    public static String MISSING_ATTRIBUTE = "Missing attribute";
    public static String OK = "ok";
    public static String PROCESSING_ERROR = "Processing error";
    public static String SYNTAX_ERROR = "Syntax error";




    public String LUA_ENTRY = "__main";

    public LuaValue actions;

    public LuaValue luaPolicy;

    public PDP(String policy){
        this.actions = LuaValue.tableOf();
        actions.set("permit", PERMIT);
        actions.set("deny", DENY);
        actions.set("notapplicable", NOT_APPLICABLE);
        actions.set("indeterminate", INDETERMINATE);

        Globals globals = JsePlatform.standardGlobals();
        globals.set("actions",actions);
        globals.load(policy).call();
        this.luaPolicy = globals.get(LUA_ENTRY);
    }


    public Integer evaluate(List<PIP.EvaluationContext> contextList, boolean formatResults){

        List<Integer> decisions = new ArrayList<Integer>();
        for(PIP.EvaluationContext context : contextList){
            LuaValue ctx = CoerceJavaToLua.coerce(context);
            LuaValue res = luaPolicy.call(ctx, actions, LuaValue.NIL);
            decisions.add(res.toint());
        }

        int finalDecision = PERMIT;
        for(Integer decision : decisions){
            if ( !decision.equals(PERMIT) ){
                finalDecision = INDETERMINATE;
                break;
            }
        }

        if(formatResults){
            return formatResponse(finalDecision, null);
        } else {
            return finalDecision;
        }
    }


    /**
     * Incorrect function
     */
    public static int formatResponse( Integer decision, @Nullable String status) {
        System.out.println("Not implemented yet, please dont call this func");
        return decision;
    }
}
