package infraestructure.adapter.out.repositories.Curso;

import domain.model.curso.Curso;
import domain.port.out.repositories.RepositorioDeCursos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaRepositorioDeCursos implements RepositorioDeCursos {

    private final SpringDataCursoRepository springRepo;
    @PersistenceContext
    private EntityManager entityManager;

    public JpaRepositorioDeCursos(SpringDataCursoRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        String sql = """
        INSERT INTO cursos (asignatura, cupo_maximo, fecha_inicio, fecha_fin, aula, profesor_id)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        entityManager.createNativeQuery(sql)
                .setParameter(1, curso.getAsignatura())
                .setParameter(2, curso.getCupoMaximo())
                .setParameter(3, curso.getFechaInicio())
                .setParameter(4, curso.getFechaFin())
                .setParameter(5, curso.getAula() != null ? curso.getAula().toString() : null)
                .setParameter(6, curso.getProfesorId() != null ? curso.getProfesorId().toString() : null)
                .executeUpdate();

        return curso;
    }


    @Override
    public Optional<Curso> buscarPorId(Integer id) {
        String sql = """
        SELECT
            id, asignatura, fecha_inicio, fecha_fin, cupo_maximo, aula, profesor_id
        FROM cursos WHERE id = ?
        """;

        List<Object[]> results = entityManager
                .createNativeQuery(sql)
                .setParameter(1, id)
                .getResultList();

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Object[] row = results.getFirst();

        LocalDate fechaInicio = ((java.sql.Date) row[2]).toLocalDate();
        LocalDate fechaFin    = ((java.sql.Date) row[3]).toLocalDate();

        Curso curso = new Curso(
                (Integer) row[0],
                (String) row[1],
                fechaInicio,
                fechaFin,
                (Integer) row[4],
                null,
                (String) row[6]
        );

        return Optional.of(curso);
    }

    @Override
    public List<Curso> listar() {
        String sql = """
    SELECT
        id, asignatura, fecha_inicio, fecha_fin, cupo_maximo, profesor_id
    FROM cursos
    """;

        List<Object[]> results = entityManager
                .createNativeQuery(sql)
                .getResultList();

        return results.stream().map(row -> {

            LocalDate fechaInicio = row[2] != null
                    ? ((java.sql.Date) row[2]).toLocalDate()
                    : null;

            LocalDate fechaFin = row[3] != null
                    ? ((java.sql.Date) row[3]).toLocalDate()
                    : null;

            return new Curso(
                    (Integer) row[0],           // id
                    (String) row[1],            // asignatura
                    fechaInicio,                // fecha inicio
                    fechaFin,                   // fecha fin
                    (Integer) row[4],           // cupo maximo
                    null,                       // aula (aún no lo manejas)
                    (String) row[5]             // profesor id
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void actualizarCurso(Curso curso) {
        String sql = """
                UPDATE cursos SET
                    asignatura = ?,
                    cupo_maximo = ?,
                    fecha_inicio = ?,
                    fecha_fin = ?,
                    aula = ?,
                    profesor_id = ?
                WHERE id = ? 
                
                """;
        entityManager.createNativeQuery(sql)
                .setParameter(1, curso.getAsignatura())
                .setParameter(2, curso.getCupoMaximo())
                .setParameter(3, curso.getFechaInicio())
                .setParameter(4, curso.getFechaFin())
                .setParameter(5, curso.getAula() != null ? curso.getAula().toString() : null)
                .setParameter(6, curso.getProfesorId() != null ? curso.getProfesorId().toString() : null)
                .setParameter(7, curso.getCursoId())
                .executeUpdate();
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        String sql = "DELETE FROM cursos WHERE id = ?";
        entityManager.createNativeQuery(sql)
                .setParameter(1, id)
                .executeUpdate();
    }

}
