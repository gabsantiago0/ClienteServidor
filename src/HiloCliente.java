import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloCliente extends Thread {
    private String nombre;
    private Socket cliente;

    public HiloCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public void run() {
        DataInputStream in = null;
        HiloCliente destinatario = null;

        try {
            System.out.println(Servidor.getTime() + "\tRecibido cliente " + cliente.getRemoteSocketAddress());
            in = new DataInputStream(cliente.getInputStream());

            String mensaje;
            enviarMensaje("Introduzca un nombre para el usuario: ");
            mensaje = in.readUTF();

            if (registrarCliente(mensaje)) {
                enviarMensaje("Te has conectado con el nic " + mensaje);

                while (true) {
                    mensaje = in.readUTF();

                    //TODO
                    //FALLO CON EL ENVIO DE MENSAJES
                    if (mensaje.startsWith(Mensajes.CHARLAR)) {
                        String[] partes = mensaje.split(" ");
                        String nic = partes[1];
                        destinatario = Servidor.buscarClientePorNombre(nic);
                        if (destinatario != null) {
                            enviarMensaje("Has iniciado una conversación con " + nic);
                            while (!mensaje.equalsIgnoreCase("SALIR")) {
                                mensaje = in.readUTF();
                                enviarMensajeAOtro(destinatario, mensaje);
                            }
                        } else {
                            enviarMensaje(Mensajes.usuarioNoEncontrado());
                        }
                        continue;
                    }


                    if (!mensaje.startsWith("#")) {
                        enviarMensaje(mensaje + Mensajes.comandoNoExiste());
                        continue;
                    }

                    if (mensaje.startsWith("#")
                            && (!mensaje.equalsIgnoreCase(Conexion.SHUTDOWN)
                            && !mensaje.equalsIgnoreCase(Conexion.SALIR)
                            && !mensaje.equalsIgnoreCase(Mensajes.AYUDA)
                            && !mensaje.equalsIgnoreCase(Mensajes.LISTAR)
                            && !mensaje.contains(Mensajes.CHARLAR))) {
                        enviarMensaje(mensaje + Mensajes.comandoNoExiste());
                        continue;
                    }

                    if (Mensajes.AYUDA.equalsIgnoreCase(mensaje)) {
                        enviarMensaje(Mensajes.ayuda());
                        continue;
                    }

                    if (Mensajes.LISTAR.equalsIgnoreCase(mensaje)) {
                        enviarMensaje(listaClientesConectados());
                        continue;
                    }

                    if (Conexion.SALIR.equalsIgnoreCase(mensaje)) {
                        enviarMensaje(Conexion.FIN_CONN_CLIENTE);
                        Servidor.CLIENTES_CONECTADOS.remove(this);
                        break;
                    }

                    if (Conexion.SHUTDOWN.equalsIgnoreCase(mensaje)) {
                        Servidor.shutdown();
                        break;
                    }
                }
            } else {
                enviarMensaje("El nombre de usuario ya existe.");
                enviarMensaje(Conexion.FIN_CONN_CLIENTE);
            }
        } catch (IOException ex) {
            Logger.getLogger(HiloCliente.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Cerrando cliente " + nombre);
        }

        System.out.println(this.nombre + " desconectado.");
    }


    //PROBABLEMENTE NO FUNCIONE BIEN
    private void enviarMensajeAOtro(HiloCliente destinatario, String mensaje) throws IOException {
        DataOutputStream out = new DataOutputStream(destinatario.getCliente().getOutputStream());
        out.writeUTF(this.nombre + ": " + mensaje);
    }


    private synchronized void enviarMensaje(String mensaje) throws IOException {
        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
        out.writeUTF(mensaje);
    }

    public synchronized void shutdown() {
        try {

            enviarMensaje(Conexion.FIN_CONN_CLIENTE);
            cliente.close();

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean registrarCliente(String nombre) {
        synchronized (Servidor.CLIENTES_CONECTADOS) {
            this.nombre = nombre;
            if (Servidor.CLIENTES_CONECTADOS.contains(this)) {
                System.err.println("Rechazando conexion para " + nombre);
                return false;
            }

            Servidor.CLIENTES_CONECTADOS.add(this);
        }
        synchronized (Servidor.HISTORICO_CONEXIONES) {
            Integer conexiones = Servidor.HISTORICO_CONEXIONES.get(nombre);
            conexiones = conexiones == null ? 1 : ++conexiones;
            Servidor.HISTORICO_CONEXIONES.put(nombre, conexiones);
        }

        return true;
    }

    private String listaClientesConectados() {
        StringBuilder sb = new StringBuilder();
        synchronized (Servidor.CLIENTES_CONECTADOS) {
            sb.append("En este momento están conectados " + Servidor.CLIENTES_CONECTADOS.size() + " usuarios:\n");
            for (HiloCliente hc : Servidor.CLIENTES_CONECTADOS) {
                sb.append(hc.getNombre()).append("\n");
            }
        }
        return sb.toString();
    }


    public String getNombre() {
        return nombre;
    }

    public Socket getCliente() {
        return cliente;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.nombre);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HiloCliente other = (HiloCliente) obj;
        return Objects.equals(this.nombre, other.nombre);
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
