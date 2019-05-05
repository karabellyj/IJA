package chess.piece;

import chess.Move;
import chess.MoveList;
import chess.Piece;
import chess.Position;

public class King extends Piece {
    private Boolean inCheck;

    public King(Side side) {
        super(side, "King");
    }

    @Override
    public MoveList getMoves(boolean checkForCheck) {
        MoveList list = new MoveList(getBoard(), checkForCheck);
        Position pos = getPosition();

        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (x != 0 || y != 0) {
                    list.addCapture(new Move(pos, new Position(pos, x, y)));
                }
            }
        }

        // TODO: add check for castling
        return list;
    }

    private boolean inCheck() {
        if (inCheck != null) {
            return inCheck;
        }
        inCheck = getBoard().check(getSide());
        return inCheck;
    }
}
