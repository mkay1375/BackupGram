package ir.mkay.backupgram.repository;

import ir.mkay.backupgram.domain.persisted.ConversationBackup;
import ir.mkay.backupgram.domain.persisted.ConversationBackupStatus;

public class ConversationBackupRepository extends BaseRepository<ConversationBackup, Integer> {
    public void resetBackupStatus() {
        forEach((i, conversationBackup) -> {
            conversationBackup.setBackupStatus(ConversationBackupStatus.NOT_BACKED_UP);
            conversationBackup.setBackedUpMessagesCount(0);
            conversationBackup.setCheckedMessages(0);
        });
    }
}
