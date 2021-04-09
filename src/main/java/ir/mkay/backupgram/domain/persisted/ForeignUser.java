package ir.mkay.backupgram.domain.persisted;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Groups, supergroups, channel and non-contact users are foreign users.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ForeignUser extends Persistable<Integer> {
    private String firstName; // or title
    private String lastName;
    private String userName;
    private String phone;
    private boolean isUser;

    public ForeignUser(Integer id, String firstName, String lastName, String userName, String phone, boolean isUser) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phone = phone;
        this.isUser = isUser;
    }
}
