package ir.mkay.backupgram.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicUserDetails {
    private String name = "";
    private String username = "";
    private String phone = "";
}
