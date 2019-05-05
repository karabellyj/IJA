package chess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MoveList implements Iterable<Move> {
    private Board board;
    private boolean check;
    private final List<Move> moves = new ArrayList<Move>();


    public MoveList(Board b) {
        this(b, true);
    }

    public MoveList(Board b, boolean checkForCheck) {
        board = b;
        check = checkForCheck;
    }

    public final boolean add(Move move) {
        moves.add(move);
        return true;
    }

    public final boolean addAll(Iterable<Move> list) {
        for (Move move : list) {
            moves.add(move);
        }
        return true;
    }

    public final boolean addMove(Move move) {
        if (board.isFree(move.getDestination())) {
            if (!causesCheck(move)) {
                moves.add(move);
                return true;
            }
            return true; // false for blocking moves
        }
        return false;
    }

    public final boolean causesCheck(Move move) {
        if (!check) {
            return false;
        }
        Piece p = board.getPiece(move.getOrigin());
        board.move(move);

        boolean ret = board.check(p.getSide());
        board.undo();

        return ret;
    }

    @Override
    public Iterator<Move> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super Move> action) {

    }
}
