package angine;


import javax.annotation.Nonnull;

public class Status {

    public enum Code{
        OK(0),
        PROCESSING_ERROR(1),
        SYNTAX_ERROR(2),
        MISSING_ATTRIBUTE(3);

        private final int value;

        Code(int i){
            this.value = i;
        }
    }


    public final Code code;
    public final String message;

    public Status(){
        this(Code.OK);
    }

    public Status(Code code){
        this(code,"No specific message");
    }

    public Status(Code code,@Nonnull String message){
        this.code = code;
        this.message = message;
    }
}
