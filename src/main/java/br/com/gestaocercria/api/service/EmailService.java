package br.com.gestaocercria.api.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarSenhaTemporaria(
            String destinatario,
            String nomeFuncionario,
            String senhaTemporaria) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(destinatario);
        mensagem.setSubject("Recuperação de senha - Sistema CERCRIA");

        mensagem.setText(
            "Olá, " + nomeFuncionario + "!\n\n" +
            "Foi solicitada a recuperação da sua senha " +
            "no Sistema CERCRIA.\n\n" +
            "Sua senha temporária é:\n\n" +
            senhaTemporaria + "\n\n" +
            "Utilize essa senha para acessar o sistema.\n\n" +
            "Após acessar o sistema, você poderá alterar sua senha.\n\n" +
            "Se você não solicitou essa recuperação, " +
            "entre em contato com o responsável pelo sistema.\n\n" +
            "Atenciosamente,\n" +
            "Sistema CERCRIA"
        );

        mailSender.send(mensagem);
    }
}