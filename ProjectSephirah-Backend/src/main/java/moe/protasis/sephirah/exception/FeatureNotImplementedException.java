package moe.protasis.sephirah.exception;

public class FeatureNotImplementedException extends APIException {
    public FeatureNotImplementedException() {
        super(501, 15, "feature not implemented");
    }
}
