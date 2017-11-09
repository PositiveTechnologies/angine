import angine.PIP;
import angine.PDP;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Test {


    public static String pathToLua = "policy.lua";

    public static String pathToEntities = "Entities.json";

    public static String pathToSchema = "scheme.json";

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
        List<Object> entities = new ArrayList<Object>();
        entities.add(urlEntity);



        List<String> roles = new ArrayList<String>();
        PIP.RequestContext request = new PIP.RequestContext(
                new Bindings.MySubject("user1", tags , roles, 10),
                entities,
                "GET");

        List<PIP.EvaluationContext> evaluationContexts = pip.createContext(request);
        Integer result = pdp.evaluate(evaluationContexts,false);
        if(result.equals(PDP.PERMIT)){
            System.out.println("permit success!");
        } else {
            throw new Exception("expect permit, got smth else");
        }
    }

    private void testDeny(PIP pip, PDP pdp) throws Exception {
        List<Object> entities = new ArrayList<Object>();
        List<String> tags = new ArrayList<String>();
        List<String> roles = new ArrayList<String>();
        roles.add("user");


        Bindings.MyUrlEntity urlEntity = new Bindings.MyUrlEntity("/admin/",tags, 10);
        entities.add(urlEntity);

        Bindings.MySubject subject =  new Bindings.MySubject("user1", tags , roles, 10);

        PIP.RequestContext request = new PIP.RequestContext(
                subject,
                entities,
                "GET"
        );

        List<PIP.EvaluationContext> evaluationContexts = pip.createContext(request);
        Integer result = pdp.evaluate(evaluationContexts,false);
        if(!result.equals(PDP.PERMIT)){
            System.out.println("deny success!");
        } else {
            throw new Exception("expect deny, got smth else");
        }
    }

    private PIP createTestPIP(){
        String json  = readFile(pathToEntities);
        String schema = readFile(pathToSchema);
        return PIP.fromJson(schema,json, new Bindings.MyFactory());
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
