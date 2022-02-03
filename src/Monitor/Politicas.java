package Monitor;

//import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Politicas {
    //Campos
    private static final int Plaza1=8;//P9
    private static final int Transicion1 = 7;//T8
    private static final int TransicionP1 = 6;//T7
    private static final int Plaza2=16;//P17
    private static final int Transicion2 = 13;//T14
    private static final int TransicionP2 = 14;//T15
    private int politica;
    static final int opcionIndiferente = 0;
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
            switch (politica){
                case opcionIndiferente:
                    break;
                case opcionLLenadoPlantaBaja:
                    if(aleatorio==Transicion1 && m.getDato(0,TransicionP1)==1){
                        aleatorio=TransicionP1;
                        siguiente = aleatorio;//en teoria no hace falta
                    }
                    break;
                case opcionSalidaCalleDos:
                    if(aleatorio==Transicion2 && m.getDato(0,TransicionP2)==1){
                        aleatorio=TransicionP2;
                        siguiente = aleatorio;
                    }
                    break;
            }
        }
        System.out.println("El hilo a despertar es: "+aleatorio);
        return aleatorio;
    }
    /**
     * Metodo que modifica la red para remover las prioridades
     * @param inhibicion
     * @return
     */
    public void quitarPrioridad(Matriz inhibicion) {
        switch(politica) {
            case opcionIndiferente:
                inhibicion.setDato(Plaza1, Transicion1, 0);
                inhibicion.setDato(Plaza2, Transicion2, 0);
                break;
            case opcionLLenadoPlantaBaja:
                inhibicion.setDato(Plaza2, Transicion2, 0);
                break;
            case opcionSalidaCalleDos:
                inhibicion.setDato(Plaza1, Transicion1, 0);
                break;
            default:
                break;
        }
    }
}
