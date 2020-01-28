#include <bits/stdc++.h>
#define deb(x) cout << #x << " is " << x << endl
using namespace std ;

class Include_Remover
{
public:
    vector<string> v ;
    string cstr, tstr ;
    bool ok, is_valid ;

    Include_Remover() {}

    Include_Remover(vector<string>v)
    {
        this->v = v ;
        cstr = "#include" ;
    }

    void Process()
    {
        int ln, cont ;
        FILE *fin ;
        fin = fopen("t_em_p.cpp","w");

        for(int i=0 ; i<v.size() ; i++)
        {
            tstr = "" ;
            ok = false ;
            is_valid = true ;
            cont = 0 ;

            ln = v[i].size() ;

            if(ln == 0)
                continue ;

            for(int j=0 ; j<ln ; j++)
            {
                if(ok)
                {
                    tstr += v[i][j] ;
                    cont++;
// cout<<tstr<< " " << tstr.size() << endl ;
                    if(cont == 8)
                    {
                        if(tstr == cstr)
                        {
                            is_valid = false ;
                            break;
                        }
                    }
                }

                else
                {
                    if(v[i][j] != ' ')
                    {
                        tstr += v[i][j] ;
                        ok = true ;
                        cont++;
                    }
                }
            }


            if(is_valid)
                fprintf(fin, "%s\n", tstr.c_str());
        }
        fclose(fin);
    }
};

class Edit_Distance
{
public:
    string str1, str2 ;
    int ln1, ln2 ;
    int **dp ;

    Edit_Distance() {}

    Edit_Distance(string str1,string str2)
    {
        this->str1 = str1 ;
        this->str2 = str2 ;

        ln1 = str1.length() ;
        ln2 = str2.length() ;

        dp = new int*[ln1+7] ;

        for(int i=0 ; i<ln1+7 ; i++)
            dp[i] = new int[ln2+7] ;

        for(int i=0 ; i<=ln2 ; i++)
            dp[0][i] = i ;

        for(int i=0 ; i<=ln1 ; i++)
            dp[i][0] = i ;
    }

    int cal_culate()
    {
        for(int i=1 ; i<=ln1 ; i++)
        {
            for(int j=1 ; j<=ln2 ; j++)
            {
                dp[i][j] = min(dp[i-1][j],dp[i][j-1]) + 1 ;

                if(str1[i-1] == str2[j-1])
                    dp[i][j] = min(dp[i][j],dp[i-1][j-1]) ;
            }
        }

        double mismatch = dp[ln1][ln2]/(1.0 * (ln1 + ln2)) ;
        double match = 1.0 - mismatch ;
        match = match*100.0 ;

        return round(match);
    }
};

vector<string>inp_vec ;
string inp_str ;

void pre_process_file(string name)
{
    ifstream cin(name); // argv[2] is input file
    inp_vec.clear();

    while(getline(cin,inp_str))
    {
        inp_vec.push_back(inp_str);
        // cout<<inp_str<<endl ;
    }

    Include_Remover *rmvr = new Include_Remover(inp_vec);
    rmvr->Process();

    system("g++ -E t_em_p.cpp -o a_f_t_e_r_process.txt 2> result.txt"); // command for replacing all macros , prerequisite: g++ global compiler

}

bool is_valid(char ch)
{
    if(ch >= 'A' && ch <= 'Z')
        return false ;

    if(ch >= 'a' && ch <= 'z')
        return false ;

    if(ch >= '0' && ch <= '9')
        return false ;

    if(ch == '_' || ch == '\n' || ch == ' ' || ch == '\r')
        return false ;

    return true ;
}
bool execute( std::string cmd )
{
    std::string file_name = "result.txt" ;
    std::system( ( cmd + " 2> " + file_name ).c_str() ) ; // redirect output to file
    ifstream cii("result.txt");
    string line;
    getline(cii, line);
    if(line == "") return true;
    return false;
}

void textToCpp(string input, string output)
{
    ifstream cin(input);
    ofstream couti(output);
    string line;
    int f = 0, compile = 0;
    int cnt = 0, check1 = 0;
    int prv = -1;
    vector<string> tmpVt, nwVt;
    while(getline(cin, line)){
        if(line.substr(0, 8) == "#include") f = 1;
        if(f){
            couti << line << endl;
            nwVt.push_back(line);
        }
        string tmp = "";
        for(char ch : line) if(ch!=' ') tmp += ch;
        if(!check1 && tmp.find("main(") != string::npos){
            check1 = 1;
            compile = 1;
        }
        for(int i = 0; i<tmp.size(); i++)
            if(tmp[i]=='{') {
                cnt++;
            } else if(tmp[i]=='}') cnt--;
        if(compile && cnt==0 && prv==1){
        	tmpVt = nwVt;
            string cmd = "g++ " + output;
            bool out = execute(cmd);
            // if(out) cout << "compile hoise" << endl;
            if(out) break;
        }
        prv = cnt;
    }
    ofstream coutAgain(output);
    for(string x : tmpVt)
    	coutAgain << x << endl;

}

string process_a_file(string name)
{
    pre_process_file(name);

    ifstream cin("a_f_t_e_r_process.txt");
    inp_vec.clear();
    int ln ;
    bool ok = false ;
    string tstr = "";


    while(getline(cin,inp_str))
    {
        ln = inp_str.size() ;

        if(ln == 0) continue;

        ok = true ;

        for(int i=0 ; i<ln ; i++)
        {
            if(inp_str[i] != ' ')
            {
                ok = (inp_str[i] != '#');
                break;
            }
        }

        if(ok)
        {
            for(int i=0 ; i<ln ; i++)
            {
                if(is_valid(inp_str[i]))
                    tstr += inp_str[i] ;
            }
        }
    }

//     cout<<"process " << name << " " << tstr << endl ;

    return tstr ;
}
string functionWiseSort(string input)
{
    vector<string> keepfunctions;
    string temp = "";
    int cnt = 0, start = 0;
    for(char ch : input)
    {
        temp += ch;
        if(ch == '{') {
            cnt++;
            start++;
        }
        else if(ch == '}') {
            cnt--;
        }
        if(cnt == 0 && start) {
            keepfunctions.push_back(temp);
            temp = "";
            start = 0;
        }
    }
    if(temp.size() > 0) {
        keepfunctions.push_back(temp);
    }
    sort(keepfunctions.begin(), keepfunctions.end());
    temp = "";
    for(string str : keepfunctions) {
        temp += str;
    }
    return temp;
}

vector< pair<string,string> >all_process_file ;
ofstream out("output.txt");
int main(int argc, char *argv[])
{
    //cout << "hi" << endl;
    string submission_id_path, submission_path, submission_id ;

    submission_id_path = "codes_to_check.txt";
    submission_id = "submission_id.txt";
    ifstream cinpath(submission_id_path);
    ifstream cinname(submission_id);
    int ln ;
    bool ok = true ;
    // deb("hi1");
// cout << "--> " << submission_id_path << endl ;
    while(getline(cinpath,submission_path) && getline(cinname, submission_id))
    {
        //printf("%s\n", submission_path.c_str());
        // cout << submission_id;
        out << submission_id << " " << submission_path << endl ;
        all_process_file.push_back(make_pair(submission_id,functionWiseSort(process_a_file(submission_path.c_str()))));
        // cout<<submission_id << " " << all_process_file.back().second<<endl ;
    }
    ofstream out("result.csv");

    ln = all_process_file.size() ;

    int maxi[ln];
    string name[ln];
    for(int i = 0; i<ln; i++)
        name[i] = "no file";
    memset(maxi, 0, sizeof maxi);
    for(int i=0 ; i<ln ; i++)
    {
        // printf("%s %s\n", all_process_file[i].first.c_str(), all_process_file[i].second.c_str());
        for(int j=0 ; j<ln ; j++)
        {
            if(i==j) continue;
            Edit_Distance *calculator = new Edit_Distance(all_process_file[i].second, all_process_file[j].second);
            int val = calculator->cal_culate();
            if(val > maxi[i]){
            	maxi[i] = val;
            	name[i] = all_process_file[j].first;
            }
        }
    }
    out << "Filename, Max Result, Url\n";
    for(int i = 0; i<ln; i++)
    {
    	out << all_process_file[i].first << ", " << maxi[i] << ", " << name[i] << endl;

    }

    return 0 ;
}
