package infraestructure.adapter.in.swing.Cursos;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import domain.model.curso.Curso;
import infraestructure.adapter.in.CursoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.Locale;

@Component
public class BuscarCursos extends JFrame {

    private JTable cursoTable;
    private JPanel buscarCursoPanel;
    private JTextField asignaturaInput;
    private JTextField fechaInicioInput;
    private JTextField fechaFinInput;
    private JTextField cupoMaximoInput;
    private JTextField profesorIdInput;
    private JLabel CursoIdText;
    private JTextField buscarIdInput;
    private JButton buscarButton;
    private JButton guardarButton;
    private JButton editarButton;
    private JButton eliminarCursoButton;
    private JTextField aulaInput;

    private ApplicationContext context;

    @Autowired
    public BuscarCursos(ApplicationContext context) {
        this.context = context;
        setContentPane(buscarCursoPanel);
        setTitle("Buscar Curso");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(false);

        cargarCursosEnTabla();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                JFrameCursos ventanaCursos = context.getBean(JFrameCursos.class);
                ventanaCursos.setVisible(true);
            }
        });

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarCursoPorId();
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editarButton.getText().equals("Editar")) {
                    habilitarEditarInputs(true);
                    editarButton.setText("Cancelar");
                } else {
                    habilitarEditarInputs(false);
                    editarButton.setText("Editar");
                    buscarCursoPorId();
                }
            }
        });

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CursoController controller = context.getBean(CursoController.class);
                Curso cursoEditado = new Curso(
                        Integer.parseInt(CursoIdText.getText()),
                        asignaturaInput.getText(),
                        LocalDate.parse(fechaInicioInput.getText()),
                        LocalDate.parse(fechaFinInput.getText()),
                        Integer.parseInt(cupoMaximoInput.getText()),
                        null,
                        profesorIdInput.getText()
                );

                int siono = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea guardar los cambios?", "Confirmar Guardado", JOptionPane.YES_NO_OPTION);
                if(siono == 0){
                    try {
                        controller.editarCurso(cursoEditado);
                        JOptionPane.showMessageDialog(null, "Cambios guardados con éxito");
                        habilitarEditarInputs(false);
                        editarButton.setText("Editar");
                        cargarCursosEnTabla();
                        buscarCursoPorId();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al guardar los cambios: " + ex.getMessage());
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        eliminarCursoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CursoController controller = context.getBean(CursoController.class);

                int siono = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el curso?", "Confirmar Eliminacion", JOptionPane.YES_NO_OPTION);
                if(siono == 0){
                    try {
                        controller.eliminarCurso(Integer.parseInt(CursoIdText.getText()));
                        JOptionPane.showMessageDialog(null, "Curso eliminado con éxito");
                        limpiarCampos();
                        habilitarEditarInputs(false);
                        editarButton.setText("Editar");
                        cargarCursosEnTabla();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al eliminar el curso: " + ex.getMessage());
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    private void buscarCursoPorId() {
        CursoController controller = context.getBean(CursoController.class);
        String textoDelID = buscarIdInput.getText();
        if (!textoDelID.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese un ID válido");
            limpiarCampos();
            throw new IllegalArgumentException("ID inválido");
        }

        var cursoPorID = controller.buscarCursoPorId(Integer.parseInt(buscarIdInput.getText()));

        try {
            var curso = cursoPorID.get();
            CursoIdText.setText(curso.getCursoId().toString());
            asignaturaInput.setText(curso.getAsignatura());
            fechaInicioInput.setText(curso.getFechaInicio().toString());
            fechaFinInput.setText(curso.getFechaFin().toString());
            cupoMaximoInput.setText(String.valueOf(curso.getCupoMaximo()));
            aulaInput.setText(curso.getAula() != null ? curso.getAula().toString() : "");
            profesorIdInput.setText(curso.getProfesorId());

            editarButton.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Curso no encontrado");
            limpiarCampos();
            editarButton.setEnabled(false);
        }
    }

    private void cargarCursosEnTabla() {
        CursoController controller = context.getBean(CursoController.class);

        var cursos = controller.listarCursos(); // obtiene lista de cursos

        // Definir columnas
        String[] columnas = {"ID", "Asignatura", "Inicio", "Fin", "Cupo", "Profesor"};

        // Crear matriz de datos
        Object[][] datos = new Object[cursos.size()][columnas.length];

        for (int i = 0; i < cursos.size(); i++) {
            var c = cursos.get(i);
            datos[i][0] = c.getCursoId();
            datos[i][1] = c.getAsignatura();
            datos[i][2] = c.getFechaInicio();
            datos[i][3] = c.getFechaFin();
            datos[i][4] = c.getCupoMaximo();
            datos[i][5] = c.getProfesorId();
        }

        // Cargar en la tabla
        cursoTable.setModel(new DefaultTableModel(datos, columnas));
    }

    public void limpiarCampos() {
        CursoIdText.setText("");
        asignaturaInput.setText("");
        fechaInicioInput.setText("");
        fechaFinInput.setText("");
        cupoMaximoInput.setText("");
        profesorIdInput.setText("");
        buscarIdInput.setText("");
    }

    public void habilitarEditarInputs(boolean habilitar) {
        asignaturaInput.setEditable(habilitar);
        fechaInicioInput.setEditable(habilitar);
        fechaFinInput.setEditable(habilitar);
        cupoMaximoInput.setEditable(habilitar);
        profesorIdInput.setEditable(habilitar);
        guardarButton.setEnabled(habilitar);
        eliminarCursoButton.setEnabled(habilitar);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        buscarCursoPanel = new JPanel();
        buscarCursoPanel.setLayout(new GridLayoutManager(11, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 24, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Buscar Curso");
        buscarCursoPanel.add(label1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        buscarCursoPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cursoTable = new JTable();
        scrollPane1.setViewportView(cursoTable);
        final JLabel label2 = new JLabel();
        label2.setText("Curso ID");
        buscarCursoPanel.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Asignatura");
        buscarCursoPanel.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Fecha Inicio");
        buscarCursoPanel.add(label4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Fecha fin");
        buscarCursoPanel.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Cupo Maximo");
        buscarCursoPanel.add(label6, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Profesor ID");
        buscarCursoPanel.add(label7, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        asignaturaInput = new JTextField();
        asignaturaInput.setEditable(false);
        asignaturaInput.setEnabled(true);
        Font asignaturaInputFont = this.$$$getFont$$$(null, Font.BOLD, -1, asignaturaInput.getFont());
        if (asignaturaInputFont != null) asignaturaInput.setFont(asignaturaInputFont);
        asignaturaInput.setForeground(new Color(-16777216));
        buscarCursoPanel.add(asignaturaInput, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fechaInicioInput = new JTextField();
        fechaInicioInput.setEditable(false);
        fechaInicioInput.setEnabled(true);
        Font fechaInicioInputFont = this.$$$getFont$$$(null, Font.BOLD, -1, fechaInicioInput.getFont());
        if (fechaInicioInputFont != null) fechaInicioInput.setFont(fechaInicioInputFont);
        fechaInicioInput.setForeground(new Color(-16777216));
        buscarCursoPanel.add(fechaInicioInput, new GridConstraints(5, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fechaFinInput = new JTextField();
        fechaFinInput.setEditable(false);
        fechaFinInput.setEnabled(true);
        Font fechaFinInputFont = this.$$$getFont$$$(null, Font.BOLD, -1, fechaFinInput.getFont());
        if (fechaFinInputFont != null) fechaFinInput.setFont(fechaFinInputFont);
        fechaFinInput.setForeground(new Color(-16777216));
        buscarCursoPanel.add(fechaFinInput, new GridConstraints(6, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cupoMaximoInput = new JTextField();
        cupoMaximoInput.setEditable(false);
        cupoMaximoInput.setEnabled(true);
        Font cupoMaximoInputFont = this.$$$getFont$$$(null, Font.BOLD, -1, cupoMaximoInput.getFont());
        if (cupoMaximoInputFont != null) cupoMaximoInput.setFont(cupoMaximoInputFont);
        cupoMaximoInput.setForeground(new Color(-16777216));
        buscarCursoPanel.add(cupoMaximoInput, new GridConstraints(7, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        profesorIdInput = new JTextField();
        profesorIdInput.setEditable(false);
        profesorIdInput.setEnabled(true);
        Font profesorIdInputFont = this.$$$getFont$$$(null, Font.BOLD, -1, profesorIdInput.getFont());
        if (profesorIdInputFont != null) profesorIdInput.setFont(profesorIdInputFont);
        profesorIdInput.setForeground(new Color(-16777216));
        buscarCursoPanel.add(profesorIdInput, new GridConstraints(9, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        CursoIdText = new JLabel();
        CursoIdText.setText("");
        buscarCursoPanel.add(CursoIdText, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarIdInput = new JTextField();
        buscarCursoPanel.add(buscarIdInput, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Buscar Por ID");
        buscarCursoPanel.add(label8, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarButton = new JButton();
        buscarButton.setText("Buscar");
        buscarCursoPanel.add(buscarButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        guardarButton = new JButton();
        guardarButton.setEnabled(false);
        guardarButton.setText("Guardar");
        buscarCursoPanel.add(guardarButton, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eliminarCursoButton = new JButton();
        eliminarCursoButton.setEnabled(false);
        eliminarCursoButton.setText("Eliminar Curso");
        buscarCursoPanel.add(eliminarCursoButton, new GridConstraints(10, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editarButton = new JButton();
        editarButton.setEnabled(false);
        editarButton.setText("Editar");
        buscarCursoPanel.add(editarButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Aula");
        buscarCursoPanel.add(label9, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        aulaInput = new JTextField();
        aulaInput.setEditable(false);
        buscarCursoPanel.add(aulaInput, new GridConstraints(8, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return buscarCursoPanel;
    }

}
