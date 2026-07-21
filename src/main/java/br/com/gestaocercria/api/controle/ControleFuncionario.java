package br.com.gestaocercria.api.controle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Funcionario;
import br.com.gestaocercria.api.repositorio.RepositorioFuncionario;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/funcionario")
public class ControleFuncionario {
    private final RepositorioFuncionario acao;

    private final BCryptPasswordEncoder encoder;

    ControleFuncionario(RepositorioFuncionario acao, BCryptPasswordEncoder encoder) {
        this.acao = acao;
        this.encoder = encoder;
    }

    @PostMapping("/cadastro")
    public Funcionario cadastrar(@RequestBody Funcionario f) {
        String senhaHash = encoder.encode(f.getSenha());
        f.setSenha(senhaHash);
        return acao.save(f);
    }

    @GetMapping("/listagem")
    public Iterable<Funcionario> selecionar() {
        return acao.findAll();
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

    @DeleteMapping("/{id}")
    public void remover(@PathVariable @NonNull Integer id) {
        acao.deleteById(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Funcionario f) {

        Funcionario usuario = acao.findByEmail(f.getEmail());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
        }

        boolean senhaCorreta = encoder.matches(f.getSenha(),usuario.getSenha());

        if (!senhaCorreta) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha inválida");
        }

        usuario.setSenha(null);

        return ResponseEntity.ok(usuario);
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
}
