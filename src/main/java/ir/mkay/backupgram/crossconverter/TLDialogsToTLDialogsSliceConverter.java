package ir.mkay.backupgram.crossconverter;

import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.messages.dialogs.TLDialogsSlice;

public class TLDialogsToTLDialogsSliceConverter {
    public TLDialogsSlice convert(TLDialogs tlDialogs) {
        TLDialogsSlice tlDialogsSlice = new TLDialogsSlice();
        tlDialogsSlice.setCount(tlDialogs.getDialogs().size());
        tlDialogsSlice.setChats(tlDialogs.getChats());
        tlDialogsSlice.setDialogs(tlDialogs.getDialogs());
        tlDialogsSlice.setMessages(tlDialogs.getMessages());
        tlDialogsSlice.setUsers(tlDialogs.getUsers());
        return tlDialogsSlice;
    }
}
