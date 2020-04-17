package capturaudp;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EnviaUDP {

    public static int quantidadePacotes = 0;
    public static int scale = 1;
    public static int larguraTela = 800;
    public static int alturaTela = 600;
    public static int BLOCK_X = 800;
    public static int BLOCK_Y = 600;
    public static int margem = 10;

    public static void main(String[] args) throws InterruptedException {

        //int buffer[] = new int[BLOCK_X * BLOCK_Y];      
        while (true) {
            //byte buffer[] = new byte[BLOCK_X * BLOCK_Y];
            byte buffer[] = new byte[40000];
            byte fim[] = new byte[1];
            fim[0] = (byte) 0;

            try {
                int aux = 0;

                Robot robot = new Robot();
                BufferedImage bi = robot.createScreenCapture(new Rectangle(larguraTela, alturaTela));

                for (int i = 0; i < BLOCK_X; i++) {
                    for (int j = 0; j < BLOCK_Y; j++) {
                        if (aux <= 39995) {
                            int pixel = bi.getRGB(i, j);
                            buffer[aux++] = (byte) (((pixel & 0x00FF0000) >> 16));
                            buffer[aux++] = (byte) (((pixel & 0x0000FF00) >> 8));
                            buffer[aux++] = (byte) (((pixel & 0x000000FF)));
                            buffer[aux++] = (byte) (((pixel & 0xFF000000) >> 24));
                        } else {
                            mandaPacote(buffer);
                            buffer = new byte[40000];
                            aux = 0;
                        }
                    }
                }
                mandaPacote(fim);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(100);
        }
    }

    public static void mandaPacote(byte buffer[]) {

        try {

            int porta = 8000;

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("192.168.0.18");
            socket.setBroadcast(true);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, porta);
            socket.send(packet);

            quantidadePacotes++;

            System.out.println("pacote" + quantidadePacotes+ "tamanho "+buffer.length);

            //socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
