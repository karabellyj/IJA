package chess;

public final class Move {
    private Position origin;
    private Position destination;

    private Move next;
    private Piece captured;

    private String replacement;
    private Piece.Side replacementSide;

    public Move(Position orig, Position dest) {
        origin = orig;
        destination = dest;
        next = null;
    }

    public Move(Move move) {
        this(move.getOrigin(), move.getDestination());
        captured = move.getCaptured();
        replacement = move.getReplacement();
        replacementSide = move.getReplacementSide();
        if (move.getNext() != null) {
            next = new Move(move.getNext());
        }
    }

    public Position getOrigin() {
        return origin;
    }

    public void setNext(Move next) {
        this.next = next;
    }

    public void setCaptured(Piece captured) {
        this.captured = captured;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public void setReplacementSide(Piece.Side replacementSide) {
        this.replacementSide = replacementSide;
    }

    public Position getDestination() {
        return destination;
    }

    public Move getNext() {
        return next;
    }

    public Piece getCaptured() {
        return captured;
    }

    public String getReplacement() {
        return replacement;
    }

    public Piece.Side getReplacementSide() {
        return replacementSide;
    }

    @Override
    public String toString() {
        int origin_row = 1 + origin.getY();
        char origin_col = (char) ('a' + origin.getX());

        int dest_row = 1 + destination.getY();
        char dest_col = (char) ('a' + destination.getX());
        return "[" + origin_col + origin_row + "]" + " -> " + "[" + dest_col + dest_row + "]";
    }
}
