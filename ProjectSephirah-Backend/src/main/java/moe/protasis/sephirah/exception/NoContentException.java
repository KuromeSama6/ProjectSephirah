package moe.protasis.sephirah.exception;

public class NoContentException extends APIException {
    public NoContentException() {
        super(204, 0, "ok");
    }
}
