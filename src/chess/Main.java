package chess;

import chess.board.ChessBoard;
import chess.piece.*;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static final int TITLE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private GridPane board;
    private VBox moves;
    private ChessBoard chess;

    private Position selectedSquare;
    private boolean lookingForMove;

    private int tab_counter = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //primaryStage.setX(0);
        //primaryStage.setY(0);
        chess = new ChessBoard();

        TabPane tabPane = new TabPane();

        // create a tab which
        // when pressed creates a new tab
        Tab newtab = new Tab("+");

        // action event
        newtab.setOnSelectionChanged(tabEvent -> {
            if (newtab.isSelected()) {
                // create Tab
                Tab tab = new Tab("Tab_" + (tab_counter + 1));

                tab_counter++;

                // add content to the tab
                BorderPane borderPane = new BorderPane();
                initBoard();
                initMoves();

                ScrollPane scrollPane = new ScrollPane();
                // Setting a horizontal scroll bar is always display
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                // Setting vertical scroll bar is never displayed.
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scrollPane.setPrefWidth(240);

                scrollPane.setContent(moves);

                // creating toolbar buttons
                Button loadButton = new Button("Load");
                Button saveButton = new Button("Save");

                ToggleGroup tg = new ToggleGroup();

                // create radiobuttons
                RadioButton r1 = new RadioButton("manual");
                RadioButton r2 = new RadioButton("automatic");

                // add radiobuttons to toggle group
                r1.setToggleGroup(tg);
                r2.setToggleGroup(tg);

                // creating toolbar
                ToolBar toolbar = new ToolBar();

                // add items
                toolbar.getItems().add(loadButton);
                toolbar.getItems().add(saveButton);
                toolbar.getItems().add(r1);
                toolbar.getItems().add(r2);

                // set orientation of the toolbar
                toolbar.setOrientation(Orientation.HORIZONTAL);

                // set actions for toolbar
                loadButton.setOnAction(buttonEvent -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Load game");
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        try {
                            loadFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Load failed.");
                    }
                });
                saveButton.setOnAction(buttonEvent -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save game");
                    File file = fileChooser.showSaveDialog(primaryStage);
                    if (file != null) {
                        try {
                            saveFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // TODO: implement actions for radio buttons

                borderPane.setCenter(board);
                borderPane.setRight(scrollPane);
                borderPane.setBottom(new VBox(toolbar));


                tab.setContent(borderPane);

                updateBoard();

                // add tab
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);

                // select the last tab
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
            }
        });

        tabPane.getTabs().add(newtab);

        primaryStage.setScene(new Scene(tabPane, TITLE_SIZE * WIDTH, TITLE_SIZE * HEIGHT));
        primaryStage.setTitle("IJA Chess Application");

        primaryStage.show();
    }

    private void saveFile(File file) throws IOException {
        // TODO: implement me
    }

    private void loadFile(File file) throws IOException {
        // TODO: implement me
    }

    private void initMoves() {
        moves = new VBox();
        moves.setPadding(new Insets(10));
        moves.setSpacing(8);
        moves.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
        moves.setMinWidth(240);

        moves.getChildren().add(new Text("Hello1"));
        moves.getChildren().add(new Text("Hello2"));
    }


    /**
     * Initializes the board variable as a GridPane. It then adds in graphics
     * representing blank spaces, sets the mouse event for square click
     * recognition, and initiates the square selection variable and whether
     * or not the user is looking to move, which should initially be false.
     */
    public void initBoard() {
        int squareSize = 60;
        int padding = 35;
        board = new GridPane();
        board.getRowConstraints().add(new RowConstraints(padding));
        board.getColumnConstraints().add(new ColumnConstraints(padding));
        for (int i = 0; i < 8; i++) {
            board.getColumnConstraints().add(new ColumnConstraints(squareSize));
            board.getRowConstraints().add(new RowConstraints(squareSize));
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Rectangle rect = new Rectangle();
                rect.setWidth(squareSize);
                rect.setHeight(squareSize);
                rect.setStroke(Color.GREY);
                rect.setStrokeWidth(3);
                rect.setFill(Color.WHITE);
                board.add(rect, i, j);
            }
        }

        //This makes it so when a square is clicked (ignoring objects
        board.setOnMousePressed(event -> squareClicked(getClickPos(event.getX(), event.getY())));//inside, it returns a proper chess coordinate as a Position object

        selectedSquare = new Position(-1, -1);
        lookingForMove = false;
    }

    /**
     * Takes the data from the existing chess matrix and places it on the
     * graphical board object
     */
    public void updateBoard() {
        board.getChildren().clear();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle rect = new Rectangle();
                rect.setWidth(60);
                rect.setHeight(60);
                rect.setStroke(Color.GREY);
                rect.setStrokeWidth(3);
                if ((i + j) % 2 == 0) {
                    rect.setFill(Color.WHITE);
                } else {
                    rect.setFill(Color.DARKGRAY);
                }
                board.add(rect, i + 1, 8 - j);
                Position pos = new Position(i, j);
                String symbol = " " + getSymbol(chess.getPiece(pos));
                Text tempText = new Text(symbol);
                Font tempFont = new Font(42);
                tempText.setFont(tempFont);
                if (!symbol.equalsIgnoreCase(" _"))
                    board.add(tempText, i + 1, 8 - j);
            }
        }
    }

    /**
     * Converts symbol of input String to actual chess character to be placed
     * on the board
     *
     * @param piece input piece
     * @return string that is an actual chess graphic, or empty space
     */
    public String getSymbol(Piece piece) {
        if (piece instanceof Bishop && piece.getSide() == Piece.Side.BLACK)
            return "♝";
        else if (piece instanceof Bishop && piece.getSide() == Piece.Side.WHITE)
            return "♗";
        else if (piece instanceof King && piece.getSide() == Piece.Side.WHITE)
            return "♔";
        else if (piece instanceof King && piece.getSide() == Piece.Side.BLACK)
            return "♚";
        else if (piece instanceof Knight && piece.getSide() == Piece.Side.BLACK)
            return "♞";
        else if (piece instanceof Knight && piece.getSide() == Piece.Side.WHITE)
            return "♘";
        else if (piece instanceof Rook && piece.getSide() == Piece.Side.BLACK)
            return "♜";
        else if (piece instanceof Rook && piece.getSide() == Piece.Side.WHITE)
            return "♖";
        else if (piece instanceof Queen && piece.getSide() == Piece.Side.BLACK)
            return "♛";
        else if (piece instanceof Queen && piece.getSide() == Piece.Side.WHITE)
            return "♕";
        else if (piece instanceof Pawn && piece.getSide() == Piece.Side.BLACK)
            return "♟";
        else if (piece instanceof Pawn && piece.getSide() == Piece.Side.WHITE)
            return "♙";
        return " ";
    }

    /**
     * Updates the move list on the right side with the input string. If it
     * would overflow, it removes the first and reprints in order to give it a
     * scrolling effect.
     *
     * @param output string that should be added to move list pane
     */
    public void printMove(String output) {
//        moveList.add(output);
//        if (moveList.size() < 23)
//            moves.getChildren().add(new Text(moveList.get(moveList.size() - 1)));
//        else {
//            moveList.remove(0);
//            moves.getChildren().clear();
//            for (int i = 0; i < moveList.size(); i++)
//                moves.getChildren().add(new Text(moveList.get(i)));
//        }
        System.out.println("move");
        /*
         * TEST HERE
         */
        //System.out.println(chess.isInCheck("white"));
        /*
         * END TEST
         */
    }

    /**
     * This should be called upon a square being clicked on the board pane. It
     * first does a bunch of input tests, then checks if the click itself is
     * legal based on current turn and whether or not the user is looking to
     * place a piece on a blank space or an enemy piece. It initiates the move
     * logic if all requirements are met.
     * <br>
     * It also works with the move history list, and prints the move. It also
     * checks for piece capture, and will also print that to the history list.
     *
     * @param pos the position of the square clicked
     */
    public void squareClicked(Position pos) {
        System.out.println(pos);
        //if(chess.isDebug())
        //    System.out.println("Looking for move is " + lookingForMove);

        /*if (pos == null)
            return;
        if (pos.getX() > 7 || pos.getX() < 0 || pos.getY() > 7 || pos.getY() < 0)
            return;
        if (chess.getPiece(pos).getClass().getSimpleName().equalsIgnoreCase("Blank") && !lookingForMove) {
            //if(chess.isDebug())
            //    System.out.println("Blank space selected without movement");
            return;
        }
        if (selectedSquare.equals(pos)) {
            //if (chess.isDebug())
            //    System.out.println("Deselecting...");
            updateBoard();
            selectedSquare.setX(-1);
            selectedSquare.setY(-1);
            lookingForMove = false;
            return;
        }
        if (chess.getTurn().equalsIgnoreCase("white") && chess.getPiece(pos).getSide().equals(Piece.Side.BLACK) && !lookingForMove) {
            //playSound.error();
            //if(chess.isDebug())
            //    System.out.println("It is white's turn");
            return;
        } else if (chess.getTurn().equalsIgnoreCase("black") && chess.getPiece(pos).getSide().equals(Piece.Side.WHITE) && !lookingForMove) {
            //playSound.error();
            //if(chess.isDebug())
            //    System.out.println("It is black's turn");
            return;
        }
        if (lookingForMove) {
            //Do move!
            //first, get piece that is piece of prior selected square
            Piece currentPiece = chess.getPiece(selectedSquare);
            Position prePos = currentPiece.getPosition();

            //setup capture logic
            boolean isNewSquareEnemy = false;
            Piece.Side newSquareColor = chess.getPiece(pos).getSide();
            String newSquareName = chess.getPiece(pos).getClass().getSimpleName();
            //if (chess.isDebug())
            //    System.out.println("The new square's color: " + newSquareColor);
            if (chess.getTurn().equals(Piece.Side.WHITE)) {
                if (newSquareColor.equals(Piece.Side.BLACK))
                    isNewSquareEnemy = true;
            } else if (chess.getTurn().equals(Piece.Side.BLACK)) {
                if (newSquareColor.equals(Piece.Side.WHITE))
                    isNewSquareEnemy = true;
            }

            //Now the actual move check
            if (!chess.move(currentPiece, pos.getX() - prePos.getX(), pos.getY() - prePos.getY())) {
                if (chess.isDebug())
                    System.out.print("Move not successful.");
                //playSound.error();
                return;
            }
            //Move done, do updates

            updateBoard();
            lookingForMove = false;
            selectedSquare.setXpos(-1);
            selectedSquare.setYpos(-1);
            chess.changeTurn();
            printMove("" + turnNumber + ". " + chess.getPiece(pos).getColor() +
                    " " + chess.getPiece(pos).getClass().getSimpleName() +
                    " moved to (" + (pos.getX() + 1) + ", " + (pos.getY() + 1) + ")");
            turnNumber++;
            if (isNewSquareEnemy) {
                //if (chess.isDebug())
                 //   System.out.println("The new square was an enemy!");
                printMove("" + newSquareColor + " " + newSquareName + " at (" +
                        (pos.getX() + 1) + ", " + (pos.getY() + 1) + ") defeated!");
                //playSound.slash();
            } else
                //playSound.place();
                return;
        } else if (!lookingForMove) {//Nothing selected, needs move
            selectedSquare = pos;
            lookingForMove = true;
            setSquareSelectedColor(selectedSquare);
            return;
        }*/
    }

    /**
     * Translates a click somewhere on the board GridePane into a computer-
     * friendly chess location as a Position object. Checks if click is
     * inside the boundaries, returns null if not on a square.
     *
     * @param eventX the event.getX() input
     * @param eventY the event.getY() input
     * @return adjusted position if valid, null if invalid
     */
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

    /**
     * A method that sets the square at the selected color to an appropriate
     * selection color. It first updates the board, as to clear any other
     * selection colors. The fill color is Color.LIGHTGRAY, the stroke is
     * Color.GRAY.
     *
     * @param pos position to set to select color
     */
    public void setSquareSelectedColor(Position pos) {
        updateBoard();
        Rectangle rect = new Rectangle();
        rect.setWidth(60);
        rect.setHeight(60);
        rect.setStroke(Color.GRAY);
        rect.setStrokeWidth(3);
        rect.setFill(Color.LIGHTGRAY);
        board.add(rect, pos.getX() + 1, 8 - pos.getY());
        String symbol = " " + getSymbol(chess.getPiece(pos));
        Text tempText = new Text(symbol);
        Font tempFont = new Font(42);
        tempText.setFont(tempFont);
        if (!symbol.equalsIgnoreCase(" _"))
            board.add(tempText, pos.getX() + 1, 8 - pos.getY());
    }
}
