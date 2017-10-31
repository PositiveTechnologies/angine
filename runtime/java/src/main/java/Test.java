import angine.PDP;
import angine.PIP;
import angine.model.MyEntities;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test {

    public static String pathToLua = "/home/ivan/IdeaProjects/luatest/src/main/resources/script.lua";

    public static void run(String[] args){
        try {
            String luaPolicy = readFile(pathToLua,Charset.defaultCharset());
            PDP pdp = new PDP(luaPolicy);
            PIP pip = createTestPIP();
            testPermit(pip, pdp);
            testDeny(pip, pdp);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testPermit(PIP pip, PDP pdp) throws Exception {
        MyEntities.MyUrlEntity urlEntity = new MyEntities.MyUrlEntity("/index.html","");
        List<Object> entities = new ArrayList<Object>();
        entities.add(urlEntity);

        PIP.RequestContext request = new PIP.RequestContext(
                new MyEntities.MySubject("user1", "user", "10.0.4.1"),
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

    private static void testDeny(PIP pip, PDP pdp) throws Exception {
        MyEntities.MyUrlEntity urlEntity = new MyEntities.MyUrlEntity("/admin","");
        List<Object> entities = new ArrayList<Object>();
        entities.add(urlEntity);

        PIP.RequestContext request = new PIP.RequestContext(
                new MyEntities.MySubject("user1", "user", "10.0.4.1"),
                entities,
                "GET");

        List<PIP.EvaluationContext> evaluationContexts = pip.createContext(request);
        Integer result = pdp.evaluate(evaluationContexts,false);
        if(!result.equals(PDP.PERMIT)){
            System.out.println("deny success!");
        } else {
            throw new Exception("expect deny, got smth else");
        }
    }

    /**
     *  here we have
     *  Subject : name = user1, role = user, ip = 10.0.4.1
     *  Url: /index.html
     *  Url: /admin
     */
    private static PIP createTestPIP(){
        Map<String, Object> attrs = new HashMap<String, Object>();
        MyEntities.MySubject mySubject = new MyEntities.MySubject("user1","user","10.0.4.1");
        attrs.put("user1",mySubject);
        MyEntities.MyUrlEntity urlEntity = new MyEntities.MyUrlEntity("/index.html","");
        attrs.put("/index.html",urlEntity);

        urlEntity = new MyEntities.MyUrlEntity("/admin","");
        attrs.put("/admin",urlEntity);
        return new PIP(attrs);
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
