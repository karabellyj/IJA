package chess;

import chess.gui.GamePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static final int TITLE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private int tabCounter = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TabPane tabPane = new TabPane();

        // create a tab which
        // when pressed creates a new tab
        Tab newTab = new Tab("+");

        // action event
        newTab.setOnSelectionChanged(tabEvent -> {
            if (newTab.isSelected()) {
                // create Tab
                Tab tab = new Tab("Tab_" + (tabCounter + 1));

                tabCounter++;

                GamePane game = new GamePane(primaryStage);

                tab.setContent(game);

                // add tab
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);

                // select the last tab
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
            }
        });

        tabPane.getTabs().add(newTab);

        primaryStage.setScene(new Scene(tabPane, TITLE_SIZE * WIDTH, TITLE_SIZE * HEIGHT));
        primaryStage.setTitle("IJA Chess Application");

        primaryStage.show();
    }
}
