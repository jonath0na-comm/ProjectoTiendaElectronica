/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tiendaelectronica;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jonat
 */
public class Inventario extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Inventario.class.getName());
    private static final String INVENTARIO_CSV = "productos.csv";
    private static final String ENCABEZADO_CSV = "MovimientoInventarioEncabezado.csv";
    private static final String DETALLE_CSV    = "MovimientoInventarioDetalle.csv";
    private DefaultTableModel modeloRemision = new DefaultTableModel();
    /**
     * Creates new form Inventario
     */
    public Inventario() {
        initComponents();
        jTextField1.requestFocus();
        // Tabla de remisión: columnas Cantidad y Producto
        modeloRemision.addColumn("Cantidad");
        modeloRemision.addColumn("Producto");
        jTable1.setModel(modeloRemision);
        // Fecha de hoy por defecto
        jTextField4.setText(LocalDate.now().toString());
        // Número de movimiento autogenerado (no editable)
        jTextField5.setEditable(false);
        jTextField5.setText(String.valueOf(generarNumeroMovimiento()));
 
    }
    // 1. GENERAR número de movimiento Lee el encabezado, obtiene el número más alto y suma 1
    private int generarNumeroMovimiento() {
        int max = 0;
        try {
            File f = new File(ENCABEZADO_CSV);
            if (!f.exists()) return 1;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] p = linea.split(",", -1);
                if (p.length > 0 && !p[0].trim().isEmpty()) {
                    try {
                        int n = Integer.parseInt(p[0].trim());
                        if (n > max) max = n;
                    } catch (NumberFormatException ignored) {}
                }
            }
            br.close();
        } catch (Exception ignored) {}
        return max + 1;
    }
    
    // 2. BUSCAR producto por código en producto Llena jTextField3 con el nombre del producto
    private void buscarProducto() {
    String codigo = jTextField1.getText().trim();
    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingresa un código");
        return;
    }
    try {
        BufferedReader br = new BufferedReader(
                new FileReader(INVENTARIO_CSV)
        );
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = linea.split(",", -1);
            if (d.length >= 2) {
                String clave = d[0].trim();
                if (clave.equalsIgnoreCase(codigo)) {
                   jTextField3.setText(d[1].trim());
                   System.out.println("TEXTO DEL CAMPO: " + jTextField3.getText());
                    br.close();
                    jTextField2.requestFocus();
                    return;
                }
            }
        }
        br.close();
        JOptionPane.showMessageDialog(this,
                "Código no encontrado");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error buscando producto");
    }
}
    // 3. AGREGAR producto a la tabla de remisión
    private void agregarRemision() {
    String codigo = jTextField1.getText().trim();
    String nombre = jTextField3.getText().trim();
    String cantTxt = jTextField2.getText().trim();
    if (codigo.isEmpty() || nombre.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Primero busca un producto válido");
        return;
    }
    if (cantTxt.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Ingresa la cantidad");
        return;
    }
    try {
        int cantidad = Integer.parseInt(cantTxt);
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser mayor a 0");
            return;
        }
        // VALIDAR DUPLICADOS
        for (int i = 0; i < modeloRemision.getRowCount(); i++) {
            String productoTabla = modeloRemision
                    .getValueAt(i, 1)
                    .toString();
            String codigoTabla = productoTabla
                    .split(" - ")[0]
                    .trim();
            if (codigoTabla.equals(codigo)) {
                int cantidadActual = Integer.parseInt(
                        modeloRemision
                                .getValueAt(i, 0)
                                .toString()
                );
                modeloRemision.setValueAt(
                        cantidadActual + cantidad,
                        i,
                        0
                );
                limpiarCamposProducto();
                return;
            }
        }
        modeloRemision.addRow(new Object[]{
            cantidad,
            codigo + " - " + nombre
        });
        limpiarCamposProducto();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
                "La cantidad debe ser un número entero");
    }
}
    private void limpiarCamposProducto() {
    jTextField1.setText("");
    jTextField2.setText("");
    jTextField3.setText("");
    jTextField1.requestFocus();
}
    // 4. GUARDAR MOVIMIENTO COMPLETO Valida → escribe CSVs → actualiza stock
    private void guardarMovimiento() {
    String numeroTxt = jTextField5.getText().trim();
    String fecha = jTextField4.getText().trim();
    String tipo = jComboBox1.getSelectedItem().toString();
    String motivo = jTextField6.getText().trim();
    // VALIDAR FECHA
    try {
        LocalDate.parse(fecha);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Fecha inválida.\nFormato correcto: yyyy-MM-dd");
        return;
    }
    // VALIDAR MOTIVO
    if (tipo.equals("Ajuste") && motivo.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "El motivo es obligatorio para Ajustes");
        return;
    }
    // VALIDAR REMISION
    if (modeloRemision.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this,
                "Agrega al menos un producto");
        return;
    }
    int numero = Integer.parseInt(numeroTxt);
    // VALIDAR DUPLICADO
    if (numeroYaExiste(numero)) {
        JOptionPane.showMessageDialog(this,
                "El número de movimiento ya existe");
        return;
    }
    // MAPA DE PRODUCTOS
    Map<String, Integer> remision = new LinkedHashMap<>();
    for (int i = 0; i < modeloRemision.getRowCount(); i++) {
        int cantidad = Integer.parseInt(
                modeloRemision.getValueAt(i, 0).toString()
        );
        String producto = modeloRemision.getValueAt(i, 1).toString();
        String codigo = producto.split(" - ")[0].trim();
        remision.put(codigo, cantidad);
    }
    // LEER INVENTARIO
    List<String[]> inventario = leerInventario();
    if (inventario == null) {
        return;
    }
    // VALIDACIONES
    for (Map.Entry<String, Integer> entry : remision.entrySet()) {
        String codigo = entry.getKey();
        int cantidad = entry.getValue();
        String[] prod = buscarEnInventario(inventario, codigo);
        if (prod == null) {
            JOptionPane.showMessageDialog(this,
                    "Producto no encontrado: " + codigo);
            return;
        }
        // VALIDAR PRODUCTO INACTIVO
        if (!tipo.equals("Ajuste")
                && prod[8].trim().equalsIgnoreCase("INACTIVO")) {
            JOptionPane.showMessageDialog(this,
                    "El producto " + codigo
                    + " está deshabilitado.\nSolo permite ajustes.");
            return;
        }
        int stockActual = Integer.parseInt(prod[5].trim());
        int stockMinimo = Integer.parseInt(prod[6].trim());
        // SOBRE INVENTARIO
        if (tipo.equals("Entrada")
                && (stockActual + cantidad) > (stockMinimo * 3)) {
            int opc = JOptionPane.showConfirmDialog(
                    this,
                    "El producto " + codigo
                    + " superará 3 veces el stock mínimo.\n¿Continuar?",
                    "Advertencia",
                    JOptionPane.YES_NO_OPTION
            );
            if (opc != JOptionPane.YES_OPTION) {
                return;
            }
        }
        // STOCK INSUFICIENTE
        if (tipo.equals("Salida")
                && cantidad > stockActual) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente para "
                    + codigo
                    + "\nDisponible: " + stockActual
                    + "\nSolicitado: " + cantidad);
            return;
        }
    }
    // GUARDAR
    escribirEncabezado(numero, fecha, tipo, motivo);
    for (Map.Entry<String, Integer> entry : remision.entrySet()) {
        escribirDetalle(
                numero,
                entry.getKey(),
                entry.getValue()
        );
        actualizarStock(
                inventario,
                entry.getKey(),
                entry.getValue(),
                tipo
        );
    }
    escribirInventario(inventario);
    JOptionPane.showMessageDialog(this,
            "Movimiento guardado correctamente");
    limpiarFormulario();
}
    private void limpiarFormulario() {
    modeloRemision.setRowCount(0);
    jTextField1.setText("");
    jTextField2.setText("");
    jTextField3.setText("");
    jTextField6.setText("");
    jTextField4.setText(LocalDate.now().toString());
    jTextField5.setText(
            String.valueOf(generarNumeroMovimiento())
    );
    jTextField1.requestFocus();
}
    private boolean numeroYaExiste(int numero) {
    try {
        File f = new File(ENCABEZADO_CSV);
        if (!f.exists()) {
            return false;
        }
        BufferedReader br = new BufferedReader(new FileReader(f));
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] datos = linea.split(",");
            if (datos[0].trim().equals(String.valueOf(numero))) {
                br.close();
                return true;
            }
        }
        br.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error verificando número");
    }
    return false;
}
    
    private List<String[]> leerInventario() {
    List<String[]> lista = new ArrayList<>();
    try {
        File f = new File(INVENTARIO_CSV);
        if (!f.exists()) {

            JOptionPane.showMessageDialog(this,
                    "No existe productos.csv");
            return null;
        }
        BufferedReader br = new BufferedReader(
                new FileReader(f)
        );
        String linea;
        boolean primera = true;
        while ((linea = br.readLine()) != null) {
            // IGNORAR ENCABEZADO
            if (primera) {
                primera = false;
                continue;
            }
            if (linea.trim().isEmpty()) {
                continue;
            }
            lista.add(linea.split(",", -1));
        }
        br.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error leyendo inventario");
        return null;
    }
    return lista;
}
    private String[] buscarEnInventario(List<String[]> inventario, String codigo) {
    for (String[] producto : inventario) {
        if (producto[0].trim().equals(codigo)) {
            return producto;
        }
    }
    return null;
}
    private void escribirEncabezado(int numero, String fecha, String tipo, String motivo) {
    try {
        File archivo = new File(ENCABEZADO_CSV);
        boolean existe = archivo.exists() && archivo.length() > 0;
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(archivo, true)
        );
        if (!existe) {
            bw.write("Numero,Fecha,Tipo,Motivo");
            bw.newLine();
        }
        bw.write(numero + "," + fecha + "," + tipo + "," + motivo);
        bw.newLine();
        bw.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error escribiendo encabezado");
    }
}

    private void escribirDetalle(int numero, String codigo, int cantidad) {
    try {
        File archivo = new File(DETALLE_CSV);
       boolean existe = archivo.exists() && archivo.length() > 0;
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(archivo, true)
        );
        if (!existe) {
            bw.write("Numero,Codigo,Cantidad");
            bw.newLine();
        }
        bw.write(numero + "," + codigo + "," + cantidad);
        bw.newLine();
        bw.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error escribiendo detalle");
    }
}
    private void actualizarStock(List<String[]> inventario,
        String codigo,
        int cantidad,
        String tipo) {
    for (String[] producto : inventario) {
        if (producto[0].trim().equals(codigo)) {
            int stock = Integer.parseInt(producto[5].trim());
            if (tipo.equals("Entrada")) {
                stock += cantidad;
            }
            if (tipo.equals("Salida")) {
                stock -= cantidad;
            }
            if (tipo.equals("Ajuste")) {
                stock = cantidad;
            }
            producto[5] = String.valueOf(stock);
        }
    }
}
    private void escribirInventario(List<String[]> inventario) {
    try {
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(INVENTARIO_CSV)
        );
        for (String[] producto : inventario) {
            bw.write(String.join(",", producto));
            bw.newLine();
        }
        bw.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error actualizando inventario");
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        jLabel3.setText("Codigo");

        jLabel4.setText("Nombre Producto");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Entrada", "Salida", "Ajuste" }));

        jLabel5.setText("Cantidad");

        jLabel6.setText("---------------------------------------------------------------------------------------------------------------------------------------");

        jLabel7.setText("Fecha ");

        jLabel8.setText("Tipo");

        jLabel9.setText("No.Movimiento ");

        jButton3.setText("Buscar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Agregar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel11.setText("Nota:");

        jButton5.setText("Guardar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Eliminar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(49, 49, 49)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(57, 57, 57)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(86, 86, 86)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addGap(20, 20, 20))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(29, 29, 29)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(6, 6, 6))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton4)
                                    .addComponent(jButton3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton5)
                                    .addComponent(jButton6))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jTextField6))))
                .addGap(48, 48, 48))
        );

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        jLabel1.setText("MOVIMIENTO  DEL INVENTARIO");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/lista.png"))); // NOI18N

        jButton1.setFont(new java.awt.Font("Dubai", 3, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/mas.png"))); // NOI18N
        jButton1.setText("Stock actual  de productos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Dubai", 3, 12)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/historial-de-pedidos.png"))); // NOI18N
        jButton2.setText("HISTORIAL");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel10.setText("Registro de Entrada, Salida y ajustes de stock");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(26, 26, 26)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton1)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10))
                            .addComponent(jLabel2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         // Abrir ventana Movimiento
    Movimiento mov = new Movimiento(this, true);

    // Oculta Inventario
    this.setVisible(false);

    // Mostrar ventana
    mov.setVisible(true);

    // Cuando cierre Movimiento regresa Inventario
    this.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Abrir ventana Historial
    Historial his = new Historial(this, true);

    // Ocultar Inventario
    this.setVisible(false);

    // Mostrar ventana
    his.setVisible(true);

    // Cuando cierre Historial regresar Inventario
    this.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        buscarProducto();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       agregarRemision();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int opcion = JOptionPane.showConfirmDialog(
        this,
        "¿Deseas guardar el movimiento?",
        "Confirmar",
        JOptionPane.YES_NO_OPTION
    );

    if (opcion == JOptionPane.YES_OPTION) {
        guardarMovimiento();
    }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int fila = jTable1.getSelectedRow();

    if (fila == -1) {

        JOptionPane.showMessageDialog(this,
                "Selecciona un producto");

        return;
    }

    modeloRemision.removeRow(fila);
    }//GEN-LAST:event_jButton6ActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Inventario().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
