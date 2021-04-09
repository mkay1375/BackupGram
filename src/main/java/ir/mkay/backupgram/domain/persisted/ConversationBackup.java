package ir.mkay.backupgram.domain.persisted;

import ir.mkay.backupgram.domain.ConversationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ConversationBackup extends Persistable<Integer> {
    private String peerName;
    private int peerId;
    private long peerAccessHash;
    private ConversationType type;

    private ConversationBackupStatus backupStatus = ConversationBackupStatus.NOT_BACKED_UP;
    private int backedUpMessagesCount = 0;
    private int checkedMessages = 0;

    private int backupMessagesCount;
    // TODO (DELAYED) - BACKUP FILES: add these to CONSTRUCTOR and CONVERTER; implement behaviour in TelegramService.java
//    private int imageMaxSize;
//    private int videoMaxSize;
//    private int soundMaxSize;
//    private int fileMaxSize;

    public ConversationBackup(Integer id, String peerName, int peerId, long peerAccessHash, ConversationType type, int backupMessagesCount) {
        super(id);
        this.peerName = peerName;
        this.peerId = peerId;
        this.peerAccessHash = peerAccessHash;
        this.type = type;
        this.backupMessagesCount = backupMessagesCount;
    }
}
