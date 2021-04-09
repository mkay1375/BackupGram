package ir.mkay.backupgram.domain.persisted;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Persistable<ID> {
    protected ID id;
}
