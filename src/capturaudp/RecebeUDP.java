package capturaudp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;

public class RecebeUDP extends JFrame implements Runnable {

    public static byte bufferNovo[];
    public static int scale = 1;
    public static int larguraTela = 800;
    public static int alturaTela = 600;
    public static int BLOCK_X = 800;
    public static int BLOCK_Y = 600;
    public static int margem = 10;
    public static int margemtempoSleep = 600;

    public static boolean primeiraImagem = true;
    public static boolean mandouTelaInteira = false;

    private Thread t = new Thread(this);
    private BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);

    RecebeUDP() {
        setSize(1200, 800);
        setTitle("Teste");

        t.start();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int aux = 0;
        for (int i = 0; i < BLOCK_X; i++) {
            for (int j = 0; j < BLOCK_Y; j++) {
                Color cor = new Color(bufferNovo[aux++] & 0xFF, bufferNovo[aux++] & 0xFF, bufferNovo[aux++] & 0xFF, bufferNovo[aux++] & 0xFF);
                g2.setColor(cor);
                g2.drawRect(margem + i * scale, margem + j * scale, scale, scale);
            }
        }
    }

    public static void main(String[] args)  throws InterruptedException{

        try {
            int porta = 8000;

            byte[] buffer = new byte[40000];

            ByteArrayOutputStream bufferCompleto = new ByteArrayOutputStream();

            String enderecoBroadcast = "192.168.0.18";
            InetAddress address = InetAddress.getByName(enderecoBroadcast);

            DatagramSocket socket = new DatagramSocket(porta);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                InetAddress client = packet.getAddress();
                int clientPort = packet.getPort();

                int quantidadePacotes = 0;
                while (buffer[0] != 0) {
                    bufferCompleto.write(buffer);
                    buffer = new byte[40000];

                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    quantidadePacotes++;
                    System.out.println("Recebido pacote" + quantidadePacotes + "tamanho "+ buffer.length);
                }
                bufferNovo = bufferCompleto.toByteArray();

                if (primeiraImagem) {
                    RecebeUDP r = new RecebeUDP();
                    r.setVisible(true);
                    primeiraImagem = false;
                }
                bufferCompleto.reset();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);// testar com 100 também
                repaint();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
