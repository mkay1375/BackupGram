package ir.mkay.backupgram.service.telegram;

import org.jetbrains.annotations.NotNull;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.structure.Chat;
import org.telegram.bot.structure.IUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryDB implements DatabaseManager {
    private static final Map<Integer, int[]> differencesData = new ConcurrentHashMap<>();

    @Override
    public Chat getChatById(int id) {
        return null;
    }

    @Override
    public IUser getUserById(int id) {
        return null;
    }

    @NotNull
    @Override
    public Map<Integer, int[]> getDifferencesData() {
        return differencesData;
    }

    @Override
    public boolean updateDifferencesData(int botId, int pts, int date, int seq) {
        differencesData.put(botId, new int[]{pts, date, seq});
        return true;
    }
}
