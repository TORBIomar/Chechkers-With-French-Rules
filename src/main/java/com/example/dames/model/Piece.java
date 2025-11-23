package com.example.dames.model;

public class Piece {
    private final boolean isWhite;
    private PieceType type;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
        this.type = PieceType.MAN;
    }

    public boolean isWhite() { return isWhite; }
    public PieceType getType() { return type; }
    public void promoteToKing() { this.type = PieceType.KING; }
}

