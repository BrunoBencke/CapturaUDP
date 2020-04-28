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

    public static byte bufferaux[];
    public static int x = 800;
    public static int y = 600;

    private BufferedImage buffimagem = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);

    RecebeUDP() {
        Thread t = new Thread(this);
        setSize(800, 600);
        t.start();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int aux = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Color cor = new Color(bufferaux[aux++] & 0xFF, bufferaux[aux++] & 0xFF, bufferaux[aux++] & 0xFF, bufferaux[aux++] & 0xFF);
                g2.setColor(cor);
                g2.drawRect(10 + i * 1, 10 + j * 1, 1, 1);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        try {
            int porta = 8000;

            byte[] buffer = new byte[40000];

            ByteArrayOutputStream bufferCompleto = new ByteArrayOutputStream();

            String enderecoBroadcast = "192.168.0.18";
            InetAddress address = InetAddress.getByName(enderecoBroadcast);

            DatagramSocket socket = new DatagramSocket(porta);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            RecebeUDP r = new RecebeUDP();
            r.setVisible(true);

            while (true) {
                socket.receive(packet);
                InetAddress client = packet.getAddress();
                int clientPort = packet.getPort();
                while (buffer[0] != 0) {
                    bufferCompleto.write(buffer);
                    buffer = new byte[40000];
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                }
                bufferaux = bufferCompleto.toByteArray();
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
                Thread.sleep(100);
                repaint();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
