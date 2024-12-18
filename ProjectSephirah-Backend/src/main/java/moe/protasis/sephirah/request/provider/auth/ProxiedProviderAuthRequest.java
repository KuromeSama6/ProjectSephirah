package moe.protasis.sephirah.request.provider.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProxiedProviderAuthRequest {
    @NotBlank
    private String keyPairId;
    @NotNull
    private Map<String, String> credentials;
}
