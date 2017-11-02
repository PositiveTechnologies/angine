package angine.generated;

import java.util.List;

public class AST{

    public abstract static class Entity  {

        public  abstract String id();

    }

    public abstract static class UrlEntity extends Entity {

        public String path;

        public int level;

        public List<String> tags;

    }

    public abstract static class Subject extends Entity {

        public String name;

        public List<String> roles;

        public int level;

        public List<String> tags;

        public  abstract String ip();

    }

}