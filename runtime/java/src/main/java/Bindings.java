import angine.generated.AST;
import angine.generated.Decoder;
import com.google.gson.JsonElement;

import java.util.List;


public class Bindings {

    public static class MySubject extends AST.Subject{

        public MySubject(String name, List<String> tags, List<String> roles, int level){
            this.name = name;
            this.level = level;
            this.tags = tags;
            this.roles = roles;
        }

        public String id() {
            return this.name;
        }

        public String ip() {
            return "127.0.0.1";
        }
    }

    public static class MyUrlEntity extends AST.UrlEntity{

        public MyUrlEntity(String path, List<String> tags, int level){
            this.path = path;
            this.tags = tags;
            this.level = level;
        }

        public String id() {
            return this.path;
        }
    }

    public static class MyFactory extends Decoder{


        public Object createEntity(JsonElement je) {
            return create(je, AST.Entity.class);
        }

        public Object createSubject(JsonElement je) {
            return create(je, MySubject.class);
        }

        public Object createUrlEntity(JsonElement je) {
            return create(je, MyUrlEntity.class);
        }
    }
}
