package br.com.gestaocercria.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${RESEND_API_KEY}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void enviarSenhaTemporaria(
            String destinatario,
            String nomeFuncionario,
            String senhaTemporaria) {

        String html = """
            <h2>Recuperação de senha - Sistema CERCRIA</h2>

            <p>Olá, %s!</p>

            <p>Foi solicitada a recuperação da sua senha
            no Sistema CERCRIA.</p>

            <p>Sua senha temporária é:</p>

            <h3>%s</h3>

            <p>Utilize essa senha para acessar o sistema.</p>

            <p>Após acessar o sistema, você poderá alterar
            sua senha para uma senha definitiva.</p>

            <p>Se você não solicitou essa recuperação,
            entre em contato com o responsável pelo sistema.</p>

            <br>

            <p>Atenciosamente,<br>
            <strong>Sistema CERCRIA</strong></p>
            """.formatted(nomeFuncionario, senhaTemporaria);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Sistema CERCRIA <onboarding@resend.dev>")
                .to(destinatario)
                .subject("Recuperação de senha - Sistema CERCRIA")
                .html(html)
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Erro ao enviar e-mail pelo Resend.", e);
        }
    }
}