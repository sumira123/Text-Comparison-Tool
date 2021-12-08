package App.com;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class ComparisonMain extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }
    private TableView<ComparisonResult> resultTable = new TableView<>();  
    private ProgressBar progressBar = new ProgressBar();

    private ThreadPool threadPool;
    private FileUtils fu = new FileUtils();

    @Override
    public void start(Stage stage)
    {
        stage.setTitle("Comparison Application");
        //stage.setMinWidth(600);
        resultTable.setMinWidth(600);
        progressBar.setMinWidth(600);

        // Create toolbar
        Button compareBtn = new Button("Compare...");
        Button stopBtn = new Button("Stop");
        ToolBar toolBar = new ToolBar(compareBtn, stopBtn);
        
        // Set up button event handlers.
        compareBtn.setOnAction(event -> crossCompare(stage));
        stopBtn.setOnAction(event -> stopComparison());
        
        // Initialise progressbar
        progressBar.setProgress(0.0);
        
        TableColumn<ComparisonResult,String> file1Col = new TableColumn<>("File 1");
        TableColumn<ComparisonResult,String> file2Col = new TableColumn<>("File 2");
        TableColumn<ComparisonResult,String> similarityCol = new TableColumn<>("Similarity");
        
        // The following tell JavaFX how to extract information from a ComparisonResult 
        // object and put it into the three table columns.
        file1Col.setCellValueFactory(   
            (cell) -> new SimpleStringProperty(cell.getValue().getFile1()) );
            
        file2Col.setCellValueFactory(   
            (cell) -> new SimpleStringProperty(cell.getValue().getFile2()) );
            
        similarityCol.setCellValueFactory(  
            (cell) -> new SimpleStringProperty(
                String.format("%.1f%%", cell.getValue().getSimilarity() * 100.0)) );
          
        // Set and adjust table column widths.
        file1Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        file2Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        similarityCol.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));            
        
        // Add the columns to the table.
        resultTable.getColumns().add(file1Col);
        resultTable.getColumns().add(file2Col);
        resultTable.getColumns().add(similarityCol);

        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(resultTable);
        mainBox.setBottom(progressBar);
        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    
    private void crossCompare(Stage stage)
    {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        dc.setTitle("Choose directory");
        File directory = dc.showDialog(stage);

        System.out.println("Comparing files within " + directory + "...");
        // reset progressbar to 0
        progressBar.setProgress(0.0);
        startComparison(directory);
    }
    
    private void stopComparison()
    {
        //System.out.println("Stopping comparison...");
        if (threadPool != null) {
            System.out.println("Stopping comparison...");
            threadPool.stop();
        }
    }


    /**
     * directory to compare
     */
    public void startComparison(File directory) {
        //delete result file before comparing
        fu.deleteResultFile();

        //get list of files in selected directory tree
        File fileList[] = new FileUtils().getListOfAllFile(directory); //new line commit File | fileList[] = new ListAllFiles().getListOfAllFile(directory);
        Vector<ComparisonResult> newResults = new Vector<>();
        Hashtable<File, char[]> dataTable = new Hashtable<>();

        //calculate number of comparison
        int numberOfComparison = (fileList.length * (fileList.length - 1)) / 2;

        //create threadpool with 2 thread and numberofcomparison queue size
        threadPool = new ThreadPool(2, numberOfComparison);
        //progress
        double progress;// = Compare.compCompleted / numberOfComparison;
        if (fileList.length > 1) {
            //if more than 1 file found then start comparison

            for (int i = 0; i < fileList.length - 1; i++) {
                for (int j = i + 1; j < fileList.length; j++) {
                    try {
                        threadPool.execute((Runnable) new Compare(fileList[i], fileList[j], dataTable, newResults));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Choose another folder with more than 1 file... ");
        }

        //wait until all comparison task complete
        while (threadPool.getRemainingTask() > 0) {

            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //set result data
            resultTable.getItems().setAll(newResults);
            // setProgress
            progress = (double) Compare.compCompleted / numberOfComparison;
            progressBar.setProgress(progress);
        }
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressBar.setProgress(1);
        threadPool.stop();
        System.out.println("Comparison Completed.");
    }

}
