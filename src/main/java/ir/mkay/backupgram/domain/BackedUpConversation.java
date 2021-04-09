package ir.mkay.backupgram.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class BackedUpConversation {
    private int id;
    private String peerName;
    private int peerId;
    private ConversationType type;
    private List<TextMessage> messages = new ArrayList<>();

    public BackedUpConversation(int id, String peerName, int peerId, ConversationType type) {
        this.id = id;
        this.peerName = peerName;
        this.peerId = peerId;
        this.type = type;
    }

    public void addTextMessage(TextMessage textMessage) {
        messages.add(textMessage);
    }

    public boolean removeTextMessage(TextMessage textMessage) {
        return messages.remove(textMessage);
    }

    public void clearMessages() {
        messages.clear();
    }

    public Stream<TextMessage> getTextMessages() {
        return messages.stream();
    }
}
