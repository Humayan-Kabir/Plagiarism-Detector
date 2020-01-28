package plagiarismdetector;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCode {
    String fileName, name;
    String extension;
    String raw, rawKeepVariables, rawNoBracketLimit;
    ArrayList <String> code;
    LinkedHashMap<Long, ArrayList<Integer> > rawMap;
    LinkedHashMap<Long, ArrayList<Integer> > keepVariablesMap;
    LinkedHashMap<Long, ArrayList<Integer> > noBracketLimitMap;
    Parameters parameters;
    public SourceCode(String filename, String str, Parameters param, String name){
        this.name = name;
        param.K = 1;
        param.W = 1;
        this.parameters = param;
        this.fileName = filename;
        code = new ArrayList<String>();
        code.add(str);
        int i = filename.lastIndexOf('.');
        if (i > 0)
            extension = filename.substring(i + 1);
        else
            extension = "";
        preprocess();
    }
    public SourceCode(String fileName, Parameters parameters, String name) {
        try {
            this.name = name;
            this.fileName = fileName;
            File file = new File(fileName);

            int i = fileName.lastIndexOf('.');
            if (i > 0)
                extension = fileName.substring(i + 1);
            else
                extension = "";

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));

            code = new ArrayList<String>();
            String str;
            while ((str = in.readLine()) != null) {
                code.add(str.toLowerCase());
            }

            in.close();
            
            this.parameters = parameters;

            preprocess();
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    
    public void preprocess() {

        Preprocessor preprocessor;

        if (extension.equals("cpp") || extension.equals("c"))
            preprocessor = new CppPreprocessor();
        else if (extension.equals("java"))
            preprocessor = new JavaPreprocessor();
        else if (extension.equals("py"))
            preprocessor = new PythonPreprocessor();
        else
            preprocessor = new DefaultPreprocessor();

        raw = preprocessor.preprocess(code, false, true);
        rawKeepVariables = preprocessor.preprocess(code, true, true);
        rawNoBracketLimit = preprocessor.preprocess(code, true, false);
//        System.out.println("raw is ........ ");
//        System.out.println(raw);
        rawMap = getFingerprints2(raw, parameters);
        keepVariablesMap = getFingerprints2(rawKeepVariables, parameters);
        noBracketLimitMap = getFingerprints2(rawNoBracketLimit, parameters);
        
    }
    public double findmatch(SourceCode a)
    {
        SourceCode me = this;
        double score = Main.process(me, a, parameters) + Main.process(a, me, parameters);
        double scoreKeepingVariables = Main.processKeepingVariables(me, a, parameters) + Main.processKeepingVariables(a, me, parameters);
        double scoreWithoutBracketLimit = Main.processWithoutBracketLimit(me, a, parameters) + Main.processWithoutBracketLimit(a, me, parameters);

        double maxi = Math.max(score, Math.max(scoreKeepingVariables, scoreWithoutBracketLimit)) / 2.0;
        return maxi;
                
    }

    public LinkedHashMap<Long, ArrayList<Integer> > getFingerprints(String raw, Parameters parameters) {
        int K = parameters.K;
        LinkedHashMap<Long, ArrayList<Integer> > map = new LinkedHashMap<Long, ArrayList<Integer>>();

        // Rolling hashes of length K
        // of the form ( s[i] * p^(k-1) + s[i+1] * p^(k-2) + ... + s[i + K - 2] * p + s[i-K+1] )
        long currentHash = 0;
        int p = parameters.p;
        long pp = 1L; // will be p^(k-1)
        for (int i = 0 ; i < K - 1 ; i ++) {
            pp *= p;
        }
        for (int i = 0 ; i < raw.length() ; i ++) {
            if (i < K) {
                currentHash = currentHash * p + raw.charAt(i);
            } else {
                currentHash -= raw.charAt(i - K) * pp;
                currentHash = currentHash * p + raw.charAt(i);
            }
            if (i >= K - 1) {
                if (!map.containsKey(currentHash)) {
                    map.put(currentHash, new ArrayList<Integer>());
                }
                map.get(currentHash).add(i);
            }
        }
        return map;
    }
    public LinkedHashMap<Long, ArrayList<Integer> > getFingerprints2(String raw, Parameters parameters ) {
     
        int w = parameters.getW(), k = parameters.getK();
        LinkedHashMap<Long, ArrayList<Integer> > map = new LinkedHashMap<Long, ArrayList<Integer>>();
        
        ArrayList<Long> ara = new ArrayList<Long>();
        
        long currentHash = 0;
        int p = parameters.p;
        long pp = 1L; 
        for (int i = 0 ; i < k - 1 ; i ++) {
            pp *= p;
        }
        for (int i = 0 ; i < raw.length() ; i ++) {
            if (i < k) {
                currentHash = currentHash * p + raw.charAt(i);
            } else {
                currentHash -= raw.charAt(i - k) * pp;
                currentHash = currentHash * p + raw.charAt(i);
            }
            if (i >= k - 1) {
                ara.add(currentHash);
            }
        }
        Deque<Integer> dq = new ArrayDeque<Integer>();
        for(int i = 0; i<ara.size(); i++)
        {
            Long val = ara.get(i);
            while(dq.isEmpty()==false && ara.get(dq.getLast()) >= val) dq.removeLast();
            while(dq.isEmpty()==false && dq.getFirst() <= i-w) dq.removeFirst();
            dq.addLast(i);
            if(i < w-1) continue;
            if(map.containsKey(ara.get(dq.getFirst())) == false){
                map.put(ara.get(dq.getFirst()), new ArrayList<Integer>());
            }
            map.get(ara.get(dq.getFirst())).add(dq.getFirst());
        }
        return map;
    }
   
    
}
