package ua.testing.demo_jpa.entity;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    ROLE_SUPERADMIN,
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_USER_WEB;


    @Override
    public String getAuthority() {
        return name();
    }
}
