package chess.piece;

import chess.*;

public class Pawn extends Piece {
    public Pawn(Side side) {
        super(side, "Pawn");
    }

    @Override
    public MoveList getMoves(boolean checkForCheck) {
        MoveList list = new MoveList(getBoard(), checkForCheck);

        Position pos = getPosition();
        Board board = getBoard();
        int dir = direction();

        Position dest = new Position(pos, 0, dir);
        Move first = new Move(pos, dest);
        addPromotion(first);

        if (list.addMove(first) && !moved()) {
            list.addMove(new Move(pos, new Position(pos, 0, 2 * dir)));
        }

        Move leftCapture = new Move(pos, new Position(pos, -1, dir));
        addPromotion(leftCapture);
        list.addMove(leftCapture);

        Move rightCapture = new Move(pos, new Position(pos, 1, dir));
        addPromotion(rightCapture);
        list.addMove(rightCapture);

        // TODO: en passant check
        return list;
    }

    private int direction() {
        if (getSide() == Side.BLACK) {
            return -1;
        } else {
            return 1;
        }
    }

    private int getPromotionRow() {
        if (getSide() == Side.BLACK) {
            return 0;
        } else {
            return getBoard().getHeight() - 1;
        }
    }

    private void addPromotion(Move move) {
        if (move.getDestination().getY() != getPromotionRow()) {
            return;
        }
        move.setNext(new Move(move.getDestination(), null)); // remove pawn
        Move promotion = new Move(null, move.getDestination());
        promotion.setReplacement("Queen"); // TODO: should be modifiable
        promotion.setReplacementSide(getSide());
        move.getNext().setNext(promotion); // add queen
    }
}
