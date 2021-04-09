package ir.mkay.backupgram.crossconverter;

import ir.mkay.backupgram.domain.TextMessage;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.message.media.TLMessageMediaDocument;
import org.telegram.api.message.media.TLMessageMediaPhoto;

import java.util.Optional;

public class TLAbsMessageToTextMessageConverter {
    public Optional<TextMessage> convert(TLAbsMessage tlAbsMessage) {
        if (tlAbsMessage instanceof TLMessage) {
            TLMessage tlMessage = (TLMessage) tlAbsMessage;
            if (!tlMessage.getMessage().isEmpty()) {
                return Optional.of(new TextMessage(
                        tlMessage.getId(),
                        tlMessage.getFromId(),
                        tlMessage.getToId().getId(),
                        tlMessage.getDate(),
                        tlMessage.getMessage()
                ));
            } else if (tlMessage.getMedia() instanceof TLMessageMediaPhoto) {
                TLMessageMediaPhoto tlMessageMediaPhoto = (TLMessageMediaPhoto) tlMessage.getMedia();
                if (tlMessageMediaPhoto.getCaption() != null && !tlMessageMediaPhoto.getCaption().isEmpty()) {
                    return Optional.of(new TextMessage(
                            tlMessage.getId(),
                            tlMessage.getFromId(),
                            tlMessage.getToId().getId(),
                            tlMessage.getDate(),
                            tlMessageMediaPhoto.getCaption()
                    ));
                }
            } else if (tlMessage.getMedia() instanceof TLMessageMediaDocument) {
                TLMessageMediaDocument tlMessageMediaDocument = (TLMessageMediaDocument) tlMessage.getMedia();
                if (tlMessageMediaDocument.getCaption() != null && !tlMessageMediaDocument.getCaption().isEmpty()) {
                    return Optional.of(new TextMessage(
                            tlMessage.getId(),
                            tlMessage.getFromId(),
                            tlMessage.getToId().getId(),
                            tlMessage.getDate(),
                            tlMessageMediaDocument.getCaption()
                    ));
                }
            }
        }

        return Optional.empty();
    }
}
