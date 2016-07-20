package keycompare;

import java.util.Iterator;
import java.util.Map;

public class Differences {

    private Map missingKeys;
    private Map extraKeys;
    private String filename1;
    private String filename2;

    public Differences() {
        missingKeys = new java.util.LinkedHashMap<String, String>();
        extraKeys = new java.util.LinkedHashMap<String, String>();
    }

    public Map getMissingKeys() {
        return missingKeys;
    }

    public void setMissingKeys(String missingKeyName) {
        this.missingKeys.put(missingKeyName, null);
    }

    public Map getExtraKeys() {
        return extraKeys;
    }

    public void setExtraKeys(String extraKeyName) {
        this.extraKeys.put(extraKeyName, null);
    }

    public String getFilename1() {
        return filename1;
    }

    public void setFilename1(String filename1) {
        this.filename1 = filename1;
    }

    public String getFilename2() {
        return filename2;
    }

    public void setFilename2(String filename2) {
        this.filename2 = filename2;
    }

    public boolean hasDifferences() {
        return ((getMissingKeys().size() > 0) || (getExtraKeys().size() > 0));
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        int differenceCount = this.getMissingKeys().size() + this.getExtraKeys().size();

        stringBuffer.append("---------------------- Summary --------------------");
        stringBuffer.append("\n\n Number of Missing Keys  : " + this.getMissingKeys().size());
        stringBuffer.append("\n\n Number of Extra Keys    : " + this.getExtraKeys().size());
        stringBuffer.append("\n\n Total differences       : " + differenceCount);
        stringBuffer.append("\n\n----------------------------------------------------\n\n");

        if (this.getMissingKeys().size() == 1) {
            stringBuffer.append("1 Key was");
        } else {
            stringBuffer.append(this.getMissingKeys().size() + " Keys were");
        }
        stringBuffer.append(" missing in " + this.getFilename2() + "\n");

        Iterator iter = this.getMissingKeys().keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            stringBuffer.append("\t[missing] " + name + "\n");
        }

        if (this.getExtraKeys().size() == 1) {
            stringBuffer.append("\n\n1 Key was");
        } else {
            stringBuffer.append("\n\n" + this.getExtraKeys().size() + " Keys were");
        }
        stringBuffer.append(" extra in " + this.getFilename2() + "\n");

        iter = this.getExtraKeys().keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            stringBuffer.append("\t[extra] " + name + "\n");
        }

        return stringBuffer.toString();
    }

}
