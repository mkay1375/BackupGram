package ir.mkay.backupgram.controller;

import ir.mkay.backupgram.Constants;
import ir.mkay.backupgram.Main;
import ir.mkay.backupgram.SharedMemoryStorage;
import ir.mkay.backupgram.service.telegram.TelegramApiInitializer;
import ir.mkay.javafx.async.SimpleAsyncTask;
import ir.mkay.javafx.util.Colors;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.telegram.bot.structure.LoginStatus;

import java.io.File;
import java.util.stream.Stream;

import static ir.mkay.javafx.util.FxUtils.*;

@Slf4j
public class LoginController {
    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField codeTextField;

    @FXML
    private Button sendCodeButton;

    @FXML
    private Text codeLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Text sendCodeStatusText;

    @FXML
    private Text loginStatusText;

    @FXML
    void initialize() {
        hide(sendCodeStatusText, loginStatusText);
        sendCodeStatusText.setFill(Color.WHITE);

        hide(codeLabel, codeTextField, loginButton);

        loadLastLoggedInPhoneNumber();
    }

    public void sendCode() {
        String phoneNumber = phoneNumberTextField.getText();

        disable(phoneNumberTextField, sendCodeButton);
        hide(sendCodeStatusText, loginStatusText);

        new SimpleAsyncTask<>(
                () -> { // do
                    LoginStatus loginStatus;
                    try {
                        loginStatus = TelegramApiInitializer.loginToTelegram(phoneNumber);
                    } catch (Exception e) {
                        log.error("Try to login/send code failed", e);
                        return LoginStatus.UNEXPECTEDERROR;
                    }
                    return loginStatus;
                },
                (loginStatus) -> { // done
                    switch (loginStatus) {
                        case ALREADYLOGGED:
                            goToMainPage();
                            return;
                        case CODESENT:
                            show(codeLabel, codeTextField, loginButton);
                            break;
                        default:
                            sendCodeStatusText.setText("Make sure the phone number is correct and try again.");
                            sendCodeStatusText.setFill(Color.valueOf(Colors.DANGER));

                            enable(phoneNumberTextField, sendCodeButton);
                            hide(codeLabel, codeTextField, loginButton);
                    }
                    show(sendCodeStatusText);
                });
    }

    public void login() {
        String code = codeTextField.getText();

        disable(codeTextField, loginButton);
        disable(phoneNumberTextField, sendCodeButton);
        hide(sendCodeStatusText);

        new SimpleAsyncTask<>(
                () -> TelegramApiInitializer.authorizeWithCode(code), // do
                (codeAccepted) -> { // done
                    if (codeAccepted) {
                        hide(loginStatusText);
                        goToMainPage();
                    } else {
                        show(loginStatusText);
                        enable(codeTextField, loginButton);
                        enable(phoneNumberTextField, sendCodeButton);
                    }
                }
        );
    }

    private void goToMainPage() {
        TelegramApiInitializer.start();
        SharedMemoryStorage.api = TelegramApiInitializer.getKernel();
        SharedMemoryStorage.phoneNumber = phoneNumberTextField.getText();
        Main.changePane("main");
    }

    private void loadLastLoggedInPhoneNumber() {
        File folder = new File(Constants.AUTH_FILE_SAVE_PATH);
        File[] currentFolder = folder.listFiles();

        if (currentFolder != null) {
            Stream.of(currentFolder)
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(Constants.AUTH_FILE_NAME_SUFFIX))
                    .sorted((a, b) -> Long.compare(b.lastModified(), a.lastModified()))
                    .limit(1)
                    .map(File::getName)
                    .map(filename -> filename.replace(Constants.AUTH_FILE_NAME_SUFFIX, ""))
                    .map(TelegramApiInitializer::decodePhoneNumber)
                    .findAny().ifPresent(phoneNumberTextField::setText);
        }
    }
}
