package plagiarismdetector;

import java.io.*;
import java.util.*;


public class Main implements Runnable {

   
    public static ArrayList< ArrayList< ArrayList<Person> > > results;
    int N;
    ArrayList<SourceCode> sourceCodes;
    private PrintWriter writer;
    public void solveFor(String ext) throws FileNotFoundException, IOException
    {
        int id = 0;
        if(ext == ".py") id = 1;
        else if(ext == ".java") id = 2;
        Parameters parameters = new Parameters();
        BufferedReader readerPath = new BufferedReader(new FileReader("codes_to_check.txt"));
        BufferedReader readerName = new BufferedReader(new FileReader("submission_id.txt"));
        sourceCodes = new ArrayList<SourceCode>();
        String str;
        N = 0;
        for (int i = 0 ; (str=readerPath.readLine())!=null ; i ++) {
            
            String name = readerName.readLine();
            if(name.length() < ext.length() || name.substring(name.length()-ext.length()).equals(ext)==false) continue;
            N++;
            sourceCodes.add(new SourceCode(str, parameters, name));
        }
                
        Pair [][]ara = new Pair[N][N];
        
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i == j) {
                    ara[i][j] = new Pair(-100.0, i);
                    continue;
                }
                double score = process(sourceCodes.get(i), sourceCodes.get(j), parameters) + process(sourceCodes.get(j), sourceCodes.get(i), parameters);
                double scoreKeepingVariables = processKeepingVariables(sourceCodes.get(i), sourceCodes.get(j), parameters) + processKeepingVariables(sourceCodes.get(j), sourceCodes.get(i), parameters);
                double scoreWithoutBracketLimit = processWithoutBracketLimit(sourceCodes.get(i), sourceCodes.get(j), parameters) + processWithoutBracketLimit(sourceCodes.get(j), sourceCodes.get(i), parameters);

                double maxi = Math.max(score, Math.max(scoreKeepingVariables, scoreWithoutBracketLimit)) / 2.0;
//                System.out.println("....." + maxi + "....." + score + " " +scoreKeepingVariables+" "+scoreWithoutBracketLimit);
                ara[i][j] = new Pair(maxi, j);
//                System.out.println("");
            }
            Arrays.sort(ara[i], 0, N,
                    
                    new Comparator<Pair>(){
                        @Override
                        public int compare(Pair o1, Pair o2) {
                            if(o1.result == o2.result) {
                                if(o1.idx < o2.idx) return 1;
                                if(o1.idx > o2.idx) return -1;
                                return 0;
                            }
                            if(o1.result < o2.result) return 1;
                            return -1;
                        }
                        
                    });
            results.get(id).add(new ArrayList<>());
            for(int j = 0; j<N-1; j++)
                results.get(id).get(i).add(new Person(sourceCodes.get(i).name, sourceCodes.get(ara[i][j].idx).name, ara[i][j].result*100.0));
        }
    }
    /* Main method */
    public void solve () throws IOException {
        results =  new ArrayList<ArrayList<ArrayList<Person> > >();
        results.add(new ArrayList< ArrayList<Person> > ());
        results.add(new ArrayList< ArrayList<Person> > ());
        results.add(new ArrayList< ArrayList<Person> > ());
        solveFor(".cpp");
        solveFor(".py");
        solveFor(".java");
        
        
        
    }

    public static double process(SourceCode l, SourceCode r, Parameters parameters) {
        LinkedHashMap<Long, ArrayList<Integer>> lMap = l.rawMap;
        LinkedHashMap<Long, ArrayList<Integer>> rMap = r.rawMap;
        int total = lMap.size();
        int matches = 0;
        for (Map.Entry<Long, ArrayList<Integer> > entry : lMap.entrySet()) {
            if (rMap.containsKey(entry.getKey())) {
                matches ++;
            }
        }

        
        if(total==0) return 0;
        return 1.0 * matches / total;
    }

    public static double processKeepingVariables(SourceCode l, SourceCode r, Parameters parameters) {
        LinkedHashMap<Long, ArrayList<Integer>> lMap = l.keepVariablesMap;
        LinkedHashMap<Long, ArrayList<Integer>> rMap = r.keepVariablesMap;
        int total = lMap.size();
        int matches = 0;
        for (Map.Entry<Long, ArrayList<Integer> > entry : lMap.entrySet()) {
            if (rMap.containsKey(entry.getKey())) {
                matches ++;
            }
        }

        
        if(total==0) return 0;
        return 1.0 * matches / total;
    }

    public static double processWithoutBracketLimit(SourceCode l, SourceCode r, Parameters parameters) {
        LinkedHashMap<Long, ArrayList<Integer>> lMap = l.noBracketLimitMap;
        LinkedHashMap<Long, ArrayList<Integer>> rMap = r.noBracketLimitMap;
        int total = lMap.size();
        int matches = 0;
        for (Map.Entry<Long, ArrayList<Integer> > entry : lMap.entrySet()) {
            if (rMap.containsKey(entry.getKey())) {
                matches ++;
            }
        }

        
        if(total==0) return 0.0;
        return 1.0 * matches / total;
    }



    public Main() {

    }

    @Override
    public void run() {
        try {
            solve();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            
        }
    }



    private static class Pair {
        double result;
        int idx;
        public Pair(double res, int id) {
            result = res;
            idx = id;
        }
    }
}
