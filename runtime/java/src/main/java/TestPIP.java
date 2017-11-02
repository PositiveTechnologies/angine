import angine.PIP;

public class TestPIP {

    public static void run(){
        String schema = "{\n" +
                "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                "    \"definitions\": {\n" +
                "        \"builtins.Entity\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"properties\": {\n" +
                "                \"type\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"additionalProperties\": false\n" +
                "        },\n" +
                "        \"builtins.Subject\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"properties\": {\n" +
                "                \"type\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"name\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"roles\": {\n" +
                "                    \"type\": \"array\",\n" +
                "                    \"items\": {\n" +
                "                        \"type\": \"string\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"level\": {\n" +
                "                    \"type\": \"integer\"\n" +
                "                },\n" +
                "                \"tags\": {\n" +
                "                    \"type\": \"array\",\n" +
                "                    \"items\": {\n" +
                "                        \"type\": \"string\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"ip\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"additionalProperties\": false\n" +
                "        },\n" +
                "        \"builtins.UrlEntity\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"properties\": {\n" +
                "                \"type\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"path\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"level\": {\n" +
                "                    \"type\": \"integer\"\n" +
                "                },\n" +
                "                \"tags\": {\n" +
                "                    \"type\": \"array\",\n" +
                "                    \"items\": {\n" +
                "                        \"type\": \"string\"\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            \"additionalProperties\": false\n" +
                "        }\n" +
                "    },\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "        \"entities\": {\n" +
                "            \"type\": \"array\",\n" +
                "            \"items\": {\n" +
                "                \"oneOf\": [\n" +
                "                    {\n" +
                "                        \"$ref\": \"#/definitions/builtins.Entity\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"$ref\": \"#/definitions/builtins.UrlEntity\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"$ref\": \"#/definitions/builtins.Subject\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"required\": [\n" +
                "        \"entities\"\n" +
                "    ],\n" +
                "    \"additionalProperties\": false\n" +
                "}";


        String json = "{\n" +
                "    \"entities\" :\n" +
                "        [\n" +
                "            { \"type\" : \"subject\", \"name\" : \"user1\" }\n" +
                "        ]\n" +
                "}";


        PIP pip = PIP.fromJson(schema,json,new Bindings.MyFactory());
    }
}
