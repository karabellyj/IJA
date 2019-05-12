package chess;

import chess.board.BoardFactory;
import chess.piece.King;
import chess.piece.PieceFactory;
import javafx.collections.ObservableList;

public abstract class Board {
    private Piece[][] board;
    private int width, height;
    private final MoveList moves = new MoveList(this);

    public abstract Boolean checkmate(Piece.Side side);

    public abstract Boolean stalemate(Piece.Side side);

    public abstract Boolean check(Piece.Side side);

    public abstract void initPieces();

    public final void clear() {
        board = new Piece[width][height];
    }

    public final Boolean checkmate() {
        return checkmate(Piece.Side.WHITE) || checkmate(Piece.Side.BLACK);
    }

    public final Boolean stalemate() {
        return stalemate(Piece.Side.WHITE) || stalemate(Piece.Side.BLACK);
    }

    public final Boolean check() {
        return check(Piece.Side.WHITE) || check(Piece.Side.BLACK);
    }

    public final Position findKing(Piece.Side side) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Position pos = new Position(x, y);
                Piece p = getPiece(pos);
                if (p instanceof King && p.getSide() == side) {
                    return pos;
                }
            }
        }
        return null;
    }

    public final void move(Move move) {
        moves.add(move);
        doMove(move);
    }

    public final void simulateMove(Move move) {
        doMove(move);
    }

    private void doMove(Move move) {
        if (move == null) {
            return;
        }
        Position orig = move.getOrigin();
        Position dest = move.getDestination();

        if (orig != null && dest != null) {
            move.setCaptured(getPiece(dest));
            setPiece(dest, getPiece(orig));
            setPiece(orig, null);
            getPiece(dest).setPosition(dest);
            getPiece(dest).increment_moved();
        } else if (orig != null && dest == null) {
            move.setCaptured(getPiece(orig));
            setPiece(orig, null);
        } else {
            setPiece(dest, PieceFactory.create(move.getReplacement(), move.getReplacementSide()));
        }
        doMove(move.getNext());
    }

    public final void undo() {
        doUndo(moves.pop());
    }

    private void doUndo(Move move) {
        if (move == null) {
            return;
        }
        doUndo(move.getNext());

        Position orig = move.getOrigin();
        Position dest = move.getDestination();

        if (orig != null && dest != null) {
            setPiece(orig, getPiece(dest));
            setPiece(dest, move.getCaptured());
            getPiece(orig).setPosition(orig);
            getPiece(orig).decrement_moved();
        } else if (orig != null && dest == null) {
            setPiece(orig, move.getCaptured());
        } else {
            setPiece(dest, null);
        }
    }

    public final Move last() {
        return moves.peek();
    }

    public final Boolean isEmpty(Position pos) {
        return getPiece(pos) == null;
    }

    public final Boolean isEmpty(Position pos, Piece.Side side) {
        Piece p = getPiece(pos);
        if (p == null) {
            return true;
        }
        return p.getSide() != side;
    }

    public final Boolean isInRange(Position pos) {
        return (pos.getX() >= 0) && (pos.getY() >= 0) && (pos.getX() < width) && (pos.getY() < height);
    }

    public final Boolean isFree(Position pos) {
        return isInRange(pos) && isEmpty(pos);
    }

    public final Boolean isFree(Position pos, Piece.Side side) {
        return isInRange(pos) && isEmpty(pos, side);
    }

    public final int moveCount() {
        return moves.size();
    }

    public final Board copy() {
        Board fresh = BoardFactory.create(this.getClass());
        for (Move move : moves) {
            fresh.move(new Move(move));
        }
        return fresh;
    }

    public final MoveList allMoves(final Piece.Side side, final boolean check) {
        MoveList list = new MoveList(this, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Piece p = board[x][y];
                if (p != null && p.getSide() == side) {
                    list.addAll(p.getMoves(check));
                }
            }
        }
        return list;
    }

    public final void setPiece(int x, int y, Piece p) {
        setPiece(new Position(x, y), p);
    }

    public final void setPiece(Position pos, Piece p) {
        board[pos.getX()][pos.getY()] = p;
        if (p != null) {
            p.setPosition(pos);
            p.setBoard(this);
        }
    }

    public final Piece getPiece(Position pos) {
        return board[pos.getX()][pos.getY()];
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public final ObservableList<Move> getObservableMoves() {
        return moves.moves;
    }
}
