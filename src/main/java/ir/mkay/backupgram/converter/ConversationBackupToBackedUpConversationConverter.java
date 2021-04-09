package ir.mkay.backupgram.converter;

import ir.mkay.backupgram.domain.BackedUpConversation;
import ir.mkay.backupgram.domain.persisted.ConversationBackup;

public class ConversationBackupToBackedUpConversationConverter {
    public BackedUpConversation convert(ConversationBackup conversationBackup) {
        return new BackedUpConversation(
                conversationBackup.getId(),
                conversationBackup.getPeerName(),
                conversationBackup.getPeerId(),
                conversationBackup.getType()
        );
    }
}
