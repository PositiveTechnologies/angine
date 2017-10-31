package angine.model;

import com.fasterxml.jackson.databind.JsonNode;

public interface Factory {

    public Object create(JsonNode jsonEntity);
}
