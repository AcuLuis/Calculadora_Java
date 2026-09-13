package com.calculadora.gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Calculadora Avanzada PRO");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Configurar un poco el diseño general del frame
        getContentPane().setBackground(new Color(30, 30, 30));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(45, 45, 45));
        tabbedPane.setForeground(Color.LIGHT_GRAY);
        tabbedPane.setFocusable(false);
        
        // Agregar paneles
        tabbedPane.addTab("   Estándar / Científica   ", new StandardPanel());
        tabbedPane.addTab("   Gráficas   ", new GraphPanel());
        tabbedPane.addTab("   Cálculo Numérico   ", new CalculusPanel());
        
        // Margen exterior suave
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}
