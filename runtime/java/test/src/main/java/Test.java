import angine.Decision;
import angine.context.EvaluationContext;
import angine.context.RequestContext;
import angine.context.ResponseContext;
import angine.util.IIdentifiable;
import angine.PIP;
import angine.PDP;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Test {


    private static String pathToLua = "policy.lua";

    private static String pathToEntities = "Entities.json";

    private static String pathToSchema = "scheme.json";

    public void run(){
        try {

            String luaPolicy = readFile(pathToLua);
            PDP pdp = new PDP(luaPolicy);
            PIP pip = createTestPIP();
            testPermit(pip, pdp);
            testDeny(pip, pdp);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void testPermit(PIP pip, PDP pdp) throws Exception {
        List<String> tags = new ArrayList<String>();
        Bindings.MyUrlEntity urlEntity = new Bindings.MyUrlEntity("/index.html",tags, 5);
        List<IIdentifiable> entities = new ArrayList<IIdentifiable>();
        entities.add(urlEntity);



        List<String> roles = new ArrayList<String>();
        RequestContext request = new RequestContext(
                new Bindings.MySubject("user1", tags , roles, 10),
                entities,
                "GET", true);

        EvaluationContext evaluationContexts = pip.createContext(request);
        ResponseContext responseContext = pdp.evaluate(evaluationContexts);
        if(responseContext.results.get(0).decision.equals(Decision.PERMIT)){
            System.out.println("permit success!");
            System.out.println(new Gson().toJson(responseContext.results.get(0).encode()));
        } else {
            throw new Exception("expect permit, got smth else");
        }
    }

    private void testDeny(PIP pip, PDP pdp) throws Exception {
        List<IIdentifiable> entities = new ArrayList<IIdentifiable>();
        List<String> tags = new ArrayList<String>();
        List<String> roles = new ArrayList<String>();
        roles.add("user");


        Bindings.MyUrlEntity urlEntity = new Bindings.MyUrlEntity("/admin/",tags, 10);
        entities.add(urlEntity);

        Bindings.MySubject subject =  new Bindings.MySubject("user1", tags , roles, 10);

        RequestContext request = new RequestContext(
                subject,
                entities,
                "GET",
                true
        );

        EvaluationContext evaluationContexts = pip.createContext(request);
        ResponseContext result = pdp.evaluate(evaluationContexts);
        if(!result.results.get(0).decision.equals(Decision.PERMIT)){
            System.out.println("deny success!");
            System.out.println(new Gson().toJson(result.results.get(0).encode()));
        } else {
            throw new Exception("expect deny, got smth else");
        }
    }

    private PIP createTestPIP(){
        String json  = readFile(pathToEntities);
        String schema = readFile(pathToSchema);
        try {
            return PIP.fromJson(schema,json, new Bindings.MyFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String readFile(String path) {
        StringBuilder result = new StringBuilder("");
        Scanner scanner = new Scanner(ClassLoader.getSystemResourceAsStream(path));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.append(line).append("\n");
        }
        scanner.close();

        return result.toString();
    }


}
