package ir.mkay.backupgram.converter;

import ir.mkay.backupgram.domain.BasicUserDetails;
import ir.mkay.backupgram.domain.persisted.Contact;

public class ContactToBasicUserDetailsConverter {
    public BasicUserDetails convert(Contact contact) {
        return new BasicUserDetails(
                contact.getFirstName() + ' ' + contact.getLastName(),
                contact.getUserName(),
                contact.getPhone()
        );
    }
}
