package ir.mkay.backupgram;

import ir.mkay.backupgram.service.telegram.TelegramApiInitializer;
import lombok.experimental.var;
import org.telegram.api.functions.upload.TLRequestUploadGetFile;
import org.telegram.api.input.filelocation.TLInputDocumentFileLocation;
import org.telegram.bot.kernel.IKernelComm;
import org.telegram.bot.structure.LoginStatus;

import java.util.Scanner;

public class App {
    static IKernelComm api;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter phone number (begin with +98): ");
        String phoneNumber = scanner.next().trim();


        LoginStatus loginStatus = TelegramApiInitializer.loginToTelegram(phoneNumber);
        if (loginStatus == LoginStatus.CODESENT) {
            System.out.println("Code sent; Enter code please: ");
            if (TelegramApiInitializer.authorizeWithCode(scanner.next()))
                loginStatus = LoginStatus.ALREADYLOGGED;
        }

        if (loginStatus == LoginStatus.ALREADYLOGGED) {
            TelegramApiInitializer.start();
            api = TelegramApiInitializer.getKernel();
            System.out.println("Connected and everything's good; Your ID: " + api.getCurrentUserId());

            run();
            System.exit(0);
        } else {
            System.out.println("Error --> Exit :)");
        }
    }

    public static void run() throws Exception {
        TLRequestUploadGetFile getFile = new TLRequestUploadGetFile();

        TLInputDocumentFileLocation fileLocation = new TLInputDocumentFileLocation();



        fileLocation.setAccessHash(6051523679387617327l);
        fileLocation.setId(5814605882028306416l);
        fileLocation.setVersion(0);

        getFile.setLocation(fileLocation);
        getFile.setLimit(10);

        int limit = 1024 * 1024; // less than 1MB; more than it use offset and ...
        int size = limit; // I think it's always ok //(int) Math.pow(2, Math.ceil(Math.log(limit) / Math.log(2)));
        var o = api.getDownloader().getApi().doGetFile(4, fileLocation, 0, size);

//       Files.write(Paths.get(Constants.AUTH_FILE_SAVE_PATH + "a"))

        System.out.println("Done");
    }
}
