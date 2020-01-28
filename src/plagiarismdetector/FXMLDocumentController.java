
package plagiarismdetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author humay
 */
public class FXMLDocumentController implements Initializable {
    
    public static File directoryNameglobal = null;
    private Label label;
    @FXML
    private Button browse;
    @FXML
    private Button check;
    @FXML
    private TextField directoryname;
    @FXML
    private ProgressIndicator progressbar;
    @FXML
    private Circle ball0;
    @FXML
    private Circle ball3;
    @FXML
    private Circle ball2;
    @FXML
    private Circle ball1;
    private Circle []ball;
    @FXML
    private Button goButton;
    private ComboBox<Integer> combobox;
    public static int comboValue = 1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ball = new Circle[4];
        ball[0] = ball0;
        ball[1] = ball1;
        ball[2] = ball2;
        ball[3] = ball3;
        map.put("ball0", new Integer(0));
        map.put("ball1", new Integer(1));
        map.put("ball2", new Integer(2));
        map.put("ball3", new Integer(3));
        
    }    
    
    @FXML
    private void getDirectory(ActionEvent event) {
        DirectoryChooser directory = new DirectoryChooser();
        File selectedDirectory = directory.showDialog(null);
        if(selectedDirectory == null) return;
        directoryname.setText(selectedDirectory.toString());
        directoryNameglobal = selectedDirectory;
        
    }
    private int endTask = 0;
    private void runTask() {
        Task longTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                progressbar.setVisible(true);
                check.setVisible(false);
                // Process p = Runtime.getRuntime().exec("./main");
                // p.waitFor();
                Main mn = new Main();
                mn.run();
                
                return null;
            }
        };
        longTask.setOnFailed(e -> {
            Exception ex = (Exception)longTask.getException();
            ex.printStackTrace();
        });
        longTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                progressbar.setVisible(false);
                check.setVisible(true);
                
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getResource("TableView.fxml"));
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Result");
                stage.setResizable(false);
                stage.show();
            }
        });
        
        new Thread(longTask).start();
    }    
    
    @FXML
    private void checkPlagiarism(ActionEvent event) throws IOException  {
        
        PrintWriter writer_path = null , writer_name = null;
        try {
            File selectedDirectory = directoryNameglobal ;
            if(selectedDirectory == null) return;
            File []files = selectedDirectory.listFiles();
            writer_path = new PrintWriter("codes_to_check.txt", "UTF-8");
            writer_name = new PrintWriter("submission_id.txt", "UTF-8");
            for(File file : files)
            {
                if(file.isDirectory() == false){
                    System.out.print(file.getName());
                    if(file.getName().length() >= 4)
                        System.out.println(" " + file.getName().substring(file.getName().length()-4));
                    else System.out.println("");
                    if(file.getName().length()>=4 && file.getName().substring(file.getName().length()-4).equalsIgnoreCase(".cpp"));
                    else if(file.getName().length()>=3 && file.getName().substring(file.getName().length()-3).equalsIgnoreCase(".py")==true);
                    else if(file.getName().length()>=5 && file.getName().substring(file.getName().length()-5).equalsIgnoreCase(".java")==true);
                    else continue;
                    writer_path.println(selectedDirectory.toString() + "\\" + file.getName());
                    writer_name.println(file.getName());
                } 
            }
            writer_name.close();
            writer_path.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            
            runTask();
            
            
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private double []diffx = new double[5];
    private double []diffy = new double[5];
    private HashMap<String, Integer> map = new HashMap<String, Integer>();
    
    private int idx;
    @FXML
    private void mouseDragged(MouseEvent event) {
        ball[idx].setCenterX(event.getX() + diffx[idx]);
        ball[idx].setCenterY(event.getY() + diffy[idx]);
        //System.out.println("" + event.getX() + " , " + event.getY());
        //System.out.println( ball[idx].getLayoutX() + " -lay- " + ball[idx].getLayoutY());
        for(int i = 0; i<4; i++)
        {
            if(i == idx) continue;
            double x1 = ball[idx].getCenterX() + ball[idx].getLayoutX();
            double y1 = ball[idx].getCenterY() + ball[idx].getLayoutY();
            double r1 = ball[idx].getRadius();
            
            double x2 = ball[i].getCenterX() + ball[i].getLayoutX();
            double y2 = ball[i].getCenterY() + ball[i].getLayoutY();
            double r2 = ball[i].getRadius();
            //System.out.println(i + " - " + x2 + " , " + y2);
            double dist = Math.sqrt((x2-x1) * (x2-x1) + (y2-y1) * (y2-y1));
            double mov = r1+r2-dist;
            if(mov <= 0.0) continue;
            //System.out.println(dist + " , " + mov + " , " + r1 + " , " + r2 );
            x2 = x2 + (x2-x1)/dist*mov;
            y2 = y2 + (y2-y1)/dist*mov;
            ball[i].setCenterX(x2 - ball[i].getLayoutX());
            ball[i].setCenterY(y2 - ball[i].getLayoutY());
        }
    }
    
    

    @FXML
    private void mousePressed(MouseEvent event) {
        String id = event.getPickResult().getIntersectedNode().getId();
        idx = map.get(id);
        diffx[idx] = ball[idx].getCenterX() - event.getX();
        diffy[idx] = ball[idx].getCenterY() - event.getY();
    }

    @FXML
    private void mouseMoved(MouseEvent event) {
        // nothing just fun :)
    }

    @FXML
    private void goTOWeb(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ShowResults.fxml"));
        Scene sc = new Scene(root);
        Stage st = new Stage();
        st.setScene(sc);
        st.setTitle("Show match!");
        st.setResizable(false);
        st.show();
    }
}
