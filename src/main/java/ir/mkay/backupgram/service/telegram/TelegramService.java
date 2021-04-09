package ir.mkay.backupgram.service.telegram;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.converter.ConversationBackupToBackedUpConversationConverter;
import ir.mkay.backupgram.crossconverter.*;
import ir.mkay.backupgram.domain.BackedUpConversation;
import ir.mkay.backupgram.domain.persisted.ConversationBackup;
import ir.mkay.backupgram.repository.ContactRepository;
import ir.mkay.backupgram.repository.ConversationRepository;
import ir.mkay.backupgram.repository.ForeignUserRepository;
import ir.mkay.backupgram.service.EventListenersRegistryService;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.telegram.api.contacts.TLAbsContacts;
import org.telegram.api.contacts.TLContacts;
import org.telegram.api.engine.RpcException;
import org.telegram.api.functions.contacts.TLRequestContactsGetContacts;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.messages.TLRequestMessagesGetHistory;
import org.telegram.api.input.peer.*;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLAbsMessages;
import org.telegram.api.messages.TLMessagesSlice;
import org.telegram.api.messages.dialogs.TLDialogs;
import org.telegram.api.messages.dialogs.TLDialogsSlice;
import org.telegram.api.user.TLAbsUser;
import org.telegram.api.user.TLUser;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.tl.TLVector;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class TelegramService {
    private IKernelComm api;
    private ContactRepository contactRepo = new ContactRepository();
    private ConversationRepository conversationRepo = new ConversationRepository();
    private ForeignUserRepository foreignUserRepository = new ForeignUserRepository();
    private TLUserToContactConverter tlUserToContactConverter = new TLUserToContactConverter();
    private TLDialogsSliceToConversationConverter tlDialogsSliceToConversationConverter = new TLDialogsSliceToConversationConverter();
    private ToForeignUserConverter toForeignUserConverter = new ToForeignUserConverter();
    private TLDialogsToTLDialogsSliceConverter tlDialogsToTLDialogsSliceConverter = new TLDialogsToTLDialogsSliceConverter();
    private TLAbsMessageToTextMessageConverter tlAbsMessageToTextMessageConverter = new TLAbsMessageToTextMessageConverter();
    private TLAbsMessagesToTLMessagesSliceConverter tlAbsMessagesToTLMessagesSliceConverter = new TLAbsMessagesToTLMessagesSliceConverter();
    private ConversationBackupToBackedUpConversationConverter conversationBackupToBackedUpConversationConverter = new ConversationBackupToBackedUpConversationConverter();
    private EventListenersRegistryService eventListenersRegistryService = new EventListenersRegistryService();

    private boolean goOn;

    public TelegramService(IKernelComm api) {
        this.api = api;
    }

    public void stop() {
        goOn = false;
    }

    public boolean isStopped() {
        return !goOn;
    }

    /**
     * Load all contacts and persist them with {@link ContactRepository}
     */
    public void loadListOfContacts() throws ExecutionException, RpcException {
        TLRequestContactsGetContacts getContacts = new TLRequestContactsGetContacts();
        getContacts.setHash("");
        TLAbsContacts contacts = (TLAbsContacts) Supporter.callButWaitIfNecessary(api, getContacts);

        if (contacts instanceof TLContacts) {
            contactRepo.add(
                    ((TLContacts) contacts).getUsers().stream()
                            .filter(tlAbsUser -> tlAbsUser instanceof TLUser)
                            .map(tlAbsUser -> tlUserToContactConverter.convert((TLUser) tlAbsUser))
                            .collect(Collectors.toList())
            );
        }
    }

    /**
     * Load all conversations and foreign users then persist them
     * with {@link ConversationRepository} and {@link ForeignUserRepository}. <br>
     * <b>Important! load contacts before calling this method otherwise
     * all of the users become foreign users.</b>
     */
    public void loadAllConversationsAndForeignUsers() throws ExecutionException, RpcException {
        goOn = true;
        TLDialogsSlice result = null;

        TLDialogsSlice currentSlice = null;
        int offsetDate = 0;
        int totalDialogsCount = 0;
        do {
            TLRequestMessagesGetDialogs getDialogs = new TLRequestMessagesGetDialogs();
            getDialogs.setLimit(Integer.MAX_VALUE);

            getDialogs.setOffsetPeer(new TLInputPeerEmpty());
            getDialogs.setOffsetDate(offsetDate);
            Object response = Supporter.callButWaitIfNecessary(api, getDialogs);
            if (response instanceof TLDialogsSlice) {
                currentSlice = (TLDialogsSlice) response;
            } else if (response instanceof TLDialogs) {
                currentSlice = tlDialogsToTLDialogsSliceConverter.convert((TLDialogs) response);
            }

            if (result == null) {
                result = currentSlice;
                totalDialogsCount = currentSlice.getCount();
            } else if (currentSlice.getCount() > 0) {
                if (currentSlice.getMessages() != null) currentSlice.getMessages().forEach(result.getMessages()::add);
                if (currentSlice.getDialogs() != null) currentSlice.getDialogs().forEach(result.getDialogs()::add);
                if (currentSlice.getChats() != null) currentSlice.getChats().forEach(result.getChats()::add);
                if (currentSlice.getUsers() != null) currentSlice.getUsers().forEach(result.getUsers()::add);
            }

            if (currentSlice.getMessages() != null) {
                OptionalInt oldestMessageDate = currentSlice.getMessages().stream()
                        .filter(message -> message instanceof TLMessage)
                        .mapToInt(message -> ((TLMessage) message).getDate())
                        .min();
                if (oldestMessageDate.isPresent()) {
                    offsetDate = oldestMessageDate.getAsInt();
                }
            }
        } while (goOn && result.getDialogs() != null && result.getDialogs().size() < totalDialogsCount);

        addForeignUsers(result);
        conversationRepo.add(tlDialogsSliceToConversationConverter.convert(result));
    }

    public List<BackedUpConversation> loadMessages(Collection<ConversationBackup> conversationBackupList) {
        goOn = true;
        List<BackedUpConversation> backedUpConversations = new ArrayList<>();
        for (var conversationBackup : conversationBackupList) {
            eventListenersRegistryService.callEventHandler(Constants.EVENT_CONVERSATION_BACKUP_STARTED, conversationBackup);

            BackedUpConversation currentConversation = conversationBackupToBackedUpConversationConverter.convert(conversationBackup);
            TLAbsInputPeer inputPeer = null;
            switch (conversationBackup.getType()) {
                case USER:
                case CONTACT:
                    inputPeer = new TLInputPeerUser();
                    ((TLInputPeerUser) inputPeer).setUserId(conversationBackup.getPeerId());
                    ((TLInputPeerUser) inputPeer).setAccessHash(conversationBackup.getPeerAccessHash());
                    break;
                case CHANNEL:
                case SUPERGROUP:
                    inputPeer = new TLInputPeerChannel();
                    ((TLInputPeerChannel) inputPeer).setChannelId(conversationBackup.getPeerId());
                    ((TLInputPeerChannel) inputPeer).setAccessHash(conversationBackup.getPeerAccessHash());
                    break;
                case GROUP:
                    inputPeer = new TLInputPeerChat();
                    ((TLInputPeerChat) inputPeer).setChatId(conversationBackup.getPeerId());
                    break;
            }

            try {
                getListOfMessages(conversationBackup, inputPeer)
                        .forEach(tlAbsMessage -> tlAbsMessageToTextMessageConverter.convert(tlAbsMessage)
                                .ifPresent(currentConversation::addTextMessage));

                conversationBackup.setBackedUpMessagesCount(currentConversation.getMessages().size());
                eventListenersRegistryService.callEventHandler(Constants.EVENT_CONVERSATION_BACKUP_DONE, conversationBackup);

                backedUpConversations.add(currentConversation);

            } catch (Exception e) {
                log.warn("Can't get messages of {}", conversationBackup.getPeerName(), e);
                eventListenersRegistryService.callEventHandler(Constants.EVENT_CONVERSATION_BACKUP_ERROR, conversationBackup);
            }

            if (!goOn)
                break;
        }
        return backedUpConversations;
    }

    private void addForeignUsers(TLDialogsSlice tlDialogsSlice) {
        tlDialogsSlice.getChats().forEach(tlAbsChat -> toForeignUserConverter.convert(tlAbsChat).ifPresent(foreignUserRepository::add));
        addForeignUsers(tlDialogsSlice.getUsers());
    }

    private void addForeignUsers(TLVector<TLAbsUser> tlAbsUsers) {
        tlAbsUsers.stream()
                .filter(tlAbsUser -> !contactRepo.exists(tlAbsUser.getId()))
                .map(toForeignUserConverter::convert)
                .forEach(foreignUser -> foreignUser.ifPresent(foreignUserRepository::add));
    }

    /**
     * @param limit Max is 100.
     */
    private Optional<TLMessagesSlice> getMessagesSlice(TLAbsInputPeer peer, int limit, int offset) throws ExecutionException, RpcException {
        TLRequestMessagesGetHistory getHistory = new TLRequestMessagesGetHistory();
        getHistory.setPeer(peer);
        getHistory.setLimit(limit);
        getHistory.setAddOffset(offset);

        var response = Supporter.callButWaitIfNecessary(api, getHistory);
        if (response instanceof TLAbsMessages) {
            return tlAbsMessagesToTLMessagesSliceConverter.convert((TLAbsMessages) response);
        }
        return Optional.empty();
    }

    private List<TLAbsMessage> getListOfMessages(ConversationBackup conversationBackup, TLAbsInputPeer peer) throws ExecutionException, RpcException {
        final int limit = conversationBackup.getBackupMessagesCount();
        ArrayList<TLAbsMessage> messages = new ArrayList<>();

        Optional<TLMessagesSlice> messagesSlice;
        int checkedMessages = 0;
        do {
            messagesSlice = getMessagesSlice(peer, limit - checkedMessages, checkedMessages);
            if (messagesSlice.isPresent()) {
                addForeignUsers(messagesSlice.get().getUsers());
                for (TLAbsMessage absMessage : messagesSlice.get().getMessages()) {
                    messages.add(absMessage);
                    checkedMessages++;
                }
                eventListenersRegistryService.callEventHandler(Constants.EVENT_SOME_MESSAGES_BACKED_UP, conversationBackup, checkedMessages);
            }
        }
        while (messagesSlice.isPresent() && messages.size() < messagesSlice.get().getCount() && messages.size() < limit);

        return messages;
    }
}
