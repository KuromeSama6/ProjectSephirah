package moe.protasis.sephirah.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import moe.protasis.sephirah.util.JsonWrapper;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class APIException extends RuntimeException {
    private int code;
    private int statusCode;
    private String message;
    private JsonWrapper errorData = new JsonWrapper();

    public APIException(String message) {
        this(400, message);
    }

    public APIException(String message, JsonWrapper errorData) {
        this(message);
        this.errorData = errorData;
    }

    public APIException(int code, String message) {
        this.code = code;
        this.message = message;

        if (code >= 500) statusCode = 1;
        else {
            if (code == 403 || code == 401) statusCode = 16;
            else statusCode = 17;
        }
    }

    public APIException(int code, int status, String message) {
        this.code = code;
        this.statusCode = status;
        this.message = message;
    }

}
