/*
 * 
 * 
 */
package output;

import java.io.OutputStream;
import java.io.PrintWriter;
import keycompare.Differences;



/**
 * 
 * @author Markandayan
 *
 */
public class TextBuilder extends AbstractBuilder {
	public void build(OutputStream out, Differences d) {
		PrintWriter pw = new PrintWriter(out);
		pw.println(d.toString());
		pw.flush();
	}
}
