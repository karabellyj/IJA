package chess;

import javafx.collections.ObservableList;

import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {

    private Board board;
    private Piece.Side turn;
    private Piece.Side winner;
    private Player white;
    private Player black;
    private Stack<Move> undone;
    private volatile Boolean done = false;
    private final CopyOnWriteArrayList<GameListener> listeners = new CopyOnWriteArrayList<>();

    public Game(Board board) {
        this.board = board;
        this.undone = new Stack<>();
    }

    public final void seat(Player white, Player black) {
        this.white = white;
        this.black = black;
    }

    public final void addListener(GameListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(int event) {
        for (GameListener listener : listeners) {
            listener.gameEvent(new GameEvent(this, event));
        }
    }

    public void runit() {
        while (!done) {
            Player player;
            if (turn == Piece.Side.WHITE) {
                turn = Piece.opposite(turn);
                player = black;
            } else {
                turn = Piece.opposite(turn);
                player = white;
            }
            System.out.println(getBoard());
            Move move = player.takeTurn(getBoard(), turn);
            board.move(move);
            System.out.println("moved");
            if (done) {
                return;
            }

            Piece.Side opposite = Piece.opposite(turn);
            if (board.checkmate(opposite)) {
                done = true;
                if (opposite == Piece.Side.BLACK) {
                    winner = Piece.Side.WHITE;
                } else {
                    winner = Piece.Side.BLACK;
                }
                notifyListeners(GameEvent.END);
                return;
            } else if (board.stalemate(opposite)) {
                done = true;
                winner = null;
                notifyListeners(GameEvent.END);
                return;
            }
            notifyListeners(GameEvent.TURN);
        }
    }

    public final void end() {
        done = true;
        winner = null;
        listeners.clear();
    }

    public final void begin() {
        done = false;
        turn = Piece.Side.BLACK;
        notifyListeners(GameEvent.TURN);
        //runit();
    }

    public final void reset() {
        notifyListeners(GameEvent.RESET);
    }

    public Board getBoard() {
        return board.copy();
    }

    public Piece.Side getWinner() {
        return winner;
    }

    public Boolean isDone() {
        return done;
    }

    public void redo() {
        if (!undone.empty()) {
            board.move(undone.pop());
            notifyListeners(GameEvent.TURN);
        }
    }

    public void undo() {
        Move last = board.last();
        if (last != null) {
            notifyListeners(GameEvent.UNDO);
            undone.push(last);
        }

    }

    public void rewind(Move selected, ObservableList moves) {
        reset();
        int i = 0;
        Move move = (Move) moves.get(i);
        while (move != selected) {
            board.simulateMove(move);
            i++;
            move = (Move) moves.get(i);
        }
        notifyListeners(GameEvent.TURN);
    }

    public void playMove(Move move) {
        board.simulateMove(move);
        notifyListeners(GameEvent.TURN);
    }

    public void repaint() {
        notifyListeners(GameEvent.TURN);
    }

}
