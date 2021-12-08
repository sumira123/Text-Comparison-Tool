package App.com;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtils {

    /**
     *get all files from gievn folder
     */
    public File[] getListOfAllFile(File directory) {
        ArrayList<File> fileList = new ArrayList<File>();
        listf(directory, fileList);
        return fileList.toArray(new File[0]);
    }


    /**
     * recursive function for find all text files in folder
     */
    private void listf(File directory, ArrayList<File> fileList) {

        // get all the files from a directory
        File[] fList = directory.listFiles();

        for (File file : fList) {

            if (file.isFile() && file.length() > 0) {
                if (file.getName().endsWith(".java") || file.getName().endsWith(".txt")
                        || file.getName().endsWith(".md") || file.getName().endsWith(".cs")) {
                    fileList.add(file);
                }
            } else if (file.isDirectory()) {
                listf(file, fileList);
            }
        }
    }


    /**
     * readfile data from scanner
     */
    public static synchronized char[] getFileData(File file) {
        StringBuilder data=new StringBuilder();
        try {
            FileReader reader = new FileReader(file);
            Scanner sc=new Scanner(file);
            data.append(sc.nextLine());
            while(sc.hasNextLine()) {
                data.append("\n"+sc.nextLine());
            }
            sc.close();
            reader.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return data.toString().toCharArray();
    }
    /**
     * write result in csv file
     */
    private String FileName="result.csv";

    public synchronized void writeComparisonResult(ComparisonResult result) {
        try {
            FileWriter fw=new FileWriter(FileName, true);
            fw.write(result.toString()+"\n\r");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *delete result file
     */
    public void deleteResultFile() {
        File file=new File(FileName);
        file.delete();
    }




}
