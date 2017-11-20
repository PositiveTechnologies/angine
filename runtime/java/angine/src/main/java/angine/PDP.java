package angine;

import angine.context.EvaluationContext;
import angine.context.ResponseContext;
import angine.result.AbstractResult;
import angine.result.Result;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import java.util.List;


public class PDP {

    static int PERMIT = 0;
    static int DENY = 1;
    static int NOT_APPLICABLE = 2;
    static int INDETERMINATE = 3;




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


    public ResponseContext evaluate(List<EvaluationContext> contextList){
        List<AbstractResult> results = new ArrayList<AbstractResult>();
        for(EvaluationContext context : contextList){
            LuaValue ctx = CoerceJavaToLua.coerce(context);
            LuaValue res = luaPolicy.call(ctx, actions, LuaValue.NIL);
            results.add(
                    new Result(
                            Decision.fromInt(res.toint())
                    )
            );
        }
        return new ResponseContext(results);
    }


}