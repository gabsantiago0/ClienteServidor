import java.util.Stack;

public class Mensajes {

    public static final String AYUDA = "#ayuda";
    public static final String LISTAR = "#listar";
    public static final String CHARLAR = "#charlar";

    public static String ayuda(){
        String mensaje = "Comandos disponibles:\n" +
                "#ayuda: Muestra los comandos disponibles\n" +
                "#listar: lista todos los usuarios conectados.\n" +
                "#charlar <usuario>: comienza la comunicación con el usuario <usuario>\n" +
                "#salir: se desconecta del chat\n" +
                "#shutdown: apagar el servidor";
        return mensaje;
    }

    public static String comandoNoExiste(){
        String mensaje = " no se reconoce como comando. Prueba a utilizar #ayuda,#listar,#charlar,#salir\n" +
                "RECUERDA USAR '#'\n";
        return mensaje;
    }

    public static String usuarioNoEncontrado(){
        String mensaje = "[ERROR] El usuario no ha sido encontrado.";
        return mensaje;
    }


}
