/*
 * 
 * 
 */

package output;

import java.io.OutputStream;
import keycompare.Differences;


/**
 * 
 * 
 * @author Markandayan
 *
 */
public interface Builder {
	public void build(OutputStream out, Differences d);
	public void build(String filename, Differences d) throws java.io.IOException;
}
