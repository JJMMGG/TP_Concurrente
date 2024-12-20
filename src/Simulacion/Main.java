package Simulacion;

import Monitor.Monitor;
import Monitor.RDP;
import Monitor.AdministradorArchivo;
import Monitor.Politicas;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    private static final int numeroHilos = 17;
    private static final int tiempoCorrida = 60000;//en milisegundos DEF=60000
    private static final String archivo="./run.log";
    private static Scanner input;
    private static String Nombres[] = { "Entrada 1",        //T1
                                        "Entrada 2",        //T2
                                        "Entrada 3",        //T3
                                        "Barrera 1",        //T4
                                        "Barrera 2",        //T5
                                        "Barrera 3",        //T6
                                        "Entrada a piso 1", //T7
                                        "Entrada a piso 2", //T8
                                        "Encender Cartel",  //T9  //CARTEL ENCENDIDO
                                        "Apagar cartel 1",  //T10  //CARTEL APAGADO DEBIDO A QUE HAY UN LUGAR EN EL PISO 2
                                        "Apagar cartel 2",  //T11  //SE APAGA EL CARTEL DEBIDO A QUE HAY LUGAR EN EL PISO 1
                                        "Salida de piso 1", //T12
                                        "Salida de piso 2", //T13
                                        "Caja calle 1",     //T14
                                        "Caja calle 2",     //T15
                                        "Salida 1",         //T16
                                        "Salida 2"};        //T17

    private static int[] Secuencias[]= {{0},{1},{2},{3},{4},{5},{6},{7},{8},
                                        {9},{10},{11},{12},{13},{14},{15},
                                        {16}};//secuencias de transiciones para cada hilo

    private static Hilos hilos[];
    private static Thread threads[];
    private static Monitor monitor;
    private static RDP redDePetri;
    private static Politicas politicas;
    private static AdministradorArchivo administradorArchivo;
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
        administradorArchivo = new AdministradorArchivo(archivo);
        redDePetri = new RDP(administradorArchivo);
        politicas = new Politicas(eleccion);
        monitor = new Monitor(redDePetri, politicas, administradorArchivo);
        //inicializacion hilos
        for(int i=0; i<numeroHilos;i++) {
            hilos[i] = new Hilos(Nombres[i],monitor,Secuencias[i]);
            threads[i] = new Thread(hilos[i]);
        }
        //	threads[0].start();
        for(int j=0;j<numeroHilos;j++) {
            threads[j].start();}

        try {
            Thread.sleep(tiempoCorrida/6);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al intentar dormir el hilo principal");
        }
        for(int k=0;k<numeroHilos;k++) {
            //threads[k].interrupt();
            threads[k].stop();;
        }
        try {
            Thread.sleep(3000);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error al intentar dormir el hilo principal");
        }
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