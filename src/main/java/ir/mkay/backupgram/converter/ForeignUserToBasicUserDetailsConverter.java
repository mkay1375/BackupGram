package ir.mkay.backupgram.converter;

import ir.mkay.backupgram.domain.BasicUserDetails;
import ir.mkay.backupgram.domain.persisted.ForeignUser;

public class ForeignUserToBasicUserDetailsConverter {
    public BasicUserDetails convert(ForeignUser foreignUser) {
        return new BasicUserDetails(
                foreignUser.getFirstName() + ' ' + foreignUser.getLastName(),
                foreignUser.getUserName(),
                foreignUser.getPhone()
        );
    }
}
