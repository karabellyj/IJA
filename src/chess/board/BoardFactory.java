package chess.board;

import chess.Board;

public class BoardFactory {
    public static Board create(Class board) {
        if (board.equals(ChessBoard.class)) {
            return new ChessBoard();
        } else {
            return null;
        }
    }
}
