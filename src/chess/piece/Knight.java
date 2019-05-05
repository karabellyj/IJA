package chess.piece;

import chess.Move;
import chess.MoveList;
import chess.Piece;
import chess.Position;

public class Knight extends Piece {
    static final int SHORT = 1;
    static final int LONG = 2;

    public Knight(Side side) {
        super(side, "Knight");
    }

    @Override
    public MoveList getMoves(boolean checkForCheck) {
        MoveList list = new MoveList(getBoard(), checkForCheck);
        list = getMoves(this, list);
        return list;
    }

    public static MoveList getMoves(Piece piece, MoveList list) {
        Position pos = piece.getPosition();

        list.addCapture(new Move(pos, new Position(pos, SHORT, LONG)));
        list.addCapture(new Move(pos, new Position(pos, LONG, SHORT)));
        list.addCapture(new Move(pos, new Position(pos, -LONG, SHORT)));
        list.addCapture(new Move(pos, new Position(pos, -LONG, -SHORT)));
        list.addCapture(new Move(pos, new Position(pos, LONG, -SHORT)));
        list.addCapture(new Move(pos, new Position(pos, SHORT, -LONG)));
        list.addCapture(new Move(pos, new Position(pos, -SHORT, -LONG)));
        list.addCapture(new Move(pos, new Position(pos, -SHORT, LONG)));

        return list;
    }
}
