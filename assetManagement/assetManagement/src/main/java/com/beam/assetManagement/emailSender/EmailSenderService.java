package com.beam.assetManagement.emailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendPortStatusEmail(List<String> newPorts, List<String> closedPorts,String assetName) throws MessagingException {
        String recipientEmail = "ginyahc62@gmail.com";
        String subject = "Port Status Update";



        List<List<String>> pairedNewPorts = pairPorts(newPorts);
        List<List<String>> pairedClosedPorts = pairPorts(closedPorts);

        Context context = new Context();
        context.setVariable("assetName", assetName);
        context.setVariable("pairedNewPorts", pairedNewPorts);
        context.setVariable("pairedClosedPorts", pairedClosedPorts);
        String content = templateEngine.process("port_status_email_template", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        try {
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(mimeMessage);
            log.info("Port status email sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send port status email.", e);
        }
    }


    private List<List<String>> pairPorts(List<String> ports) {
        List<List<String>> pairedPorts = new ArrayList<>();
        for (int i = 0; i < ports.size(); i += 2) {
            List<String> pair = new ArrayList<>();
            pair.add(ports.get(i));
            if (i + 1 < ports.size()) {
                pair.add(ports.get(i + 1));
            }
            pairedPorts.add(pair);
        }
        return pairedPorts;
    }
}
