
package SISTEMA;

import java.util.ArrayList;

public class CALCULADORA extends javax.swing.JFrame {
    String resultado;
    public CALCULADORA() {
        initComponents();
        this.pantalla.setText("0");
        resultado="";
        this.setResizable(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pantalla = new javax.swing.JLabel();
        tres = new javax.swing.JButton();
        uno = new javax.swing.JButton();
        dos = new javax.swing.JButton();
        seis = new javax.swing.JButton();
        cuatro = new javax.swing.JButton();
        cinco = new javax.swing.JButton();
        nueve = new javax.swing.JButton();
        siete = new javax.swing.JButton();
        ocho = new javax.swing.JButton();
        divide = new javax.swing.JButton();
        punto = new javax.swing.JButton();
        cero = new javax.swing.JButton();
        suma = new javax.swing.JButton();
        resta = new javax.swing.JButton();
        producto = new javax.swing.JButton();
        igual = new javax.swing.JButton();
        elimina = new javax.swing.JButton();
        ans = new javax.swing.JButton();
        limpiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pantalla.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pantalla.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tres.setText("3");
        tres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tresActionPerformed(evt);
            }
        });

        uno.setText("1");
        uno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unoActionPerformed(evt);
            }
        });

        dos.setText("2");
        dos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dosActionPerformed(evt);
            }
        });

        seis.setText("6");
        seis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seisActionPerformed(evt);
            }
        });

        cuatro.setText("4");
        cuatro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cuatroActionPerformed(evt);
            }
        });

        cinco.setText("5");
        cinco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cincoActionPerformed(evt);
            }
        });

        nueve.setText("9");
        nueve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nueveActionPerformed(evt);
            }
        });

        siete.setText("7");
        siete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sieteActionPerformed(evt);
            }
        });

        ocho.setText("8");
        ocho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ochoActionPerformed(evt);
            }
        });

        divide.setText("/");
        divide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divideActionPerformed(evt);
            }
        });

        punto.setText(".");
        punto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puntoActionPerformed(evt);
            }
        });

        cero.setText("0");
        cero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ceroActionPerformed(evt);
            }
        });

        suma.setText("+");
        suma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumaActionPerformed(evt);
            }
        });

        resta.setText("-");
        resta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restaActionPerformed(evt);
            }
        });

        producto.setText("*");
        producto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productoActionPerformed(evt);
            }
        });

        igual.setText("=");
        igual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                igualActionPerformed(evt);
            }
        });

        elimina.setText("C");
        elimina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminaActionPerformed(evt);
            }
        });

        ans.setText("ANS");
        ans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ansActionPerformed(evt);
            }
        });

        limpiar.setText("Limpiar");
        limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(elimina, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ans, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(igual, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(resta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(producto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(suma, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(punto, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cero, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(divide, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(siete, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ocho, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(nueve, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cuatro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cinco, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(seis, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pantalla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(uno, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tres, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(limpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(pantalla, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uno)
                    .addComponent(tres)
                    .addComponent(dos))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cuatro)
                    .addComponent(seis)
                    .addComponent(cinco))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(siete)
                    .addComponent(nueve)
                    .addComponent(ocho))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(punto)
                    .addComponent(divide)
                    .addComponent(cero))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resta)
                    .addComponent(suma)
                    .addComponent(producto))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elimina)
                    .addComponent(igual)
                    .addComponent(ans))
                .addGap(18, 18, 18)
                .addComponent(limpiar)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    public String agregar(){
        if(this.pantalla.getText().equals("0")){
            return "";
        }else{
            return this.pantalla.getText();
        }
    }
    
        public void verifica_opera(String dato){
        String captura = pantalla.getText();
        if(dato.equals("+")){
            if(!captura.endsWith(dato) && !captura.endsWith("-") 
               && !captura.endsWith("/") && !captura.endsWith("*")){
                pantalla.setText(captura+dato);
            }
        }else if(dato.equals("-")){
            if(!captura.endsWith(dato) && !captura.endsWith("+") 
               && !captura.endsWith("/") && !captura.endsWith("*")){
                pantalla.setText(captura+dato);
            }
        }else if(dato.equals("*")){
            if(!captura.endsWith(dato) && !captura.endsWith("+") 
               && !captura.endsWith("/") && !captura.endsWith("-")){
                pantalla.setText(captura+dato);
            }
        }else{
            if(!captura.endsWith(dato) && !captura.endsWith("+") 
               && !captura.endsWith("*") && !captura.endsWith("-")){
                pantalla.setText(captura+dato);
            }
        }
    }
    
        public void verifica_punto(){
        String captura = this.pantalla.getText();
        String[] partes = captura.split("[+*/-]");
        String[] ver = captura.split("");
        
        String u = ver[ver.length-1];
        String ultimo = partes[partes.length-1];
        if(!ultimo.contains(".") && !u.equals("+") && !u.equals("-")
           && !u.equals("*") && !u.equals("/")){
            this.pantalla.setText(pantalla.getText()+".");
        }
    }
        
    private void tresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tresActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"3");
    }//GEN-LAST:event_tresActionPerformed

    private void seisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seisActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"6");
    }//GEN-LAST:event_seisActionPerformed

    private void nueveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nueveActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"9");
    }//GEN-LAST:event_nueveActionPerformed

    private void divideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divideActionPerformed
        this.verifica_opera("/");
    }//GEN-LAST:event_divideActionPerformed

    private void sumaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumaActionPerformed
        this.verifica_opera("+");
    }//GEN-LAST:event_sumaActionPerformed

    private void igualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_igualActionPerformed
            String texto = pantalla.getText();
        String todo[] = texto.split("");
        String op = todo[todo.length - 1];

        if (!op.equals("+") && !op.equals("-") && !op.equals("*") && !op.equals("/")) {
            String numero_aux[] = texto.split("[+*/-]");
            String operador_aux[] = texto.split("[0-9.]+");

            if (operador_aux.length > 0) {
                ArrayList<String> numeros = new ArrayList<>();
                ArrayList<String> operadores = new ArrayList<>();
                ArrayList<String> aux = new ArrayList<>();
                boolean errorDivision = false;

                for (String numero : numero_aux) {
                    numeros.add(numero);
                }
                for (String operador : operador_aux) {
                    if (!operador.isEmpty()) {
                        operadores.add(operador);
                    }
                }
                
                while (!numeros.isEmpty() || !operadores.isEmpty()) {
                    if (!numeros.isEmpty()) {
                        aux.add(numeros.remove(0));
                    }
                    if (!operadores.isEmpty()) {
                        aux.add(operadores.remove(0));
                    }
                }

                String[] operaciones = {"*", "/", "+", "-"};
                for (String operacion : operaciones) {
                    for (int p = 0; p < aux.size(); p++) {
                        if (aux.get(p).equals(operacion)) {
                            double a = Double.parseDouble(aux.get(p - 1));
                            double b = Double.parseDouble(aux.get(p + 1));
                            double resultado = 0;

                            switch (operacion) {
                                case "*":
                                    resultado = a * b;
                                    break;
                                case "/":
                                    if (b == 0) {
                                        errorDivision = true;
                                    } else {
                                        resultado = a / b;
                                    }
                                    break;
                                case "+":
                                    resultado = a + b;
                                    break;
                                case "-":
                                    resultado = a - b;
                                    break;
                            }
                            aux.remove(p);
                            aux.remove(p - 1);
                            aux.remove(p - 1);
                            aux.add(p - 1, Double.toString(resultado));
                            p--;
                        }
                    }
                }
                
                if (errorDivision) {
                    this.pantalla.setText("Error");
                    this.pantalla.setEnabled(false);
                } else {
                    String resultadoFinal = "";
                    for (String elemento : aux) {
                        resultadoFinal += elemento;
                    }
                    resultado = resultadoFinal;
                    this.pantalla.setText(resultadoFinal);
                }
            }
        }
    }//GEN-LAST:event_igualActionPerformed

    private void unoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unoActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"1");
    }//GEN-LAST:event_unoActionPerformed

    private void dosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dosActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"2");
    }//GEN-LAST:event_dosActionPerformed

    private void cuatroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cuatroActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"4");
    }//GEN-LAST:event_cuatroActionPerformed

    private void cincoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cincoActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"5");
    }//GEN-LAST:event_cincoActionPerformed

    private void sieteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sieteActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"7");
    }//GEN-LAST:event_sieteActionPerformed

    private void ochoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ochoActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"8");
    }//GEN-LAST:event_ochoActionPerformed

    private void ceroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ceroActionPerformed
        String captura = this.agregar();
        pantalla.setText(captura+"0");
    }//GEN-LAST:event_ceroActionPerformed

    private void puntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puntoActionPerformed
        this.verifica_punto();
    }//GEN-LAST:event_puntoActionPerformed

    private void restaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restaActionPerformed
        this.verifica_opera("-");
    }//GEN-LAST:event_restaActionPerformed

    private void productoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productoActionPerformed
        this.verifica_opera("*");
    }//GEN-LAST:event_productoActionPerformed

    private void eliminaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminaActionPerformed
        String captura[] = this.pantalla.getText().split("");
        String palabra="";
        if(captura.length==1){
            this.pantalla.setText("0");
        }else{
            for(int i=0; i<captura.length-1;i++){
                palabra+=captura[i];
            }
            this.pantalla.setText(palabra);   
        }
    }//GEN-LAST:event_eliminaActionPerformed

    private void ansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ansActionPerformed
        if(!resultado.equals("")){
            this.pantalla.setText(resultado);
        }
    }//GEN-LAST:event_ansActionPerformed

    private void limpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarActionPerformed
        this.pantalla.setText("0");
        this.pantalla.setEnabled(true);
    }//GEN-LAST:event_limpiarActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CALCULADORA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CALCULADORA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CALCULADORA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CALCULADORA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CALCULADORA().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ans;
    private javax.swing.JButton cero;
    private javax.swing.JButton cinco;
    private javax.swing.JButton cuatro;
    private javax.swing.JButton divide;
    private javax.swing.JButton dos;
    private javax.swing.JButton elimina;
    private javax.swing.JButton igual;
    private javax.swing.JButton limpiar;
    private javax.swing.JButton nueve;
    private javax.swing.JButton ocho;
    private javax.swing.JLabel pantalla;
    private javax.swing.JButton producto;
    private javax.swing.JButton punto;
    private javax.swing.JButton resta;
    private javax.swing.JButton seis;
    private javax.swing.JButton siete;
    private javax.swing.JButton suma;
    private javax.swing.JButton tres;
    private javax.swing.JButton uno;
    // End of variables declaration//GEN-END:variables
}
