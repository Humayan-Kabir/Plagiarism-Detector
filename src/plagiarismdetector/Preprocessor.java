package plagiarismdetector;

import java.util.ArrayList;

/**
 * Author: humayan maruf ovi
 * Date: 4.1.19
 * Time: 07:37
 */
public interface Preprocessor {

    public String preprocess(ArrayList <String> code, boolean keepVariables, boolean useLimitingBracketDepth);

}
