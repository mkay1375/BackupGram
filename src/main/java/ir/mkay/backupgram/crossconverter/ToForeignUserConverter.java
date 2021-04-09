package ir.mkay.backupgram.crossconverter;

import ir.mkay.backupgram.domain.persisted.ForeignUser;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.TLChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;

import java.util.Optional;

public class ToForeignUserConverter {
    public Optional<ForeignUser> convert(TLAbsUser tlAbsUser) {
        ForeignUser user = null;
        if (tlAbsUser instanceof TLUser) {
            TLUser tlUser = (TLUser) tlAbsUser;
            user = new ForeignUser(
                    tlUser.getId(),
                    tlUser.getFirstName(),
                    tlUser.getLastName(),
                    tlUser.getUserName(),
                    tlUser.getPhone(),
                    true
            );
        } else {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public Optional<ForeignUser> convert(TLAbsChat tlAbsChat) {
        int id;
        String firstNameOrTitle, userName;
        ForeignUser user;

        if (tlAbsChat instanceof TLChat) {
            TLChat tlChat = (TLChat) tlAbsChat;
            id = tlChat.getId();
            firstNameOrTitle = tlChat.getTitle();
            userName = "";
        } else if (tlAbsChat instanceof TLChannel) {
            TLChannel tlChannel = (TLChannel) tlAbsChat;
            id = tlChannel.getId();
            firstNameOrTitle = tlChannel.getTitle();
            userName = tlChannel.getUsername();
        } else {
            return Optional.empty();
        }

        user = new ForeignUser(id, firstNameOrTitle, "", userName, "", false);
        return Optional.of(user);
    }
}
