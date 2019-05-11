package chess;

public interface Player {
    /**
     * Inform the player it is time to take its turn.
     *
     * @param board the current board
     * @param side  the player's side
     * @return the selected move for this player
     */
    Move takeTurn(Board board, Piece.Side side);
}
