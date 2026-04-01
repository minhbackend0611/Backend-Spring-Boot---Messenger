package com.example.spring_security.entities.Enum;

public enum ConversationType {
    PRIVATE, GROUP;

    public static class Converter extends AbstractEnumConverter<ConversationType> {
        public Converter() {
            super(ConversationType.class);
        }
    }

}
