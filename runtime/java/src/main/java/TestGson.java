import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Created by ivan on 28.10.17.
 */
public class TestGson {

    public static void run(){


        String json = "{\n" +
                "    \"entities\" :\n" +
                "        [\n" +
                "            { \"type\" : \"subject\", \"name\" : \"user1\", \"role\" : \"user\", \"ip\" : \"192.1.69.0.1\" },\n" +
                "            { \"type\" : \"urlentity\", \"path\" : \"/index\", \"xz\" : \"kek\" }\n" +
                "        ]\n" +
                "}";

        Bindings.MyFactory myFactory = new Bindings.MyFactory();
        Map<String, Object> objects = null;
        try {
            objects = myFactory.fromJson(json);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for(Object object : objects.values()){
            if(object instanceof Bindings.MySubject){
                System.out.println(((Bindings.MySubject) object).name);
            } else if(object instanceof Bindings.MyUrlEntity){
                System.out.println( ((Bindings.MyUrlEntity) object).path );
            } else {
                System.out.println(object.getClass().toString());
            }
        }
    }



}
