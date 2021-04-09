package ir.mkay.backupgram.domain.persisted;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Contact extends Persistable<Integer> {
    private String firstName;
    private String lastName;
    private String userName;
    private String phone;

    public Contact(Integer id, String firstName, String lastName, String userName, String phone) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.phone = phone;
    }
}
