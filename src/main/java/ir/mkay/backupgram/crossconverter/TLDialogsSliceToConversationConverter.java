package ir.mkay.backupgram.crossconverter;

import ir.mkay.backupgram.domain.ConversationType;
import ir.mkay.backupgram.domain.persisted.Conversation;
import ir.mkay.backupgram.repository.ContactRepository;
import lombok.experimental.var;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.dialog.TLDialog;
import org.telegram.api.messages.dialogs.TLDialogsSlice;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChannel;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.user.TLUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TLDialogsSliceToConversationConverter {
    private ContactRepository contactRepo = new ContactRepository();

    public List<Conversation> convert(TLDialogsSlice dialogs) {
        List<Conversation> result = new ArrayList<>();

        int id = 0;
        for (TLDialog tlDialog : dialogs.getDialogs()) {
            Conversation conversation = new Conversation();
            conversation.setId(id++);
            var peer = tlDialog.getPeer();
            if (peer instanceof TLPeerUser) {
                var userOptional = convertToTLUser(peer, dialogs);
                if (userOptional.isPresent()) {
                    var user = userOptional.get();
                    if (contactRepo.exists(user.getId()))
                        conversation.setType(ConversationType.CONTACT);
                    else
                        conversation.setType(ConversationType.USER);

                    conversation.setPeerName(user.getFirstName() + " " + user.getLastName());
                    conversation.setPeerId(user.getId());
                    conversation.setPeerAccessHash(user.getAccessHash());
                } else {
                    conversation.setPeerName("Unknown");
                }
            } else if (peer instanceof TLPeerChat) {
                conversation.setType(ConversationType.GROUP);
                var chatOptional = convertToTLChat(peer, dialogs);
                if (chatOptional.isPresent()) {
                    var chat = chatOptional.get();
                    conversation.setPeerName(chat.getTitle());
                    conversation.setPeerId(chat.getId());
                } else {
                    conversation.setPeerName("Unknown");
                }
            } else if (peer instanceof TLPeerChannel) {

                var channelOptional = convertToTLChannel(peer, dialogs);
                if (channelOptional.isPresent()) {
                    var channel = channelOptional.get();
                    if (channel.getFlags() == 8288)
                        conversation.setType(ConversationType.CHANNEL);
                    else
                        conversation.setType(ConversationType.SUPERGROUP);

                    conversation.setPeerName(channel.getTitle());
                    conversation.setPeerId(channel.getId());
                    conversation.setPeerAccessHash(channel.getAccessHash());
                } else {
                    conversation.setPeerName("ناشناس");
                }
            }

            result.add(conversation);
        }

        return result;
    }

    private static Optional<TLUser> convertToTLUser(TLAbsPeer peer, TLDialogsSlice dialogs) {
        return dialogs.getUsers().stream()
                .filter(user -> user.getId() == peer.getId())
                .filter(user -> user instanceof TLUser)
                .map(user -> (TLUser) user)
                .findFirst();
    }

    private static Optional<TLChat> convertToTLChat(TLAbsPeer peer, TLDialogsSlice dialogs) {
        return dialogs.getChats().stream()
                .filter(chat -> chat.getId() == peer.getId())
                .filter(chat -> chat instanceof TLChat)
                .map(chat -> (TLChat) chat)
                .findFirst();
    }

    private static Optional<TLChannel> convertToTLChannel(TLAbsPeer peer, TLDialogsSlice dialogs) {
        return dialogs.getChats().stream()
                .filter(channel -> channel.getId() == peer.getId())
                .filter(channel -> channel instanceof TLChannel)
                .map(channel -> (TLChannel) channel)
                .findFirst();
    }
}
