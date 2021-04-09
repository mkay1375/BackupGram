package ir.mkay.backupgram.converter;

import ir.mkay.backupgram.domain.persisted.Conversation;
import ir.mkay.backupgram.domain.persisted.ConversationBackup;

public class ConversationToConversationBackupConverter {
    public ConversationBackup convert (Conversation conversation, int backupMessagesCount) {
        return new ConversationBackup(
                conversation.getId(),
                conversation.getPeerName(),
                conversation.getPeerId(),
                conversation.getPeerAccessHash(),
                conversation.getType(),
                backupMessagesCount);
    }
}
