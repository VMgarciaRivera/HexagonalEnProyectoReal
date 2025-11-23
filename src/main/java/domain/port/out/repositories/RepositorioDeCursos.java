package domain.port.out.repositories;

import domain.model.curso.Curso;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Victor Garcia
 */
public interface RepositorioDeCursos {
    // Interfaz que utiliza el adaptador del repositorio
    Curso guardar(Curso curso);
    Optional<Curso> buscarPorId(Integer id);
    List<Curso> listar();
    void actualizarCurso(Curso curso);
    void eliminar(Integer id);
}
