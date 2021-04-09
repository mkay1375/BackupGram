package org.telegram.api.updates;

import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.user.TLAbsUser;
import org.telegram.tl.StreamingUtils;
import org.telegram.tl.TLContext;
import org.telegram.tl.TLVector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type TL updates combined.
 */
public class TLUpdatesCombined extends TLAbsUpdates {
    /**
     * The constant CLASS_ID.
     */
    public static final int CLASS_ID = 0x725b04c3;

    private TLVector<TLAbsUpdate> updates;
    private TLVector<TLAbsUser> users;
    private TLVector<TLAbsChat> chats;
    private int date;
    private int seqStart;
    private int seq;

    /**
     * Instantiates a new TL updates combined.
     */
    public TLUpdatesCombined() {
        super();
    }

    /**
     * Gets updates.
     *
     * @return the updates
     */
    public TLVector<TLAbsUpdate> getUpdates() {
        return this.updates;
    }

    /**
     * Sets updates.
     *
     * @param updates the updates
     */
    public void setUpdates(TLVector<TLAbsUpdate> updates) {
        this.updates = updates;
    }

    /**
     * Gets users.
     *
     * @return the users
     */
    public TLVector<TLAbsUser> getUsers() {
        return this.users;
    }

    /**
     * Sets users.
     *
     * @param users the users
     */
    public void setUsers(TLVector<TLAbsUser> users) {
        this.users = users;
    }

    /**
     * Gets chats.
     *
     * @return the chats
     */
    public TLVector<TLAbsChat> getChats() {
        return this.chats;
    }

    /**
     * Sets chats.
     *
     * @param chats the chats
     */
    public void setChats(TLVector<TLAbsChat> chats) {
        this.chats = chats;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public int getDate() {
        return this.date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(int date) {
        this.date = date;
    }

    /**
     * Gets seq start.
     *
     * @return the seq start
     */
    public int getSeqStart() {
        return this.seqStart;
    }

    /**
     * Sets seq start.
     *
     * @param seqStart the seq start
     */
    public void setSeqStart(int seqStart) {
        this.seqStart = seqStart;
    }

    /**
     * Gets seq.
     *
     * @return the seq
     */
    public int getSeq() {
        return this.seq;
    }

    /**
     * Sets seq.
     *
     * @param seq the seq
     */
    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getClassId() {
        return CLASS_ID;
    }

    public void serializeBody(OutputStream stream)
            throws IOException {
        StreamingUtils.writeTLVector(this.updates, stream);
        StreamingUtils.writeTLVector(this.users, stream);
        StreamingUtils.writeTLVector(this.chats, stream);
        StreamingUtils.writeInt(this.date, stream);
        StreamingUtils.writeInt(this.seqStart, stream);
        StreamingUtils.writeInt(this.seq, stream);
    }

    public void deserializeBody(InputStream stream, TLContext context)
            throws IOException {
        this.updates = StreamingUtils.readTLVector(stream, context);
        this.users = StreamingUtils.readTLVector(stream, context);
        this.chats = StreamingUtils.readTLVector(stream, context);
        this.date = StreamingUtils.readInt(stream);
        this.seqStart = StreamingUtils.readInt(stream);
        this.seq = StreamingUtils.readInt(stream);
    }

    public String toString() {
        return "updatesCombined#725b04c3";
    }
}