package ir.mkay.backupgram.converter;

import ir.mkay.backupgram.domain.persisted.Conversation;
import ir.mkay.backupgram.domain.persisted.ConversationBackup;

public class ConversationBackupToConversationConverter {
    public Conversation convert(ConversationBackup conversationBackup) {
        return new Conversation(
                conversationBackup.getId(),
                conversationBackup.getPeerName(),
                conversationBackup.getPeerId(),
                conversationBackup.getPeerAccessHash(),
                conversationBackup.getType()
        );
    }
}
