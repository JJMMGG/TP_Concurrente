package Monitor;

public class SensibilizadasConTiempo {
    //Campos
    long timeStamp[];
    long esperando[];
    //private Matriz esperando;	//vector binario indica si espera o no
    private Matriz tiempos;		//[alfa,beta]
    static final String pathMatrizTiempos="./init_files/M_Time.txt";
    /**
     * Constructor de la clase SensibilizadasConTiempo
     */
    public SensibilizadasConTiempo(int transiciones) {
        timeStamp = new long [transiciones];
        esperando = new long[transiciones];
        tiempos = new Matriz(2,transiciones); //=>[t0,t1,t2,...tT]x2
        tiempos.cargarMatriz(pathMatrizTiempos);//le puse tiempos de prueba al archivo despues verificar
        iniciarArreglos(transiciones);
    }
    /**
     * Metodo que inicia los arrays esperando y timeStamp con valores igual 0
     */
    private void iniciarArreglos(int transiciones){
        for(int i=0;i<transiciones;i++){
            esperando[i]=0;
            timeStamp[i]=0;
        }
    }
    /**
     * Metodo que setea un nuevo timeStamp para una determinada transicion
     * @param transicion
     */
    public void setNuevoTimeStamp(int transicion) {
        timeStamp[transicion] = System.currentTimeMillis();
    }
    /**
     * Metodo que verifica si el tiempo transcurrido luego de sensibilizada
     * la transicion se encuentra dentro de los valores alfa y beta, es decir
     * dentro de la ventana.
     * @param transicion
     * @return
     */
    public boolean testVentanaTiempo(int transicion) {
        long ahora = System.currentTimeMillis();
        long tiempoTranscurrido = ahora-timeStamp[transicion];
        int alfa = tiempos.getDato(0, transicion);
        int beta = tiempos.getDato(1, transicion);
        if((alfa<=tiempoTranscurrido)&&(tiempoTranscurrido<=beta)) {
            return true;
        }
        return false;
    }
    /**
     * Metodo que verifica si la transicion esta antes de la ventana
     * @param transicion
     * @return
     */
    public boolean antesDeLaVentana(int transicion) {
        long ahora = System.currentTimeMillis();
        long tiempoTranscurrido = ahora-timeStamp[transicion];
        int alfa = tiempos.getDato(0, transicion);
        if (tiempoTranscurrido<alfa) {
            return true;
        }
        return false;
    }
    /**
     * seteamos a la transicion esperando.
     * y ponemos a dormir el hilo para que espere.
     * @param transicion
     */
    public void setEsperando(int transicion) {
        esperando[transicion] = 1;
    }
    /**
     * Metodo que remueve a la transicion de la espera.
     * @param transicion
     */
    public void resetEsperando(int transicion) {
        esperando[transicion] = 0;
    }
    /**
     * Metodo que verifica si la transicion esta esperando
     * @param transicion transicion que queremos verificar
     * @return true si esta esperando sino false
     */
    public boolean Esperando(int transicion) {
        if(esperando[transicion]==1) {
            return true;
        }
        return false;
    }
    /**
     * Metodo que pone a dormir al hilo con el calculo
     * timeStamp+alfa-ahora
     * @param transicion
     */
    public void dormir(int transicion) {
        int alfa = tiempos.getDato(0, transicion);
        long ahora = System.currentTimeMillis();
        try{
            Thread.sleep(timeStamp[transicion]+alfa-ahora);//en milisegundos
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Al intentar hacer dormir");
        }
    }
}
