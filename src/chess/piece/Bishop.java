package chess.piece;

import chess.Move;
import chess.MoveList;
import chess.Piece;
import chess.Position;

public class Bishop extends Piece {

    public Bishop(Side side) {
        super(side, "Bishop");
    }

    @Override
    public MoveList getMoves(boolean checkForCheck) {
        MoveList list = new MoveList(getBoard(), checkForCheck);
        list = getMoves(this, list);
        return list;
    }

    // FIXME: remove duplicate parts of this method
    public static MoveList getMoves(Piece piece, MoveList list) {
        Position pos = piece.getPosition();
        int x = pos.getX();
        int y = pos.getY();


        while (x >= 0 && y >= 0) {
            x--;
            y--;
            Position dest = new Position(x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(dest)) {
                break;
            }
        }
        x = pos.getX();
        y = pos.getY();
        while (x < piece.getBoard().getWidth() && y < piece.getBoard().getHeight()) {
            x++;
            y++;
            Position dest = new Position(x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(dest)) {
                break;
            }
        }
        x = pos.getX();
        y = pos.getY();
        while (x >= 0 && y < piece.getBoard().getHeight()) {
            x--;
            y++;
            Position dest = new Position(x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(dest)) {
                break;
            }
        }
        x = pos.getX();
        y = pos.getY();
        while (x < piece.getBoard().getWidth() && y >= 0) {
            x++;
            y--;
            Position dest = new Position(x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(dest)) {
                break;
            }
        }
        return list;
    }
}
