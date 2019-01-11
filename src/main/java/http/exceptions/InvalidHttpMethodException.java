package http.exceptions;

public class InvalidHttpMethodException extends InvalidRequestException {

    public InvalidHttpMethodException(String message) {
        super(message);
    }

}