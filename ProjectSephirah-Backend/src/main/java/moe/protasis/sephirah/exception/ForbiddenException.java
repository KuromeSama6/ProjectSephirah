package moe.protasis.sephirah.exception;

public class ForbiddenException extends APIException {
    public ForbiddenException() {
        super(403, 16, "operation not permitted");
    }
}
