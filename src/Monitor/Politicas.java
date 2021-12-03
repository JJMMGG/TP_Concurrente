package Monitor;

//import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Politicas {
    //Campos
    private static final int Plaza1=9;//P10
    private static final int Transicion1 = 7;//T8
    private static final int Plaza2=24;//P25
    private static final int Transicion2 = 13;//T14
    private int politica;
    static final int opcionLLenadoPlantaBaja = 1;
    static final int opcionSalidaCalleDos = 2;
    public Politicas(int politica){
        this.politica = politica;
    }
    /**
     * Metodo que devuleve una transicion
     * @param m matriz que contiene el resultado de Vc and Vs
     * @return
     */
    public int cual(Matriz m) {
        //Random rand = new Random();
        int maximo = m.getNumColumnas();
        int aleatorio=0,siguiente=0;
        while(siguiente!=1) {
            //aleatorio = rand.nextInt(maximo);
            aleatorio=ThreadLocalRandom.current().nextInt(maximo);
            siguiente=m.getDato(0, aleatorio);
        }
        return siguiente;
    }
    /**
     * Metodo que modifica la red para remover las prioridades
     * @param inhibicion
     * @return
     */
    public void quitarPrioridad(Matriz inhibicion) {
        switch(politica) {
            case opcionLLenadoPlantaBaja:
                inhibicion.setDato(Plaza1, Transicion1, 0);
                break;
            case opcionSalidaCalleDos:
                inhibicion.setDato(Plaza2, Transicion2, 0);
                break;
            default:
                break;
        }
    }
}