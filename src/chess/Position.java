package chess;

import java.util.Objects;

public final class Position implements Comparable<Position> {
    private final int x, y;

    public Position(int xPos, int yPos) {
        x = xPos;
        y = yPos;
    }

    public Position(Position pos, int deltaX, int deltaY) {
        this(pos.x + deltaX, pos.y + deltaY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Position o) {
        if (o.y == y) {
            return x - o.x;
        } else {
            return y - o.y;
        }
    }
}
