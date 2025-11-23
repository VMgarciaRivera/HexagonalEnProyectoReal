package infraestructure.adapter.in;

import application.port.in.CursoUseCases;
import domain.model.curso.Curso;
import domain.valueobjects.curso.Aula;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class CursoController {

    private final CursoUseCases cursoUseCases;

    public CursoController(CursoUseCases cursoUseCases) {
        this.cursoUseCases = cursoUseCases;
    }

    /**
     * Controlador para registrar un curso desde cualquier adaptador de entrada (Swing, REST, CLI, etc).
     */
    public Curso registrarCurso(
            String asignatura,
            int cupoMaximo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Aula aula,
            String profesorId
    ) {

        if (asignatura == null || asignatura.isBlank()) {
            throw new IllegalArgumentException("La asignatura no puede estar vacía");
        }

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }

        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor a cero");
        }

        // Delegación al caso de uso (lógica de negocio)
        return cursoUseCases.registrarCurso(
                null,
                asignatura,
                cupoMaximo,
                fechaInicio,
                fechaFin,
                aula,
                profesorId
        );
    }

    public Optional<Curso> buscarCursoPorId(Integer Id) {

        if (Id == null || Id < 0) {
            throw new IllegalArgumentException("El ID del curso no puede estar vacío");
        }

        return cursoUseCases.buscarCursoPorId(Id);
    }

    public List<Curso> listarCursos() {
        return cursoUseCases.listarCursos();
    }

    public void editarCurso(Curso curso) {
        cursoUseCases.actualizarCurso(curso);
    }

    public void eliminarCurso(Integer id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("El ID del curso no puede estar vacío");
        }
        cursoUseCases.eliminarCurso(id);
    }

}
