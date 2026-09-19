package com.picnic.delivery.config;

import java.util.UUID;
import org.springframework.security.core.Authentication;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * The customer_id of the caller, as supplied by the gateway.
     */
    public static UUID currentCustomerId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomerPrincipal principal)) {
            throw new IllegalStateException("No authenticated customer on the request");
        }
        return principal.customerId();
    }
}
