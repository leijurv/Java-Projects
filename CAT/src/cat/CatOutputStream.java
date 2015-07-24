/*
 * Copyright 2014 Lurf Jurv
 * All rights reserved
 */
package cat;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author leijurv
 */
public class CatOutputStream extends OutputStream {
    private final CAT c;
    private final OutputStream output;
    public CatOutputStream(OutputStream output, CAT c) {
        this.c = c;
        this.output = output;
    }
    @Override
    public void write(int b) throws IOException {
        output.write(c.encode((byte) (b & 0xff)));
    }
    @Override
    public void write(byte[] b) throws IOException {
        output.write(c.encode(b));
    }
    @Override
    public void close() throws IOException {
        output.close();
    }
    @Override
    public void flush() throws IOException {
        output.flush();
    }
}
