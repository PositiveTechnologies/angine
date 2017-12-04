package angine;

import angine.context.AccessRequest;
import angine.context.EvaluationContext;
import angine.context.ResponseContext;
import angine.result.Result;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PDP {

    public static int PERMIT = 0;
    public static int DENY = 1;
    public static int NOT_APPLICABLE = 2;
    public static int INDETERMINATE = 3;




    private String LUA_ENTRY = "__main";

    private LuaValue actions;

    private LuaValue luaPolicy;

    private Object handlers;

    public PDP(String policy){
        this(policy, null);
    }

    public PDP(String policy, @Nullable Object handlers) throws IllegalArgumentException{
        this.actions = LuaValue.tableOf();
        actions.set("permit", PERMIT);
        actions.set("deny", DENY);
        actions.set("notapplicable", NOT_APPLICABLE);
        actions.set("indeterminate", INDETERMINATE);

        Globals globals = JsePlatform.standardGlobals();
        globals.set("actions",actions);
        globals.load(policy).call();
        this.luaPolicy = globals.get(LUA_ENTRY);
        if(luaPolicy == null){
            throw new IllegalArgumentException("Policy's entry point not found.");
        }
        this.handlers = handlers;
    }


    public ResponseContext evaluate(EvaluationContext evaluationContext){
        List<AccessRequest> accessRequests = evaluationContext.accessRequests;
        List<Result> results = new ArrayList<Result>();
        for(AccessRequest accessRequest : accessRequests){
            LuaValue ctx = CoerceJavaToLua.coerce(accessRequest);
            LuaValue handlers = CoerceJavaToLua.coerce(this.handlers);
            LuaValue res = luaPolicy.call(ctx, actions, handlers);
            results.add( new Result(
                    Decision.fromInt(res.toint())
            ));
        }
        if(evaluationContext.combinedDecision){
            return new ResponseContext(Collections.singletonList(getCombinedDecision(results)));
        } else {
            return new ResponseContext(results);
        }

    }


    public static Result getCombinedDecision( List<Result> results){
        if(results == null || results.size() == 0){
            return new Result(
                    Decision.Indeterminate,
                    new Status(Status.Code.PROCESSING_ERROR)
            );
        }

        for(Result result : results){
            if(results.get(0).decision != result.decision){
                return new Result(
                        Decision.Indeterminate,
                        new Status(Status.Code.PROCESSING_ERROR)
                );
            }
        }
        return results.get(0);

    }


}