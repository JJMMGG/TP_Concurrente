package Monitor;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Monitor {
    //Campos
    private RDP red;            //
    private Semaphore mutex;    //
    private Cola cola; 			//cola donde se pondran los hilos
    private Politicas politica; //
    private Matriz and;			//matriz que contiene el resultado de la operacion Vc&Vs
    private AdministradorArchivo archivo;
    /**
     * Constructor de la clase Monitor
     */
    public Monitor(RDP red,Politicas politica, AdministradorArchivo archivo) {
        this.politica = politica;
        this.red = red; 										//la red sobre la cual se trabajara
        this.archivo = archivo;
        //politica.quitarPrioridad(red.getMatrizInhibicion());
        red.sensibilizar();
        cola = new Cola(red.getSensibilizadas().getNumColumnas());
        mutex = new Semaphore(1,true);				//el semaforo que se utilizara, solo uno puede entrar y es justo.
    }
    /**
     * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro
     * y recalcula el vector de sensibilizadas tambien tiene en cuenta si la transicion a disparar esta o no sensibilizada.
     *@param n_transicion numero de transicion.
     *@return : -0 retorna 0 si el disparo no es exitoso.
     *          -1 retorna 1 si el disparo no es exitoso.
     */
    public void dispararTransicion(int n_transicion) throws InterruptedException, IOException
    {
        mutex.acquire();
        System.out.println("En este momento la cola al monitor tiene " +mutex.getQueueLength() +" hilos esperando");

        boolean k = true;
        int m = 0;
        int n_t;
        while(k == true) {
            red.imprimirVectorMarcado();
            System.out.println("Disparo: Transicion"+ n_transicion);
            k=red.Disparar(n_transicion, mutex);//, modo_de_disparo);
            if(k==true){
                //saveInFile(n_transicion);
                m=calcularVsAndVc();
                if(m==0){
                    k = false;
                }
                else {//<>0 hay alguien esperando y esta sensibilizado
                    n_t = politica.cual(and);//que hilo?
                    cola.sacarDeCola(n_t);
                    break;//aca creeria que deberiamos expulsar el primer hilo
                }
            }
            else {
                System.out.println("Encolado");
                mutex.release();//el hilo actual libera el monitor
                cola.ponerEnCola(n_transicion);//en cola porque fallo el disparo
                mutex.acquire();//temporal
            }
        }
        System.out.println("Libero el monitor");
        mutex.release();//el hilo actual libera el monitor
    }
    /**
     * Metodo que realiza la operacion And entre Vs y Vc, luego
     * se examina la matriz resultante y devuelve 1 o 0
     * @return
     */
    public int calcularVsAndVc(){
        Matriz Vs = red.getSensibilizadas();
        Matriz Vc = cola.quienesEstan();
        and = Vs.getAnd(Vc);//m
        if(and.esNula()) {
            return 0;
        }
        return 1;
    }

    /**
     * Metodo que guarda la informacion en el archivo
     * @param transicion transicion que va a ser disparada
     */
    /*public void saveInFile(int transicion) throws IOException {
        Matriz Marcado=red.getVectorMarcadoActual();
        String infoMarcado="vector marcado actual : ";
        infoMarcado += Marcado.getDatosConFormato();
        archivo.EscribirEnArchivo(infoMarcado);
        Matriz Extendido = red.getSensibilizadas();
        String infoExtendido = "vector extendido : ";
        infoExtendido += Extendido.getDatosConFormato();
        archivo.EscribirEnArchivo(infoExtendido);
        String infoTransicion = "transicion : "+transicion+"\n";
        archivo.EscribirEnArchivo(infoTransicion);
    }*/
}