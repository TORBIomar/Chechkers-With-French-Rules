package com.example.dames.model;

public record Move(int fromRow, int fromCol, int toRow, int toCol, int capturedRow, int capturedCol) {
    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(fromRow, fromCol, toRow, toCol, -1, -1);
    }

    public boolean hasCapture() {
        return capturedRow >= 0 && capturedCol >= 0;
    }
}

