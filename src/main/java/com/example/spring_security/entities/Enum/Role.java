package com.example.spring_security.entities.Enum;

public enum Role {
    USER, ADMIN;

    public static class Converter extends AbstractEnumConverter<Role> {
        public Converter() {
            super(Role.class);
        }
    }

}
