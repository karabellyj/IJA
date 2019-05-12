package chess.gui;

import chess.*;
import chess.piece.*;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BoardPane extends GridPane implements GameListener, Player {

    private enum Mode {
        MANUAL,
        AUTOMATIC;
    }

    private Board board;
    private Position selected = null;
    private MoveList moves = null;
    private Piece.Side side = Piece.Side.WHITE;
    private Move selectedMove;
    private Mode mode = Mode.MANUAL;


    public BoardPane(Board board) {
        super();
        this.board = board;

        initBoard();
        updateBoard();
    }

    private void initBoard() {
        int padding = 35;
        int squareSize = 60;
        getRowConstraints().add(new RowConstraints(padding));
        getColumnConstraints().add(new ColumnConstraints(padding));
        for (int i = 0; i < 8; i++) {
            getColumnConstraints().add(new ColumnConstraints(squareSize));
            getRowConstraints().add(new RowConstraints(squareSize));
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Rectangle rect = new Rectangle();
                rect.setWidth(squareSize);
                rect.setHeight(squareSize);
                rect.setStroke(Color.GREY);
                rect.setStrokeWidth(3);
                rect.setFill(Color.WHITE);
                add(rect, i, j);
            }
        }
        //This makes it so when a square is clicked (ignoring objects
        setOnMousePressed(event -> squareClicked(getClickPos(event.getX(), event.getY())));//inside, it returns a proper chess coordinate as a Position object
    }

    private void updateBoard() {
        int h = board.getHeight();
        int w = board.getWidth();

        getChildren().clear();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Rectangle rect = new Rectangle();
                rect.setWidth(60);
                rect.setHeight(60);
                rect.setStroke(Color.GREY);
                rect.setStrokeWidth(3);
                if ((x + y) % 2 == 0) {
                    rect.setFill(Color.DARKGRAY);
                } else {
                    rect.setFill(Color.WHITE);
                }

                add(rect, x + 1, 8 - y);
                Position pos = new Position(x, y);
                Rectangle piece = getSymbol(board.getPiece(pos));

                if (piece != null)
                    add(piece, x + 1, 8 - y);
            }
        }
        char col = 'a';
        for (int x = 0; x < w; x++) {
            Text tempText = new Text("" + col);
            col++;
            add(tempText, x + 1, 9);
        }
        for (int y = 0; y < h; y++) {
            Text tempText = new Text("" + (y + 1));
            add(tempText, 0, 8 - y);
        }

        // highlight selected square
        if (selected != null) {
            setSquareColor(selected, Color.LIGHTGRAY);

            // highlight piece moves
            if (moves != null) {
                for (Move m : moves) {
                    setSquareColor(m.getDestination(), Color.LIGHTGREEN);
                }
            }
        }

    }

    @Override
    public void gameEvent(GameEvent event) {
        if (event.getType() == GameEvent.RESET) {
            board.clear();
            board.initPieces();
            side = Piece.Side.WHITE;
        }
        if (event.getType() == GameEvent.UNDO) {
            board.undo();
            side = Piece.opposite(side);
        }
        updateBoard();
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        //latch = new CountDownLatch(1);

        this.board = board;
        this.side = side;

        updateBoard();


        /*try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("BoardPane interupted");
        }*/
        return selectedMove;
    }

    public void squareClicked(Position pos) {

        if (pos == null) {
            return;
        }

        if (!board.isInRange(pos)) {
            return;
        }

        if (board.getPiece(pos) == null && moves == null) {
            return;
        }

        System.out.println(pos);


        if (pos.equals(selected)) {
            /* Deselect */
            selected = null;
            moves = null;
        }

        /*if (side.equals(Piece.Side.WHITE) && board.getPiece(pos).getSide().equals(Piece.Side.BLACK) && moves == null) {
            return;
        } else if (side.equals(Piece.Side.BLACK) && board.getPiece(pos).getSide().equals(Piece.Side.WHITE) && moves == null) {
            return;
        }*/

        if (moves != null && moves.containsDest(pos)) {
            /* Move selected piece */
            Move move = moves.getMoveByDest(pos);
            selected = null;
            moves = null;
            selectedMove = move;
            board.move(selectedMove);
            System.out.println("moved");

            /*Piece.Side opposite = Piece.opposite(side);
            if (board.checkmate(opposite)) {
                done = true;
                if (opposite == Piece.Side.BLACK) {
                    winner = Piece.Side.WHITE;
                } else {
                    winner = Piece.Side.BLACK;
                }
                return;
            } else if (board.stalemate(opposite)) {
                done = true;
                winner = null;
                return;
            }*/

            side = Piece.opposite(side);
        } else {
            /* Select this position */
            Piece p = board.getPiece(pos);
            if (p != null && p.getSide() == side) {
                selected = pos;
                moves = p.getMoves(true);
            }
        }
        updateBoard();
    }

    public Position getClickPos(double eventX, double eventY) {
        int padding = 38;//Usually 35, but added a bit in order to fix borders
        if (eventX < padding || eventY < padding)
            return null;
        else if (eventX > 518 || eventY > 518)
            return null;
        int x = ((int) (eventX - padding)) / 60;
        int y = ((int) (eventY - padding)) / 60;
        return new Position(x, 7 - y);
    }

    public Rectangle getSymbol(Piece piece) {
        Rectangle rect = new Rectangle();
        rect.setWidth(60);
        rect.setHeight(60);
        if (piece instanceof Bishop && piece.getSide() == Piece.Side.BLACK) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/bishop_b.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Bishop && piece.getSide() == Piece.Side.WHITE) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/bishop_w.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof King && piece.getSide() == Piece.Side.WHITE) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/king_w.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof King && piece.getSide() == Piece.Side.BLACK) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/king_b.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Knight && piece.getSide() == Piece.Side.BLACK) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/knight_l_b.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Knight && piece.getSide() == Piece.Side.WHITE) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/knight_l_w.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Rook && piece.getSide() == Piece.Side.BLACK) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/rook_b.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Rook && piece.getSide() == Piece.Side.WHITE) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/rook_w.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Queen && piece.getSide() == Piece.Side.BLACK) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/queen_b.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Queen && piece.getSide() == Piece.Side.WHITE) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/queen_w.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Pawn && piece.getSide() == Piece.Side.BLACK) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/pawn_b.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        } else if (piece instanceof Pawn && piece.getSide() == Piece.Side.WHITE) {
            Image img = new Image(getClass().getResourceAsStream("../resources/figures/pawn_w.png"));
            rect.setFill(new ImagePattern(img));
            return rect;
        }
        return null;
    }

    private void setSquareColor(Position pos, Color color) {
        Rectangle rect = new Rectangle();
        rect.setWidth(60);
        rect.setHeight(60);
        rect.setStroke(Color.GRAY);
        rect.setStrokeWidth(3);
        rect.setFill(color);
        add(rect, pos.getX() + 1, 8 - pos.getY());
        Rectangle piece = getSymbol(board.getPiece(pos));
        if (piece != null)
            add(piece, pos.getX() + 1, 8 - pos.getY());
    }
}
