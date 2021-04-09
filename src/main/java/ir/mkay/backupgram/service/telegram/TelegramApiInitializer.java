package ir.mkay.backupgram.service.telegram;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.SharedMemoryStorage;
import org.telegram.api.engine.TelegramApi;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.TelegramBot;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.BotConfig;
import org.telegram.bot.structure.LoginStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class TelegramApiInitializer {
    private static TelegramBot kernel;

    public static LoginStatus loginToTelegram(String phoneNumber) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final DatabaseManager databaseManager = new InMemoryDB();
        final BotConfig botConfig = new BotConfigImpl(phoneNumber);
        final SimpleChatUpdatesBuilder builder = new SimpleChatUpdatesBuilder(databaseManager);

        kernel = new TelegramBot(botConfig, builder, SharedMemoryStorage.apiId, SharedMemoryStorage.apiHash, Constants.DEVICE_MODEL, Constants.APP_NAME, Constants.APP_VERSION, Constants.LANGUAGE);
        return kernel.init();
    }

    public static boolean logout(String phoneNumber) {
        if (phoneNumber != null) {
            try {
                Files.delete(Paths.get(Constants.AUTH_FILE_SAVE_PATH + encodePhoneNumber(phoneNumber) + Constants.AUTH_FILE_NAME_SUFFIX));
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static String encodePhoneNumber(String phoneNumber) {
        return new String(Base64.getEncoder().encode(phoneNumber.getBytes()));
    }

    public static String decodePhoneNumber(String encodedPhoneNumber) {
        return new String(Base64.getDecoder().decode(encodedPhoneNumber.getBytes()));
    }

    public static boolean authorizeWithCode(String code) {
        return kernel.getKernelAuth().setAuthCode(code.trim());
    }

    public static void start() {
        kernel.startBot();
    }

    public static void stop() {
        if (kernel != null)
            kernel.stopBot();
    }

    public static IKernelComm getKernel() {
        return kernel.getKernelComm();
    }

    public static TelegramApi getTelegramApi() {
        return kernel.getKernelComm().getApi();
    }
}
