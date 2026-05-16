/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package tiendaelectronica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author Jonat
 */
public class Configuración extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Configuración.class.getName());
    private static final String ARCHIVO_CONFIG = "configuracion.csv";
    
    private double costoPorPedido = 0.0;
    private double costoMantenimiento = 0.0;
    private int tiempoEntrega = 0;
 
    public Configuración(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargarConfiguracion();
    }
 
    // ═════════════════════════════════════════════════════════════════════
    // CARGAR CONFIGURACIÓN
    // ═════════════════════════════════════════════════════════════════════
    private void cargarConfiguracion() {
        try {
            File archivo = new File(ARCHIVO_CONFIG);
            
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    boolean primera = true;
                    
                    while ((linea = br.readLine()) != null) {
                        if (primera) {
                            primera = false;
                            continue;
                        }
                        
                        String[] datos = linea.split(",", -1);
                        if (datos.length >= 3) {
                            try {
                                costoPorPedido = Double.parseDouble(datos[0].trim());
                                costoMantenimiento = Double.parseDouble(datos[1].trim());
                                tiempoEntrega = Integer.parseInt(datos[2].trim());
                            } catch (NumberFormatException e) {
                                costoPorPedido = 0.0;
                                costoMantenimiento = 0.0;
                                tiempoEntrega = 0;
                            }
                        }
                    }
                }
                
                mostrarValoresEnCampos();
            } else {
                jTextField1.setText("");
                jTextField2.setText("");
                jTextField3.setText("");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la configuración: " + e.getMessage(),
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ═════════════════════════════════════════════════════════════════════
    // MOSTRAR VALORES EN CAMPOS
    // ═════════════════════════════════════════════════════════════════════
    private void mostrarValoresEnCampos() {
        DecimalFormat df = new DecimalFormat("0.00");
        
        if (costoPorPedido > 0) {
            jTextField1.setText(df.format(costoPorPedido));
        } else {
            jTextField1.setText("");
        }
        
        if (costoMantenimiento > 0) {
            jTextField2.setText(df.format(costoMantenimiento));
        } else {
            jTextField2.setText("");
        }
        
        if (tiempoEntrega > 0) {
            jTextField3.setText(String.valueOf(tiempoEntrega));
        } else {
            jTextField3.setText("");
        }
    }
 
    // ═════════════════════════════════════════════════════════════════════
    // VALIDAR DATOS
    // ═════════════════════════════════════════════════════════════════════
    private boolean validarDatos() {
        String txt1 = jTextField1.getText().trim();
        String txt2 = jTextField2.getText().trim();
        String txt3 = jTextField3.getText().trim();
        
        if (txt1.isEmpty() || txt2.isEmpty() || txt3.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios",
                    "Validación de Datos",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double costo = Double.parseDouble(txt1);
            if (costo <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El costo por pedido debe ser mayor a 0",
                        "Validación de Datos",
                        JOptionPane.WARNING_MESSAGE);
                jTextField1.requestFocus();
                return false;
            }
            
            double mantenimiento = Double.parseDouble(txt2);
            if (mantenimiento <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El costo de mantenimiento debe ser mayor a 0",
                        "Validación de Datos",
                        JOptionPane.WARNING_MESSAGE);
                jTextField2.requestFocus();
                return false;
            }
            
            if (txt3.contains(".") || txt3.contains(",")) {
                JOptionPane.showMessageDialog(this,
                        "El tiempo de entrega debe ser un número entero (sin decimales)",
                        "Validación de Datos",
                        JOptionPane.WARNING_MESSAGE);
                jTextField3.requestFocus();
                return false;
            }
            
            int entrega = Integer.parseInt(txt3);
            if (entrega <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El tiempo de entrega debe ser mayor a 0",
                        "Validación de Datos",
                        JOptionPane.WARNING_MESSAGE);
                jTextField3.requestFocus();
                return false;
            }
            
            costoPorPedido = costo;
            costoMantenimiento = mantenimiento;
            tiempoEntrega = entrega;
            
            return true;
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Los valores deben ser numéricos",
                    "Error de Formato",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
 
    // ═════════════════════════════════════════════════════════════════════
    // GUARDAR CONFIGURACIÓN
    // ═════════════════════════════════════════════════════════════════════
    private void guardarConfiguracion() {
        try {
            try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_CONFIG))) {
                pw.println("CostoPorPedido,CostoMantenimiento,TiempoEntrega");
                
                DecimalFormat df = new DecimalFormat("0.00");
                pw.println(df.format(costoPorPedido) + "," 
                        + df.format(costoMantenimiento) + "," 
                        + tiempoEntrega);
            }
            
            JOptionPane.showMessageDialog(this,
                    "Configuración actualizada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la configuración: " + e.getMessage(),
                    "Error de Guardado",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ═════════════════════════════════════════════════════════════════════
    // MÉTODOS ESTÁTICOS PARA ACCEDER A LA CONFIGURACIÓN
    // ═════════════════════════════════════════════════════════════════════
    public static double obtenerCostoPorPedido() {
        try {
            File archivo = new File(ARCHIVO_CONFIG);
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    boolean primera = true;
                    while ((linea = br.readLine()) != null) {
                        if (primera) {
                            primera = false;
                            continue;
                        }
                        String[] datos = linea.split(",", -1);
                        if (datos.length >= 1) {
                            return Double.parseDouble(datos[0].trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0.0;
    }
 
    public static double obtenerCostoMantenimiento() {
        try {
            File archivo = new File(ARCHIVO_CONFIG);
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    boolean primera = true;
                    while ((linea = br.readLine()) != null) {
                        if (primera) {
                            primera = false;
                            continue;
                        }
                        String[] datos = linea.split(",", -1);
                        if (datos.length >= 2) {
                            return Double.parseDouble(datos[1].trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0.0;
    }
 
    public static int obtenerTiempoEntrega() {
        try {
            File archivo = new File(ARCHIVO_CONFIG);
            if (archivo.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    boolean primera = true;
                    while ((linea = br.readLine()) != null) {
                        if (primera) {
                            primera = false;
                            continue;
                        }
                        String[] datos = linea.split(",", -1);
                        if (datos.length >= 3) {
                            return Integer.parseInt(datos[2].trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0;
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel2.setBackground(new java.awt.Color(102, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Configuración de Parámetros");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel2.setText("Módulo de Análisis de Inventarios");

        jLabel3.setIcon(new javax.swing.ImageIcon("C:\\Users\\Jonat\\Documents\\NetBeansProjects\\TiendaElectronica\\src\\IMG\\base-de-datos (1).png")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel4.setText("Costo por Pedido u Orden de Compra");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel5.setText("Costo de Mantenimiento o Conservación");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel6.setText("Tiempo de Entrega (Lead Time)");

        jButton1.setBackground(new java.awt.Color(30, 58, 95));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Actualizar Configuración");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                Configuración dialog = new Configuración(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
