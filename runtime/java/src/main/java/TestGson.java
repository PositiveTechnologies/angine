import angine.model.Decoder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by ivan on 28.10.17.
 */
public class TestGson {

    public static void run(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Decoder.class, new Decoder())
                .create();

        String json = "{\n" +
                "    \"entities\" :\n" +
                "        [\n" +
                "            { \"type\" : \"subject\", \"name\" : \"user1\", \"role\" : \"user\", \"ip\" : \"192.1.69.0.1\" },\n" +
                "            { \"type\" : \"urlentity\", \"path\" : \"/index\", \"type\" : \"kek\" }\n" +
                "        ]\n" +
                "}";
        Map<String, Object> attrs = (Map<String, Object>) gson.fromJson(json, Decoder.class);
    }
}
