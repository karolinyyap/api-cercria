package br.com.gestaocercria.api.controle;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.gestaocercria.api.entidade.Funcionario;
import br.com.gestaocercria.api.repositorio.RepositorioFuncionario;
import br.com.gestaocercria.api.securityConfig.JwtService;
import br.com.gestaocercria.api.service.EmailService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/funcionario")
public class ControleFuncionario {
    private final EmailService emailService;

    private final RepositorioFuncionario acao;

    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    ControleFuncionario(RepositorioFuncionario acao, BCryptPasswordEncoder encoder, JwtService jwtService, EmailService emailService) {
        this.acao = acao;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody Funcionario f) {

        Funcionario existente = acao.findByEmail(f.getEmail());

        if (existente != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Já existe um funcionário cadastrado com este e-mail.");
        }

        String senhaHash = encoder.encode(f.getSenha());
        f.setSenha(senhaHash);

        return ResponseEntity.ok(acao.save(f));
    }

    @GetMapping("/listagem")
    public Iterable<Funcionario> selecionar() {
        return acao.findByExcluidoFalseOrderByNomeAsc();
    }

    @GetMapping("/{id}")            
    public Funcionario buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public ResponseEntity<?> editar(@RequestBody Funcionario f) {

        Funcionario existente = acao.findById(f.getId()).orElse(null);

        if (existente == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado.");
        }

        Funcionario funcionarioComEmail = acao.findByEmail(f.getEmail());

        if (funcionarioComEmail != null &&
            funcionarioComEmail.getId() != f.getId()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Já existe outro funcionário cadastrado com este e-mail.");
        }

        f.setSenha(existente.getSenha());

        return ResponseEntity.ok(acao.save(f));
    }

    @PutMapping("/excluir/{id}")
    public Funcionario excluir(@PathVariable Integer id) {

        Funcionario func = acao.findById(id)
            .orElseThrow(() -> new RuntimeException("Funcionario não encontrado"));

        func.setExcluido(true);

        return acao.save(func);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Funcionario f) {

        Funcionario usuario = acao.findByEmail(f.getEmail());

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos");
        }

        if (usuario.getSenha() == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Funcionário não possui senha cadastrada");
        }

        if (f.getSenha() == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos");
        }

        boolean senhaCorreta = encoder.matches(
            f.getSenha(),
            usuario.getSenha()
        );

        if (!senhaCorreta) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario.getEmail());

        if (token == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar token de autenticação");
        }

        usuario.setSenha(null);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("funcionario", usuario);

        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/alterar-senha")
    public Funcionario alterarSenha(@RequestBody Funcionario f) {

        Funcionario usuario = acao.findById(f.getId()).orElse(null);

        if (usuario == null) {
            return null;
        }

        String senhaHash = encoder.encode(f.getSenha());
        usuario.setSenha(senhaHash);

        return acao.save(usuario);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(
            @RequestBody Map<String, String> dados) {

        String email = dados.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("E-mail não informado.");
        }

        email = email.trim().toLowerCase();

        Funcionario funcionario = acao.findByEmail(email);

        if (funcionario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("E-mail não encontrado.");
        }

        // Gera uma senha temporária de 8 números
        SecureRandom random = new SecureRandom();

        String senhaTemporaria = String.format(
                "%08d",
                random.nextInt(100_000_000)
        );

        // Salva a senha criptografada
        funcionario.setSenha(
                encoder.encode(senhaTemporaria)
        );

        acao.save(funcionario);

        // Envia a senha para o e-mail
        emailService.enviarSenhaTemporaria(
                funcionario.getEmail(),
                funcionario.getNome(),
                senhaTemporaria
        );

        return ResponseEntity.ok(
                Map.of(
                        "mensagem",
                        "Uma senha temporária foi enviada para o e-mail cadastrado."
                )
        );
    }
}
