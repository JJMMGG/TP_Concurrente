package Monitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AdministradorArchivo {

    private BufferedWriter BufferEscritura;

    /**
     * Constructor de la clase
     * @param NombreArchivo nombre que recibira el arhivo
     */
    public AdministradorArchivo(String NombreArchivo) throws IOException {
        BufferEscritura = new BufferedWriter(new FileWriter(NombreArchivo));
    }
    /**
     * Metodo que escribe en la informacion recibida en el archivo
     * @param dato dato que se quiere escribir en el archivo
     */
    public void EscribirEnArchivo(String dato) throws IOException{
        BufferEscritura.write(dato);
        BufferEscritura.flush();
    }
}
