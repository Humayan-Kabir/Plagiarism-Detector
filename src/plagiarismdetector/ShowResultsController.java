/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagiarismdetector;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Shahriar
 */
public class ShowResultsController implements Initializable {
    private Person Showingitem;
    @FXML
    private Slider slider;
    @FXML
    private Button slidersubmit;
    @FXML
    private TextField textSensitivity;
    @FXML
    private Label labelofcode0;
    @FXML
    private Label labelofcode1;
    @FXML
    private Label labelofres, lineMatched;
    
    public ShowResultsController() {
    }
    ArrayList<String> st0, st1;
    public ShowResultsController(Person item)  {
        Showingitem = item;
        System.out.println(item.person1 + " " + item.person2 + " " + item.matching);
        File dir = FXMLDocumentController.directoryNameglobal;
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(dir.getPath() + "\\" + item.person1) );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShowResultsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        String line = null;
        System.out.println(dir.getPath() + "\\" + item.person1);
        st0 = new ArrayList<>();
        st1 = new ArrayList<>();
        try {
            while((line = bf.readLine()) != null){
                st0.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(ShowResultsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            bf = new BufferedReader(new FileReader(dir.getPath() + "\\" + item.person2) );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShowResultsController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        try {
            while((line = bf.readLine()) != null){
                st1.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(ShowResultsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("helllllllo");
        
    }
    
    @FXML
    private WebView code0;
    public static final String escapeHTML(String s){
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
           char c = s.charAt(i);
           switch (c) {
              case '<': sb.append("&lt;"); break;
              case '>': sb.append("&gt;"); break;
              case '&': sb.append("&amp;"); break;
              case '"': sb.append("&quot;"); break;
              case 'à': sb.append("&agrave;");break;
              case 'À': sb.append("&Agrave;");break;
              case 'â': sb.append("&acirc;");break;
              case 'Â': sb.append("&Acirc;");break;
              case 'ä': sb.append("&auml;");break;
              case 'Ä': sb.append("&Auml;");break;
              case 'å': sb.append("&aring;");break;
              case 'Å': sb.append("&Aring;");break;
              case 'æ': sb.append("&aelig;");break;
              case 'Æ': sb.append("&AElig;");break;
              case 'ç': sb.append("&ccedil;");break;
              case 'Ç': sb.append("&Ccedil;");break;
              case 'é': sb.append("&eacute;");break;
              case 'É': sb.append("&Eacute;");break;
              case 'è': sb.append("&egrave;");break;
              case 'È': sb.append("&Egrave;");break;
              case 'ê': sb.append("&ecirc;");break;
              case 'Ê': sb.append("&Ecirc;");break;
              case 'ë': sb.append("&euml;");break;
              case 'Ë': sb.append("&Euml;");break;
              case 'ï': sb.append("&iuml;");break;
              case 'Ï': sb.append("&Iuml;");break;
              case 'ô': sb.append("&ocirc;");break;
              case 'Ô': sb.append("&Ocirc;");break;
              case 'ö': sb.append("&ouml;");break;
              case 'Ö': sb.append("&Ouml;");break;
              case 'ø': sb.append("&oslash;");break;
              case 'Ø': sb.append("&Oslash;");break;
              case 'ß': sb.append("&szlig;");break;
              case 'ù': sb.append("&ugrave;");break;
              case 'Ù': sb.append("&Ugrave;");break;         
              case 'û': sb.append("&ucirc;");break;         
              case 'Û': sb.append("&Ucirc;");break;
              case 'ü': sb.append("&uuml;");break;
              case 'Ü': sb.append("&Uuml;");break;
              case '®': sb.append("&reg;");break;         
              case '©': sb.append("&copy;");break;   
              case '€': sb.append("&euro;"); break; 
              case '\n': sb.append("<br>"); break;
              case '\t': sb.append("&nbsp;&nbsp;&nbsp;&nbsp;"); break;
              // be carefull with this one (non-breaking whitee space)
              case ' ': sb.append("&nbsp;");break;         

              default:  sb.append(c); break;
           }
        }
        return sb.toString();
     }
    public static String stringToHTMLString(String string) {
        StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++)
            {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss 
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                    }
                else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                    }
                }
            else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("&lt;br/&gt;");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160 )
                        // nothing special only 7 Bit
                        sb.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(new Integer(ci).toString());
                        sb.append(';');
                        }
                    }
                }
            }
        return sb.toString();
    }
    @FXML
    private WebView code1;
    
    String[] colorLine(String []st, int linenum)
    {
        String []ret = new String[st.length];
        for(int i = 0; i<st.length; i++){
            ret[i] = st[i];
        }
        ret[linenum] = "<p style=\"background-color: yellow;\">" + st[linenum] + "</p>";
        
        return ret;
    }
    int [][]dp = new int[1003][1003];
    int [][]isEqual = new int[1003][1003];
    int rec(int l, int r)
    {
        if(l == -1 || r == -1) return 0;
        if(dp[l][r] != -1) return dp[l][r];
        if(isEqual[l][r] == 1){
            return dp[l][r] = 1 + rec(l-1 , r-1);
        } else{
            int ret = rec(l-1, r);
            ret = Math.max(ret, rec(l, r-1));
            return dp[l][r] = ret; 
        }
    }
    ArrayList<Integer> sol0, sol1;
    void print(int l, int r)
    {
        if(l == -1 || r == -1) {
            return;
        }
        if(isEqual[l][r]==1){
            print(l-1, r-1);
            sol0.add(l);
            sol1.add(r);
            return;
        }
        if(rec(l, r) == rec(l-1, r)){
            print(l-1, r);
        } else print(l, r-1);
    }
    String getHtml(String []st)
    {
        String allText = "";
        for(int i = 0; i<st.length; i++)
            allText = allText + "<br>" + escapeHTML(st[i]);
        return "<html><body>"+allText+"</body> </html>";
    }
    String[] getTextArray(ArrayList<String> st)
    {
        String []s = new String[st.size()];
        for(int i = 0; i<st.size(); i++)
            s[i] = st.get(i);
        return s;
    }
    void show_text(WebEngine view, String[] st, ArrayList<Integer> sol)
    {
        String alltext = "";
        
        for(int i = 0; i<st.length; i++)
        {
            st[i] = escapeHTML(st[i]);
        }
        for(int i = 0; i<sol.size(); i++){
            st[sol.get(i)] = "<div style=\"background-color: yellow; display:inline-block;\">" + st[sol.get(i)] + "</div>";
        }
        for(int i = 0; i<st.length; i++)
            alltext = alltext + "<br>" + st[i];
        view.loadContent("<html><body>"+alltext+"</body> </html>");
    }
    WebEngine web0;
    WebEngine web1;
    SourceCode []src0;
    SourceCode []src1;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(40);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);
        slider.valueProperty().addListener( 
             new ChangeListener<Number>() { 
  
            public void changed(ObservableValue <? extends Number >  
                      observable, Number oldValue, Number newValue) 
            { 
  
                textSensitivity.setText(String.format("%.02f%%", slider.getValue()));
            } 
        }); 
        
        labelofcode0.setText(Showingitem.person1);
        labelofcode1.setText(Showingitem.person2);
        String st = String.format("%.02f%%", Showingitem.matching).toString();
        labelofres.setText(st);
        
  
        
        web0 = code0.getEngine();
        web1 = code1.getEngine();
        src0 = new SourceCode[st0.size()];
        src1 = new SourceCode[st1.size()];
        Parameters pr = new Parameters();
        
        File dir = FXMLDocumentController.directoryNameglobal;
        for(int i = 0; i<st0.size(); i++){
            src0[i] = new SourceCode(dir.getPath() + "\\" + Showingitem.person1, st0.get(i), pr, Showingitem.person1);
        }
        
        for(int i = 0; i<st1.size(); i++){
            //
            src1[i] = new SourceCode(dir.getPath() + "\\" + Showingitem.person2, st1.get(i), pr, Showingitem.person2);
        }
        // System.out.println("hkello");
        for(int i = 0; i<st0.size(); i++)
        {
            for(int j = 0; j<st1.size(); j++)
            {
                double match = src0[i].findmatch(src1[j]);
                
                sensitivity = 0.75;
                slider.setValue(sensitivity * 100);
                if(match >= sensitivity){
                    isEqual[i][j] = 1;
                } else{
                    isEqual[i][j] = 0;
                }
                dp[i][j] = -1;
            }
        }
        sol0 = new ArrayList<>();
        sol1 = new ArrayList<>();
        
        print(src0.length-1, src1.length-1);
        lineMatched.setText(String.format("%d line matched!", sol0.size()).toString());
//        System.out.println(sol0.size());
//        System.out.println(sol1.size());
        
        show_text(web0, getTextArray(st0), sol0);
        show_text(web1, getTextArray(st1), sol1);
    }    
    double sensitivity;
    @FXML
    private void sliderapply(ActionEvent event) {
        for(int i = 0; i<st0.size(); i++)
        {
            for(int j = 0; j<st1.size(); j++)
            {
                double match = src0[i].findmatch(src1[j]);
                sensitivity = slider.getValue()/100.0;
                if(match >= sensitivity){
                    isEqual[i][j] = 1;
                } else{
                    isEqual[i][j] = 0;
                }
                dp[i][j] = -1;
            }
        }
        sol0 = new ArrayList<>();
        sol1 = new ArrayList<>();
        
        print(src0.length-1, src1.length-1);
        lineMatched.setText(String.format("%d line matched!", sol0.size()).toString());
        show_text(web0, getTextArray(st0), sol0);
        show_text(web1, getTextArray(st1), sol1);
    }
    
}
