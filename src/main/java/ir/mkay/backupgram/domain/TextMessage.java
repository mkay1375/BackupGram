package ir.mkay.backupgram.domain;

import ir.mkay.backupgram.domain.persisted.Persistable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextMessage {
    private int id;
    private int fromId;
    private int toId;
    private int date;
    private String message;
}
