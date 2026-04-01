package com.example.spring_security.entities.Enum;

public enum Gender {
    HIDDEN, MALE, FEMALE;

    public static class Converter extends AbstractEnumConverter<Gender> {
        public Converter() {
            super(Gender.class);
        }
    }

}
