public class Conexion {
    private Integer puerto;
    private String servidor;
    private static Conexion singleton = null;
    public final static String FIN_CONN_CLIENTE = "fin_cliente ";
    public final static String SALIR = "#SALIR";
    public final static String SHUTDOWN = "#shutdown";

    private Conexion() {
        puerto = 1234;
        servidor = "localhost";
    }

    /**
     * Devuelve el puerto por el que escucha el servidor.
     * @return puerto del servidor
     */
    public static Integer PUERTO() {
        return getConexion().puerto;
    }

    /**
     * Devuelve la dirección ip o el host en el que escucha el servidor.
     * @return host del servidor.
     */
    public static String SERVIDOR() {
        return getConexion().servidor;
    }

    private static Conexion getConexion() {
        if (singleton == null) {
            singleton = new Conexion();
        }
        return singleton;
    }
}
