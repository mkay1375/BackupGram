package ir.mkay.backupgram.domain.persisted;

import ir.mkay.backupgram.domain.ConversationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Conversation extends Persistable<Integer> {
    private String peerName;
    private int peerId;
    private long peerAccessHash;
    private ConversationType type;

    public Conversation(Integer id, String peerName, int peerId, long peerAccessHash, ConversationType type) {
        super(id);
        this.peerName = peerName;
        this.peerId = peerId;
        this.peerAccessHash = peerAccessHash;
        this.type = type;
    }
}
