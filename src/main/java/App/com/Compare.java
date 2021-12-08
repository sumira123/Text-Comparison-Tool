package App.com;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;


public class Compare implements Runnable{

    public static int compCompleted=0;
    private File f1, f2;
    private char[] f1Data,f2Data;
    private Hashtable<File, char[]> dataTable;
    private FileUtils fileUtils;
    private Vector<ComparisonResult> newResults;
    private double threshold =0.5;

    public Compare(File f1, File f2,Hashtable<File, char[]> dataTable,  Vector<ComparisonResult> newResults) {
        this.f1 = f1;
        this.f2 = f2;
        this.dataTable=dataTable;
        fileUtils=new FileUtils();
        this.newResults=newResults;
    }

    @Override
    public void run() {
        readDataFromFile();
        //read data from file
        //calculate similarity
        double similarity=calcSimilarity(f1Data, f2Data);
        ComparisonResult result=new ComparisonResult(f1.getName(), f2.getName(), similarity);
        System.out.println(result);
        //check similarity with threshold for higher similarity
        if(similarity>=threshold)
            newResults.add(result);
            //write result in file
            fileUtils.writeComparisonResult(result);
        compCompleted++;
    }

    /**
     * read data from both file
     */
    private void readDataFromFile() {
        f1Data=dataTable.get(f1);
        f2Data=dataTable.get(f2);
        if(f1Data==null) {

            f1Data=fileUtils.getFileData(f1);
        }
        if(f2Data==null) {

            f2Data=fileUtils.getFileData(f2);
        }
        dataTable.put(f1, f1Data);
        dataTable.put(f2,f2Data);
    }

    /**
     * calculate similarity using LCS algorithm
     */
    private synchronized double calcSimilarity(char f1[], char f2[]) {

        int subsolutions[][] = new int[f1.length + 1][f2.length + 1];
        boolean directionLeft[][] = new boolean[f1.length + 1][f2.length + 1];

        // fill first row and first column of subsolutions with zeros

        for (int i = 1; i <= f1.length; i++) {
            for (int j = 1; j <= f2.length; j++) {
                if (f1[i - 1] == f2[j - 1]) {
                    subsolutions[i][j] = subsolutions[i - 1][j - 1] + 1;
                } else if (subsolutions[i - 1][j] > subsolutions[i][j - 1]) {
                    subsolutions[i][j] = subsolutions[i - 1][j];
                    directionLeft[i][j] = true;
                } else {
                    subsolutions[i][j] = subsolutions[i][j - 1];
                    directionLeft[i][j] = false;
                }
            }
        }
        int matches = 0;
        int i = f1.length, j = f2.length;
        while (i > 0 && j > 0) {
            if (f1[i - 1] == f2[j - 1]) {
                matches += 1;
                i -= 1;
                j -= 1;
            }

            else if (directionLeft[i][j]) {
                i -= 1;
            } else {
                j -= 1;
            }
        }
        return(double) (matches * 2) / (f1.length + f2.length);
    }

}
