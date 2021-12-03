package Simulacion;

import Monitor.Monitor;
import Monitor.RDP;
import Monitor.Politicas;
import java.util.Scanner;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final int numeroHilos = 15;
    private static final int tiempoCorrida = 60000;//en milisegundos
    private static Scanner input;
    /*private static Escribir_txt Transiciones;
    private static Escribir_txt Plazas;
    private static Escribir_txt Estadisticas;*/
    private static String Nombres[] = { "Entrada 1",       //T0
                                        "Entrada 2",       //T1
                                        "Entrada 3",       //T2
                                        "Barrera 1",       //T3
                                        "Barrera 2",       //T5
                                        "Barrera 3",       //T4
                                        "Entrada a piso 1",//T6
                                        "Entrada a piso 2,Rampa de subida",//T7 T9
                                        "Rampa de bajada,Salida de piso 2", //T10
                                        "Salida de piso 1", //T8
                                        "Caja calle 1,Salida 1 ",//T12 T14
                                        "Caja calle 2,Salida 2",    //T13 T15
                                        "Encender Cartel", //T16  //CARTEL ENCENDIDO
                                        "Apagar cartel 1", //T17  //CARTEL APAGADO DEBIDO A QUE HAY UN LUGAR EN EL PISO 2
                                        "Apagar cartel 2"};//T18  //SE APAGA EL CARTEL DEBIDO A QUE HAY LUGAR EN EL PISO 1

    private static int[] Secuencias[]= {{0},{1},{2},{3},{5},{4},{6},{7,9},
                                        {10,11},{8},{12,14},{13,15},{16},{17},
                                        {18}};//secuencias de transiciones para cada hilo

    private static Hilos hilos[];
    private static Thread threads[];
    private static Monitor monitor;
    private static RDP redDePetri;
    private static Politicas politicas;
    public static void main(String[] args) {
        input = new Scanner(System.in);
        System.out.println("#####Inicio Simulacion#####");
        System.out.println("Seleccione una politica:");
        System.out.println("0.Prioridad aleatoria");
        System.out.println("1.Prioridad llenar de vehiculos planta baja.Prioridad salida indistinta");
        System.out.println("2.Prioridad salida a calle 2. Prioridad de llenado indistinta.");
        int eleccion = -1;
        while(eleccion<0) {
            eleccion = input.nextInt();
        }
        try {
            iniciarPrograma(eleccion);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void iniciarPrograma(int eleccion) throws IOException{
        hilos = new Hilos[numeroHilos];
        threads = new Thread[numeroHilos];
        /*String transicion = "./Transiciones.txt";
        Transiciones = new Escribir_txt(transicion);
        String plazas = "./Plazas.txt";
        Plazas = new Escribir_txt(plazas);
        String estadisticas = "./Estadisticas.txt";
        Estadisticas = new Escribir_txt(estadisticas);
        redDePetri = new RDP(Transiciones, Plazas); // se envian los txt donde se van a escribir lo datos
        politicas = new Politicas(eleccion);
        monitor = new Monitor(redDePetri,politicas);*/
        redDePetri = new RDP();
        politicas = new Politicas(eleccion);
        monitor = new Monitor(redDePetri, politicas);
        //inicializacion hilos
        for(int i=0; i<numeroHilos;i++) {
            hilos[i] = new Hilos(Nombres[i],monitor,Secuencias[i]);
            threads[i] = new Thread(hilos[i]);
        }
        //	threads[0].start();
        for(int j=0;j<numeroHilos;j++) {
            threads[j].start();}

        try {
            Thread.sleep(tiempoCorrida);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al intentar dormir el hilo principal");
        }
        for(int k=0;k<numeroHilos;k++) {
            //	threads[k].interrupt();
            threads[k].stop();;
        }
        try {
            Thread.sleep(3000);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al intentar dormir el hilo principal");
        }
        /*System.out.println("Invariant test beginning");
        Test_Invariantes test = new Test_Invariantes(redDePetri,Estadisticas);
        test.testear();
        String tiempo = "Tiempo de simulacion en milisegundos: "+ tiempoCorrida+" " ;
        try {
            Estadisticas.Escribir_Dato(tiempo);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("Finish test");*/
    }
}
/*RDP red=new RDP();
        red.printInfo();
        red.sensibilizar();
        red.printInfo();*/
        /*int aleatorio=0;
        int maximo=2;
        for(int i=0; i<100 ;i++){
            System.out.println("El valor es:"+aleatorio);
            aleatorio=ThreadLocalRandom.current().nextInt(maximo);
        }*/