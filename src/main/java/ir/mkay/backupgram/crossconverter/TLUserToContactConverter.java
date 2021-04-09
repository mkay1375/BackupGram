package ir.mkay.backupgram.crossconverter;

import ir.mkay.backupgram.domain.persisted.Contact;
import org.telegram.api.user.TLUser;

public class TLUserToContactConverter {
    public Contact convert(TLUser tlUser) {
        return new Contact(
                tlUser.getId(),
                tlUser.getFirstName(),
                tlUser.getLastName(),
                tlUser.getUserName(),
                tlUser.getPhone()
        );
    }
}
