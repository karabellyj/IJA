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

    @Override
    public Iterator<Move> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super Move> action) {

    }
}
