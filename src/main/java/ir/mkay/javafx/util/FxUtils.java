package ir.mkay.javafx.util;

import ir.mkay.backupgram.Constants;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FxUtils {
    public static void hide(Node... nodes) {
        for (Node node : nodes)
            node.setVisible(false);
    }

    public static void show(Node... nodes) {
        for (Node node : nodes)
            node.setVisible(true);
    }

    public static void disable(Node... nodes) {
        for (Node node : nodes)
            node.setDisable(true);
    }

    public static void enable(Node... nodes) {
        for (Node node : nodes)
            node.setDisable(false);
    }

    public static void hideAndDisable(Node... nodes) {
        hide(nodes);
        disable(nodes);
    }

    public static void enableAndShow(Node... nodes) {
        enable(nodes);
        show(nodes);
    }

    public static List<Node> getAllChildren(Pane pane) {
        List<Node> result = new ArrayList<>();

        pane.getChildren().forEach(node -> {
            if (node instanceof Pane)
                result.addAll(getAllChildren((Pane) node));
            else
                result.add(node);
        });
        return result;
    }

    public static Pane clone(Pane pane) {
        Pane result = new Pane(pane);

        pane.getChildren().forEach(node -> {
            if (node instanceof Pane) {
                pane.getChildren().add(clone((Pane) node));
            } else {
                try {
                    Constructor<? extends Node> constructor = node.getClass().getConstructor(node.getClass());
                    result.getChildren().add(constructor.newInstance(node));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                }
            }
        });

        return result;
    }

    public static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText, String okText) {
        ButtonType ok = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);

        Alert alert = new Alert(alertType, contentText, ok);
        alert.getDialogPane().setNodeOrientation(Constants.NODE_ORIENTATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.show();
    }

    public static Optional<ButtonType> prompt(String title, String headerText, String contentText, String okText, String cancelText) {
        ButtonType ok = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert prompt = new Alert(Alert.AlertType.CONFIRMATION, contentText, ok, cancel);
        prompt.getDialogPane().setNodeOrientation(Constants.NODE_ORIENTATION);
        prompt.setTitle(title);
        prompt.setHeaderText(headerText);
        return prompt.showAndWait();
    }

    public static <T> void addToList(ListView<T> list, T object, int... position) {
        if (position.length > 0)
            list.getItems().add(position[0], object);
        else
            list.getItems().add(object);

    }

    public static <T> void removeFromList(ListView<T> list, T object) {
        list.getItems().remove(object);
    }

    public static <T> void filterList(ListView<T> list, String filter, Collection<T> originalCollection, Function<T, String> getter) {
        list.getItems().clear();
        if (filter.isEmpty()) {
            list.getItems().addAll(originalCollection);
        } else {
            originalCollection.forEach(t -> {
                if (getter.apply(t).toLowerCase().contains(filter))
                    list.getItems().add(t);
            });
        }
    }

    public static void infiniteRotateAnimation(Node node, double width, double height) {
        final Rotate rotationTransform = new Rotate(0, width / 2, height / 2);
        node.getTransforms().add(rotationTransform);

        final Timeline rotationAnimation = new Timeline();
        rotationAnimation.getKeyFrames()
                .add(
                        new KeyFrame(
                                Duration.seconds(2),
                                new KeyValue(
                                        rotationTransform.angleProperty(),
                                        360
                                )
                        )
                );
        rotationAnimation.setCycleCount(Animation.INDEFINITE);
        rotationAnimation.play();
    }

    public static void removeTransforms(Node node) {
        node.getTransforms().clear();
    }

    public static boolean hasAnyTransforms(Node node) {
        return node.getTransforms().size() > 0;
    }
}

