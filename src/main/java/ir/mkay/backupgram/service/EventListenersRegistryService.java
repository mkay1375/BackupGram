package ir.mkay.backupgram.service;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventListenersRegistryService {
    private static final HashMap<String, Object> eventHandlers = new HashMap<>();

    public void addEventHandler(String eventName, Runnable eventHandler) {
        eventHandlers.put(eventName, eventHandler);
    }

    public void addEventHandler(String eventName, Consumer eventHandler) {
        eventHandlers.put(eventName, eventHandler);
    }

    public void addEventHandler(String eventName, BiConsumer eventHandler) {
        eventHandlers.put(eventName, eventHandler);
    }

    public void callEventHandler(String eventName, Object... args) {
        Object eventHandler = eventHandlers.get(eventName);
        if (eventHandler != null) {
            if (eventHandler instanceof Runnable) {
                checkArgs(args, 0);
                ((Runnable) eventHandler).run();
            } else if (eventHandler instanceof Consumer) {
                checkArgs(args, 1);
                ((Consumer) eventHandler).accept(args[0]);
            } else if (eventHandler instanceof BiConsumer) {
                checkArgs(args, 2);
                ((BiConsumer) eventHandler).accept(args[0], args[1]);
            }
        }
    }

    private void checkArgs(Object[] args, int requiredArgsCount) {
        if (args.length != requiredArgsCount) {
            throw new RuntimeException(String.format("Number of args does not match with number of args event handler required; %d required", args.length));
        }
    }
}

