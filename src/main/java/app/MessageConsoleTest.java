package app;

import exceptions.ErrorConectionMongoException;
import service.MensajeService;
import service.UsuarioService;
import entity.Mensaje;
import entity.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public class MessageConsoleTest {
    public static void main(String[] args) {
        try {
            UsuarioService usuarioService = new UsuarioService();
            MensajeService mensajeService = new MensajeService();

            Usuario remitente = usuarioService.getById(1);
            Usuario destinatario = usuarioService.getById(2);

            if (remitente == null || destinatario == null) {
                System.out.println("Make sure users with id 1 and 2 exist in Mongo before running this test.");
                return;
            }

            Mensaje mensaje = new Mensaje();
            mensaje.setFechaEnvio(LocalDateTime.now());
            mensaje.setContenido("Prueba desde consola: Hola!");
            mensaje.setTipo("privado");
            mensaje.setRemitente(remitente);
            mensaje.setDestinatario(destinatario);

            mensajeService.createMensaje(mensaje);
            System.out.println("Mensaje enviado desde consola con id: " + mensaje.getId());

            System.out.println("Mensajes enviados por remitente:");
            List<Mensaje> enviados = mensajeService.getMensajesPorRemitente(remitente.getId());
            for (Mensaje m : enviados) {
                System.out.println(m.getId() + ": " + m.getContenido() + " -> " + (m.getDestinatario()!=null?m.getDestinatario().getMail():"(grupo)"));
            }

            System.out.println("Mensajes recibidos por destinatario:");
            List<Mensaje> recibidos = mensajeService.getMensajesPorDestinatario(destinatario.getId());
            for (Mensaje m : recibidos) {
                System.out.println(m.getId() + ": " + m.getContenido() + " <- " + (m.getRemitente()!=null?m.getRemitente().getMail():"(grupo)"));
            }

        } catch (ErrorConectionMongoException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
