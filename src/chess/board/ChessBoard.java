package chess.board;

import chess.Board;
import chess.Piece;
import chess.Position;
import chess.piece.*;

public class ChessBoard extends Board {
    static final int WIDTH = 8;
    static final int HEIGHT = 8;
    static final int WHITE_PAWN_ROW = 1;
    static final int BALCK_PAWN_ROW = 6;
    static final int BLACK_ROW = 7;
    static final int WHITE_ROW = 0;

    public ChessBoard() {
        setWidth(WIDTH);
        setHeight(HEIGHT);
        clear();

        for (int x = 0; x < WIDTH; x++) {
            setPiece(x, WHITE_PAWN_ROW, new Pawn(Piece.Side.WHITE));
            setPiece(x, BALCK_PAWN_ROW, new Pawn(Piece.Side.BLACK));
        }

        setPiece(0, WHITE_ROW, new Rook(Piece.Side.WHITE));
        setPiece(1, WHITE_ROW, new Knight(Piece.Side.WHITE));
        setPiece(2, WHITE_ROW, new Bishop(Piece.Side.WHITE));
        setPiece(3, WHITE_ROW, new Queen(Piece.Side.WHITE));
        setPiece(4, WHITE_ROW, new King(Piece.Side.WHITE));
        setPiece(5, WHITE_ROW, new Bishop(Piece.Side.WHITE));
        setPiece(6, WHITE_ROW, new Knight(Piece.Side.WHITE));
        setPiece(7, WHITE_ROW, new Rook(Piece.Side.WHITE));

        setPiece(0, BLACK_ROW, new Rook(Piece.Side.BLACK));
        setPiece(1, BLACK_ROW, new Knight(Piece.Side.BLACK));
        setPiece(2, BLACK_ROW, new Bishop(Piece.Side.BLACK));
        setPiece(3, BLACK_ROW, new Queen(Piece.Side.BLACK));
        setPiece(4, BLACK_ROW, new King(Piece.Side.BLACK));
        setPiece(5, BLACK_ROW, new Bishop(Piece.Side.BLACK));
        setPiece(6, BLACK_ROW, new Knight(Piece.Side.BLACK));
        setPiece(7, BLACK_ROW, new Rook(Piece.Side.BLACK));
    }

    @Override
    public Boolean checkmate(Piece.Side side) {
        return check(side) && (moveCount(side) == 0);
    }

    @Override
    public Boolean stalemate(Piece.Side side) {
        return !check(side) && (moveCount(side) == 0);
    }

    private int moveCount(Piece.Side side) {
        return 0;
    }

    @Override
    public Boolean check(Piece.Side side) {
        Piece.Side attacker;

        if (side == Piece.Side.WHITE) {
            attacker = Piece.Side.BLACK;
        } else {
            attacker = Piece.Side.WHITE;
        }
        Position kingPos = findKing(side);

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Piece p = getPiece(new Position(x, y));
                if ((p != null) && (p.getSide() == attacker) && p.getMoves(false).containsDest(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }
}
