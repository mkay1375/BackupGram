package ir.mkay.backupgram.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.huri.jcal.JalaliCalendar;
import ir.mkay.backupgram.SharedMemoryStorage;
import ir.mkay.backupgram.converter.ContactToBasicUserDetailsConverter;
import ir.mkay.backupgram.converter.ForeignUserToBasicUserDetailsConverter;
import ir.mkay.backupgram.domain.*;
import ir.mkay.backupgram.repository.ContactRepository;
import ir.mkay.backupgram.repository.ForeignUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class BackupService {
    private ContactRepository contactRepo = new ContactRepository();
    private ForeignUserRepository foreignUserRepo = new ForeignUserRepository();
    private ContactToBasicUserDetailsConverter contactToBasicUserDetailsConverter = new ContactToBasicUserDetailsConverter();
    private ForeignUserToBasicUserDetailsConverter foreignUserToBasicUserDetailsConverter = new ForeignUserToBasicUserDetailsConverter();

    public String saveBackup(List<BackedUpConversation> backedUpConversations, String path, String backupFolderName) {
        if (!path.endsWith("/") || !path.endsWith("\\"))
            path += File.separator;

        BackupResult backupResult = new BackupResult();
        backupResult.setContacts(contactRepo.findAll());
        backupResult.setConversations(backedUpConversations);
        backupResult.setForeignUsers( // Save foreign users which are whether sender or receiver of a message
                foreignUserRepo.findAll().stream()
                        .filter(
                                foreignUser -> backedUpConversations.stream()
                                        .anyMatch(
                                                backedUpConversation -> backedUpConversation.getTextMessages()
                                                        .anyMatch(
                                                                message -> message.getFromId() == foreignUser.getId() || message.getToId() == foreignUser.getId()
                                                        )
                                        )
                        )
                        .collect(Collectors.toList())
        );
        backupResult.setCurrentUserId(SharedMemoryStorage.api.getCurrentUserId());

        try {
            tryToRemoveOldFiles(path);
            extractBackupResultFiles(path);
            String result = "var backupResult = " + new ObjectMapper().writeValueAsString(backupResult);
            Files.write(Paths.get(path + "backup-result/data/data.js"), result.getBytes(), StandardOpenOption.CREATE);
            renameFile(path + "backup-result", path + backupFolderName);
        } catch (IOException e) {
            log.error("Can't write backup to disk", e);
            return null;
        }
        return path + backupFolderName;
    }

    public boolean saveBackupAsExcel(List<BackedUpConversation> backedUpConversations, String path, String backupFolderName) {
        if (!path.endsWith("/") || !path.endsWith("\\"))
            path += File.separator;
        if (!backupFolderName.endsWith("/") || !backupFolderName.endsWith("\\"))
            backupFolderName += File.separator;

        Workbook workbook = new XSSFWorkbook();

        Row row;
        for (BackedUpConversation conversation : backedUpConversations) {
            Sheet sheet = workbook.createSheet(conversation.getPeerName());

            // Setting up titles:
            row = sheet.createRow(0);

            row.createCell(0).setCellValue("Date");
            row.createCell(1).setCellValue("Message");
            if (conversation.getType() != ConversationType.CHANNEL) {
                row.createCell(2).setCellValue("Sender");
                row.createCell(3).setCellValue("Sender Username");
                row.createCell(4).setCellValue("Sender Phone Number");
                if (conversation.getType() != ConversationType.GROUP && conversation.getType() != ConversationType.SUPERGROUP) {
                    row.createCell(5).setCellValue("Receiver");
                    row.createCell(6).setCellValue("Receiver Username");
                    row.createCell(7).setCellValue("Receiver Phone Number");
                }
            }

            int rowCounter = 1;
            for (TextMessage message : conversation.getMessages()) {
                row = sheet.createRow(rowCounter++);
                row.createCell(0).setCellValue(convertToDateString(new Date(message.getDate() * 1000l)));
                row.createCell(1).setCellValue(message.getMessage());
                if (conversation.getType() != ConversationType.CHANNEL) {
                    BasicUserDetails sender = findUserInContactsAndForeignUsers(message.getFromId());
                    row.createCell(2).setCellValue(sender.getName());
                    row.createCell(3).setCellValue(sender.getUsername());
                    row.createCell(4).setCellValue(sender.getPhone());

                    if (conversation.getType() != ConversationType.GROUP && conversation.getType() != ConversationType.SUPERGROUP) {
                        BasicUserDetails receiver = findUserInContactsAndForeignUsers(message.getToId());
                        row.createCell(5).setCellValue(receiver.getName());
                        row.createCell(6).setCellValue(receiver.getUsername());
                        row.createCell(7).setCellValue(receiver.getPhone());
                    }
                }
            }
        }

        try {
            workbook.write(Files.newOutputStream(Paths.get(path + backupFolderName + "BackupExcel.xlsx")));
            workbook.close();
            return true;
        } catch (IOException e) {
            log.error("Can't write excel backup to disk", e);
            return false;
        }
    }

    private void tryToRemoveOldFiles(String path) {
        try {
            if (Files.exists(Paths.get(path + "backup-result/"))) {
                Files.walk(Paths.get(path + "backup-result/"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (Exception e) {
            log.warn("Can't delete old backup files", e);
        }
    }

    private void extractBackupResultFiles(String outputFolder) throws IOException {

        byte[] buffer = new byte[1024];

        //create output directory is not exists
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //get the zip file content
        ZipInputStream zis =
                new ZipInputStream(this.getClass().getResourceAsStream("/backup-result.zip"));
        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

            String fileName = ze.getName();
            File newFile = new File(outputFolder + File.separator + fileName);

            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();

            if (!fileName.endsWith("/")) {
                if (!newFile.exists())
                    newFile.createNewFile();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
            }
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    /**
     * Rename file or folder
     *
     * @return <code>true</code> if and only if the renaming succeeded;
     * <code>false</code> otherwise
     */
    private static boolean renameFile(String oldPath, String newPath) {
        try {
            return new File(oldPath).renameTo(new File(newPath));
        } catch (Exception e) {
            log.warn("Can't rename folder from {} to {}", oldPath, newPath, e);
            return false;
        }
    }

    private BasicUserDetails findUserInContactsAndForeignUsers(int userId) {
        return contactRepo
                .find(userId)
                .map(contactToBasicUserDetailsConverter::convert)
                .orElse(
                        foreignUserRepo
                                .find(userId)
                                .map(foreignUserToBasicUserDetailsConverter::convert)
                                .orElse(new BasicUserDetails())
                );
    }

    private String convertToPersianDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        JalaliCalendar persianDate = new JalaliCalendar(new GregorianCalendar(year, month, day));
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                persianDate.getYear(), persianDate.getMonth(), persianDate.getDay(),
                hours, minutes, seconds);
    }

    private String convertToDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(date);
    }
}
