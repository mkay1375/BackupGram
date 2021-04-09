package ir.mkay.backupgram.service.telegram;

import ir.mkay.backupgram.Constants;
import org.telegram.bot.structure.BotConfig;

class BotConfigImpl extends BotConfig {
    private String phoneNumber;

    BotConfigImpl(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setAuthfile(Constants.AUTH_FILE_SAVE_PATH + TelegramApiInitializer.encodePhoneNumber(phoneNumber) + Constants.AUTH_FILE_NAME_SUFFIX);
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String getBotToken() {
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }
}
