package ir.mkay.backupgram;

import javafx.geometry.NodeOrientation;

import java.io.File;

public abstract class Constants {
    public static final String APP_NAME = "BackupGram";
    public static final String APP_VERSION = "0.9.6";
    public static final String AUTH_FILE_SAVE_PATH; // Filled in static part
    public static final String AUTH_FILE_NAME_SUFFIX = ".auth";

    public static final String DEVICE_MODEL = "PC";
    public static final String LANGUAGE = "en";

    public static final int MAX_CUSTOM_CONVERSATION_TITLE_LENGTH = 35;

    static {
        AUTH_FILE_SAVE_PATH = System.getProperty("user.dir") + File.separator;
    }

    public static final String EVENT_CONVERSATION_BACKUP_STARTED = "conversationBackupStarted";
    public static final String EVENT_CONVERSATION_BACKUP_DONE = "conversationBackupDone";
    public static final String EVENT_CONVERSATION_BACKUP_ERROR = "conversationBackupError";
    public static final String EVENT_SOME_MESSAGES_BACKED_UP = "someMessagesBackedUp";
    public static final String EVENT_FLOOD_HAPPENED = "floodHappened";
    public static final String EVENT_FLOOD_ENDED = "floodEnded";

    public static final NodeOrientation NODE_ORIENTATION = NodeOrientation.LEFT_TO_RIGHT;
}
