package Monitor;

import java.util.concurrent.Semaphore;

public class Monitor {
    //Campos
    private RDP red;            //
    private Semaphore mutex;    //
    private Cola cola; 			//cola donde se pondran los hilos
    private Politicas politica; //
    private Matriz and;			//matriz que contiene el resultado de la operacion Vc&Vs
    /**
     * Constructor de la clase Monitor
     */
    public Monitor(RDP red,Politicas politica) {
        this.politica = politica;
        this.red = red; 										//la red sobre la cual se trabajara
        politica.quitarPrioridad(red.getMatrizInhibicion());
        red.sensibilizar();
        cola = new Cola(red.getSensibilizadas().getNumColumnas());
        mutex = new Semaphore(1,true);							//el semaforo que se utilizara, solo uno puede entrar y es justo.
    }
    /**
     * Este metodo dispara una transicion de la rdp indicada por parametro, teniendo en cuenta el modo indicado por parametro
     * y recalcula el vector de sensibilizadas tambien tiene en cuenta si la transicion a disparar esta o no sensibilizada.
     *@param n_transicion numero de transicion.
     *@return : -0 retorna 0 si el disparo no es exitoso.
     *          -1 retorna 1 si el disparo no es exitoso.
     */
    public void dispararTransicion(int n_transicion)
    {
        try {
            mutex.acquire();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Disparo: Transicion"+ n_transicion);
        boolean k = true;
        int m = 0;
        int n_t;
        while(k == true) {
            k=red.Disparar(n_transicion);//, modo_de_disparo);
            if(k==true){
                m=calcularVsAndVc();
                if(m==0){
                    k = false;
                }
                else {//<>0
                    n_t = politica.cual(and);//que hilo?
                    cola.sacarDeCola(n_t);//este metodo obtiene el siguiente hilo debido a que el anterior disparo exitosamente
                    //aca creeria que deberiamos expulsar el primer hilo
                }
            }
            else {
                mutex.release();//el hilo actual libera el monitor
                cola.ponerEnCola(n_transicion);//en realidad se pone en cola porque fallo el disparo
            }
        }
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
    void imprimir() {
        System.out.println("----------------------------------MONITOR----------------------------------\n");
    }
}