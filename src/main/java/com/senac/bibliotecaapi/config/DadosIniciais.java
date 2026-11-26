package com.senac.bibliotecaapi.config;

import com.senac.bibliotecaapi.entity.Autor;
import com.senac.bibliotecaapi.entity.Categoria;
import com.senac.bibliotecaapi.entity.Livro;
import com.senac.bibliotecaapi.entity.PerfilUsuario;
import com.senac.bibliotecaapi.entity.Usuario;
import com.senac.bibliotecaapi.repository.AutorRepository;
import com.senac.bibliotecaapi.repository.CategoriaRepository;
import com.senac.bibliotecaapi.repository.LivroRepository;
import com.senac.bibliotecaapi.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

// Insere um usuário, uma categoria, um autor e um livro ao iniciar,
// para o exemplo do POST /emprestimos funcionar de primeira (ids 1).
@Component
public class DadosIniciais implements CommandLineRunner {

    private final CategoriaRepository categorias;
    private final AutorRepository autores;
    private final LivroRepository livros;
    private final UsuarioRepository usuarios;

    public DadosIniciais(CategoriaRepository categorias, AutorRepository autores,
                         LivroRepository livros, UsuarioRepository usuarios) {
        this.categorias = categorias;
        this.autores = autores;
        this.livros = livros;
        this.usuarios = usuarios;
    }

    @Override
    public void run(String... args) {
        if (livros.count() > 0 || usuarios.count() > 0) {
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setNome("Romance");
        categoria = categorias.save(categoria);

        Autor autor = new Autor();
        autor.setNome("Machado de Assis");
        autor.setNacionalidade("Brasileira");
        autor = autores.save(autor);

        Livro livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setIsbn("9788535911664");
        livro.setAnoPublicacao(1899);
        livro.setCategoria(categoria);
        livro.setAutores(Set.of(autor));
        livros.save(livro);

        PerfilUsuario perfil = new PerfilUsuario();
        perfil.setTelefone("11999999999");
        perfil.setEndereco("Rua A, 100");
        perfil.setDataNascimento(LocalDate.of(2000, 5, 10));

        Usuario usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setPerfil(perfil);
        usuarios.save(usuario);
    }
}