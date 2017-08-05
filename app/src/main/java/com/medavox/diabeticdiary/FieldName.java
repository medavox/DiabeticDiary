package com.medavox.diabeticdiary;

/**
 * @author Adam Howard
 * @date 31/07/2017
 */
public enum FieldName {
    BG(0),
    CP(1),
    QA(2),
    BI(3),
    KT(4),
    NOTES(5);
    private int position;
    public static final int ARRAY_LENGTH = 6;

    FieldName(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
