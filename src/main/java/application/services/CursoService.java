package application.services;

import application.port.in.CursoUseCases;
import domain.model.curso.Curso;
import domain.port.out.repositories.RepositorioDeCursos;
import domain.valueobjects.curso.Aula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Service
@Transactional
public class CursoService implements CursoUseCases {

    private final RepositorioDeCursos repositorioDeCursos;

    @Autowired
    public CursoService(RepositorioDeCursos repositorioDeCursos) {
        this.repositorioDeCursos = repositorioDeCursos;
    }

    @Override
    public Curso registrarCurso(Integer cursoId, String asignatura, int cupoMaximo, LocalDate fechaInicio, LocalDate fechaFin, Aula aula, String profesorId) {
        Curso curso = new Curso(
                null,
                asignatura,
                fechaInicio,
                fechaFin,
                cupoMaximo,
                aula,
                profesorId
        );
        return repositorioDeCursos.guardar(curso);
    }

    @Override
    public Optional<Curso> buscarCursoPorId(Integer id) {

        if (id < 0 || id == null) {
            throw new IllegalArgumentException("El ID del curso no puede ser nulo o negativo");

        }

        return repositorioDeCursos.buscarPorId(id);
    }

    @Override
    public List<Curso> listarCursos() {
        return repositorioDeCursos.listar();
    }

    @Override
    public void actualizarCurso(Curso curso) {
        repositorioDeCursos.actualizarCurso(curso);
    }

    @Override
    public void eliminarCurso(Integer id) {
        repositorioDeCursos.eliminar(id);
    }

}
