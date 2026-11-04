package com.calculadora.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Calculadora Avanzada PRO");
        setSize(1080, 760);
        setMinimumSize(new Dimension(920, 660)); // espacio para el historial y las gráficas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImages(cargarIconos());

        // Configurar un poco el diseño general del frame
        getContentPane().setBackground(Tema.FONDO);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Tema.fuente(Font.BOLD, 16));
        tabbedPane.setBackground(Tema.FONDO_PESTANAS);
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

    /** Ícono en varios tamaños; el sistema elige el adecuado para la barra de tareas y el título. */
    private static List<Image> cargarIconos() {
        List<Image> iconos = new ArrayList<>();
        for (int tamano : new int[]{16, 24, 32, 48, 64, 128, 256}) {
            URL recurso = MainFrame.class.getResource("/icons/icono-" + tamano + ".png");
            if (recurso != null) {
                iconos.add(new ImageIcon(recurso).getImage());
            }
        }
        return iconos;
    }
}
