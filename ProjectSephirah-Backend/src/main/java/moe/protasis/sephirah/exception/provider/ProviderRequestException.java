package moe.protasis.sephirah.exception.provider;

import moe.protasis.sephirah.exception.APIException;
import moe.protasis.sephirah.util.JsonWrapper;

public class ProviderRequestException extends APIException {
    public ProviderRequestException(int code, int statusCode, String message, JsonWrapper errorData) {
        super(code, statusCode, message, errorData);
    }

    public ProviderRequestException(String message) {
        super(61, 502, message);
    }
}
