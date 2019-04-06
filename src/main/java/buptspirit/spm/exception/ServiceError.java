package buptspirit.spm.exception;

import buptspirit.spm.message.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public enum ServiceError {
    /* unknown errors */
    UNKNOWN(0, Response.Status.INTERNAL_SERVER_ERROR, "server internal error occurred, refer to the team bupt-spirit"),

    /* raw http error, do not use */
    RAW_HTTP_ERROR(1, Response.Status.INTERNAL_SERVER_ERROR, "raw http error, refer to status"),
    /* assertion error, do not use */
    ASSERTION(2, Response.Status.BAD_REQUEST, "assertion error"),

    /* authentication or authorization errors */
    UNAUTHENTICATED(10001, Response.Status.UNAUTHORIZED, "client must login to get access rights to the resource"),
    FORBIDDEN(10002, Response.Status.FORBIDDEN, "client does not have access rights to the resource"),

    // third position
    // GET - 1
    // POST - 2
    // PUT - 3
    // DELETE - 4

    /* session resource errors */
    POST_SESSION_INVALID_USERNAME_OR_PASSWORD(20201, Response.Status.UNAUTHORIZED, "username or password provided is invalid"),

    /* student resource errors */
    GET_STUDENT_NO_SUCH_STUDENT(30101, Response.Status.BAD_REQUEST, "no such user"),
    POST_STUDENT_USERNAME_ALREADY_EXISTS(30201, Response.Status.BAD_REQUEST, "username already exists"),

    /* notice resource errors */
    GET_NOTICE_NO_SUCH_NOTICE(40101, Response.Status.BAD_REQUEST, "no such notice"),
    PUT_NOTICE_NO_SUCH_NOTICE(40302, Response.Status.BAD_REQUEST, "no such notice"),
    DELETE_NOTICE_NO_SUCH_NOTICE(40403, Response.Status.BAD_REQUEST, "no such notice"),

    /* teacher resource errors */
    GET_TEACHER_NO_SUCH_TEACHER(50101, Response.Status.BAD_REQUEST, "no such user"),
    POST_TEACHER_USERNAME_ALREADY_EXISTS(50201, Response.Status.BAD_REQUEST, "username already exists"),
    ;

    private final int code;
    private final Response.Status status;
    private final String message;

    ServiceError(int code, Response.Status status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Response toResponse() {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setCode(getCode());
        errorMessage.setStatus(getStatus().getStatusCode());
        errorMessage.setMessage(getMessage());
        return Response.status(getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public ServiceException toException() {
        return new ServiceException(this);
    }
}