package ua.testing.demo_jpa.auth;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
