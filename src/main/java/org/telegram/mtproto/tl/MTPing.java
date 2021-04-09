package org.telegram.mtproto.tl;

import org.telegram.tl.TLContext;
import org.telegram.tl.TLObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.telegram.tl.StreamingUtils.readLong;
import static org.telegram.tl.StreamingUtils.writeLong;

/**
 * Created with IntelliJ IDEA.
 * User: Ruben Bermudez
 * Date: 03.11.13
 * Time: 8:22
 */
public class MTPing extends TLObject {
    public static final int CLASS_ID = 0x7abe77ec;

    private long pingId;

    public MTPing(long pingId) {
        this.pingId = pingId;
    }

    public MTPing() {

    }

    @Override
    public int getClassId() {
        return CLASS_ID;
    }

    @Override
    public void serializeBody(OutputStream stream) throws IOException {
        writeLong(this.pingId, stream);
    }

    @Override
    public void deserializeBody(InputStream stream, TLContext context) throws IOException {
        this.pingId = readLong(stream);
    }

    @Override
    public String toString() {
        return "ping#7abe77ec";
    }
}
