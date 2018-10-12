/*
 * la nave del juego
 */
package codigo;

import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Jorge Cisneros
 */
public class Nave {
    
    public Image imagen = null;
    public int x = 0;
    public int y = 0; 
    
    private boolean pulsadoIzquierda = false;
    private boolean pulsadoDerecha = false;
    
    private int anchoPantalla;
    
    public Nave(int _anchoPantalla){
//        try{
//            imagen = ImageIO.read(getClass().getResource("/imagenes/nave.png"));
//        }
//        catch (IOException ramon){
//        }
        anchoPantalla = _anchoPantalla;
    }

    public boolean isPulsadoIzquierda() {
        return pulsadoIzquierda;
    }

    public void setPulsadoIzquierda(boolean pulsadoIzquierda) {
        this.pulsadoIzquierda = pulsadoIzquierda;
    }

    public boolean isPulsadoDerecha() {
        return pulsadoDerecha;
    }

    public void setPulsadoDerecha(boolean pulsadoDerecha) {
        this.pulsadoDerecha = pulsadoDerecha;
    }
    
    //método para mover a la nave 
    public void mueve(){
        if (pulsadoIzquierda && x >0){
            x-=3;
        }
        
        if (pulsadoDerecha && x < anchoPantalla - imagen.getWidth(null)){
            x+=3;
        }        
        //TODO: falta mover a la derecha
    }
}
