package Monitor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class RDP {
    /////////Marcado inicial/////////////////////////////////////////////////////////
    static final String pathMatrizIncidencia="./init_files/M_I.txt";
    static final String pathMatrizPre="./init_files/M_Pre.txt";
    static final String pathMatrizPost="./init_files/M_Post.txt";
    static final String pathMatrizInhibicion="./init_files/M_B.txt";
    static final String pathVectorMarcadoInicial="./init_files/V_MI.txt";
    static final String pathMatrizTiempos="./init_files/M_Time.txt";
    static final String pathMatrizLector="./init_files/M_L.txt";

    //private int [] VE  = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; // Indica el numero de transciones sensibilizadas
    private String[] Place = {"P0","P1","P10","P11","P12","P13","P14","P15","P16","P17","P18","P19","P2","P20","P21","P22","P23","P3","P4","P5","P6","P7","P8","P9"};
    // private int [] Places = {0,1,10,13,14,15,17,18,19,20,22,23,24,25,26,27,28,29,3,30,31,4,5,8,9};

    private final int numeroTransiciones; //almacena el numero de transiciones "n".
    private final int numeroPlazas;       //almacena el numero de plazas "m".
    //m x n
    private Matriz IEntrada;
    private Matriz ISalida;
    private Matriz Incidencia;
    private Matriz Inhibicion;
    //n x n
    private Matriz Identidad;
    //1 x m
    private Matriz VectorMarcadoInicial;
    private Matriz VectorMarcadoActual;
    private Matriz VectorMarcadoNuevo;
    //1 x n
    private Matriz VectorExtendido;
    private Matriz VectorSensibilizado;
    private Matriz VectorInhibicion;

    //EXTRAEXTRA
    
    private SensibilizadasConTiempo gestionarTiempo;
    /**
     * Constructor de la clase Red de Petri
     */
    public RDP() {

        numeroTransiciones = Transiciones(pathMatrizIncidencia);	//Extraccion de la cantidad de transiciones.
        numeroPlazas = Plazas(pathMatrizIncidencia);				//Extraccion de la cantidad de plazas.
        //Matrices
        IEntrada = new Matriz(numeroPlazas,numeroTransiciones);
        ISalida = new Matriz(numeroPlazas,numeroTransiciones);
        Incidencia = new Matriz(numeroPlazas,numeroTransiciones);
        Inhibicion = new Matriz(numeroPlazas,numeroTransiciones);
        Identidad = new Matriz(numeroTransiciones,numeroTransiciones);
        //Vectores
        VectorMarcadoInicial = new Matriz(1,numeroPlazas);
        VectorMarcadoActual = new Matriz(1,numeroPlazas);
        VectorMarcadoNuevo = new Matriz(1,numeroPlazas);
        VectorSensibilizado = new Matriz(1,numeroTransiciones);
        VectorInhibicion = new Matriz(1,numeroTransiciones);
        VectorExtendido = new Matriz(1,numeroTransiciones);

        Incidencia.cargarMatriz(pathMatrizIncidencia);
        IEntrada.cargarMatriz(pathMatrizPre);
        ISalida.cargarMatriz(pathMatrizPost);
        Inhibicion.cargarMatriz(pathMatrizInhibicion);
        Identidad.cargarIdentidad();
        VectorMarcadoInicial.cargarMatriz(pathVectorMarcadoInicial);
        VectorMarcadoActual.cargarMatriz(pathVectorMarcadoInicial);

        //sensibilizar();
    }
    /**
     * Este metodo devuelve la cantidad de transiciones disponibles en la red
     * @param Matriz Matriz de incidencia
     * @return transiciones de la red
     */
    private int Transiciones(String Matriz){
        int transiciones = 0;
        try {
            input = new Scanner(new File(Matriz));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                for (int fila = 0 ; fila <line.length (); fila ++) {
                    char c = line.charAt (fila);
                    if(c == '1' || c == '0') {
                        transiciones ++ ;
                    }
                }
                break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return transiciones;
    }
    /**
     * Este metodo devuelve la cantidad de plazas disponible en la red
     * @param Matriz Matriz de incidencia
     * @return Plazas de la red
     */
    private int Plazas(String Matriz) {
        int Plazas = 0;
        try {
            input = new Scanner(new File(Matriz));
            while (input.hasNextLine()) {
                input.nextLine();
                Plazas ++ ;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return Plazas;
    }
    /**
     * Este metodo verifica si la transicion esta sensibilizada en el vector
     * extendido (VE)
     * @param transicion : transicion que se desea saber si esta habilitada.
     * @return true estan habilitadas, caso contrario false.
     */
    public boolean estaSensibilizada(int transicion) {
        if(VectorExtendido.getDato(0, transicion)==1){
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * Metodo que devuelve el vector con las transiciones sensibilizadas
     * @return vector extendido
     */
    public Matriz getSensibilizadas() {
        return VectorExtendido;
    }
    /**
     * Metodo que devuelve la matriz de inhibicion
     * @return matriz inhibicion
     */
    public Matriz getMatrizInhibicion() {
        return Inhibicion;
    }
    /**
     * Metodo que sensibiliza las transiciones y carga el vector extendido
     */
    public void sensibilizar() {
        sensibilizarVectorB();
        sensibilizarVectorE();
        sensibilizarVectorL();
        //Ex = E and B and L and V and G and Z cambiar aca si algo se agrega
        VectorExtendido = VectorSensibilizado.getAnd(VectorInhibicion);
    }
    /**
     * Metodo que calcula el vector Inhibicion
     */
    public void sensibilizarVectorB() {
        //Para obtener el vector B neceitamos la formula B=H*Q
        //vector Q donde cada elemento es cero si la marca de la plaza es distinta de cero, uno en otro caso
        Matriz Q = new Matriz(VectorMarcadoActual.getNumFilas(),VectorMarcadoActual.getNumColumnas());
        for(int i=0; i<Q.getNumFilas(); i++) {
            for(int j=0; j<Q.getNumColumnas(); j++) {
                if(VectorMarcadoActual.getDato(i, j)==0) {//duda 1 o 0?
                    Q.setDato(i, j, 0);
                }
                else {
                    Q.setDato(i, j, 1);
                }
            }
        }//[1xn]=[1xnumeroDePlazas] si hacemos una transposicion => nx1
        //H [nxm]=[numeroDePlazasxnumeroDeTransiciones], si o si hay que transponer =>mxn
        Matriz H = Inhibicion.getTranspuesta();
        Matriz B = H.getMultiplicacion(Q.getTranspuesta());
        VectorInhibicion = B.getComplemento().getTranspuesta();
    }
    /**
     * Metodo que calcula el vector Lector
     */
    public void sensibilizarVectorL() {
        Matriz W = new Matriz(1,VectorMarcadoActual.getNumColumnas());//1xn
        for(int i =0 ; i<VectorMarcadoActual.getNumColumnas(); i++) {
            if(VectorMarcadoActual.getDato(0, i) > 0) {
                W.setDato(0, i, 1);
            }
            else W.setDato(0, i, 0);
        }
        Matriz R = InhibicionLector.getTranspuesta();//m x n =>n x m
        Matriz Auxiliar = InhibicionLector;
        Matriz VectorTNL = new Matriz(1,InhibicionLector.getNumColumnas()); //Vector de transiciones que no tienen un arco lector
        //Genero un vector con las transiciones que no tiene arco lector
        int flag =0;
        for(int s =0 ; s<InhibicionLector.getNumColumnas() ; s++) {
            flag = 0;
            for(int d =0 ; d<InhibicionLector.getNumFilas() ; d++) {
                flag = flag + Auxiliar.getDato(d, s);
            }
            if(flag==0) {
                VectorTNL.setDato(0, s, 1);
            }
            else VectorTNL.setDato(0, s, 0);
        }
        Matriz L = R.getMultiplicacion(W.getTranspuesta()); // L = R X W
        L = L.getTranspuesta();
        for(int co =0 ; co<InhibicionLector.getNumColumnas() ; co++) {
            L.setDato(0, co, ( L.getDato(0, co)+VectorTNL.getDato(0, co)));
        }
        VectorLector = L;
    }
    /**
     * Metodo que calcula el vector sensibilizado
     */
    public void sensibilizarVectorE() {
        for (int i = 0; i < IEntrada.getNumColumnas(); i++) {
            int e = 1;
            for (int j = 0; j < IEntrada.getNumFilas(); j++) {
                if (VectorMarcadoActual.getDato(0, j) < IEntrada.getDato(j, i)) {
                    e = 0;
                }
                VectorSensibilizado.setDato(0, i, e);
            }
        }
    }
    /**
     * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro
     *@param n_T : numero de transicion.
     *@param flag indica el modo de disparo:
     *			 -true : modo de disparo explicito, modifica el vector de marcado actual VMA
     *           -false : modo de disparo implicito, no modifica el vector de marcado actual VMA
     *@return : -0 retorna 0 si el disparo no es exitoso.
     *          -1 retorna 1 si el disparo es exitoso.
     */
    public boolean Disparar(int transicion){ //, boolean flag) {
        if(!estaSensibilizada(transicion)) {
            return false;
        }
        boolean k;
        if(gestionarTiempo.testVentanaTiempo(transicion)) {
            if(!gestionarTiempo.Esperando(transicion)) {
                gestionarTiempo.setNuevoTimeStamp(transicion);
                k = true;
            }
            else {
                k = false;
            }
        }
        else {
            //no esta dentro de la ventana esta antes?
            if(gestionarTiempo.antesDeLaVentana(transicion)) {
                //aca hay que liberar el monitor(antes de poner a dormir al hilo)
                gestionarTiempo.setEsperando(transicion);
                gestionarTiempo.dormir(transicion);
                gestionarTiempo.resetEsperando(transicion);//en esta parte segun el diagrama no va este reset pero para hace falta
            }
            else {
                k = false;
            }
        }
        if(k=true) {
            gestionarTiempo.resetEsperando(transicion);//va aca en teoria
            Matriz aux = Incidencia.getMultiplicacion(Identidad.getColumna(transicion)).getTranspuesta();
            VectorMarcadoActual =VectorMarcadoActual.getSuma(aux);
            sensibilizar();
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * Este metodo muestra el vector indicado por parametro
     * @param vector vector a imprimir.
     */
    public void mostrar(int[] vector ,int Tipo) {
        System.out.println("\n");
        if(Tipo == 0) {
            String [] t = {"T0","T1","T10","T11","T12","T13","T14","T15","T16","T17","T18","T2","T3","T4","T5","T6","T7","T8","T9"};
            for(int n=0 ; n<vector.length ; n++) System.out.print(t[n] +":" + vector[n] +" ");
        }
        else if(Tipo > 0) {

            for(int n=0 ; n<vector.length ; n++) System.out.print(Place [n] +":" + vector[n] +" ");
        }
    }
}
