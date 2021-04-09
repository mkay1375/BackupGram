package org.telegram.api.functions.messages;

import org.telegram.api.messages.stickers.featured.TLAbsMessagesFeaturedStickers;
import org.telegram.api.messages.stickers.recent.TLAbsMessagesRecentStickers;
import org.telegram.tl.StreamingUtils;
import org.telegram.tl.TLContext;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type TL request messages get chats.
 */
public class TLRequestMessagesGetRecentStickers extends TLMethod<TLAbsMessagesRecentStickers> {
    /**
     * The constant CLASS_ID.
     */
    public static final int CLASS_ID = 0x5ea192c9;

    private static final int FLAG_ATTACHED = 0x00000001; // 0

    private int flags;
    private int hash;

    /**
     * Instantiates a new TL request messages get chats.
     */
    public TLRequestMessagesGetRecentStickers() {
        super();
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public TLAbsMessagesRecentStickers deserializeResponse(InputStream stream, TLContext context)
            throws IOException {
        final TLObject res = StreamingUtils.readTLObject(stream, context);
        if (res == null) {
            throw new IOException("Unable to parse response");
        }
        if ((res instanceof TLAbsMessagesRecentStickers)) {
            return (TLAbsMessagesRecentStickers) res;
        }
        throw new IOException("Incorrect response type. Expected " + TLAbsMessagesFeaturedStickers.class.getName() + ", got: " + res.getClass().getCanonicalName());
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public void enableAttached(boolean enabled) {
        if (enabled) {
            this.flags |= FLAG_ATTACHED;
        } else {
            this.flags &= ~FLAG_ATTACHED;
        }
    }

    public void serializeBody(OutputStream stream)
            throws IOException {
        StreamingUtils.writeInt(flags, stream);
        StreamingUtils.writeInt(hash, stream);
    }

    public void deserializeBody(InputStream stream, TLContext context)
            throws IOException {
        flags = StreamingUtils.readInt(stream);
        hash = StreamingUtils.readInt(stream);
    }

    public String toString() {
        return "messages.getRecentStickers#5ea192c9";
    }
}