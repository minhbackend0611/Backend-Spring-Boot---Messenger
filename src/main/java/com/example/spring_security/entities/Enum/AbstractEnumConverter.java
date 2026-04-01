package com.example.spring_security.entities.Enum;

public abstract class AbstractEnumConverter<E extends Enum<E>> {

    private final E[] enumConstants;

    protected AbstractEnumConverter(Class<E> enumClass) {
        this.enumConstants = enumClass.getEnumConstants();
    }

    public E fromShort(Short code) {
        if (code == null) return this.enumConstants[0];
        int index = code.intValue();
        if (index < 0 || index >= enumConstants.length) return this.enumConstants[0];
        return enumConstants[index];
    }
}

