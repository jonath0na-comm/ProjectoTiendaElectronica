/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package tiendaelectronica;

import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jonathan vargas arciniega 
 * 26 de abril de 2026 
 */
public class Productos extends javax.swing.JDialog {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Productos.class.getName());

    /**
     * Creates new form Produc
     */
    public Productos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
         jLabel12.setText("REGISTRO DE PRODUCTOS");
         jButton1.setEnabled(true);  // Guardar ON
         jButton2.setEnabled(false); // Actualizar OFF
         jTextField1.requestFocus();
         SwingUtilities.invokeLater(new Runnable() {
         public void run() {
             jTextField1.requestFocusInWindow();
         }
     });
}
    
     public void setDatos(String codigo, String descripcion, String categoria,
            String costo, String precio, String stockActual,
            String stockMinimo, String entrega, String estado,
            String demanda) {
        jTextField1.setText(codigo);
        jTextField2.setText(descripcion);
        jComboBox2.setSelectedItem(categoria);
        jTextField4.setText(costo);
        jTextField5.setText(precio);
        jTextField6.setText(stockActual);
        jTextField7.setText(stockMinimo);
        jTextField8.setText(entrega);
        jTextField9.setText(demanda);
        jComboBox1.setSelectedItem(estado);
        jTextField1.setEditable(false);
        jLabel12.setText("EDITAR PRODUCTO");
        jButton1.setEnabled(false); 
        jButton2.setEnabled(true);  
    }
     
     private boolean validarDatos() {
    String codigo = jTextField1.getText().trim();
    String descripcion = jTextField2.getText().trim();
    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "El código del producto está vacío");
        return false;
    }
    if (descripcion.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "La descripción del producto está vacía");
        return false;
    }
    try {
        double costo = Double.parseDouble(jTextField4.getText());
        double precio = Double.parseDouble(jTextField5.getText());
        int stockActual = Integer.parseInt(jTextField6.getText());
        int stockMinimo = Integer.parseInt(jTextField7.getText());
        int entrega = Integer.parseInt(jTextField8.getText());
        int demanda = Integer.parseInt(jTextField9.getText());
        // COSTO
        if (costo <= 0) {
            JOptionPane.showMessageDialog(this,
                    "El costo debe ser mayor a 0");
            return false;
        }
        // PRECIO
        if (precio <= 0) {
            JOptionPane.showMessageDialog(this,
                    "El precio debe ser mayor a 0");
            return false;
        }
        // COSTO MAYOR AL PRECIO
        if (costo >= precio) {
            int opc = JOptionPane.showConfirmDialog(this,
                    "El costo es mayor o igual al precio.\n¿Desea continuar?",
                    "Advertencia",
                    JOptionPane.YES_NO_OPTION);
            if (opc != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        // STOCK ACTUAL
        if (stockActual < 0) {
            JOptionPane.showMessageDialog(this,
                    "El stock actual no puede ser negativo");
            return false;
        }
        // STOCK MINIMO
        if (stockMinimo <= 0) {
            JOptionPane.showMessageDialog(this,
                    "El stock mínimo debe ser mayor a 0");
            return false;
        }
        // ENTREGA
        if (entrega <= 0) {
            JOptionPane.showMessageDialog(this,
                    "El tiempo de entrega debe ser mayor a 0");
            return false;
        }
        // DEMANDA
        if (demanda <= 0) {
            JOptionPane.showMessageDialog(this,
                    "La demanda actual debe ser mayor a 0");
            return false;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "Hay valores inválidos.\n"
                + "Verifique que costo, precio, stock, entrega y demanda sean números válidos.");
        return false;
    }
    return true;
}
     
    private boolean existeClave(String codigo) {
        try {
            File archivo = new File("productos.csv");
            if (!archivo.exists()) return false;
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.split(",")[0].equals(codigo)) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (Exception e) {
        }
        return false;
    }

    private void guardarProducto() {
    if (!validarDatos()) return;
    String codigo = jTextField1.getText();
    if (existeClave(codigo)) {
        JOptionPane.showMessageDialog(this,
                "Ya existe un producto con el código: " + codigo);
        return;
    }
    try {
        double costo = Double.parseDouble(jTextField4.getText());
        double precio = Double.parseDouble(jTextField5.getText());
        PrintWriter pw = new PrintWriter(new FileWriter("productos.csv", true));
        pw.println(
                jTextField1.getText() + "," +
                jTextField2.getText() + "," +
                jComboBox2.getSelectedItem() + "," +
                String.format("%.2f", costo) + "," +
                String.format("%.2f", precio) + "," +
                jTextField6.getText() + "," +
                jTextField7.getText() + "," +
                jTextField8.getText() + "," +
                jComboBox1.getSelectedItem() + "," +
                jTextField9.getText()
        );
        pw.close();
        JOptionPane.showMessageDialog(this,
                "Producto guardado correctamente");
        dispose();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Ocurrió un error al guardar el producto");
    }
}

    private void actualizarProducto() {
    if (!validarDatos()) return;
    try {
        double costo = Double.parseDouble(jTextField4.getText());
        double precio = Double.parseDouble(jTextField5.getText());
        File archivo = new File("productos.csv");
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        ArrayList<String> lista = new ArrayList<>();
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split(",");
            if (datos[0].equals(jTextField1.getText())) {
                linea = String.join(",",
                        jTextField1.getText(),
                        jTextField2.getText(),
                        jComboBox2.getSelectedItem().toString(),
                        String.format("%.2f", costo),
                        String.format("%.2f", precio),
                        jTextField6.getText(),
                        jTextField7.getText(),
                        jTextField8.getText(),
                        jComboBox1.getSelectedItem().toString(),
                        jTextField9.getText()
                );
            }
            lista.add(linea);
        }
        br.close();
        PrintWriter pw = new PrintWriter(new FileWriter(archivo));
        for (String l : lista) {
            pw.println(l);
        }
        pw.close();
        JOptionPane.showMessageDialog(this,
                "Producto actualizado correctamente");
        dispose();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Ocurrió un error al actualizar el producto");
    }
}

    private void desactivarProducto() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Cambiar estado del producto?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;
            File archivo = new File("productos.csv");
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            ArrayList<String> lista = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos[0].equals(jTextField1.getText())) {
                    datos[8] = datos[8].equals("ACTIVO") ? "INACTIVO" : "ACTIVO";
                    linea = String.join(",", datos);
                }
                lista.add(linea);
            }
            br.close();
            PrintWriter pw = new PrintWriter(new FileWriter(archivo));
            for (String l : lista) pw.println(l);
            pw.close();
            JOptionPane.showMessageDialog(this, "Estado actualizado");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error");
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
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/caja.png"))); // NOI18N

        jLabel2.setText("Codigo del Producto");

        jLabel3.setText("Descripción");

        jLabel4.setText("Categoria");

        jLabel5.setText("Costo $");

        jLabel6.setText("Precio $");

        jLabel7.setText("Stock Actual");

        jLabel8.setText("Stock Minimo");

        jLabel9.setText("Tiempo de entrega");

        jLabel10.setText("Estado");

        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Actualizar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(204, 204, 204));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/boton-x.png"))); // NOI18N
        jButton5.setBorder(null);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVO", "INACTIVO" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton3.setText("Eliminar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel11.setText("Demanda Actual");

        jLabel12.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        jLabel12.setText("REGISTRO DE PRODUCTOS ");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Computadoras y Laptops", "Componentes de PC", "Perifericos", "Monitores", "Impresoras y escaneres", "Redes y conectividad", "Almacenamiento", "Accesorios para Celulares", "Smartphones y Tablets", "Audio y Sonido", "Video y Entretenimiento", "Energia y Protección", "Cámaras y VIideovigilancia", "Gadgets y Wearebles", "Consumibles" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel6)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                    .addGap(41, 41, 41)))
                            .addComponent(jTextField1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(72, 72, 72)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField7)
                            .addComponent(jTextField8)
                            .addComponent(jTextField6)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel12)
                        .addComponent(jButton5)))
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
         jButton5.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton5ActionPerformed(evt);
    }
      });
         this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
              guardarProducto();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
            actualizarProducto();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        desactivarProducto();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

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
                Productos dialog = new Productos(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
