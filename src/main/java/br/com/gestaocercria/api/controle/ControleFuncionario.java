package br.com.gestaocercria.api.controle;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.gestaocercria.api.entidade.Funcionario;
import br.com.gestaocercria.api.repositorio.RepositorioFuncionario;
import br.com.gestaocercria.api.securityConfig.JwtService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/funcionario")
public class ControleFuncionario {
    private final RepositorioFuncionario acao;

    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    ControleFuncionario(RepositorioFuncionario acao, BCryptPasswordEncoder encoder, JwtService jwtService) {
        this.acao = acao;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/cadastro")
    public Funcionario cadastrar(@RequestBody Funcionario f) {
        String senhaHash = encoder.encode(f.getSenha());

        f.setSenha(senhaHash);
        f.setSenhaTemporaria(false);

        return acao.save(f);
    }

    @GetMapping("/listagem")
    public Iterable<Funcionario> selecionar() {
        return acao.findByExcluidoFalse();
    }

    @GetMapping("/{id}")            
    public Funcionario buscarPorId(@PathVariable @NonNull Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Funcionario editar(@RequestBody Funcionario f) {
        Funcionario existente = acao.findById(f.getId()).orElse(null);

        if (existente == null) {
            return null;
        }

        f.setSenha(existente.getSenha());
        return acao.save(f);
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
                    .body("Usuário não encontrado");
        }

        boolean senhaCorreta =
                encoder.matches(f.getSenha(), usuario.getSenha());

        if (!senhaCorreta) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Senha inválida");
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        Boolean senhaTemporaria = usuario.getSenhaTemporaria();
        usuario.setSenha(null);

        return ResponseEntity.ok(Map.of("token", token,"funcionario", usuario,"senhaTemporaria", senhaTemporaria));
    }

    @PutMapping("/alterar-senha")
    public Funcionario alterarSenha(@RequestBody Funcionario f) {
        Funcionario usuario = acao.findById(f.getId()).orElse(null);

        if (usuario == null) {
            return null;
        }

        String senhaHash = encoder.encode(f.getSenha());
        usuario.setSenha(senhaHash);
        usuario.setSenhaTemporaria(false);

        return acao.save(usuario);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<?> recuperarSenha(@RequestBody Map<String, String> dados) {
        String email = dados.get("email");
        Funcionario usuario = acao.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado.");
        }

        //Gera uma senha temporária
        String senhaTemporaria = gerarSenhaTemporaria();

        //Salva a senha criptografada
        usuario.setSenha(encoder.encode(senhaTemporaria));

        //Marca como temporária
        usuario.setSenhaTemporaria(true);

        acao.save(usuario);

        //Retorna a senha temporária para o sistema
        return ResponseEntity.ok(
            Map.of(
                "mensagem", "Senha temporária gerada com sucesso.",
                "senhaTemporaria", senhaTemporaria
            )
        );
    }

    private String gerarSenhaTemporaria() {
        int numero = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(numero);
    }
}
