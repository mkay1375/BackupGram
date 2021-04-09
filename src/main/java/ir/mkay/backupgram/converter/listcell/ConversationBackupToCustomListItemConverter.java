package ir.mkay.backupgram.converter.listcell;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.Main;
import ir.mkay.backupgram.domain.persisted.ConversationBackup;
import ir.mkay.javafx.component.CustomListItem;
import javafx.scene.control.ListCell;


public class ConversationBackupToCustomListItemConverter extends ListCell<ConversationBackup> {
    public void updateItem(ConversationBackup item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            CustomListItem customListItem = new CustomListItem();

            if (item.getPeerName().length() < Constants.MAX_CUSTOM_CONVERSATION_TITLE_LENGTH)
                customListItem.setPrimaryText(item.getPeerName());
            else
                customListItem.setPrimaryText(item.getPeerName().substring(0, Constants.MAX_CUSTOM_CONVERSATION_TITLE_LENGTH) + "...");

            switch (item.getBackupStatus()) {
                case BACKING_UP:
                    customListItem.setImage(Main.getImage("DOWNLOADING"));
                    if (item.getCheckedMessages() > 0) {
                        customListItem.setSecondaryText(item.getCheckedMessages() + " messages checked...");
                    } else {
                        customListItem.setSecondaryText("...");
                    }
                    break;
                case BACKED_UP:
                    customListItem.setImage(Main.getImage("TICK"));
                    if (item.getBackedUpMessagesCount() > 0) {
                    customListItem.setSecondaryText(item.getBackedUpMessagesCount() + " messages were backed up.");
                    } else {
                        customListItem.setSecondaryText("No message found.");
                    }
                    break;
                case FLOOD:
                    customListItem.setImage(Main.getImage("WAIT"));
                    customListItem.setSecondaryText("Please wait...");
                    break;
                case ERROR:
                    customListItem.setImage(Main.getImage("ERROR"));
                    customListItem.setSecondaryText("Error");
                    break;
                default:
                    customListItem.setSecondaryText("At most backup " + item.getBackupMessagesCount() + " messages");
                    customListItem.setImage(Main.getImage(item.getType().toString()));
            }

            setGraphic(customListItem);
        } else {
            setGraphic(null);
        }
    }
}
