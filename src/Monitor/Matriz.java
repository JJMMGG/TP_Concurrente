package Monitor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Matriz {
    //Campos
    private int matriz[][];
    private int filas;
    private int columnas;
    private Scanner input;
    /**
     * Constructor de la clase Matriz
     * @param filas numero de filas
     * @param columnas numero de columnas
     */
    public Matriz(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        matriz = new int [filas][columnas];
    }
    /**
     * Constructor de la clase Matriz
     * @param matriz se recibe una matriz
     */
    public Matriz(int matriz[][]) {
        this.matriz = matriz;
        filas = matriz.length;
        columnas = matriz[0].length;
    }
    /**
     * Devuelve la cantidad de filas que tiene la matriz
     * @return filas numero de filas
     */
    public int getNumFilas() {
        return filas;
    }
    /**
     * Devuelve la cantidad de columnas que tiene la matriz
     * @return columnas numero de columnas
     */
    public int getNumColumnas(){
        return columnas;
    }
    /**
     * Metodo para cargar un dato en una detarminada fila y columna
     * @param fi fila en la que se quiere cargar el dato
     * @param co columna en la que se quiere cargar el dato
     * @param dato dato que se quiere cargar
     */
    public void setDato(int fi, int co, int dato){
        matriz[fi][co]=dato;
    }
    /**
     * Metodo para obtener el dato de una determinada fila y columna
     * @param fi fila de la cual se quiere obtener el dato
     * @param co columna de la cual se quiere obtener el dato
     * @return dato
     */
    public int getDato(int fi, int co){
        int dato = matriz[fi][co];
        return dato;
    }
    /**
     * Metodo para saber si la matriz es nula (todos sus elementos = 0)
     * @return true si es nula
     */
    public boolean esNula(){
        for(int i=0;i<filas;i++) {
            for(int j=0; j<columnas;j++) {
                if(this.getDato(i, j) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Metodo para imprimir los datos contenidos en la matriz
     */
    public void imprimirMatriz() {
        System.out.println("Datos:");
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++) {
                System.out.print(this.getDato(i, j) + "  ");
            }
            System.out.println();
        }
        System.out.println("############################");
    }
    /**
     *
     * @param archivo
     */
    public void cargarMatriz(String archivo){
        int fila=0;
        try {
            input = new Scanner(new File(archivo));
            while (input.hasNextLine()) {
                String  line = input.nextLine();
                String[] linea = line.split("	");
                for (int columna = 0; columna < columnas; columna++) {
                    matriz[fila][columna] = Integer.parseInt(linea[columna]);
                }
                fila ++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Metodo que realiza la operacion and entre la matriz que llama
     * el metodo y la que se pasa por parametro
     * @param Matriz
     * @return
     */
    public Matriz getAnd(Matriz B){
        Matriz And = new Matriz(B.getNumFilas(),B.getNumColumnas());
        for(int i=0; i<this.getNumFilas(); i++) {
            for(int j=0; j<this.getNumColumnas(); j++) {
                if(this.getDato(i,j) == 1 && B.getDato(i, j) == 1) {
                    And.setDato(i,j,1);
                }
                else {
                    And.setDato(i, j, 0);
                }
            }
        }
        return And;
    }
    /**
     * Metodo que realiza la multiplicacion entre la matriz que llama
     * el metodo y la que se pasa por parametro
     * @param B
     * @return matriz con el resultado (A X B)
     */
    public Matriz getMultiplicacion(Matriz B) {
        if(this.getNumColumnas()!=B.getNumFilas()) {
            throw new RuntimeException("Diferentes Tama�os");
        }
        Matriz Mult = new Matriz(this.getNumFilas(),B.getNumColumnas());
        int dat=0;
        for(int i=0; i<this.getNumFilas(); i++) {
            for(int j=0; j<B.getNumColumnas(); j++) {
                for(int k=0; k<B.getNumFilas(); k++) {
                    dat += this.getDato(i, k) * B.getDato(k, j);
                }
                Mult.setDato(i, j, dat);
                dat=0;
            }
        }
        return Mult;
    }
    /**
     * Metodo que transpone la matriz que llama este metodo
     * @return matriz transpuesta
     */
    public Matriz getTranspuesta() {
        Matriz Transpuesta = new Matriz(this.getNumColumnas(),this.getNumFilas());
        for(int i=0; i<this.getNumFilas();i++) {
            for(int j=0; j<this.getNumColumnas();j++) {
                Transpuesta.setDato(j, i, this.getDato(i,j));
            }
        }
        return Transpuesta;
    }
    /**
     * Metodo que devuelve el complemento de la matriz que llamo el metodo
     * @return matriz complemento
     */
    public Matriz getComplemento() {
        Matriz comp = new Matriz(this.getNumFilas(),this.getNumColumnas());
        for(int i=0; i<this.getNumFilas(); i++) {
            for(int j=0; j<this.getNumColumnas(); j++) {
                if(this.getDato(i, j) == 0) {
                    comp.setDato(i, j, 1);
                }
                else {
                    comp.setDato(i, j, 0);
                }
            }
        }
        return comp;
    }
    /**
     * Metodo que carga una matriz como una matriz identidad
     */
    public void cargarIdentidad() {
        for(int i=0; i<this.getNumFilas();i++) {
            for(int j=0; j<this.getNumColumnas();j++) {
                if(j==i) {
                    this.setDato(i, j, 1);
                }
                else {
                    this.setDato(i, j, 0);
                }
            }
        }
    }
    /**
     * Metodo que devuelve la columna correspondiente a la tansicion
     * @param transicion
     * @return columna de la matriz transpuesta
     */
    public Matriz getColumna(int columna) {
        Matriz col = new Matriz(this.getNumColumnas(),1);
        for(int i=0; i<this.getNumColumnas();i++) {
            col.setDato(i, 0, this.getDato(i,columna));
        }
        return col;
    }
    /**
     * Metodo que devuelve la suma entre la matriz que llama el metodo
     * y la que se pasa como parametro
     * @param B matriz con la que se sumara
     * @return suma de las dos matrices
     */
    public Matriz getSuma(Matriz B) {
        Matriz suma = new Matriz(this.getNumFilas(),this.getNumColumnas());
        for(int i=0; i<this.getNumFilas(); i++) {
            for(int j=0; j<this.getNumColumnas(); j++) {
                int resultado = this.getDato(i, j)+B.getDato(i, j);
                suma.setDato(i, j, resultado);
            }
        }
        return suma;
    }
}
