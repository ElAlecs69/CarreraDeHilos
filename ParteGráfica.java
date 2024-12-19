package CarreraHilosGráfico;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ParteGráfica extends JFrame {

    private static final long serialVersionUID = 1L;
    private JLabel EHilo1, EHilo2;
    private JProgressBar BarraHilo1, BarraHilo2;
    private JButton Botón1, BotónDemonio;
    private Thread hilo1, hilo2, demonio;
    private volatile boolean ca = false;
    private Prueba ganador, perdedor, prueba1, prueba2;
    
    public ParteGráfica() {

        setTitle("Carrera de Hilos");
        setSize(400, 200);
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(null);

        EHilo1 = new JLabel("       Hilo 1");
        EHilo2 = new JLabel("       Hilo 2");
        BarraHilo1 = new JProgressBar(0, 100);
        BarraHilo2 = new JProgressBar(0, 100);
        Botón1 = new JButton("Iniciar Carrera");
        BotónDemonio = new JButton("Activar Demonio");
        BotónDemonio.setVisible(false);

        add(EHilo1);
        add(BarraHilo1);
        add(EHilo2);
        add(BarraHilo2);
        add(new JLabel());
        add(Botón1);
        add(new JLabel());
        add(BotónDemonio);

        Botón1.addActionListener(e -> {
            if (!ca) {
                iniciarCarrera();
            }
        });

        BotónDemonio.addActionListener(e -> {
            if (perdedor != null && perdedor.getPosicion() < 100) {
                iniciarDemonio();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public synchronized boolean LaCarreraEstáActiva() {
        return ca;
    }

    public synchronized void setCarreraActiva(boolean ca) {
        this.ca = ca;
    }

    public synchronized void iniciarCarrera() {
        if (hilo1 != null && hilo1.isAlive()) {
            hilo1.interrupt();
        }
        if (hilo2 != null && hilo2.isAlive()) {
            hilo2.interrupt();
        }

        ca = true;

        EHilo1.setText("Hilo 1 - Posición: 0");
        EHilo2.setText("Hilo 2 - Posición: 0");
        BarraHilo1.setValue(0);
        BarraHilo2.setValue(0);

        prueba1 = new Prueba("Hilo 1", EHilo1, BarraHilo1, this);
        prueba2 = new Prueba("Hilo 2", EHilo2, BarraHilo2, this);

        hilo1 = new Thread(prueba1);
        hilo2 = new Thread(prueba2);

        hilo1.start();
        hilo2.start();
    }

    public synchronized void iniciarDemonio() {
        if (demonio == null || !demonio.isAlive()) {
            demonio = new Thread(() -> {
                Random random = new Random();
                while (perdedor != null && perdedor.getPosicion() < 100) {
                    int reduccion = random.nextInt(6);  
                    ganador.reducirPosicion(reduccion);
                    int r = random.nextInt(6);
                    perdedor.seguirCarrera(r);

                    if (ganador.getPosicion() <= 0 || perdedor.getPosicion() >= 100) {
                        break; // Detener si las condiciones límite se alcanzan
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            
            demonio.setDaemon(true);
            demonio.start();
        }
    }

    public synchronized void marcarGanador(Prueba prueba1) {
        this.ganador = prueba1;
        BotónDemonio.setVisible(true); 
    }
    
    public synchronized void marcarPerdedor(Prueba prueba2) {
        this.perdedor = prueba2;
    }
    
    public synchronized Prueba getPrueba1() {
        return prueba1;
    }

    public synchronized Prueba getPrueba2() {
        return prueba2;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParteGráfica::new);
    }
}