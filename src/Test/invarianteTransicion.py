import re
'''
    param: name of file
    return: disparos as string
    e.g:
'''
def readFile(filename):
    disparos=''
    with open(filename, 'rt') as f:
        data = f.readlines()
    for line in data:
        if line.__contains__('disparo'):
            line_arr=line.split('=')
            disparo=int(line_arr[1])
            disparos+='T'+'{:X}'.format(disparo)

    return disparos

'''
    Run re.subn iterative
    re.subn(pattern, repl, string, count=0, flags=0)
    brief:  Return the string obtained by replacing the leftmost non-overlapping occurrences of pattern
            in string by the replacement repl. If the pattern isn’t found, string is returned unchanged.
    return: a tuple (new_string, number_of_subs_made).
'''
def tinv_matcher():
    logfilenameI='./log.txt'
    logfilenameO='./logTinv.txt'
    pattern='(T0)(.*?)((T1)(.*?)((T5)(.*?)(T7)(.*?)(T3)(.*?)(T4)(.*?)(T8)|(T6)(.*?)(T3)(.*?)(T4)|(T3)(.*?)((T4)(.*?)(T6)|(T6)(.*?)(T4)))|(T2)(.*?)((TB)(.*?)(TC)(.*?)(T9)(.*?)(TA)(.*?)(TE)|(TD)(.*?)(T9)(.*?)(TA)|(T9)(.*?)((TA)(.*?)(TD)|(TD)(.*?)(TA))))'
    repl='\g<2>\g<5>\g<8>\g<10>\g<12>\g<14>\g<17>\g<19>\g<22>\g<25>\g<28>\g<31>\g<34>\g<36>\g<38>\g<40>\g<43>\g<45>\g<48>\g<51>\g<54>'
    string=''
    terminate=False
    iteration=0

    # msg
    print("Generating file : "+logfilenameO+" ...")

    # Disparos to string (i.e. T0T1T6T0T1T6T0T2T13T0T2T13T0T1T6...)
    string=readFile(logfilenameI)

    # Create logTinv.txt
    logfile = open(logfilenameO, "a")
    logfile.truncate(0) # Clear file
    logfile.write("INPUT: "+string+'\n\n') # First Line

    while(not terminate):
        print("Iteration: "+str(iteration))
        line = re.subn(pattern, repl, string)
        matches=int(line[1])
        string=str(line[0])
        leng=len(string)
        logfile.write('| ITERATION{:>5} | LENGHT{:>6} | MATCHES{:>5}'.format(str(iteration), str(leng), str(matches))+" | OUTPUT "+string+'\n')
        iteration+=1
        if not matches:
            terminate=True
    print("Check File !!! => "+logfilenameO)
    logfile.write("\n MEMO T-INVARIANTS:\n{T0,T1,T5,T7,T3,T4,T8}\n{T0,T1,T6,T3,T4}\n{T0,T2,TB,TC,T9,TA,TE}\n{T0,T2,TD,T9,TA}")
    logfile.close



if __name__ == "__main__":
    tinv_matcher()
