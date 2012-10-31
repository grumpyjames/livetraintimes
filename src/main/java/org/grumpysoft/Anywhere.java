package org.grumpysoft;

public enum Anywhere implements Station {
    INSTANCE;

    @Override
    public String threeLetterCode() {
        return "ANY";
    }

    @Override
    public String fullName() {
        return "Anywhere!";
    }
}
