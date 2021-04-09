package ir.mkay.backupgram.crossconverter;

import org.telegram.api.messages.TLAbsMessages;
import org.telegram.api.messages.TLChannelMessages;
import org.telegram.api.messages.TLMessages;
import org.telegram.api.messages.TLMessagesSlice;

import java.util.Optional;

public class TLAbsMessagesToTLMessagesSliceConverter {
    public Optional<TLMessagesSlice> convert(TLAbsMessages tlAbsMessages) {
        TLMessagesSlice tlMessagesSlice;
        if (tlAbsMessages instanceof TLMessagesSlice) {
            tlMessagesSlice = (TLMessagesSlice) tlAbsMessages;
        } else if (tlAbsMessages instanceof TLMessages) {
            TLMessages tlMessages = (TLMessages) tlAbsMessages;
            tlMessagesSlice = new TLMessagesSlice();
            tlMessagesSlice.setCount(tlMessages.getMessages().size());
            tlMessagesSlice.setChats(tlMessages.getChats());
            tlMessagesSlice.setUsers(tlMessages.getUsers());
            tlMessagesSlice.setMessages(tlMessages.getMessages());
        } else if (tlAbsMessages instanceof TLChannelMessages) {
            TLChannelMessages tlChannelMessages = (TLChannelMessages) tlAbsMessages;
            tlMessagesSlice = new TLMessagesSlice();
            tlMessagesSlice.setCount(tlChannelMessages.getCount());
            tlMessagesSlice.setChats(tlChannelMessages.getChats());
            tlMessagesSlice.setUsers(tlChannelMessages.getUsers());
            tlMessagesSlice.setMessages(tlChannelMessages.getMessages());
        } else {
            return Optional.empty();
        }
        return Optional.of(tlMessagesSlice);
    }
}
