package chess;

public class GameEvent {
    public static final int TURN = 0;
    public static final int END = 1;
    public static final int RESET = 2;
    public static final int UNDO = 3;

    private final int type;
    private final Game game;

    public GameEvent(Game invoker, int eventType) {
        game = invoker;
        type = eventType;
    }

    public int getType() {
        return type;
    }

    public Game getGame() {
        return game;
    }
}
