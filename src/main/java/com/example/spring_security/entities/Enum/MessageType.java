package com.example.spring_security.entities.Enum;

public enum MessageType {
    TEXT, IMAGE, SYSTEM;

    public static class Converter extends AbstractEnumConverter<MessageType> {
        public Converter() {
            super(MessageType.class);
        }
    }

}
