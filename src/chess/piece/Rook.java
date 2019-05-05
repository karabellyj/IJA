package chess.piece;

import chess.Move;
import chess.MoveList;
import chess.Piece;
import chess.Position;

public class Rook extends Piece {
    public Rook(Side side) {
        super(side, "Rook");
    }


    @Override
    public MoveList getMoves(boolean checkForCheck) {
        MoveList list = new MoveList(getBoard(), checkForCheck);
        list = getMoves(this, list);
        return list;
    }

    // FIXME: remove code duplication
    public static MoveList getMoves(Piece piece, MoveList list) {
        Position pos = piece.getPosition();
        int x = pos.getX();
        int y = pos.getY();

        while (x >= 0) {
            x--;
            Position dest = new Position(pos, x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(pos)) {
                break;
            }
        }

        x = pos.getX();
        while (y >= 0) {
            y--;
            Position dest = new Position(pos, x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(pos)) {
                break;
            }
        }

        y = pos.getY();
        while (x < piece.getBoard().getWidth()) {
            x++;
            Position dest = new Position(pos, x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(pos)) {
                break;
            }
        }

        x = pos.getX();
        while (y < piece.getBoard().getHeight()) {
            y++;
            Position dest = new Position(pos, x, y);

            if (!list.addCapture(new Move(pos, dest))) {
                break;
            }
            if (!piece.getBoard().isFree(pos)) {
                break;
            }
        }
        return list;
    }
}
