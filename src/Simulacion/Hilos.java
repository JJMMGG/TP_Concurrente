package Simulacion;

import Monitor.Monitor;

public class Hilos implements Runnable{
    private String nombre;
    private Monitor monitor;
    private int secuencia[];
    /**
     * Constructor de la clase Hilos
     * @param nombre nombre del hilo
     * @param monitor el monitor que ejecutara el metodo disparar
     * @param secuencia secuencia de transiciones que disparara el hilo
     */
    public Hilos(String nombre,Monitor monitor,int secuencia[]) {
        this.nombre = nombre;
        this.monitor = monitor;
        this.secuencia = secuencia;
    }
    /**
     * Metodod run
     */
    public void run() {
        while(true) {
            for(int i=0; i<secuencia.length; i++) {
                try {
                    monitor.dispararTransicion(secuencia[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
