package spring.infra.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEventPresenceConfirmation(
            String to,
            String confirmationUrl,
            String eventName
    ) {
        if (confirmationUrl == null || confirmationUrl.isBlank()) {
            throw new IllegalArgumentException("No URL provided");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmação de presença");
        message.setText("""
                Olá!

                Clique no link abaixo para confirmar sua presença no evento: %s

                %s

                Atenciosamente,
                Equipe do Evento
                """.formatted(eventName, confirmationUrl));

        mailSender.send(message);
    }
}