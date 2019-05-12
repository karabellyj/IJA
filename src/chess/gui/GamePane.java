package chess.gui;

import chess.*;
import chess.board.ChessBoard;
import chess.parser.PGN;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class GamePane extends BorderPane implements GameListener {

    private final BoardPane boardPane;
    private final ToolBar toolBar;
    private final ListView listView;
    private Timeline timeline;

    private Game game;
    private Board board;
    private ObservableList<Move> moves;
    private Move selected;
    private boolean first = true;

    public GamePane(Stage primaryStage) {
        super();
        board = new ChessBoard();
        game = new Game(board);
        moves = board.getObservableMoves();
        boardPane = new BoardPane(board);
        toolBar = new ToolBar();
        listView = new ListView();

        listView.setPrefWidth(240);
        listView.setOrientation(Orientation.VERTICAL);
        listView.setItems(moves);

        Label spinnerLabel = new Label("Duration:");
        Spinner<Double> spinner = new Spinner<>();
        final int initValue = 1;
        // Value factory
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 60, initValue, 0.1);
        spinner.setValueFactory(valueFactory);


        // creating toolbar buttons
        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");
        Button resetButton = new Button("Reset");
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button undoButton = new Button("<");
        Button redoButton = new Button(">");

        ToggleGroup tg = new ToggleGroup();

        // create radiobuttons
        RadioButton r1 = new RadioButton("manual");
        RadioButton r2 = new RadioButton("automatic");

        // add radiobuttons to toggle group
        r1.setToggleGroup(tg);
        r2.setToggleGroup(tg);

        // first button is default
        r1.setSelected(true);

        // add items to toolBar
        toolBar.getItems().add(loadButton);
        toolBar.getItems().add(saveButton);
        toolBar.getItems().add(resetButton);
        toolBar.getItems().addAll(undoButton, redoButton);

        toolBar.getItems().add(r1);
        toolBar.getItems().add(r2);

        toolBar.getItems().add(startButton);
        toolBar.getItems().add(stopButton);

        toolBar.getItems().addAll(spinnerLabel, spinner);

        // set orientation of the toolbar
        toolBar.setOrientation(Orientation.HORIZONTAL);

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
        resetButton.setOnAction(buttonEvent -> {
            listView.getSelectionModel().selectFirst();

            game.reset();

        });
        undoButton.setOnAction(buttonEvent -> game.undo());
        redoButton.setOnAction(buttonEvent -> game.redo());
        startButton.setOnAction(buttonAction -> {
            if (selected == null) {
                listView.getSelectionModel().selectFirst();
            }
            timeline = new Timeline(new KeyFrame(Duration.millis(spinner.getValue() * 1000), ae -> play()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
        });
        stopButton.setOnAction(buttonAction -> timeline.stop());

        // TODO: implement actions for radio buttons
        // add a change listener
        undoButton.disableProperty().bind(r2.selectedProperty());
        redoButton.disableProperty().bind(r2.selectedProperty());
        startButton.disableProperty().bind(r1.selectedProperty());
        stopButton.disableProperty().bind(r1.selectedProperty());
        spinner.disableProperty().bind(r1.selectedProperty());

        listView.getSelectionModel().selectedItemProperty().addListener((ob, o, n) -> {
            selected = (Move) n;
            // game.rewind(selected, moves);
            // game.playMove(selected);
        });


        tg.selectedToggleProperty().addListener((ob, o, n) -> {

            RadioButton rb = (RadioButton) tg.getSelectedToggle();

            if (rb != null) {

            }
        });

        setCenter(boardPane);
        setRight(listView);
        setBottom(new VBox(toolBar));

        game.addListener(this);
        game.addListener(boardPane);
        game.seat(getPlayer(), getPlayer());
        game.begin();
    }

    private void saveFile(File file) throws IOException {
        if (file == null) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new PrintWriter(file.getPath() + ".pgn"))) {
            if (moves.size() < 1) {
                return;
            }

            int turnNumber = 1;
            Board tempBoard = new ChessBoard();
            Piece.Side turn = Piece.Side.WHITE;
            for (int i = 0; i < moves.size(); i++) {
                try {
                    String s = PGN.moveToString(moves.get(i), tempBoard, turn);
                    if (i % 2 == 0) {
                        writer.write(turnNumber + ". " + s);
                        System.out.printf("%d. %s", turnNumber, s);
                        turnNumber++;
                    } else {
                        writer.write(" " + s);
                        writer.newLine();
                        System.out.printf(" %s\n", s);
                    }
                    tempBoard.move(moves.get(i));
                    turn = Piece.opposite(turn);
                } catch (Exception e) {
                    System.out.println(e);
                    break;
                }
            }
        }
    }

    private void loadFile(File file) throws IOException {
        if (file == null) {
            return;
        }
        // reset board and initialize
        game.reset();
        moves.clear();

        // initialize reader
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));

        String moveLine = null;
        while ((moveLine = reader.readLine()) != null) {
            try {
                moveLine = moveLine.replaceAll("\\b[\\d]+[\\s]*[\\.]+", ""); // replace leading numbers
                moveLine = moveLine.trim();
                String[] split = moveLine.split("[\\s]+");

                if (split.length > 2 || split.length < 1) {
                    throw new Exception("Wrong number of part moves");
                }
                Piece.Side turn = Piece.Side.WHITE;
                for (String s : split) {
                    Move move = PGN.parseLine(s, board, turn);
                    board.move(move);
                    game.repaint();
                    turn = Piece.opposite(turn);
                }

            } catch (Exception e) {
                System.out.println(e);
                break;
            }
        }

        reader.close();
    }

    @Override
    public void gameEvent(GameEvent event) {
        System.out.println(event.getType());
    }

    public final Player getPlayer() {
        return boardPane;
    }

    private void play() {
        if (listView.getSelectionModel().getSelectedIndex() == 0) {
            game.reset();
            first = false;
        } else if (listView.getSelectionModel().getSelectedIndex() != 0 && first) {
            game.rewind(selected, moves);
            first = false;
        }
        game.playMove(selected);

        if (moves.size() - 1 == listView.getSelectionModel().getSelectedIndex()) {
            timeline.stop();
            first = true;
        }
        listView.getSelectionModel().selectNext();
    }
}
