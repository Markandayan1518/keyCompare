/*
 * 
 * 
 */
package output;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Set;
import java.util.Iterator;
import keycompare.Differences;

/**
 *
 * Generates xml output for a Differences instance
 *
 * @author Markandayan
 */
public class XmlBuilder extends AbstractBuilder {

    public void build(OutputStream out, Differences differences) {
        PrintWriter printWriter = new PrintWriter(out);

        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        printWriter.print("<keysdiff filename1=\"");

        String filename1 = differences.getFilename1();

        if (filename1 == null) {
            filename1 = "filename1.zip";
        }
        printWriter.print(filename1);
        printWriter.print("\" filename2=\"");

        String filename2 = differences.getFilename2();

        if (filename2 == null) {
            filename2 = "filename2.zip";
        }
        printWriter.print(filename2);
        printWriter.println("\">");

        printWriter.println("<differences>");
        addCountTag(printWriter, "missingCount", differences.getMissingKeys().size());
        addCountTag(printWriter, "extraCount", differences.getExtraKeys().size());
        addCountTag(printWriter, "totalDifferencesCount", differences.getMissingKeys().size() + differences.getExtraKeys().size());
        writeMissingKeys(printWriter, differences.getMissingKeys().keySet());
        writeExtraKeys(printWriter, differences.getExtraKeys().keySet());
        printWriter.println("</differences>");
        printWriter.println("</keysdiff>");

        printWriter.flush();
    }

    protected void writeMissingKeys(PrintWriter pw, Set missing) {
        Iterator iter = missing.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            pw.print("<missing>");
            pw.print(key);
            pw.println("</missing>");
        }

    }

    protected void writeExtraKeys(PrintWriter pw, Set extra) {
        Iterator iter = extra.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            pw.print("<extra>");
            pw.print(key);
            pw.println("</extra>");
        }
    }

    private void addCountTag(PrintWriter pw, String tagName, int count) {
        pw.print("<" + tagName + ">");
        pw.print(count);
        pw.print("</" + tagName + ">");
    }

}
