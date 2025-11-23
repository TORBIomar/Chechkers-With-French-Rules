package com.example.dames.controller;

import com.example.dames.model.Piece;
import com.example.dames.model.Tile;
import com.example.dames.model.PieceType;
import com.example.dames.model.Move;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class BoardController {
    @FXML
    private GridPane grid;
    @FXML
    private Label statusLabel;

    private final int SIZE = 8;
    private Tile[][] board = new Tile[SIZE][SIZE];
    private Tile selected = null;
    private boolean isWhiteTurn = true;
    private List<Move> currentCaptureChain = new ArrayList<>();
    private Tile captureStartTile = null;

    @FXML
    public void initialize() {
        initModel();

        // Set up column and row constraints to fill space
        for (int i = 0; i < SIZE; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / SIZE);
            grid.getColumnConstraints().add(colConst);

            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / SIZE);
            grid.getRowConstraints().add(rowConst);
        }

        buildUI();
        updateStatus();

        // Set up resize listener after scene is available
        Platform.runLater(() -> {
            if (grid.getScene() != null) {
                grid.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() > 0)
                        buildUI();
                });
                grid.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.doubleValue() > 0)
                        buildUI();
                });
            }
        });
    }

    private void initModel() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = new Tile(r, c);
            }
        }
        // Place pieces: rows 0..2 black (false), rows 5..7 white (true) on dark squares
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < SIZE; c++) {
                if ((r + c) % 2 == 1)
                    board[r][c].setPiece(new Piece(false));
            }
        }
        for (int r = 5; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if ((r + c) % 2 == 1)
                    board[r][c].setPiece(new Piece(true));
            }
        }
    }

    private void buildUI() {
        grid.getChildren().clear();
        // Keep column and row constraints - don't clear them

        // Calculate tile size to fill available space dynamically
        double availableWidth = 640;
        double availableHeight = 640;
        if (grid.getScene() != null) {
            availableWidth = grid.getScene().getWidth();
            availableHeight = grid.getScene().getHeight();
        } else if (grid.getWidth() > 0 && grid.getHeight() > 0) {
            availableWidth = grid.getWidth();
            availableHeight = grid.getHeight();
        }
        double tileSize = Math.min(availableWidth / SIZE, availableHeight / SIZE);
        if (tileSize <= 0)
            tileSize = 60; // Minimum fallback size

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                StackPane tilePane = new StackPane();
                boolean isLight = (r + c) % 2 == 0;
                tilePane.getStyleClass().clear(); // Clear all styles first
                tilePane.getStyleClass().add(isLight ? "light-tile" : "dark-tile");

                // Use max size to fill the grid cell
                tilePane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                tilePane.setPrefSize(tileSize, tileSize);

                // Set explicit background color - bind to tilePane size
                Rectangle background = new Rectangle();
                background.widthProperty().bind(tilePane.widthProperty());
                background.heightProperty().bind(tilePane.heightProperty());
                if (isLight) {
                    background.setFill(Color.web("#f0d9b5")); // Light beige
                } else {
                    background.setFill(Color.web("#b58863")); // Dark brown
                }
                tilePane.getChildren().add(background);

                final int rr = r, cc = c;
                tilePane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> onTileClicked(rr, cc, tilePane));

                if (!board[r][c].isEmpty()) {
                    Piece piece = board[r][c].getPiece();
                    Circle pieceCircle = createPieceVisual(piece, tileSize);
                    // Bind piece size to tile size
                    pieceCircle.radiusProperty().bind(tilePane.widthProperty().multiply(0.375));
                    tilePane.getChildren().add(pieceCircle);
                }

                grid.add(tilePane, c, r);
            }
        }

        highlightPossibleMoves();
    }

    private Circle createPieceVisual(Piece piece, double tileSize) {
        // Initial radius, will be bound to tile size
        Circle circle = new Circle(tileSize * 0.375);
        circle.getStyleClass().clear(); // Clear any existing styles

        if (piece.getType() == PieceType.KING) {
            if (piece.isWhite()) {
                circle.setFill(Color.web("#ffd700")); // Gold
                circle.setStroke(Color.web("#cc5500"));
                circle.setStrokeWidth(4);
                circle.getStyleClass().add("king-white");
            } else {
                circle.setFill(Color.web("#8e44ad")); // Purple
                circle.setStroke(Color.web("#4a235a"));
                circle.setStrokeWidth(4);
                circle.getStyleClass().add("king-black");
            }
        } else {
            if (piece.isWhite()) {
                circle.setFill(Color.web("#ffffff")); // White
                circle.setStroke(Color.web("#2c3e50"));
                circle.setStrokeWidth(3);
                circle.getStyleClass().add("piece-white");
            } else {
                circle.setFill(Color.web("#1a1a1a")); // Black
                circle.setStroke(Color.web("#ffffff"));
                circle.setStrokeWidth(3);
                circle.getStyleClass().add("piece-black");
            }
        }
        return circle;
    }

    private void highlightPossibleMoves() {
        if (selected == null)
            return;

        Piece selectedPiece = selected.getPiece();
        if (selectedPiece == null || selectedPiece.isWhite() != isWhiteTurn)
            return;

        List<Move> moves = getValidMoves(selected.getRow(), selected.getCol());
        for (Move move : moves) {
            StackPane tilePane = getTilePane(move.toRow(), move.toCol());
            if (tilePane != null) {
                // Remove possible-move class if it exists, then add it
                tilePane.getStyleClass().remove("possible-move");
                tilePane.getStyleClass().add("possible-move");

                // Add a visual indicator circle for better visibility
                Circle indicator = new Circle();
                indicator.radiusProperty().bind(tilePane.widthProperty().multiply(0.375));
                if (move.hasCapture()) {
                    // Red/orange for captures
                    indicator.setFill(Color.web("rgba(255, 100, 100, 0.4)"));
                    indicator.setStroke(Color.web("#ff6464"));
                } else {
                    // Green for regular moves
                    indicator.setFill(Color.web("rgba(0, 255, 136, 0.4)"));
                    indicator.setStroke(Color.web("#00ff88"));
                }
                indicator.setStrokeWidth(3);
                indicator.getStyleClass().add("move-indicator");
                tilePane.getChildren().add(indicator);
            }
        }
    }

    private StackPane getTilePane(int row, int col) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private void onTileClicked(int r, int c, StackPane pane) {
        Tile t = board[r][c];

        // If we're in a capture chain, only allow continuing the capture
        if (!currentCaptureChain.isEmpty() && captureStartTile != null) {
            if (t == captureStartTile) {
                // Clicked on the same piece - allow continuing
                selected = t;
                buildUI();
                return;
            }

            // Try to continue the capture
            Move nextMove = findContinuationMove(captureStartTile.getRow(), captureStartTile.getCol(), r, c);
            if (nextMove != null) {
                executeCaptureMove(nextMove);
                return;
            } else {
                // Invalid continuation, end the chain
                endCaptureChain();
            }
        }

        if (selected == null) {
            // Select a piece
            if (!t.isEmpty() && t.getPiece().isWhite() == isWhiteTurn) {
                selected = t;
                buildUI();
            }
        } else {
            // Try to move
            if (t == selected) {
                // Deselect
                selected = null;
                buildUI();
            } else if (!t.isEmpty() && t.getPiece().isWhite() == isWhiteTurn) {
                // Select different piece
                selected = t;
                buildUI();
            } else {
                // Try to move to this tile
                Move move = findValidMove(selected.getRow(), selected.getCol(), r, c);
                if (move != null) {
                    if (move.hasCapture()) {
                        executeCaptureMove(move);
                    } else {
                        executeRegularMove(move);
                    }
                } else {
                    selected = null;
                    buildUI();
                }
            }
        }
    }

    private Move findValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        List<Move> moves = getValidMoves(fromRow, fromCol);
        for (Move move : moves) {
            if (move.toRow() == toRow && move.toCol() == toCol) {
                return move;
            }
        }
        return null;
    }

    private Move findContinuationMove(int fromRow, int fromCol, int toRow, int toCol) {
        List<Move> captures = findCaptures(fromRow, fromCol);
        for (Move move : captures) {
            if (move.toRow() == toRow && move.toCol() == toCol) {
                return move;
            }
        }
        return null;
    }

    private List<Move> getValidMoves(int row, int col) {
        List<Move> moves = new ArrayList<>();
        Tile tile = board[row][col];
        if (tile.isEmpty())
            return moves;

        Piece piece = tile.getPiece();
        boolean isWhite = piece.isWhite();

        // Check for captures first (mandatory in French/Moroccan rules)
        List<Move> captures = findCaptures(row, col);
        if (!captures.isEmpty()) {
            return captures; // Only captures are allowed
        }

        // Regular moves (only if no captures available)
        if (piece.getType() == PieceType.MAN) {
            int direction = isWhite ? -1 : 1;
            addMoveIfValid(moves, row, col, row + direction, col - 1);
            addMoveIfValid(moves, row, col, row + direction, col + 1);
        } else { // KING
            // Kings can move any number of squares diagonally
            for (int dr = -1; dr <= 1; dr += 2) {
                for (int dc = -1; dc <= 1; dc += 2) {
                    for (int dist = 1; dist < SIZE; dist++) {
                        int newRow = row + dr * dist;
                        int newCol = col + dc * dist;
                        if (!isValidPosition(newRow, newCol))
                            break;
                        if (!board[newRow][newCol].isEmpty())
                            break;
                        moves.add(new Move(row, col, newRow, newCol));
                    }
                }
            }
        }

        return moves;
    }

    private List<Move> findCaptures(int row, int col) {
        List<Move> captures = new ArrayList<>();
        Tile tile = board[row][col];
        if (tile.isEmpty())
            return captures;

        Piece piece = tile.getPiece();
        boolean isWhite = piece.isWhite();
        boolean isKing = piece.getType() == PieceType.KING;

        int[][] directions = { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };

        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];

            if (isKing) {
                // King can jump over pieces at any distance
                for (int dist = 1; dist < SIZE; dist++) {
                    int jumpRow = row + dr * dist;
                    int jumpCol = col + dc * dist;

                    if (!isValidPosition(jumpRow, jumpCol))
                        break;

                    Tile jumpTile = board[jumpRow][jumpCol];

                    if (jumpTile.hasEnemyPiece(isWhite)) {
                        // Found an enemy piece, check all landing positions after it
                        for (int landDist = 1; landDist < SIZE; landDist++) {
                            int landRow = jumpRow + dr * landDist;
                            int landCol = jumpCol + dc * landDist;

                            if (!isValidPosition(landRow, landCol))
                                break;

                            if (board[landRow][landCol].isEmpty()) {
                                // Check if this piece was already captured in current chain
                                boolean alreadyCaptured = false;
                                for (Move prevMove : currentCaptureChain) {
                                    if (prevMove.hasCapture() &&
                                            prevMove.capturedRow() == jumpRow &&
                                            prevMove.capturedCol() == jumpCol) {
                                        alreadyCaptured = true;
                                        break;
                                    }
                                }
                                if (!alreadyCaptured) {
                                    captures.add(new Move(row, col, landRow, landCol, jumpRow, jumpCol));
                                }
                            } else {
                                break; // Landing position blocked
                            }
                        }
                        break; // Can only jump over one piece at a time
                    } else if (!jumpTile.isEmpty()) {
                        break; // Blocked by friendly piece
                    }
                }
            } else {
                // Regular piece: single square jump - FORWARD ONLY
                int forwardDirection = isWhite ? -1 : 1; // White moves up (-1), black moves down (+1)

                // Only allow forward captures (check if dr matches forward direction)
                if (dr == forwardDirection) {
                    int jumpRow = row + dr;
                    int jumpCol = col + dc;
                    int landRow = jumpRow + dr;
                    int landCol = jumpCol + dc;

                    if (isValidPosition(jumpRow, jumpCol) && isValidPosition(landRow, landCol)) {
                        Tile jumpTile = board[jumpRow][jumpCol];
                        Tile landTile = board[landRow][landCol];

                        if (jumpTile.hasEnemyPiece(isWhite) && landTile.isEmpty()) {
                            // Check if this piece was already captured
                            boolean alreadyCaptured = false;
                            for (Move prevMove : currentCaptureChain) {
                                if (prevMove.hasCapture() &&
                                        prevMove.capturedRow() == jumpRow &&
                                        prevMove.capturedCol() == jumpCol) {
                                    alreadyCaptured = true;
                                    break;
                                }
                            }
                            if (!alreadyCaptured) {
                                captures.add(new Move(row, col, landRow, landCol, jumpRow, jumpCol));
                            }
                        }
                    }
                }
            }
        }

        return captures;
    }

    private void executeRegularMove(Move move) {
        Tile from = board[move.fromRow()][move.fromCol()];
        Tile to = board[move.toRow()][move.toCol()];
        Piece piece = from.getPiece();

        to.setPiece(piece);
        from.setPiece(null);

        // Check for promotion
        if (piece.getType() == PieceType.MAN) {
            if ((piece.isWhite() && move.toRow() == 0) || (!piece.isWhite() && move.toRow() == SIZE - 1)) {
                piece.promoteToKing();
            }
        }

        animateMove(move);
        selected = null;
        isWhiteTurn = !isWhiteTurn;
        buildUI();
        updateStatus();
        checkGameOver();
    }

    private void executeCaptureMove(Move move) {
        Tile from = board[move.fromRow()][move.fromCol()];
        Tile to = board[move.toRow()][move.toCol()];
        Piece piece = from.getPiece();

        // Move piece
        to.setPiece(piece);
        from.setPiece(null);

        // Remove captured piece
        if (move.hasCapture()) {
            board[move.capturedRow()][move.capturedCol()].setPiece(null);
        }

        // Check for promotion
        if (piece.getType() == PieceType.MAN) {
            if ((piece.isWhite() && move.toRow() == 0) || (!piece.isWhite() && move.toRow() == SIZE - 1)) {
                piece.promoteToKing();
            }
        }

        // Add to capture chain
        currentCaptureChain.add(move);
        captureStartTile = to;
        selected = to;

        animateMove(move);

        // Check if more captures are possible
        List<Move> continuedCaptures = findCaptures(move.toRow(), move.toCol());
        if (!continuedCaptures.isEmpty()) {
            // Must continue capturing
            buildUI();
            updateStatus();
        } else {
            // Capture chain ends
            endCaptureChain();
        }
    }

    private void endCaptureChain() {
        currentCaptureChain.clear();
        captureStartTile = null;
        selected = null;
        isWhiteTurn = !isWhiteTurn;
        buildUI();
        updateStatus();
        checkGameOver();
    }

    private void animateMove(Move move) {
        StackPane fromPane = getTilePane(move.fromRow(), move.fromCol());
        StackPane toPane = getTilePane(move.toRow(), move.toCol());

        if (fromPane != null && toPane != null && !fromPane.getChildren().isEmpty()) {
            javafx.scene.Node pieceNode = fromPane.getChildren().get(0);
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), pieceNode);
            double deltaX = (move.toCol() - move.fromCol()) * 80;
            double deltaY = (move.toRow() - move.fromRow()) * 80;
            tt.setByX(deltaX);
            tt.setByY(deltaY);
            tt.setOnFinished(e -> {
                // Move the node to the target pane after animation
                fromPane.getChildren().remove(pieceNode);
                toPane.getChildren().add(pieceNode);
                pieceNode.setTranslateX(0);
                pieceNode.setTranslateY(0);
            });
            tt.play();
        }
    }

    private void addMoveIfValid(List<Move> moves, int fromRow, int fromCol, int toRow, int toCol) {
        if (isValidPosition(toRow, toCol) && board[toRow][toCol].isEmpty()) {
            moves.add(new Move(fromRow, fromCol, toRow, toCol));
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private void updateStatus() {
        if (statusLabel != null) {
            String player = isWhiteTurn ? "Blanc" : "Noir";
            if (!currentCaptureChain.isEmpty()) {
                statusLabel.setText("Tour de " + player + " - Capture continue!");
            } else {
                statusLabel.setText("Tour de " + player);
            }
        }
    }

    private void checkGameOver() {
        boolean whiteHasMoves = hasValidMoves(true);
        boolean blackHasMoves = hasValidMoves(false);

        if (!whiteHasMoves && isWhiteTurn) {
            statusLabel.setText("Noir gagne!");
        } else if (!blackHasMoves && !isWhiteTurn) {
            statusLabel.setText("Blanc gagne!");
        }
    }

    private boolean hasValidMoves(boolean isWhite) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Tile tile = board[r][c];
                if (!tile.isEmpty() && tile.getPiece().isWhite() == isWhite) {
                    if (!getValidMoves(r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
