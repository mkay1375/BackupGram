package ir.mkay.backupgram.controller;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.Main;
import ir.mkay.backupgram.SharedMemoryStorage;
import ir.mkay.backupgram.converter.ConversationBackupToConversationConverter;
import ir.mkay.backupgram.converter.ConversationToConversationBackupConverter;
import ir.mkay.backupgram.converter.listcell.ConversationBackupToCustomListItemConverter;
import ir.mkay.backupgram.converter.listcell.ConversationToCustomListItemConverter;
import ir.mkay.backupgram.domain.BackedUpConversation;
import ir.mkay.backupgram.domain.persisted.Conversation;
import ir.mkay.backupgram.domain.persisted.ConversationBackup;
import ir.mkay.backupgram.domain.persisted.ConversationBackupStatus;
import ir.mkay.backupgram.repository.BaseRepository;
import ir.mkay.backupgram.repository.ConversationBackupRepository;
import ir.mkay.backupgram.repository.ConversationRepository;
import ir.mkay.backupgram.service.BackupService;
import ir.mkay.backupgram.service.EventListenersRegistryService;
import ir.mkay.backupgram.service.telegram.TelegramApiInitializer;
import ir.mkay.backupgram.service.telegram.TelegramService;
import ir.mkay.javafx.async.SimpleAsyncTask;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ir.mkay.javafx.util.FxUtils.*;

@Slf4j
public class MainController {
    private TelegramService telegramService;
    private BackupService backupService = new BackupService();
    private ConversationRepository conversationRepo = new ConversationRepository();
    private ConversationBackupRepository conversationBackupRepo = new ConversationBackupRepository();
    private ConversationToConversationBackupConverter conversationToConversationBackupConverter = new ConversationToConversationBackupConverter();
    private ConversationBackupToConversationConverter conversationBackupToConversationConverter = new ConversationBackupToConversationConverter();
    private EventListenersRegistryService eventListenersRegistry = new EventListenersRegistryService();

    private SimpleAsyncTask backupTask = null;

    @FXML
    private ListView<Conversation> conversationsListView;
    @FXML
    private ListView<ConversationBackup> selectedConversationsListView;
    @FXML
    private Button addToBackupListButton;
    @FXML
    private Button removeFromBackupListButton;
    @FXML
    private Button chooseBackupPathButton;
    @FXML
    private Button backupButton;
    @FXML
    private Button cancelBackupButton;
    @FXML
    private Button logoutButton;
    @FXML
    private TextField backupCountTextField;
    @FXML
    private TextField backupPathTextField;
    @FXML
    private TextField imageMaxSizeTextField;
    @FXML
    private TextField videoMaxSizeTextField;
    @FXML
    private TextField soundMaxSizeTextField;
    @FXML
    private TextField fileMaxSizeTextField;
    @FXML
    private TextField backupConversationSearchTextField;
    @FXML
    private TextField conversationSearchTextField;
    @FXML
    private ProgressIndicator generalProgressIndicator;
    @FXML
    private CheckBox imageCheckBox;
    @FXML
    private CheckBox videoCheckBox;
    @FXML
    private CheckBox soundCheckBox;
    @FXML
    private CheckBox fileCheckBox;

    @FXML
    public void initialize() {
        generalProgressIndicator.setProgress(-1);
        telegramService = new TelegramService(SharedMemoryStorage.api);

        conversationsListView.setCellFactory(param -> new ConversationToCustomListItemConverter());
        selectedConversationsListView.setCellFactory(param -> new ConversationBackupToCustomListItemConverter());

        addEventListeners();

        conversationsListView.setItems(FXCollections.observableArrayList());
        selectedConversationsListView.setItems(FXCollections.observableArrayList());

        loadContactsAndConversations();
    }

    public void addToBackupList() {
        if (backupCountTextField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Enter the number of messages to be backed up.",
                    "For each conversation, number of messages that should be backup up must be defined; if you want a full backup of a conversation, enter a big number.",
                    "OK");
            return;
        }
        int backupMessagesCount = Integer.parseInt(backupCountTextField.getText());
        conversationsListView.getSelectionModel().getSelectedItems().forEach(conversation -> {
            ConversationBackup conversationBackup = conversationToConversationBackupConverter.convert(conversation, backupMessagesCount);
            conversationBackupRepo.add(conversationBackup);
            addToList(selectedConversationsListView, conversationBackup);

            conversationRepo.removeById(conversation.getId());
            removeFromList(conversationsListView, conversation);
        });
    }

    public void removeFromBackupList() {
        selectedConversationsListView.getSelectionModel().getSelectedItems().forEach(conversationBackup -> {
            var conversation = conversationBackupToConversationConverter.convert(conversationBackup);
            conversationRepo.add(conversation);
            addToList(conversationsListView, conversation);

            conversationBackupRepo.removeById(conversationBackup.getId());
            removeFromList(selectedConversationsListView, conversationBackup);
        });
    }

    public void filterConversationList(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            conversationSearchTextField.setText("");
        }
        String filter = conversationSearchTextField.getText().toLowerCase();
        filterList(conversationsListView, filter, conversationRepo.findAll(), Conversation::getPeerName);
    }

    public void filterBackupConversationList(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            backupConversationSearchTextField.setText("");
        }
        String filter = backupConversationSearchTextField.getText().toLowerCase();
        filterList(selectedConversationsListView, filter, conversationBackupRepo.findAll(), ConversationBackup::getPeerName);
    }

    public void chooseBackupPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Backup Path");
        File selectedDirectory = directoryChooser.showDialog(Main.getPrimaryStage());
        if (selectedDirectory != null)
            backupPathTextField.setText(selectedDirectory.getAbsolutePath());
    }

    public void backup() {
        if (backupPathTextField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING,
                    "Backup Path Not Defined",
                    "Please choose a backup path.",
                    "",
                    "OK");
        } else { // Do backup
            show(generalProgressIndicator, cancelBackupButton);
            hide(backupButton);
            conversationBackupRepo.resetBackupStatus();

            backupTask = new SimpleAsyncTask<>(
                    () -> telegramService.loadMessages(conversationBackupRepo.findAll()), // take backup
                    (backedUpConversations) -> { // taking backup done
                        if (telegramService.isStopped()) {
                            conversationBackupRepo.resetBackupStatus();
                        } else {
                            String backupFolderName = String.format("BackupGram - %s", new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()));
                            new SimpleAsyncTask<>(
                                    () -> saveBackup(backedUpConversations, backupPathTextField.getText(), backupFolderName), // save backup
                                    (saveLocation) -> { // backup saved
                                        hide(generalProgressIndicator, cancelBackupButton);
                                        show(backupButton);
                                        if (saveLocation != null) {
                                            showAlert(Alert.AlertType.INFORMATION,
                                                    "Congrats!",
                                                    "Backup of your selected conversations is complete.",
                                                    "Your backup data is saved in here:\n" +
                                                            saveLocation,
                                                    "OK");
                                        } else {
                                            showAlert(Alert.AlertType.ERROR,
                                                    "Oops!",
                                                    "Something went wrong.",
                                                    "Make sure the backup path is correct and try again.",
                                                    "OK");
                                        }
                                    }
                            );
                        }
                    }
            );
        }
    }

    public void cancelBackup() {
        prompt("Stop Backup",
                "Are you sure you want to cancel the backup process?",
                "Nothing will be stored if you cancel the backup.",
                "Yes, Stop Backup",
                "No, Forget it")
                .ifPresent(buttonType -> {
                    if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        if (backupTask != null)
                            backupTask.interrupt();
                        telegramService.stop();
                    }
                });
    }

    public void toggleTextField(ActionEvent event) {
        if (event.getSource() instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) event.getSource();
            if (checkBox.getId().contains("image"))
                imageMaxSizeTextField.setDisable(!checkBox.isSelected());
            else if (checkBox.getId().contains("video"))
                videoMaxSizeTextField.setDisable(!checkBox.isSelected());
            else if (checkBox.getId().contains("sound"))
                soundMaxSizeTextField.setDisable(!checkBox.isSelected());
            else if (checkBox.getId().contains("file"))
                fileMaxSizeTextField.setDisable(!checkBox.isSelected());
        }
    }

    public void logout() {
        BaseRepository.clearRepositoriesData();
        TelegramApiInitializer.logout(SharedMemoryStorage.phoneNumber);
        Main.changePane("login");
    }

    private void loadContactsAndConversations() {
        new SimpleAsyncTask<>(
                () -> { // do
                    try {
                        telegramService.loadListOfContacts();
                        telegramService.loadAllConversationsAndForeignUsers();
                        return true;
                    } catch (Exception e) {
                        log.error("Can't load contacts and conversations", e);
                    }
                    return false;
                },
                statusOK -> { // done
                    if (statusOK) {
                        conversationRepo.forEach((id, conversation) -> addToList(conversationsListView, conversation));

                        hide(generalProgressIndicator);
                        enable(conversationSearchTextField);
                        enable(backupConversationSearchTextField);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Can't load list of conversations.",
                                "Make sure your internet connection is okay.",
                                "OK");
                        hide(generalProgressIndicator);
                    }
                }
        );
    }

    private void addEventListeners() {
        eventListenersRegistry.addEventHandler(Constants.EVENT_CONVERSATION_BACKUP_STARTED, conversationBackupObj -> {
            ConversationBackup conversationBackup = ((ConversationBackup) conversationBackupObj);
            conversationBackup.setBackupStatus(ConversationBackupStatus.BACKING_UP);
            selectedConversationsListView.refresh();
        });

        eventListenersRegistry.addEventHandler(Constants.EVENT_CONVERSATION_BACKUP_DONE, conversationBackupObj -> {
            ((ConversationBackup) conversationBackupObj).setBackupStatus(ConversationBackupStatus.BACKED_UP);
            selectedConversationsListView.refresh();
        });

        eventListenersRegistry.addEventHandler(Constants.EVENT_CONVERSATION_BACKUP_ERROR, conversationBackupObj -> {
            ((ConversationBackup) conversationBackupObj).setBackupStatus(ConversationBackupStatus.ERROR);
            selectedConversationsListView.refresh();
        });

        eventListenersRegistry.addEventHandler(Constants.EVENT_SOME_MESSAGES_BACKED_UP, (conversationBackupObj, checkedMessages) -> {
            ((ConversationBackup) conversationBackupObj).setCheckedMessages((Integer) checkedMessages);
            selectedConversationsListView.refresh();
        });

        eventListenersRegistry.addEventHandler(Constants.EVENT_FLOOD_HAPPENED, () -> {
            selectedConversationsListView.getItems().stream()
                    .filter(conversationBackup -> conversationBackup.getBackupStatus() == ConversationBackupStatus.BACKING_UP)
                    .findAny()
                    .ifPresent(conversationBackup -> conversationBackup.setBackupStatus(ConversationBackupStatus.FLOOD));
            selectedConversationsListView.refresh();
        });

        eventListenersRegistry.addEventHandler(Constants.EVENT_FLOOD_ENDED, () -> {
            selectedConversationsListView.getItems().stream()
                    .filter(conversationBackup -> conversationBackup.getBackupStatus() == ConversationBackupStatus.FLOOD)
                    .findAny()
                    .ifPresent(conversationBackup -> conversationBackup.setBackupStatus(ConversationBackupStatus.BACKING_UP));
            selectedConversationsListView.refresh();
        });
    }

    private String saveBackup(List<BackedUpConversation> backedUpConversations, String path, String backupFolderName) {
        String saveLocation = backupService.saveBackup(backedUpConversations, path, backupFolderName);
        boolean excelBackupOK = backupService.saveBackupAsExcel(backedUpConversations, path, backupFolderName);
        return saveLocation;
    }
}
