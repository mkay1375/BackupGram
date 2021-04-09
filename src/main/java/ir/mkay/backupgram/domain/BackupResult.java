package ir.mkay.backupgram.domain;

import ir.mkay.backupgram.domain.persisted.Contact;
import ir.mkay.backupgram.domain.persisted.Conversation;
import ir.mkay.backupgram.domain.persisted.ForeignUser;
import lombok.Data;

import java.util.Collection;

@Data
public class BackupResult {
    private Collection<BackedUpConversation> conversations;
    private Collection<Contact> contacts;
    private Collection<ForeignUser> foreignUsers;
    private int currentUserId;
}
