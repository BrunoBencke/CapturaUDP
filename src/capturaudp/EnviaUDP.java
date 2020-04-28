package capturaudp;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EnviaUDP {

    public static void envia(byte buffer[]) {

        try {
            int porta = 8000;

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("192.168.0.18");
            socket.setBroadcast(true);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, porta);
            socket.send(packet);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
  
        while (true) {
            byte buffer[] = new byte[40000];
            byte fim[] = new byte[1];
            fim[0] = (byte) 0;

            try {
                int aux = 0;

                Robot robot = new Robot();
                BufferedImage bi = robot.createScreenCapture(new Rectangle(800, 600));

                for (int i = 0; i < 800; i++) {
                    for (int j = 0; j < 600; j++) {
                        if (aux <= 39995) {
                            int pixel = bi.getRGB(i, j);
                            buffer[aux++] = (byte) (((pixel & 0x00FF0000) >> 16));
                            buffer[aux++] = (byte) (((pixel & 0x0000FF00) >> 8));
                            buffer[aux++] = (byte) (((pixel & 0x000000FF)));
                            buffer[aux++] = (byte) (((pixel & 0xFF000000) >> 24));
                        } else {
                            envia(buffer);
                            buffer = new byte[40000];
                            aux = 0;
                        }
                    }
                }
                envia(fim);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(100);
        }
    }
}
