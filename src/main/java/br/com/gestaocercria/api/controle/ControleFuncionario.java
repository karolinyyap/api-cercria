package br.com.gestaocercria.api.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import br.com.gestaocercria.api.entidade.Funcionario;
import br.com.gestaocercria.api.repositorio.RepositorioFuncionario;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/funcionario")
@CrossOrigin(origins = "http://localhost:4200")
public class ControleFuncionario {
    @Autowired
    private RepositorioFuncionario acao;

    @Autowired
    private BCryptPasswordEncoder encoder;

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
    public Funcionario buscarPorId(@PathVariable Integer id) {
        return acao.findById(id).orElse(null);
    }

    @PutMapping("/edicao")
    public Funcionario editar(@RequestBody Funcionario f) {
        String senhaHash = encoder.encode(f.getSenha());
        f.setSenha(senhaHash);
        return acao.save(f);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Integer id) {
        acao.deleteById(id);
    }

    @PostMapping("/login")
    public Funcionario login(@RequestBody Funcionario f) {
        Funcionario usuario = acao.findByEmail(f.getEmail());

        if (usuario != null && encoder.matches(f.getSenha(), usuario.getSenha())) {
            usuario.setSenha(null); 
            return usuario;
        }

        return null;
    }

    public boolean login(String email, String senhaDigitada) {
        Funcionario usuario = acao.findByEmail(email);

        if (usuario == null) {
            return false;
        }

        return encoder.matches(senhaDigitada, usuario.getSenha());
    }
}
