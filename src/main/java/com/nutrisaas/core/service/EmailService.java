package com.nutrisaas.core.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String to, String rawToken) {
        String subject = "Restablecer contraseña";
        String html = "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Restablecer contraseña</title>"
                + "<style>"
                + "  body { font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 0; }"
                + "  .container { max-width: 600px; margin: 40px auto; background-color: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }"
                + "  h2 { color: #4CAF50; }"
                + "  .token-box { background-color: #f0f0f0; border-radius: 8px; padding: 15px; text-align: center; font-size: 1.2em; letter-spacing: 2px; margin: 20px 0; font-weight: bold; }"
                + "  p { line-height: 1.5; }"
                + "  .footer { font-size: 0.9em; color: #777; margin-top: 30px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"container\">"
                + "<h2>Restablecer contraseña</h2>"
                + "<p>Hola,</p>"
                + "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>"
                + "<p>Tu código temporal es:</p>"
                + "<div class=\"token-box\">" + rawToken + "</div>"
                + "<p>Ingresa este código en la pantalla y escribe tu nueva contraseña. El código expira en 1 hora.</p>"
                + "<p>Si no solicitaste este cambio, ignora este correo.</p>"
                + "<div class=\"footer\">&copy; 2025 NutriSaaS. Todos los derechos reservados.</div>"
                + "</div>"
                + "</body>"
                + "</html>";


        sendHtmlEmail(to, subject, html);
    }

    public void sendConfirmationPasswordChanged(String to) {
        String subject = "Contraseña actualizada";
        String html = "<p>Hola,</p><p>Tu contraseña ha sido actualizada correctamente. Si no fuiste tú contacta con soporte inmediatamente.</p>";
        sendHtmlEmail(to, subject, html);
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
