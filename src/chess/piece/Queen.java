package chess.piece;

import chess.MoveList;
import chess.Piece;

public class Queen extends Piece {
    public Queen(Side side) {
        super(side, "Queen");
    }

    @Override
    public MoveList getMoves(boolean checkForCheck) {
        MoveList list = new MoveList(getBoard(), checkForCheck);
        // reuse move implementations of Rook and Bishop
        list = Rook.getMoves(this, list);
        list = Bishop.getMoves(this, list);
        return list;
    }
}
