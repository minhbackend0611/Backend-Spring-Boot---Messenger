package com.example.spring_security.entities.Enum;

public enum GroupRole {
    MEMBER, ADMIN;

    public static class Converter extends AbstractEnumConverter<GroupRole> {
        public Converter() {
            super(GroupRole.class);
        }
    }

}
