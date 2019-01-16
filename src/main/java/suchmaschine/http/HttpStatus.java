package suchmaschine.http;

public enum HttpStatus {

    Ok(200, "Ok"),
    BadRequest(400, "Bad Request"),
    Forbidden(403, "Forbidden"),
    NotFound(404, "Not Found"),
    MethodNotAllowed(405, "Method Not Allowed"),
    ;

    private int code;
    private String text;

    HttpStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

}