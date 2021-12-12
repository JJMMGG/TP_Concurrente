import re
'''
    Parameters
    ----------
    nombre_archivo :
                    El nombre del archivo
    Returns
    -------
    string
        Contiene las transiciones disparadas en la simulacion
'''
def leer_archivo(nombre_archivo):
    transiciones=''
    with open(nombre_archivo, 'rt') as f:
        info = f.readlines()
    for linea in info:
        if linea.__contains__('transicion'):
            line_arr=linea.split(' : ')
            transicion=int(line_arr[1])
            if(transicion==16):
                transiciones+='TX'
            else:
                transiciones+='T'+'{:X}'.format(transicion)

    return transiciones

'''
    Run re.subn iterative
    re.subn(pattern, repl, string, count=0, flags=0)
    brief:  Return the string obtained by replacing the leftmost non-overlapping occurrences of pattern
            in string by the replacement repl. If the pattern isn’t found, string is returned unchanged.
    return: a tuple (new_string, number_of_subs_made).
'''
def tinv_matcher():
    archivoEntrada='../../run.log'
    archivoSalida='./invariantes.log'
    patron='((((T0)(.*?)(T3)(.*?))|((T1)(.*?)(T4)(.*?))|((T2)(.*?)(T5)(.*?)))(((T6)(.*?)(TB)(.*?)())|((T7)(.*?)(TC)(.*?)))(((TD)(.*?)(TF))|((TE)(.*?)(TX))))|(((T8)(.*?)((T9)|(TA))))'
    #patron correcto
    #((((T0)(.*?)(T3)(.*?))|((T1)(.*?)(T4)(.*?))|((T2)(.*?)(T5)(.*?)))(((T6)(.*?)(TB)(.*?)())|((T7)(.*?)(TC)(.*?)))(((TD)(.*?)(TF))|((TE)(.*?)(TX))))|(((T8)(.*?)((T9)|(TA))))
    repl='\g<5>\g<7>\g<10>\g<12>\g<15>\g<17>\g<21>\g<23>\g<27>\g<29>\g<33>\g<37>\g<42>'
    transiciones=''
    terminate=False
    iteration=0
    #transiciones='T1T0T2T4T1T1T1T6TBT4T6T5T0T6T1T2T0TET5TBT7T3T2T6T3T7T2T2T5T2T7T0TXT0TDTFTCT4TET1TXT7T4TCTDTBT6T3T1T7TFTDTFT4TBT1T7T3TDTFT0T6TCT4T7TDT0T1T4TBT1T6T3T0T6T5T7T4T2TFT6T5TET7TXT4T2TCTDT6T1T5TCTFT2T6TETBT4T1T7T5T7T5T7TXT1T2TET5T2T7T2T3T7T4TCT0T7TXTET1T4TXTCTET6TCT4T6T1T1T5T6T4T1T2T7T4T6T1T3T0T7T5T6T5T7T2TXT5T6TDT5T7TFT4T7T4TBTDTCT2T2T2T1TFT7TETCT5T7T5T2T2T6T3TXT7T0T4TET1T7TBT3TXT1T7T5T7TDT2TBT4T6T5T6T2T5T2T7T5T7T5T7T5T7T4T7T2T5T2T7T5T7T4T7T3T7TFTETXT3T7T2T5T7T2T2T2T5T7T5T7T5T7T0T0T0T1T1T1T3T6T4T6T4T6T3T6T3T6T4T6T2T2T2T5T6T1T4T6T5T0T6T3T6T5T6T0T0T1T4T6T2T5T6T3T6T3T6T2T2T1T1T4T6T5T6T4T6T5T6T8TCTDTFTCTDTFTATCTDTFTBTETXTCTETXTCTETXTCTDTFTBTETXTBTDTFTCTDTFTCTETXTBTDTFTCTETXTBTDTFTCTETXTBTDTFTCTETXTCTDTFTCTDTFTBTETXTCTETXTCTETXTCTDTFTBTETXTBTDTFTBTDTFTBTETXTBTETXTBTETXTBTETXTBTDTFTCTDTFTCTETXTBTETXTBTDTFTCTETXTCTDTFTBTDTFTBTETXTBTETXTCTETXTCTETXTBTDTFTBTDTFTBTETXTBTDTFTCTDTFTCTETXTBTETXTBTETXTCTETXTCTETXTBTDTFTCTETXTCTETXTCTETXTCTDTFTBTETXTBTDTFTBTDTF'
    transiciones=leer_archivo(archivoEntrada)
    # Create logTinv.txt
    logfile = open(archivoSalida, "a")
    logfile.truncate(0) # Clear file
    logfile.write("INPUT: "+transiciones+'\n\n') # First Line

    while(not terminate):
        print("Iteracion: "+str(iteration))
        line = re.subn(patron, repl, transiciones)
        matches=int(line[1])
        transiciones=str(line[0])
        leng=len(transiciones)
        logfile.write('| ITERACION{:>5} | LARGO{:>6} | COINCIDENCIAS{:>5}'.format(str(iteration), str(leng), str(matches))+" | OUTPUT "+transiciones+'\n')
        iteration+=1
        if not matches:
            terminate=True
    print("Guardado en : "+archivoSalida)
    logfile.write("\n T-INVARIANTES:\n{T0,T3,T6,TB,TD,TF}\n{T0,T3,T6,TB,TE,TX}\n{T0,T3,T7,TC,TD,TF}\n{T0,T3,T7,TC,TE,TX}\n{T1,T4,T6,TB,TD,TF}\n{T1,T4,T6,TB,TE,TX}\n{T1,T4,T7,TC,TD,TF}\n{T1,T4,T7,TC,TE,TX}\n{T2,T5,T6,TB,TD,TF}\n{T2,T5,T6,TB,TE,TX}\n{T2,T5,T7,TC,TD,TF}\n{T2,T5,T7,TC,TE,TX}\n{T8,T9}\n{T8,TA}")
    logfile.close



if __name__ == "__main__":
    tinv_matcher()
