package keycompare;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import output.Builder;
import output.HtmlBuilder;
import output.TextBuilder;
import output.XmlBuilder;

/**
 *
 * @author mark-4304
 */
public class Main {

    private static String resourcesPath;
    private static String outputFolder;
    private static Map compareMap;
    private static String outputFormat;

    public static void main(String[] args) throws IOException {
        try {
            readProperties(args[0]);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        //System.out.println(compareMap.keySet());  //-> Path of base resources files

        compareMap.forEach((baseResources, cmpResourcesList) -> {
            try {
                compare(baseResources.toString(), (ArrayList<String>) cmpResourcesList);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Cant Compare Files", ex);
            }
        });

    }

    public static void readProperties(String propertiesPath) {
        Properties prop = new Properties();
        InputStream input = null;
        String[] baseFiles;
        try {
            input = new FileInputStream(propertiesPath);
            // load a properties file
            prop.load(input);
            if ((prop.getProperty("PATH") == null) || (prop.getProperty("FILENAMES") == null) || (prop.getProperty("OUTPUTFOLDER") == null)) {
                System.err.println("The Values of conf.properties cant be null\n\n");
                System.err.println("Create File : conf.properties");
                System.err.println("---Sample config.properties---\n"
                        + "PATH=C:/ManageEngine/DesktopCentral_Server/lib/resources\n"
                        + "FILENAMES=ApplicationResources, JSApplicationResources\n"
                        + "OUTPUTFOLDER=KeyDiff");
                System.exit(1);
            }
            // get the property value and print it out
            System.out.println("------------conf.properties---------");
            System.out.println("Given resources path : " + prop.getProperty("PATH"));
            System.out.println("Base File Names : " + prop.getProperty("FILENAMES"));
            System.out.println("Output Folder   : " + prop.getProperty("OUTPUTFOLDER"));
            System.out.println("Output Format   : " + prop.getProperty("OUTPUTFORMAT"));
            System.out.println("------------------------------------");

            resourcesPath = prop.getProperty("PATH");
            outputFolder = prop.getProperty("OUTPUTFOLDER");
            outputFormat = prop.getProperty("OUTPUTFORMAT");
            baseFiles = prop.getProperty("FILENAMES").split(",");
            readPath(resourcesPath, baseFiles);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Take List of ApplicationResources and JSApplicationResources to be
     * compare
     *
     * @param path the command line path
     */
    public static void readPath(String path, String[] baseFiles) throws IOException {
        compareMap = new LinkedHashMap<String, ArrayList<String>>();
        for (String baseFile : baseFiles) {
            List fileList = new ArrayList();
            Files.walk(Paths.get(path)).forEach(new Consumer<Path>() {
                public void accept(Path filePath) {
                    if (Files.isRegularFile(filePath)) {
                        if (filePath.toString().indexOf("\\" + baseFile.trim()) >= 0) {
                            fileList.add(filePath.toString().trim());
                        }
                    }
                }
            });
            compareMap.put(fileList.remove(0), fileList);
        }
    }

    private static void compare(String baseResources, List cmpResourcesList) throws IOException {
        String baseFilePath = baseResources;
        Map baseContent = getSearchableContent(baseFilePath);
        Iterator<String> iterator = cmpResourcesList.iterator();
        while (iterator.hasNext()) {
            Differences differences = new Differences();
            String filePath = iterator.next();
            Map content = getSearchableContent(filePath);
            differences = compareContents(baseContent, content);
            differences.setFilename1(baseFilePath);
            differences.setFilename2(filePath);
            System.out.println("\n\nDifferences Between :  \nFilename1 : " + differences.getFilename1() + "  \nFilename2 : " + differences.getFilename2());
            if (differences.hasDifferences()) {
                createReport(filePath, differences);
            } else {
                System.out.println("  ----   NULL   ----  ");
            }
        }
    }

    /**
     * To Get Searchable Content Type object from given file
     *
     * @param filepath location of the file
     */
    public static Map getSearchableContent(String filePath) {
        File file = new File(filePath);
        Scanner scanner = null;
        Map searchableContent = new LinkedHashMap<String, LinkedHashSet<String>>();
        String previousPackageName = null;
        String packageName = null;
        String keyName = null;
        Set keys = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "File Not Found", ex);
        }
        //scanning line by line 
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //skip line supports starts with ## or empty line
            if (line.startsWith(" ") || line.startsWith("#") || line.length() < 2) {
                continue;
            }
            //System.out.println(line);
            //finding package name and key name
            String[] strArray = line.split("=");
            String str = strArray[0];
            int index = str.lastIndexOf(".");
            try {
                packageName = str.substring(0, index);
                keyName = str.substring(index + 1);
            } catch (StringIndexOutOfBoundsException ex) {
                try {
                    int index1 = str.lastIndexOf("_");
                    packageName = str.substring(0, index1);
                    keyName = str.substring(index1 + 1);
                } catch (StringIndexOutOfBoundsException ex1) {
                    // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "String Index Out Of Bounds :  " + line, ex1);
                }
            }
            //Update previousPackageName for First Time 
            if (previousPackageName == null) {
                previousPackageName = packageName;
                keys = new LinkedHashSet<String>();
            }
            try {
                //Updating Searchable Content Map
                if (previousPackageName.equals(packageName)) {
                    keys.add(keyName);
                } else {
                    searchableContent.put(previousPackageName, keys);
                    previousPackageName = packageName;

                    if (searchableContent.containsKey(previousPackageName) == true) {
                        keys = (LinkedHashSet<String>) searchableContent.get(previousPackageName);
                    } else {
                        keys = new LinkedHashSet<String>();
                    }
                    keys.add(keyName);
                }
            } catch (Exception e) {
                // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, line, e);
            }
        }
        //Adding Last item to Map
        searchableContent.put(previousPackageName, keys);
        //System.out.println(searchableContent);
        return searchableContent;
    }

    /**
     * Take List of ApplicationResources and JSApplicationResources to be
     * compare
     *
     * @param path the command line path
     */
    public static Differences compareContents(Map baseContent, Map content) {
        Differences builder = new Differences();

        for (Iterator it = baseContent.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, LinkedHashSet<String>> packageEntry = (Map.Entry<String, LinkedHashSet<String>>) it.next();
            String packageName = packageEntry.getKey();

            try {
                //Finding Extra Keys
                Set baseKeysSet = (LinkedHashSet<String>) baseContent.get(packageName);
                Set keysSet = (LinkedHashSet<String>) content.get(packageName);
                Iterator<String> iterator = keysSet.iterator();
                while (iterator.hasNext()) {
                    String keyName = iterator.next();
                    if (!baseKeysSet.contains(keyName)) {
                        builder.setExtraKeys(packageName + "." + keyName);
                        //System.out.println(packageName + "." + keyName);
                    }
                }
            } catch (Exception ex) {
                //Missing Package Missing Key 
                //System.err.println("Not Package Avaible in tmp" + baseContent.get(packageName));
                Set KeysSet = (LinkedHashSet<String>) baseContent.get(packageName);
                Iterator<String> iterator = KeysSet.iterator();
                while (iterator.hasNext()) {
                    String keyName = iterator.next();

                    builder.setMissingKeys(packageName + "." + keyName);
                    //System.out.println(packageName + "." + keyName);
                }
            }

            try {//Find Missing Individual Key
                Set keysSet = (LinkedHashSet<String>) content.get(packageName);
                Set baseKeys = (Set<String>) baseContent.get(packageName);
                Iterator<String> iterator = baseKeys.iterator();
                while (iterator.hasNext()) {
                    String keyName = iterator.next();
                    if (!keysSet.contains(keyName)) {
                        builder.setMissingKeys(packageName + "." + keyName);

                    }
                }
            } catch (Exception ex) {
                // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "  --- : "+ packageName, ex);
                Set KeysSet = (LinkedHashSet<String>) baseContent.get(packageName);
                Iterator<String> iterator = KeysSet.iterator();
                while (iterator.hasNext()) {
                    String keyName = iterator.next();

                    builder.setMissingKeys(packageName + "." + keyName);

                }
            }

        }
        return builder;
    }

    private static void createReport(String filePath, Differences differences) throws IOException {

        String currentDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
        //javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory();
        int index = filePath.lastIndexOf("\\");
        String fileName = filePath.substring(index + 1, filePath.length() - 11);
        String outputPath = currentDirectory + "\\" + outputFolder + "\\";
        makeDirectory(outputPath);
        generatedText(fileName, outputPath, differences);

    }

    private static void generatedText(String fileName, String outputPath, Differences stringBuilder) throws IOException {
        File createReportFile = null;
        String reportFile = null;
        try {
            reportFile = outputPath + fileName + "." + outputFormat;
            createReportFile = new File(reportFile);
            createReportFile.createNewFile();
        } catch (IOException ex) {
            // Logger.getLogger(Main.class.getName()).log(Level.SEVERE, createReportFile.toString(), ex);
        }
        /*
         * To write contents of StringBuffer to a file, use
         * BufferedWriter class.
         */
        System.out.println("Report File  : " + reportFile);
        writeOutputFile(reportFile, stringBuilder);

    }

    private static void makeDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        // if the directory does not exist, create it
        if (!directory.exists()) {
            try {
                if (directory.mkdir()) {
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory Not created");
                }
            } catch (SecurityException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, directory.getAbsolutePath(), ex);
            }
        }
    }

    private static void writeOutputFile(String filename, Differences differences) throws java.io.IOException {
        Builder builder = null;
        if (filename.endsWith(".html")) {
            builder = new HtmlBuilder();
        } else if (filename.endsWith(".xml")) {
            builder = new XmlBuilder();
        } else {
            builder = new TextBuilder();
        }
        builder.build(filename, differences);

    }
}
