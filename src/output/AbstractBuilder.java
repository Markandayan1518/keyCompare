/*
 * 
 * 
 */
package output;

import java.io.*;
import keycompare.Differences;

/**
 *
 * @author Markandayan
 */
public abstract class AbstractBuilder
        implements Builder {

    public void build(String filename, Differences d) throws IOException {
        FileOutputStream fileOutputStream = null;

        fileOutputStream = new FileOutputStream(filename);
        build(fileOutputStream, d);
        fileOutputStream.flush();
    }

    public abstract void build(OutputStream out, Differences d);
}
