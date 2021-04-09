package ir.mkay.backupgram.service.telegram;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.service.EventListenersRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.api.engine.RpcException;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
abstract class Supporter {
    static <T extends TLObject> TLObject callButWaitIfNecessary(IKernelComm api, TLMethod<T> method) throws ExecutionException, RpcException {
        final EventListenersRegistryService eventListenersRegistryService = new EventListenersRegistryService();
        while (true) {
            try {
                return api.getApi().doRpcCall(method);
            } catch (RpcException e) {
                if (e.getErrorTag().contains("FLOOD_WAIT_")) {
                    int timeout = Integer.parseInt(e.getErrorTag().substring("FLOOD_WAIT_".length()));
                    try {
                        log.info("Flood happened; waiting for {} seconds", timeout);
                        eventListenersRegistryService.callEventHandler(Constants.EVENT_FLOOD_HAPPENED);
                        Thread.sleep(timeout * 1000);
                        eventListenersRegistryService.callEventHandler(Constants.EVENT_FLOOD_ENDED);
                    } catch (InterruptedException ex) {
                        return null;
                    }
                } else {
                    throw e;
                }
            } catch (TimeoutException | IOException e) {
                throw new ExecutionException(e);
            }
        }
    }
}
