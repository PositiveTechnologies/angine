package angine;

import java.util.Map;

public interface INodesDecoder {
    Map<String, IIdentifiable> fromJson(String jsonString) throws Exception;
}

