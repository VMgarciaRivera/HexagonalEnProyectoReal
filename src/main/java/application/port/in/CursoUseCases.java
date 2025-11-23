package application.port.in;

import domain.model.curso.Curso;
import domain.valueobjects.curso.Aula;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CursoUseCases {
    Curso registrarCurso(Integer cursoId ,String asignatura, int cupoMaximo, LocalDate fechaInicio, LocalDate fechaFin, Aula aula, String profesorId);
    Optional<Curso> buscarCursoPorId(Integer Id);
    List<Curso> listarCursos();
    void actualizarCurso(Curso curso);
    void eliminarCurso(Integer id);
}
