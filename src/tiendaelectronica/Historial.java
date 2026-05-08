/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package tiendaelectronica;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Jonat
 */
public class Historial extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Historial.class.getName());

     private List<Object[]> todasLasFilas = new ArrayList<>();
    /**
     * Creates new form Historial
     */
    public Historial(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Configurar columnas
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Número");
        modelo.addColumn("Fecha");
        modelo.addColumn("Tipo");
        modelo.addColumn("Producto");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Motivo");

        jTable1.setModel(modelo);

        // Cargar historial
        cargarHistorial();

        // Búsqueda en tiempo real por código de producto
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });
    }

    // ═════════════════════════════════════════════
    // CARGAR HISTORIAL
    // ═════════════════════════════════════════════
    public void cargarHistorial() {

        todasLasFilas.clear();

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);

        try {

            // 1. Leer encabezado
            Map<String, String[]> encabezados = new LinkedHashMap<>();

            File fEnc = new File("MovimientoInventarioEncabezado.csv");

            if (fEnc.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(fEnc));

                String linea;
                boolean primera = true;

                while ((linea = br.readLine()) != null) {

                    if (primera) {
                        primera = false;
                        continue;
                    }

                    String[] p = linea.split(",", -1);

                    if (p.length >= 4) {

                        encabezados.put(
                                p[0].trim(),
                                new String[]{
                                    p[1].trim(),
                                    p[2].trim(),
                                    p[3].trim()
                                }
                        );
                    }
                }

                br.close();
            }

            // 2. Leer inventario
            Map<String, String> nombres = new HashMap<>();

            File fInv = new File("productos.csv");

            if (fInv.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(fInv));

                String linea;

                while ((linea = br.readLine()) != null) {

                    String[] d = linea.split(",", -1);

                    if (d.length >= 2) {

                        nombres.put(d[0].trim(), d[1].trim());
                    }
                }

                br.close();
            }

            // 3. Leer detalle
            File fDet = new File("MovimientoInventarioDetalle.csv");

            if (fDet.exists()) {

                BufferedReader br = new BufferedReader(new FileReader(fDet));

                String linea;
                boolean primera = true;

                while ((linea = br.readLine()) != null) {

                    if (primera) {
                        primera = false;
                        continue;
                    }

                    String[] p = linea.split(",", -1);

                    if (p.length < 3) {
                        continue;
                    }

                    String num = p[0].trim();
                    String cod = p[1].trim();
                    String cant = p[2].trim();

                    String[] enc = encabezados.getOrDefault(
                            num,
                            new String[]{"", "", ""}
                    );

                    String nombre = nombres.getOrDefault(cod, cod);

                    todasLasFilas.add(new Object[]{
                        num,
                        enc[0],
                        enc[1],
                        nombre,
                        cant,
                        enc[2],
                        cod
                    });
                }

                br.close();
            }

            // 4. Ordenar descendente por fecha
            todasLasFilas.sort((a, b)
                    -> b[1].toString().compareTo(a[1].toString()));

            // 5. Mostrar filas
            for (Object[] fila : todasLasFilas) {

                modelo.addRow(new Object[]{
                    fila[0],
                    fila[1],
                    fila[2],
                    fila[3],
                    fila[4],
                    fila[5]
                });
            }

        } catch (Exception e) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Error cargando historial"
            );
        }
    }

    // ═════════════════════════════════════════════
    // FILTRAR
    // ═════════════════════════════════════════════
    private void filtrar() {

        String texto = jTextField1.getText().trim().toLowerCase();

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

        modelo.setRowCount(0);

        for (Object[] fila : todasLasFilas) {

            String cod = fila[6].toString().toLowerCase();

            if (texto.isEmpty() || cod.startsWith(texto)) {

                modelo.addRow(new Object[]{
                    fila[0],
                    fila[1],
                    fila[2],
                    fila[3],
                    fila[4],
                    fila[5]
                });
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("DialogInput", 3, 18)); // NOI18N
        jLabel1.setText("HISTORIAL DE MOVIMIENTO ");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextField1.setText("jTextField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(jLabel1)
                .addGap(65, 65, 65)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Historial dialog = new Historial(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
