package com.example.spring_security.entities.Enum;

public enum FriendRequestStatus {
    PENDING, ACCEPTED, REJECTED, CANCELLED;

    public static class Converter extends AbstractEnumConverter<FriendRequestStatus> {
        public Converter() {
            super(FriendRequestStatus.class);
        }
    }

}
