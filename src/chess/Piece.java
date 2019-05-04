package chess;

import javafx.scene.image.Image;

public abstract class Piece {

    private Side side;
    private Position position;
    private Board board;
    private int moved = 0;
    private String name;

    public enum Side {
        WHITE(1),
        BLACK(-1);

        private int value;

        Side(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }

    public Piece(Side owner, String pieceName) {
        side = owner;
        name = pieceName;
    }

    public abstract MoveList getMoves(boolean checkForCheck);

    public final void setPosition(Position pos) {
        position = pos;
    }

    public final Position getPosition() {
        return position;
    }

    public final void setBoard(Board b) {
        board = b;
    }

    public final Board getBoard() {
        return board;
    }

    public final void setSide(Side owner) {
        side = owner;
    }

    public final Side getSide() {
        return side;
    }

    public final Image getImage() {
        return null;
    }

    public final Boolean moved() {
        return moved != 0;
    }

    public final void increment_moved() {
        moved++;
    }

    public final void decrement_moved() {
        moved--;
    }

    public static Side opposite(Side s) {
        if (s == Side.BLACK) {
            return Side.WHITE;
        } else {
            return Side.BLACK;
        }
    }

}
