package chess.piece;

import chess.Piece;

public class PieceFactory {
    private PieceFactory() {
    }

    public static Piece create(String name, Piece.Side side) {
        switch (name) {
            case "Queen":
                return new Queen(side);
            case "Bishop":
                return new Bishop(side);
            case "Rook":
                return new Rook(side);
            case "Knight":
                return new Knight(side);
            default:
                return null;
        }
    }
}
