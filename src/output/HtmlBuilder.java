/*
 *
 *
 */
package output;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.Set;
import java.util.Iterator;
import java.util.TreeSet;
import keycompare.Differences;

/**
 *
 * Generates html output for a Differences instance
 *
 * @author Markandayan
 *
 *
 */
public class HtmlBuilder extends AbstractBuilder {

    @Override
    public void build(OutputStream out, Differences d) {
        PrintWriter pw = new PrintWriter(out);

        pw.println("<html>");
        pw.println("<META http-equiv=\"Content-Type\" content=\"text/html\">");
        pw.println("<head>");
        pw.println("<title>File differences</title>");
        addScript(pw);
        pw.println("</head>");

        pw.println("<body text=\"#000000\" vlink=\"#000000\" alink=\"#000000\" link=\"#000000\">");

        pw.println(getStyleTag());

        pw.print("<p>First file: ");
        String filename1 = d.getFilename1();

        if (filename1 == null) {
            filename1 = "filename1.zip";
        }
        pw.print(filename1);
        pw.println("<br>");

        pw.print("Second file: ");

        String filename2 = d.getFilename2();

        if (filename2 == null) {
            filename2 = "filename2.zip";
        }
        pw.print(filename2);
        pw.println("</p>");

        writeSummary(pw, d);
        writeMissingKeys(pw, d.getMissingKeys().keySet());
        writeExtraKeys(pw, d.getExtraKeys().keySet());

        pw.println("<hr>");
        pw.println("<p>");
        pw.println("Generated at " + new java.util.Date());
        pw.println("</p>");
        pw.println("</body>");

        pw.println("</html>");

        pw.flush();

    }

    protected void writeMissingKeys(PrintWriter pw, Set missing) {
        writeDiffSet(pw, " Missing Keys in second file ", missing, "hideMissKey");
    }

    protected void writeExtraKeys(PrintWriter pw, Set extra) {
        writeDiffSet(pw, " Extra Keys in second file ", extra, "hideExtraKey");
    }

    protected void writeDiffSet(PrintWriter pw, String name, Set s, String viewId) {
        Set treeSet = new TreeSet(s);

        pw.println("<TABLE CELLSPACING=\"1\" CELLPADDING=\"3\" WIDTH=\"100%\" BORDER=\"0\">");

        pw.println("\n<tr>");
        pw.println("<td class=\"diffs\" colspan=\"2\">" + "<div onClick=\"" + viewId + "();\"> " +name + " (" + s.size() + " keys)"+ "</div>"+"</td>");
        pw.println("</tr>\n");
        pw.println("<tr id=\""+ viewId +"\">");
        pw.println("<td width=\"20\">");
        pw.println("</td>");
        pw.println("<td>");
        if (s.size() > 0) {
            pw.println("<ul>");
            Iterator iter = treeSet.iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                pw.print("<li>");
                pw.print(key);
                pw.println("</li>");
            }
            pw.println("</ul>");
        }
        pw.println("</td>");
        pw.println("</tr>");
        pw.println("</table>");

    }

    protected String getStyleTag() {
        StringBuilder sb = new StringBuilder();

        sb.append("<style type=\"text/css\">");
        sb.append(" body, p { ");
        sb.append(" font-family: verdana,arial,helvetica; ");
        sb.append(" font-size: 80%; ");
        sb.append(" color:#000000; ");
        sb.append(" } \n");
        sb.append(" 	  .diffs { \n");
        sb.append("         font-family: verdana,arial,helvetica; \n");
        sb.append("         font-size: 80%; \n");
        sb.append(" font-weight: bold; \n");
        sb.append(" text-align:left; \n");
        sb.append(" background:#a6caf0; \n");
        sb.append(" } \n");
        sb.append(" tr, td { \n");
        sb.append(" font-family: verdana,arial,helvetica; \n");
        sb.append(" font-size: 80%; \n");
        sb.append(" background:#eeeee0; \n");
        sb.append(" } \n");
        sb.append(" </style>\n");

        return sb.toString();
    }

    private void writeSummary(PrintWriter pw, Differences differences) {
        int differenceCount = differences.getMissingKeys().size() + differences.getExtraKeys().size();

        pw.println("<TABLE CELLSPACING=\"1\" CELLPADDING=\"3\" WIDTH=\"100%\" BORDER=\"0\">");
        pw.println("<tr>");
        pw.println("<td class=\"diffs\" colspan=\"4\"> Summary </td>");
        pw.println("</tr>");
        pw.println("<tr>");
        pw.println("<td width=\"400\">");
        pw.println("</td>");
        pw.println("<tr><td colspan=\"2\">Number of Missing Keys  </td><td>" + differences.getMissingKeys().size() + "</td></tr>");
        pw.println("<tr><td colspan=\"2\">Number of Extra Keys    </td><td>" + differences.getExtraKeys().size() + "</td></tr>");
        pw.println("<tr><td colspan=\"2\">Total differences       </td><td> " + differenceCount + "</td></tr>");
        pw.println("</tr>");
        pw.println("</table>");
    }

    private void addScript(PrintWriter pw) {
        pw.println("<script>\n"
                + "function hideMissKey() {\n"
                + " if( document.getElementById(\"hideMissKey\").style.display=='none' ){\n"
                + "   document.getElementById(\"hideMissKey\").style.display = '';\n"
                + " }else{\n"
                + "   document.getElementById(\"hideMissKey\").style.display = 'none';\n"
                + " }\n"
                + "}\n"
                + "function hideExtraKey() {\n"
                + " if( document.getElementById(\"hideExtraKey\").style.display=='none' ){\n"
                + "   document.getElementById(\"hideExtraKey\").style.display = '';\n"
                + " }else{\n"
                + "   document.getElementById(\"hideExtraKey\").style.display = 'none';\n"
                + " }\n"
                + "}\n"
                + "</script>\n"
                + "");
    }
}
