package com.example.dames.model;

public class Tile {
    private final int row, col;
    private Piece piece;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public Piece getPiece() { return piece; }
    public void setPiece(Piece piece) { this.piece = piece; }
    public boolean isEmpty() { return piece == null; }
    
    public boolean hasEnemyPiece(boolean isWhite) {
        return !isEmpty() && piece.isWhite() != isWhite;
    }
    
    public boolean hasFriendlyPiece(boolean isWhite) {
        return !isEmpty() && piece.isWhite() == isWhite;
    }
}

