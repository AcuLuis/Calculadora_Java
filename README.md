<div align="center">

<img src="docs/img/icono.png" alt="Ícono" width="110">

# Calculadora Avanzada en Java

**Calculadora científica, gráfica y de cálculo numérico hecha con Java Swing**

![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Swing%20%2B%20FlatLaf-4A90E2?style=for-the-badge)
![NetBeans](https://img.shields.io/badge/NetBeans-compatible-1B6AC6?style=for-the-badge&logo=apachenetbeanside&logoColor=white)

[![Compilación y pruebas](https://github.com/AcuLuis/Calculadora_Java/actions/workflows/build.yml/badge.svg)](https://github.com/AcuLuis/Calculadora_Java/actions/workflows/build.yml)
![Pruebas](https://img.shields.io/badge/pruebas-121%20JUnit%205-25A162)
[![Licencia: MIT](https://img.shields.io/badge/licencia-MIT-yellow)](LICENSE)

<img src="docs/img/estandar.png" alt="Modo estándar / científico" width="720">

</div>

---

## 📖 Descripción

Aplicación de escritorio que reúne en una sola ventana tres herramientas matemáticas:

1. **Calculadora estándar / científica** con jerarquía de operaciones y paréntesis.
2. **Graficador 2D** de funciones `f(x)` en un intervalo definido por el usuario.
3. **Cálculo numérico**: derivada en un punto e integral definida.

El proyecto sigue una separación tipo **MVC** (lógica matemática por un lado, interfaz por otro) y se gestiona con **Maven**, así que se abre directamente en NetBeans, IntelliJ IDEA, Eclipse o VS Code.

## ✨ Características

| Módulo | Qué hace |
|---|---|
| **Estándar / Científica** | Operaciones `+ − × ÷`, potencias, `x²`, `x⁻¹`, `√`, `n!`, `%`, `±`, `sin`, `cos`, `tan`, `log`, `ln`, constantes `π` y `e`, y la tecla `ANS` con el último resultado. Modo **RAD / DEG** para los ángulos. Resultados limpios (`0.1+0.2 = 0.3`, `sin(π) = 0`) y notación científica para números muy grandes o pequeños. |
| **Historial** | Cada operación queda guardada; un clic (o las flechas `↑` `↓`) la recupera para editarla o recalcularla. |
| **Teclado físico** | Puedes escribir la operación completa con el teclado; `Enter` calcula, `Esc` borra y `Ctrl+C` / `Ctrl+V` copian y pegan. |
| **Gráficas** | Hasta **6 funciones a la vez** (separadas por `;`) con colores y leyenda. **Zoom** con la rueda del ratón, **arrastrar** para moverse y coordenadas de cada función bajo el cursor. Deja huecos donde la función no existe, corta la línea en las asíntotas (`1/x`, `tan(x)`) y ajusta el eje Y para que los picos no aplasten la curva. |
| **Derivada** | Derivada numérica con **diferencias finitas centrales** y su **recta tangente** dibujada. Detecta cuándo la derivada **no existe**: esquinas (`abs(x)` en 0), saltos (`floor(x)`), tangentes verticales (`cbrt(x)` en 0) o funciones definidas solo a un lado (`sqrt(x)` en 0). |
| **Integral definida** | **Simpson adaptativo** con error estimado: subdivide solo donde la función lo necesita. Resuelve singularidades integrables (`1/sqrt(x)`, `ln(x)` y `sin(x)/x` en 0) con cuadratura **tanh-sinh**, avisa cuando la integral **diverge** (`1/x` en [0, 1]) y sombrea el área en la gráfica. |
| **Errores claros** | Los errores se muestran en rojo dentro de la ventana, indicando qué campo revisar, sin ventanas emergentes. |
| **Tema oscuro** | Interfaz moderna con **FlatLaf** (Dark). |

## 📸 Capturas

<table>
  <tr>
    <td align="center" colspan="2">
      <img src="docs/img/graficas.png" alt="Graficador de funciones" width="720"><br>
      <sub><b>Graficador</b> — sin(x)·x y cos(x) a la vez, con sus valores bajo el cursor</sub>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="docs/img/derivada.png" alt="Derivada numérica" width="360"><br>
      <sub><b>Derivada</b> — f(x) = x³ − 2x en x = 1 y su recta tangente</sub>
    </td>
    <td align="center">
      <img src="docs/img/integral.png" alt="Integral definida" width="360"><br>
      <sub><b>Integral</b> — ∫ (x² − 2) dx de −2 a 2: el área bajo el eje cuenta en negativo</sub>
    </td>
  </tr>
</table>

## 🏗️ Arquitectura

```
src/main/java/com/calculadora
├── main
│   └── Main.java            → Punto de entrada, aplica el tema FlatLaf
├── math                     → Modelo (lógica pura)
│   ├── EvaluadorMath.java   → Evalúa expresiones y funciones f(x)
│   ├── FormatoNumero.java   → Presenta los resultados de forma legible
│   ├── Muestreo.java        → Puntos de una curva, huecos, asíntotas y rango Y
│   └── Calculus.java        → Análisis de la derivada e integral adaptativa
└── gui                      → Vista / Controlador
    ├── MainFrame.java       → Ventana principal con pestañas
    ├── StandardPanel.java   → Teclado, pantalla e historial de la calculadora
    ├── GraphPanel.java      → Graficador con zoom, desplazamiento y coordenadas
    ├── CalculusPanel.java   → Pestañas de derivada e integral con sus gráficas
    ├── GraficaFuncion.java  → Gráfica reutilizable con el tema oscuro
    └── Tema.java            → Colores y fuentes de toda la aplicación

src/test/java/com/calculadora/math   → Pruebas JUnit 5 del modelo
```

## 🛠️ Tecnologías

- **Java 8+** — lenguaje base.
- **Maven** — dependencias y empaquetado.
- **[exp4j](https://www.objecthunter.net/exp4j/)** — motor de evaluación de expresiones.
- **[XChart](https://knowm.org/open-source/xchart/)** — gráficos cartesianos.
- **[FlatLaf](https://www.formdev.com/flatlaf/)** — Look and Feel moderno para Swing.
- **[JUnit 5](https://junit.org/junit5/)** — pruebas unitarias.
- **GitHub Actions** — compila y prueba cada cambio con Java 8, 17 y 21 en Linux y Windows.

## 🚀 Instalación y ejecución

### Requisitos

- JDK 8 o superior.
- Maven 3.6+ (no es necesario si usas NetBeans o IntelliJ, ya lo traen integrado).

### Opción 1 — NetBeans

1. Clona el repositorio:
   ```bash
   git clone https://github.com/AcuLuis/Calculadora_Java.git
   ```
2. En NetBeans: **File → Open Project…** y selecciona la carpeta `Calculadora_Java` (se reconoce como proyecto Maven).
3. Espera a que se descarguen las dependencias.
4. Pulsa **Run Project (F6)**.

### Opción 2 — Terminal

```bash
mvn clean package
java -jar target/calculadora-avanzada-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 🧪 Pruebas

El modelo matemático tiene **121 pruebas JUnit 5**: expresiones y sintaxis, formato de resultados, derivadas que existen y que no, integrales convergentes, con singularidades y divergentes, el muestreo de las gráficas y un *fuzzing* de 100 000 expresiones mal formadas.

```bash
mvn test
```

En NetBeans: clic derecho sobre el proyecto → **Test (Alt+F6)**. Las pruebas también se ejecutan en cada `mvn package` y en GitHub Actions con cada *push*.

## ✍️ Sintaxis de las expresiones

| Escribe | Significa |
|---|---|
| `2^3` | Potencia (2³) |
| `sqrt(x)` o `√(x)` | Raíz cuadrada |
| `5!` | Factorial (120) |
| `50%`, `200*10%` | Porcentaje (0.5 y 20) |
| `ANS` | Último resultado calculado |
| `sin(x)`, `cos(x)`, `tan(x)` | Trigonométricas (en **radianes**, o en grados con el botón **DEG**) |
| `asin(x)`, `acos(x)`, `atan(x)` | Trigonométricas inversas |
| `log(x)` | Logaritmo en **base 10** |
| `ln(x)` | Logaritmo **natural** |
| `log2(x)` | Logaritmo en base 2 |
| `abs(x)`, `exp(x)`, `cbrt(x)` | Valor absoluto, eˣ, raíz cúbica |
| `pi` o `π`, `e` | Constantes π y e |
| `2pi`, `3(x+1)` | Multiplicación implícita |

> 💡 En los modos **Gráficas** y **Cálculo Numérico** la variable debe llamarse `x`. Para graficar varias funciones sepáralas con `;`, por ejemplo `sin(x); cos(x); x^2/10`. Los límites y el punto de evaluación también aceptan expresiones, por ejemplo `pi/2` o `-2*pi`.

## ⌨️ Atajos de teclado

| Tecla | Acción |
|---|---|
| `0`–`9`, `.` o `,` | Escribir números |
| `+` `-` `*` `/` `^` `(` `)` `%` `!` | Operadores |
| Letras (`sin`, `sqrt`, `pi`, `ans`…) | Escribir funciones y constantes |
| `Enter` o `=` | Calcular |
| `Retroceso` | Borrar el último carácter (o la función completa, como `sin(`) |
| `Esc` o `Supr` | Borrar todo |
| `Ctrl+C` / `Ctrl+V` | Copiar el resultado / pegar una expresión |
| `↑` / `↓` | Recorrer el historial de operaciones |
| `Enter` en Gráficas y Cálculo Numérico | Trazar o calcular sin usar el ratón |

**En la gráfica:** rueda del ratón para acercar o alejar, arrastrar para moverse y doble clic para volver a la vista inicial.

> Si te falta un paréntesis de cierre, la calculadora lo agrega por ti: `sin(30` se calcula como `sin(30)`.

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**: puedes usarlo, modificarlo y compartirlo libremente, siempre que conserves el aviso de copyright. Consulta el archivo [LICENSE](LICENSE).

---

<div align="center">

## ⭐ ¿Te gustó el proyecto?

Si esta calculadora te fue útil, te ayudó a estudiar o simplemente te pareció interesante,<br>
**regálame una estrella** en GitHub. Es gratis, toma un segundo y me motiva muchísimo a seguir mejorándola. 🙌

<a href="https://github.com/AcuLuis/Calculadora_Java/stargazers">
  <img src="https://img.shields.io/github/stars/AcuLuis/Calculadora_Java?style=for-the-badge&logo=github&label=Dale%20una%20estrella&color=FFD700" alt="Dale una estrella">
</a>

¿Encontraste un error o tienes una idea? Abre un [issue](https://github.com/AcuLuis/Calculadora_Java/issues) — toda sugerencia es bienvenida.

<br>

Hecho con ☕ y Java por **[Luis Antonio Acuña Ayala](https://github.com/AcuLuis)**

</div>
