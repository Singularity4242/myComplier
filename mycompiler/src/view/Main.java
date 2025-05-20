package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private FXMLLoader loader,loader1;
    private Parent root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("郭蕙宁 202025310107 词法语法语义分析");
        Class<? extends Main> aClass = getClass();
        loader = new FXMLLoader(getClass().getResource("/fxml/mygui.fxml"));
        try {
            root = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/mygui.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
