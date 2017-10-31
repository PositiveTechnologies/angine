package angine.generated;

/**
 * Предполагается, что aule сгенерировал эти файлы
 */
public abstract class Entities {

    public static class Entity  {

        public Entity(String id) {
            this.id = id;
        }

        public String id;

    }

    public static class UrlEntity extends Entity {

        public UrlEntity(String path, String type) {
            super(path);
            this.path = path;
            this.type = type;
        }

        public String path;

        public String type;

    }

    public static class Subject extends Entity {

        public Subject(String name, String role, String ip) {
            super(name);
            this.name = name;
            this.role = role;
            this.ip = ip;
        }

        public String name;

        public String role;

        public String ip;

    }
}
