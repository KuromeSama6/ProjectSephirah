package moe.protasis.sephirah.exception.provider;

import moe.protasis.sephirah.exception.APIException;

public class ProviderNotAvailableException extends APIException {
    public ProviderNotAvailableException(String message) {
        super(502, 58, message);
    }
}
