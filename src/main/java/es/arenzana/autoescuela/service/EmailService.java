package es.arenzana.autoescuela.service;

import es.arenzana.autoescuela.model.Ajustes;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class EmailService {

    private final AjustesService ajustesService;

    public EmailService(AjustesService ajustesService) {
        this.ajustesService = ajustesService;
    }

    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        Ajustes config = ajustesService.obtenerAjustes();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getMailHost());
        mailSender.setPort(config.getMailPort());
        mailSender.setUsername(config.getMailUsername());
        mailSender.setPassword(config.getMailPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        String protocolo = config.getMailProtocol();

        if ("SSL".equals(protocolo)) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", config.getMailPort().toString());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.ssl.checkserveridentity", "false");
            props.put("mail.smtp.ssl.trust", "*");
        } else if ("STARTTLS".equals(protocolo)) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", "*");
        } else {
            props.put("mail.smtp.starttls.enable", "false");
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(config.getMailUsername(), config.getNombreEmpresa());
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo", e);
        }
    }

    public void enviarCorreoBienvenida(String destinatario, String nombreUsuario) {
        Ajustes config = ajustesService.obtenerAjustes();
        String asunto = "Bienvenido a " + config.getNombreEmpresa();
        String mensaje = "Hola " + nombreUsuario + ",\n\nTu cuenta ha sido creada en " + config.getNombreEmpresa();
        enviarCorreo(destinatario, asunto, mensaje);
    }
    
    public void enviarCorreoTest(String destinatario, String nombreUsuario) {
        Ajustes config = ajustesService.obtenerAjustes();
        String asunto = "Prueba correo: " + config.getNombreEmpresa();
        String mensaje = "Si has recibido este correo, la configuración SMTP de " + config.getNombreEmpresa() + " es correcta.";
        enviarCorreo(destinatario, asunto, mensaje);
    }
}
