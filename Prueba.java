package CarreraHilosGráfico;

import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

class Prueba implements Runnable {

    private String n;
    private JProgressBar Barra;
    private int p = 0;
    private Random a = new Random();
    private JLabel E;
    private ParteGráfica pg;

    public Prueba(String n, JLabel E, JProgressBar Barra, ParteGráfica pg) {
        this.n = n;
        this.E = E;
        this.Barra = Barra;
        this.pg = pg;
    }

    @Override
    public void run() {
        while (pg.LaCarreraEstáActiva() && !Thread.currentThread().isInterrupted()) {
            int avance = a.nextInt(10);
            p += avance;
            E.setText("       " + n + "        posición: " + p);
            Barra.setValue(p);

            if (p >= 100) {
                pg.setCarreraActiva(false);
                Prueba perdedor = IdentificaciónDeHilos();
                pg.marcarGanador(this);
                pg.marcarPerdedor(perdedor);
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void reducirPosicion(int reduccion) {
        p -= reduccion;
        if (p < 0) p = 0;
        Barra.setValue(p);
        E.setText("       " + n + "        posición: " + p);

        try {
            Thread.sleep(200); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void seguirCarrera(int r) {
        p += r;
        if (p > 100) p = 100;
        Barra.setValue(p);
        E.setText("       " + n + "        posición: " + p);

        try {
            Thread.sleep(200); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private Prueba IdentificaciónDeHilos() {
        return "Hilo 1".equals(n) ? pg.getPrueba2() : pg.getPrueba1();
    }
    
    public int getPosicion() {
        return p;
    }
}
