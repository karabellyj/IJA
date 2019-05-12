package chess;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MoveList implements Iterable<Move> {
    private Board board;
    private boolean check;
    protected final ObservableList<Move> moves = FXCollections.observableArrayList();


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

    public final boolean addCapture(Move move) {
        Piece p = board.getPiece(move.getOrigin());

        if (board.isFree(move.getDestination(), p.getSide())) {
            if (!causesCheck(move)) {
                add(move);
                return true;
            }
            return true;
        }
        return false;
    }

    public final boolean addOnlyCapture(Move move) {
        Piece p = board.getPiece(move.getOrigin());

        if (board.isFree(move.getDestination(), p.getSide()) && !board.isFree(move.getDestination()) && !causesCheck(move)) {
            add(move);
            return true;
        }
        return false;
    }

    public final Move pop() {
        if (isEmpty()) {
            return null;
        }
        Move last = moves.get(moves.size() - 1);
        moves.remove(moves.size() - 1);
        return last;
    }

    public final Move peek() {
        if (isEmpty()) {
            return null;
        }
        return moves.get(moves.size() - 1);
    }

    public final int size() {
        return moves.size();
    }

    public final boolean isEmpty() {
        return moves.isEmpty();
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

    public final Move getMoveByDest(Position dest) {
        for (Move move : this) {
            if (dest.equals(move.getDestination())) {
                return move;
            }
        }
        return null;
    }

    @Override
    public Iterator<Move> iterator() {
        return moves.iterator();
    }

    public final boolean containsDest(Position pos) {
        for (Move move : this) {
            if (pos.equals(move.getDestination())) {
                return true;
            }
        }
        return false;
    }
}
