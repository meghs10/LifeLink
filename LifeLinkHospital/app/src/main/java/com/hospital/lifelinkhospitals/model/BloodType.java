package com.hospital.lifelinkhospitals.model;

public enum BloodType {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-");

    private final String display;

    BloodType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
