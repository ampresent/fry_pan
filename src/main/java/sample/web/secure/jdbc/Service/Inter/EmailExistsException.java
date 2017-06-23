package sample.web.secure.jdbc.Service.Inter;

/**
 * Created by wuyihao on 5/13/17.
 */
public class EmailExistsException extends Exception{
    public EmailExistsException(String s) {super(s);}
}
