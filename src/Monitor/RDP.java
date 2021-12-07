package Monitor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class RDP {
    /////////Marcado inicial/////////////////////////////////////////////////////////
    static final String pathMatrizIncidencia="./init_files/M_I.txt";
    static final String pathMatrizPre="./init_files/M_Pre.txt";
    static final String pathMatrizPost="./init_files/M_Post.txt";
    static final String pathMatrizInhibicion="./init_files/M_B.txt";
    static final String pathVectorMarcadoInicial="./init_files/V_MI.txt";
    static final String pathMatrizLector="./init_files/M_L.txt";

    //private int [] VE  = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; // Indica el numero de transciones sensibilizadas
    private String[] Place = {"P0","P1","P10","P11","P12","P13","P14","P15","P16","P17","P18","P19","P2","P20","P21","P22","P23","P3","P4","P5","P6","P7","P8","P9"};
    // private int [] Places = {0,1,10,13,14,15,17,18,19,20,22,23,24,25,26,27,28,29,3,30,31,4,5,8,9};

    private AdministradorArchivo archivo;
    private final int numeroTransiciones; //almacena el numero de transiciones "n".
    private final int numeroPlazas;       //almacena el numero de plazas "m".
    //m x n
    private Matriz IEntrada;//I-:pre
    private Matriz ISalida;//I+:post
    private Matriz Incidencia;
    private Matriz Inhibicion;
    private Matriz InhibicionLector;
    //n x n
    private Matriz Identidad;
    //1 x m
    private Matriz VectorMarcadoInicial;
    private Matriz VectorMarcadoActual;
    private Matriz VectorMarcadoViejo;
    //1 x n
    private Matriz VectorExtendido;
    private Matriz VectorSensibilizado;
    private Matriz VectorInhibicion;
    private Matriz VectorLector;

    //EXTRAEXTRA
    private Matriz VectorSensibilizadoViejo;

    private SensibilizadasConTiempo gestionarTiempo;
    private Scanner input;
    /**
     * Constructor de la clase Red de Petri
     */
    public RDP(AdministradorArchivo archivo) {

        numeroTransiciones = Transiciones(pathMatrizIncidencia);	//Extraccion de la cantidad de transiciones.
        numeroPlazas = Plazas(pathMatrizIncidencia);				//Extraccion de la cantidad de plazas.
        //Matrices
        IEntrada = new Matriz(numeroPlazas,numeroTransiciones);
        ISalida = new Matriz(numeroPlazas,numeroTransiciones);
        Incidencia = new Matriz(numeroPlazas,numeroTransiciones);
        Inhibicion = new Matriz(numeroPlazas,numeroTransiciones);
        InhibicionLector = new Matriz(numeroPlazas, numeroTransiciones);
        Identidad = new Matriz(numeroTransiciones,numeroTransiciones);
        //Vectores
        VectorMarcadoInicial = new Matriz(1,numeroPlazas);
        VectorMarcadoActual = new Matriz(1,numeroPlazas);
        VectorMarcadoViejo = new Matriz(1,numeroPlazas);
        VectorSensibilizado = new Matriz(1,numeroTransiciones);
        VectorSensibilizadoViejo = new Matriz(1,numeroTransiciones);
        VectorInhibicion = new Matriz(1,numeroTransiciones);
        VectorExtendido = new Matriz(1,numeroTransiciones);
        VectorLector = new Matriz(1,numeroTransiciones);

        Incidencia.cargarMatriz(pathMatrizIncidencia);
        IEntrada.cargarMatriz(pathMatrizPre);
        ISalida.cargarMatriz(pathMatrizPost);
        Inhibicion.cargarMatriz(pathMatrizInhibicion);
        InhibicionLector.cargarMatriz(pathMatrizLector);
        Identidad.cargarIdentidad();
        VectorMarcadoInicial.cargarMatriz(pathVectorMarcadoInicial);
        VectorMarcadoActual.cargarMatriz(pathVectorMarcadoInicial);
        //EXTRA
        gestionarTiempo = new SensibilizadasConTiempo(numeroTransiciones);
        //sensibilizar();
        this.archivo=archivo;
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
        sensibilizarVectorE();
        sensibilizarVectorB();
        sensibilizarVectorL();
        //Ex = E and B and L and V and G and Z cambiar aca si algo se agrega
        VectorExtendido = VectorSensibilizado.getAnd(VectorInhibicion).getAnd(VectorLector);
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
        Matriz B = H.getMultiplicacion(Q.getTranspuesta());//mx1
        VectorInhibicion = B.getComplemento().getTranspuesta();//1xm
    }
    /**
     * Metodo que calcula el vector Lector
     */
    public void sensibilizarVectorL() {
        //vector W donde cada elemento es uno si la marca de la plaza es
        //distinta de cero, cero en otro caso
        Matriz W = new Matriz(1,VectorMarcadoActual.getNumColumnas());//1xn 1xP
        for(int i =0 ; i<VectorMarcadoActual.getNumColumnas(); i++) {
            if(VectorMarcadoActual.getDato(0, i) > 0) {
                W.setDato(0, i, 1);
            }
            else W.setDato(0, i, 0);
        }
        Matriz R = InhibicionLector.getTranspuesta();//PxT n x m =>m x n
        Matriz Auxiliar = InhibicionLector;
        //Vector de transiciones que no tienen un arco lector
        Matriz VectorTNL = new Matriz(1,InhibicionLector.getNumColumnas());
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
        Matriz L = R.getMultiplicacion(W.getTranspuesta());//m x n . n x 1 = m x 1
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
     *@param transicion : numero de transicion.
     *@return : true si el disparo es exitoso.
     *          false si el disparo no es exitoso.
     */
    public boolean Disparar(int transicion, Semaphore mutex) throws InterruptedException, IOException{ //, boolean flag) {
        if(!estaSensibilizada(transicion)) {
            return false;
        }
        if(!checkInvariantePlaza()){
            throw new RuntimeException("NO SE CUMPLIO UN INVARIANTE DE PLAZA");
        }
        boolean k=verificarSensibilizacionZ(transicion,mutex);
        if(k==true) {
            saveInFile(transicion);
            gestionarTiempo.resetEsperando(transicion);
            Matriz aux = Incidencia.getMultiplicacion(Identidad.getColumna(transicion)).getTranspuesta();
            VectorMarcadoActual =VectorMarcadoActual.getSuma(aux);
            sensibilizar();
            gestionarTiempo.setNuevoTimeStamp(transicion);
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * Metodo que verifica las condiciones de temporizacion de las
     * transiciones
     * @oaram transicion
     * @return
     */
    public boolean verificarSensibilizacionZ(int transicion, Semaphore mutex) throws InterruptedException{
        if(gestionarTiempo.testVentanaTiempo(transicion)) {
            if(!gestionarTiempo.Esperando(transicion)) {
                gestionarTiempo.setNuevoTimeStamp(transicion);
                return true;
            }
            else {
                return false;
            }
        }
        else {//no esta dentro de la ventana
            if(gestionarTiempo.antesDeLaVentana(transicion)) {//esta antes?
                gestionarTiempo.setEsperando(transicion);
                mutex.release();//aca hay que liberar el monitor(antes de poner a dormir al hilo)
                gestionarTiempo.dormir(transicion);
                mutex.acquire();//aca deberia adquirirlo
                gestionarTiempo.resetEsperando(transicion);//en esta parte segun el diagrama no va este reset pero para mi hace falta
                return true;//si pongo false me arriesgo a pasarme de la ventana(solo si fuera muy pequena)
            }
            else{
                //gestionarTiempo.resetEsperando(transicion);
                System.out.println("YA SE PASO");
                return false;
            }
        }
    }
    /***/
    public void imprimirVectorMarcado() {
        System.out.println("Vector de Marcado Actual");
        VectorMarcadoActual.imprimirMatriz();
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
    public Matriz getVectorMarcadoActual(){
        return VectorMarcadoActual;
    }
    public boolean checkInvariantePlaza(){
        if(VectorMarcadoActual.getDato(0,0)+VectorMarcadoActual.getDato(0,1)!=3
        ||VectorMarcadoActual.getDato(0,2)+VectorMarcadoActual.getDato(0,3)!=3
        ||VectorMarcadoActual.getDato(0,4)+VectorMarcadoActual.getDato(0,5)!=3
        ||VectorMarcadoActual.getDato(0,6)+VectorMarcadoActual.getDato(0,7)!=1
        ||VectorMarcadoActual.getDato(0,8)+VectorMarcadoActual.getDato(0,9)!=30
        ||VectorMarcadoActual.getDato(0,12)+VectorMarcadoActual.getDato(0,13)!=30
        ||VectorMarcadoActual.getDato(0,14)+VectorMarcadoActual.getDato(0,15)!=1
        ||VectorMarcadoActual.getDato(0,16)+VectorMarcadoActual.getDato(0,17)!=1
        ||VectorMarcadoActual.getDato(0,19)+VectorMarcadoActual.getDato(0,20)!=1
        ||VectorMarcadoActual.getDato(0,17)+VectorMarcadoActual.getDato(0,18)+
                VectorMarcadoActual.getDato(0,19)!=1
        ||VectorMarcadoActual.getDato(0,1)+VectorMarcadoActual.getDato(0,3)+
                VectorMarcadoActual.getDato(0,5)+VectorMarcadoActual.getDato(0,7)+
                VectorMarcadoActual.getDato(0,9)+VectorMarcadoActual.getDato(0,13)+
                VectorMarcadoActual.getDato(0,15)+VectorMarcadoActual.getDato(0,17)+
                VectorMarcadoActual.getDato(0,19)+VectorMarcadoActual.getDato(0,21)!=100
        ||VectorMarcadoActual.getDato(0,10)+VectorMarcadoActual.getDato(0,11)!=1)
            return false;
        return true;
    }
    /**
     * Metodo que guarda la informacion en el archivo
     * @param transicion transicion que va a ser disparada
     */
    public void saveInFile(int transicion) throws IOException {
        String infoMarcado="vector marcado actual : ";
        infoMarcado += VectorMarcadoActual.getDatosConFormato();
        archivo.EscribirEnArchivo(infoMarcado);
        String infoExtendido = "vector extendido : ";
        infoExtendido += VectorExtendido.getDatosConFormato();
        archivo.EscribirEnArchivo(infoExtendido);
        String infoTransicion = "transicion : "+transicion+"\n";
        archivo.EscribirEnArchivo(infoTransicion);
    }
}
