# Calculadora Avanzada en Java

Este proyecto consiste en una calculadora científica y gráfica desarrollada en Java, estructurada bajo el patrón de diseño Modelo-Vista-Controlador (MVC) y gestionada a través de Maven. La aplicación ofrece una interfaz de usuario moderna, capacidades de evaluación matemática compleja y funciones de cálculo numérico avanzadas.

## Caracteristicas Principales

- **Modo Estándar y Científico**: Evaluación de expresiones matemáticas complejas respetando la jerarquía de operaciones, uso de paréntesis, funciones trigonométricas, logaritmos y raíces.
- **Modo Gráfico (2D)**: Capacidad de renderizar funciones matematicas f(x) en un plano cartesiano interactivo especificando limites.
- **Cálculo Numérico**: 
  - Cálculo de integrales definidas utilizando la Regla de Simpson.
  - Cálculo de derivadas evaluadas en un punto especifico mediante el metodo de diferencias finitas centrales.
- **Interfaz Moderna**: Diseno renovado utilizando FlatLaf con un tema oscuro (Dark Mode), optimizado para una mejor experiencia de usuario (UX).

## Arquitectura

El proyecto esta organizado bajo el patron MVC:
- **Modelo**: Clases encargas de la logica (`EvaluadorMath`, `Calculus`).
- **Vista y Controlador**: Paneles de la interfaz grafica de usuario (`MainFrame`, `StandardPanel`, `GraphPanel`, `CalculusPanel`) que reaccionan a los eventos del usuario y solicitan informacion al modelo.

## Tecnologias y Librerias

- **Java 8+**
- **Maven**: Gestor de dependencias y construccion del proyecto.
- **exp4j**: Motor de evaluacion de expresiones matematicas.
- **XChart**: Libreria ligera para la creacion de graficos cartesianos.
- **FlatLaf**: Biblioteca de "Look and Feel" para una interfaz Swing moderna.

## Requisitos

- Java Development Kit (JDK) 8 o superior.
- Maven 3.6 o superior (opcional si se utiliza un IDE que lo integre automaticamente).

## Instalacion y Ejecucion

1. Clonar el repositorio.
2. Abrir el proyecto en un Entorno de Desarrollo Integrado (IDE) compatible con Java y Maven (como IntelliJ IDEA, Eclipse, NetBeans o VS Code).
3. Permitir que el IDE descargue las dependencias definidas en el archivo `pom.xml`.
4. Ejecutar la clase principal ubicada en: `src/main/java/com/calculadora/main/Main.java`.

Si se desea compilar y ejecutar desde la terminal (teniendo Maven instalado):
```bash
mvn clean package
java -cp target/calculadora-avanzada-1.0-SNAPSHOT-jar-with-dependencies.jar com.calculadora.main.Main
```