/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tiendaelectronica;

import java.io.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jonathan vargas arciniega 
 * 26 de abril de 2026
 */
public class ArchivoCSV {
     private static final String RUTA = "productos.csv";

    public static void guardar(JTable tabla) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA))) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();

           
            StringBuilder sbHeader = new StringBuilder();
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                if (i > 0) sbHeader.append(",");
                sbHeader.append(modelo.getColumnName(i));
            }
            bw.write(sbHeader.toString());
            bw.newLine();

          
            for (int i = 0; i < modelo.getRowCount(); i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    if (j > 0) sb.append(",");
                    Object val = modelo.getValueAt(i, j);
                    sb.append(val != null ? val.toString() : "");
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cargar(JTable tabla) {
        try {
            File archivo = new File(RUTA);
            if (!archivo.exists()) return;

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
                modelo.setRowCount(0);
                String linea;
                boolean primera = true;
                while ((linea = br.readLine()) != null) {
                    if (primera) {
                        primera = false;
                        continue;
                    }
                    if (!linea.trim().isEmpty()) {
                        modelo.addRow(linea.split(",", -1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}