package chess;

public interface GameListener {
    /**
     * Called when a game event has occured.
     *
     * @param event object describing the event
     */
    void gameEvent(GameEvent event);
}
