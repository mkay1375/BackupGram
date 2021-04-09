package ir.mkay.backupgram;

import ir.mkay.backupgram.service.telegram.TelegramApiInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Main extends Application {

    private static final String START_PANE = "login";

    private static HashMap<String, Image> images = new HashMap<>();

    private static Scene scene;
    private static Stage primaryStage;

    public static void changePane(String paneName) {
        primaryStage.hide();
        Pane pane;
        try {
            pane = FXMLLoader.load(Main.class.getResource("/panes/" + paneName + ".fxml"));
            scene.setRoot(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        primaryStage.setX(
                primaryStage.getX() - ((pane.getPrefWidth() - primaryStage.getWidth()) / 2)
        );
        primaryStage.setY(
                primaryStage.getY() - ((pane.getPrefHeight() - primaryStage.getHeight()) / 2)
        );

        primaryStage.setWidth(pane.getPrefWidth());
        primaryStage.setHeight(pane.getPrefHeight());
        primaryStage.setMinWidth(pane.getPrefWidth());
        primaryStage.setMinHeight(pane.getPrefHeight());
        primaryStage.show();
    }

    public static Image getImage(String imageName) {
        return images.get(imageName);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Main.primaryStage = primaryStage;

        Scene scene = new Scene(FXMLLoader.load(Main.class.getResource("/panes/" + START_PANE + ".fxml")));
        scene.getStylesheets().add("/panes/general.css");
        Main.scene = scene;

        primaryStage.setOnCloseRequest(event -> {
            TelegramApiInitializer.stop();
            System.exit(0);
        });
        primaryStage.setTitle("BackupGram");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        // TODO: Add version to here and login page and bot infos
        Map<String, String> argsMap = parseArgs(args);
        setApiIdAndHash(argsMap);
        log.info("App started");
        loadResources();
        launch(args);
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> result = new HashMap<>();
        for (String arg : args) {
            try {
                String[] argParts = arg.substring(2).split("[=: ]+", 2);
                result.put(argParts[0], argParts[1]);
            } catch (Exception e) {
                log.warn("Invalid arg: {}", arg, e);
            }
        }
        return result;
    }

    private static void setApiIdAndHash(Map<String, String> args) {
        if (!args.containsKey("api-id")) {
            throw new IllegalArgumentException("api-id is missing; pass as '--api-id=12345'");
        }
        if (!args.containsKey("api-hash") || args.get("api-hash").matches("\\s+")) {
            throw new IllegalArgumentException("api-hash is missing; pass as '--api-hash=SOME_HASH'");
        }
        try {
            SharedMemoryStorage.apiId = Integer.parseInt(args.get("api-id"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("api-id is not a valid number", e);
        }
        SharedMemoryStorage.apiHash = args.get("api-hash");
    }

    private static void loadResources() {
        images.put("USER", new Image(Main.class.getResourceAsStream("/images/user.png")));
        images.put("CONTACT", new Image(Main.class.getResourceAsStream("/images/contact.png")));
        images.put("GROUP", new Image(Main.class.getResourceAsStream("/images/group.png")));
        images.put("SUPERGROUP", new Image(Main.class.getResourceAsStream("/images/supergroup.png")));
        images.put("CHANNEL", new Image(Main.class.getResourceAsStream("/images/channel.png")));

        images.put("TICK", new Image(Main.class.getResourceAsStream("/images/tick.png")));
        images.put("DOWNLOADING", new Image(Main.class.getResourceAsStream("/images/downloading.png")));
        images.put("WAIT", new Image(Main.class.getResourceAsStream("/images/wait.png")));
        images.put("ERROR", new Image(Main.class.getResourceAsStream("/images/error.png")));
    }
}
