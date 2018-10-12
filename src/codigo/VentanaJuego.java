/*
 *Juego de los invasores del espacio

ejercicio creado para explicar los siguientes conceptos:
 - hilos de ejecución paralela
 - ArrayList

 */
package codigo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author Jorge Cisneros
 */
public class VentanaJuego extends javax.swing.JFrame {
    
    
    static int ANCHOPANTALLA = 800;
    static int ALTOPANTALLA = 600;
    //cuantos marcianos van a salir en la pantalla
    int filasMarcianos = 5;
    int columnasMarcianos = 10;
    
    BufferedImage buffer = null;
    int contador = 0;
    Nave miNave = new Nave(ANCHOPANTALLA);
    Disparo miDisparo = new Disparo(ALTOPANTALLA);
    Marciano miMarciano = new Marciano(ANCHOPANTALLA);
    
    //el array de dos dimensiones que guarda la lista de marcianos
    Marciano [][] listaMarcianos = new Marciano[filasMarcianos][columnasMarcianos]; 
    //dirección en la que se mueve el grupo de marcianos
    boolean direccionMarcianos = false; 
    
    BufferedImage plantilla = null;
    Image[] imagenes = new Image[30]; 
    
    
    //bucle de animación del juego
    //en este caso, es un hilo de ejecución nuevo que se encarga
    //de refrescar el contenido de la pantalla
    
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: código de la animación
            bucleDelJuego();
        }
    });
    
    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        //cargo la plantilla con todos los sprites de los marcianos
        try{
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        }
        catch (IOException e){}
        
        //guardo cada sprite en un Image individual. De esta forma es mas facil dibujarlos 
        //dependiendo de lo que se necesite 
        for (int i=0; i < 5; i++){
            for (int j=0; j<4; j++){
                //corto el trozo de 64x64 que corresponde a ese marciano
                imagenes[i*4 + j] = plantilla.getSubimage(j*64+1, i*64+1, 64, 64);
                //cambio el tamaño a 32x32
                imagenes[i*4 +j] = imagenes[i*4+j].getScaledInstance(32,32,Image.SCALE_SMOOTH); 
            }
        }
        imagenes[21]= plantilla.getSubimage(66+1, 320, 64, 32);
        
        //hay que quitar la opción "resizable" del jPanel para que se ajuste 
        //correctamente Creditos: Junior
        setSize(ANCHOPANTALLA , ALTOPANTALLA  );
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();
        miNave.x = ANCHOPANTALLA/2 - miNave.imagen.getWidth(this)/2;
        miNave.y = ALTOPANTALLA - miNave.imagen.getHeight(this) - 40;
        miNave.imagen = imagenes[20];
        
        
        //creamos el array de marcianos
        for (int i=0; i<filasMarcianos; i++){
            for (int j=0; j<columnasMarcianos; j++){
                listaMarcianos[i][j] = new Marciano(ANCHOPANTALLA);
                listaMarcianos[i][j].imagen = imagenes[2*i];
                listaMarcianos[i][j].imagen2 = imagenes[2*i+1];
                listaMarcianos[i][j].x = j* (15 + listaMarcianos[i][j].imagen.getWidth(null));
                listaMarcianos[i][j].y = i* (10 + listaMarcianos[i][j].imagen.getHeight(null));

            }
        }
        
        //inicio el temporizador
        temporizador.start();
        
    }

    private void pintaMarcianos(Graphics2D _g2) {
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                if (listaMarcianos[i][j].vida) {
                    listaMarcianos[i][j].mueve(direccionMarcianos);
                    if (contador < 50) {
                        _g2.drawImage(listaMarcianos[i][j].imagen, listaMarcianos[i][j].x, listaMarcianos[i][j].y, null);
                    } else if (contador < 100) {
                        _g2.drawImage(listaMarcianos[i][j].imagen2, listaMarcianos[i][j].x, listaMarcianos[i][j].y, null);
                    } else {
                        contador = 0;
                    }
                    if (listaMarcianos[i][j].x == ANCHOPANTALLA - listaMarcianos[i][j].imagen.getWidth(null) || listaMarcianos[i][j].x == 0) {
                        direccionMarcianos = !direccionMarcianos;
                        for (int k = 0; k < filasMarcianos; k++) {
                            for (int m = 0; m < columnasMarcianos; m++) {
                                listaMarcianos[k][m].y += listaMarcianos[k][m].imagen.getHeight(null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void chequeaColision(){
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        rectanguloDisparo.setFrame(miDisparo.getX(), miDisparo.getY(),
                miDisparo.imagen.getWidth(null), miDisparo.imagen.getHeight(null));
        for (int i = 0; i < filasMarcianos; i++) {
            for (int j = 0; j < columnasMarcianos; j++) {
                if (listaMarcianos[i][j].vida) {
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].x,
                            listaMarcianos[i][j].y,
                            listaMarcianos[i][j].imagen.getWidth(null),
                            listaMarcianos[i][j].imagen.getHeight(null));
                    if (rectanguloDisparo.intersects(rectanguloMarciano)) {
                        //si esto es true es que los dos rectangulos han chocado en algun punto
                        listaMarcianos[i][j].vida = false;
                        //recolocamos al marciano y al disparo muy por debajo de la pantalla
                        miDisparo.setY(2000);
                        miDisparo.setDisparado(false);
                    }
                }
            }
        }

    }
    
    private void bucleDelJuego(){
        //el bucle de animación gobierna el redibujado de los objetos en 
        //el jpanel1
        //primero borro todo lo que hay en el buffer
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        
        ////////////////////////////////////////////////////////////////////////
        //redibujamos cada elemento en su nueva posición en el buffer
        
               
        contador++;
        if (miDisparo.isDisparado()){
            miDisparo.mueve();
        }
        g2.drawImage(miDisparo.imagen, miDisparo.getX(), miDisparo.getY(), null);
        //pinto la nave
        
        miNave.mueve();
        g2.drawImage(miNave.imagen, miNave.x, miNave.y, null);
        
        pintaMarcianos(g2);
        
        chequeaColision();
        
        ////////////////////////////////////////////////////////////////////////
        
        //dibujo de golpe el buffer sobre el jpanel1
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);
        
        
        
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 768, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 522, Short.MAX_VALUE)
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

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
       switch (evt.getKeyCode()){
           case KeyEvent.VK_LEFT : miNave.setPulsadoIzquierda(true);  break;
           case KeyEvent.VK_RIGHT : miNave.setPulsadoDerecha(true);  break;
           case KeyEvent.VK_SPACE : miDisparo.setDisparado(true); 
                                    miDisparo.posicionaDisparo(miNave);
                                    break;
       }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
       switch (evt.getKeyCode()){
           case KeyEvent.VK_LEFT : miNave.setPulsadoIzquierda(false);  break;
           case KeyEvent.VK_RIGHT : miNave.setPulsadoDerecha(false);  break;
       }
    }//GEN-LAST:event_formKeyReleased

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
