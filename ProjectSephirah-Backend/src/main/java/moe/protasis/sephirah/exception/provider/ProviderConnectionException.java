package moe.protasis.sephirah.exception.provider;

import moe.protasis.sephirah.exception.APIException;

public class ProviderConnectionException extends APIException {
    public ProviderConnectionException(String message) {
        super(504, 39, message);
    }
}