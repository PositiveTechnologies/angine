import java.util.List;

public class AST{

    public static class Entity  {
    
        public  abstract String id();
        
    }
    
    public static class UrlEntity extends Entity {
    
        public String path;
        
        public int level;
        
        public List<String> tags;
        
    }
    
    public static class Subject extends Entity {
    
        public String name;
        
        public List<String> roles;
        
        public int level;
        
        public List<String> tags;
        
        public  abstract String ip();
        
    }
    
}