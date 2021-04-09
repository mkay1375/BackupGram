package ir.mkay.javafx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class CustomListItem extends VBox {
    @FXML
    private ImageView image;

    @FXML
    Text primaryText;

    @FXML
    Text secondaryText;

    public CustomListItem() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/custom-list-item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ImageView getImageNode() {
        return image;
    }

    public Text getPrimaryTextNode() {
        return primaryText;
    }

    public Text getSecondaryTextNode() {
        return secondaryText;
    }

    public void setImage(Image image) {
        this.image.setImage(image);
    }

    public void setPrimaryText(String text) {
        primaryText.setText(text);
    }

    public void setSecondaryText(String text) {
        secondaryText.setText(text);
    }
}
