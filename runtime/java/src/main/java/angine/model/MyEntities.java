package angine.model;

import angine.generated.Entities;

/**
 * Реализация сгенерированных классов
 */
public class MyEntities {

    public static class MyEntity extends Entities.Entity{

        public MyEntity(String id) {
            super(id);
        }
    }


    public static class MyUrlEntity extends Entities.UrlEntity{

        public MyUrlEntity(String path, String type) {
            super(path, type);
        }
    }


    public static class MySubject extends Entities.Subject{

        public MySubject(String name, String role, String ip) {
            super(name, role, ip);
        }

    }
}
