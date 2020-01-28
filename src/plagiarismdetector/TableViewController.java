package plagiarismdetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class TableViewController implements Initializable {

    @FXML
    private TableView<Person> tableview;
    @FXML
    private TableColumn<Person, String> person1;
    @FXML
    private TableColumn<Person, String> person2;
    @FXML
    private TableColumn<Person, Double> matching;

    ObservableList<Person> list = FXCollections.observableArrayList();
    @FXML
    private MenuItem manuSaveAs;
    @FXML
    private ToggleGroup group;
    @FXML
    private RadioButton togAll;
    @FXML
    private RadioButton togEnter;
    @FXML
    private Button subNo;
    @FXML
    private TextField enterNo;
    @FXML
    private Label statusLabel;
    public static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    @FXML
    private MenuButton sortby1st;
    @FXML
    private MenuButton sortby2nd;
    @FXML
    private MenuButton sortbyresult;
    ObservableList<Person> getResult(int len)
    {
        ObservableList<Person> lst = FXCollections.observableArrayList();
        BufferedReader buffer = null;
        ArrayList< ArrayList< ArrayList< Person > > > ara = Main.results;
        for(int i = 0; i<3; i++)
        {
            for(ArrayList<Person> x : ara.get(i)){
                int c = 0;
                for(Person p : x){
                    if(c >= len) break;
                    lst.add(p);
                    c++;
                }
            }
        }
        
        return lst;
    }
    

    
    private void printRow(Person item) throws IOException {
        FXMLLoader fx = new FXMLLoader(getClass().getResource("ShowResults.fxml"));
        fx.setController(new ShowResultsController(item));
        Parent root = fx.load();
        Scene sc = new Scene(root);
        Stage st = new Stage();
        st.setScene(sc);
        st.setTitle("Show match!");
        st.setResizable(false);
        st.show();
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        person1.setCellValueFactory(new PropertyValueFactory<Person, String>("person1"));
        person2.setCellValueFactory(new PropertyValueFactory<Person, String>("person2"));
        matching.setCellValueFactory(new PropertyValueFactory<Person, Double>("matching"));
        person1.setStyle( "-fx-alignment: CENTER;");
        person2.setStyle( "-fx-alignment: CENTER;");
        matching.setStyle( "-fx-alignment: CENTER;");
        list = getResult(2147483647);
        if(list.size() == 0) return;
        tableview.setItems(list);
        tableview.setRowFactory(tv -> {
        TableRow<Person> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY 
                     && event.getClickCount() == 2) {
                    
                    Person clickedRow = row.getItem();
                    try {
                        printRow(clickedRow);
                    } catch (IOException ex) {
                        Logger.getLogger(TableViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return row ;
        });
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
        public void changed(ObservableValue<? extends Toggle> ov,
            Toggle old_toggle, Toggle new_toggle) {
          if (group.getSelectedToggle() != null) {
            if(((Control)group.getSelectedToggle()).getId().equals("togAll")){
                //System.out.println("ki khobor?");
                list = getResult(2147483647);
                tableview.setItems(list);
                statusLabel.setText("All pairs are showing");
                enterNo.setDisable(true);
                subNo.setDisable(true);
            } else{
                statusLabel.setText("Please Enter a number and click on 'Submit' to view!");
                enterNo.setDisable(false);
                subNo.setDisable(false);
            }
          }
        }
        });
        
    }    

    @FXML
    private void menuSaveAs(ActionEvent event) throws FileNotFoundException {
        FileChooser filechooser = new FileChooser();
        FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        filechooser.getExtensionFilters().add(ext);
        File file = filechooser.showSaveDialog(null);
        if(file == null) return;
        list = tableview.getItems();
        PrintWriter writer = new PrintWriter(file.getPath());
        writer.println("1st Person, 2nd Person, Result");
        for(Person p : list)
        {
            writer.println(p.person1 + ", " + p.person2 + ", " + p.matching);
        }
        writer.close();
    }

    @FXML
    private void exitTable(ActionEvent event) {
        Stage stage = (Stage)tableview.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void setNoButtonPressed(ActionEvent event) {
        String num = enterNo.getText();
        if(isInteger(num) == false){
            statusLabel.setText("Please Enter a valid number!");
        } else{
            int val = Integer.parseInt(num);
            list = getResult(val);
            tableview.setItems(list);
            String st = String.format("Showing maximum %d matches for every source code!", val);
            statusLabel.setText(st);
        }
    }

    @FXML
    private void sort1stasc(ActionEvent event) {
        list.sort(new Comparator<Person>(){
                        @Override
                        public int compare(Person o1, Person o2) {
                            return o1.person1.compareTo(o2.person1);                        }
                        
                    });
        
    }

    @FXML
    private void sort1stdesc(ActionEvent event) {
        list.sort(new Comparator<Person>(){
                        @Override
                        public int compare(Person o1, Person o2) {
                            return o2.person1.compareTo(o1.person1);                        }
                        
                    });
    }

    @FXML
    private void sort2ndasc(ActionEvent event) {
        list.sort(new Comparator<Person>(){
                        @Override
                        public int compare(Person o1, Person o2) {
                            return o1.person2.compareTo(o2.person2);                        }
                        
                    });
    }

    @FXML
    private void sort2nddesc(ActionEvent event) {
        list.sort(new Comparator<Person>(){
                        @Override
                        public int compare(Person o1, Person o2) {
                            return o2.person2.compareTo(o1.person2);                        }
                        
                    });
    }

    @FXML
    private void sortresasc(ActionEvent event) {
        list.sort(new Comparator<Person>(){
                        @Override
                        public int compare(Person o1, Person o2) {
                            if(o1.matching < o2.matching) return -1;
                            if(o1.matching > o2.matching) return 1;   
                            return 0;
                        }
                        
                    });
    }

    @FXML
    private void sortresdesc(ActionEvent event) {
        list.sort(new Comparator<Person>(){
                        @Override
                        public int compare(Person o1, Person o2) {
                            if(o1.matching < o2.matching) return 1;
                            if(o1.matching > o2.matching) return -1;   
                            return 0;
                        }
                        
                    });
    }
    
}
