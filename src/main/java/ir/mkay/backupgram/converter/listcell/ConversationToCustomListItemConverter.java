package ir.mkay.backupgram.converter.listcell;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.Main;
import ir.mkay.backupgram.domain.persisted.Conversation;
import ir.mkay.javafx.component.CustomListItem;
import javafx.scene.control.ListCell;

public class ConversationToCustomListItemConverter  extends ListCell<Conversation> {
    public void updateItem(Conversation item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            CustomListItem customListItem = new CustomListItem();
            if (item.getPeerName().length() < Constants.MAX_CUSTOM_CONVERSATION_TITLE_LENGTH)
                customListItem.setPrimaryText(item.getPeerName());
            else
                customListItem.setPrimaryText(item.getPeerName().substring(0, Constants.MAX_CUSTOM_CONVERSATION_TITLE_LENGTH) + "...");

            customListItem.setSecondaryText("");
            customListItem.setImage(Main.getImage(item.getType().toString()));

            setGraphic(customListItem);
        } else {
            setGraphic(null);
        }
    }
}