package ir.mkay.backupgram.service.telegram;

import org.telegram.bot.ChatUpdatesBuilder;
import org.telegram.bot.handlers.DefaultUpdatesHandler;
import org.telegram.bot.handlers.UpdatesHandlerBase;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

class SimpleChatUpdatesBuilder implements ChatUpdatesBuilder {
    private IKernelComm kernelComm;
    private IDifferenceParametersService differenceParametersService;
    private DatabaseManager databaseManager;

    SimpleChatUpdatesBuilder(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void setKernelComm(IKernelComm kernelComm) {
        this.kernelComm = kernelComm;
    }

    @Override
    public void setDifferenceParametersService(IDifferenceParametersService differenceParametersService) {
        this.differenceParametersService = differenceParametersService;
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public UpdatesHandlerBase build() {
        if (kernelComm == null)
            throw new NullPointerException("Can't build the handler without a KernelComm");
        if (differenceParametersService == null)
            throw new NullPointerException("Can't build the handler without a differenceParamtersService");

        // The main business is here:
        return new DefaultUpdatesHandler(kernelComm, differenceParametersService, databaseManager);
    }
}
