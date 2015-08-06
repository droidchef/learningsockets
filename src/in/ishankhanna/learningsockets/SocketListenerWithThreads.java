package in.ishankhanna.learningsockets;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class SocketListenerWithThreads extends Thread{

    private ServerSocket listeningSocket;
    private SocketAddress previousClientsSocketAddress;

    @Override public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client on port " +
                                listeningSocket.getLocalPort() + "...");
                Socket client = listeningSocket.accept();
                System.out.println("Remote Socket Address : " + client.getRemoteSocketAddress().toString());
                if (previousClientsSocketAddress == null || !client.getRemoteSocketAddress().equals(previousClientsSocketAddress)) {
                    System.out.println("Just connected to "
                                    + client.getRemoteSocketAddress());
                    previousClientsSocketAddress = client.getRemoteSocketAddress();
                    Runnable runnable = new Runnable() {
                        @Override public void run() {
                            try {
                                while(true) {
                                    DataInputStream in =
                                                    new DataInputStream(client.getInputStream());
                                    System.out.println("Message in Thread with ID : " + currentThread().getId() + "," + in.readUTF());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Thread thread = new Thread(runnable);
                    System.out.println("Spawned new thread with ID : " + thread.getId());
                    thread.start();
                }
            } catch(SocketTimeoutException s)
            {
                System.out.println("Socket timed out!");
                break;
            }catch(IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

    public SocketListenerWithThreads(int port) throws IOException
    {
        listeningSocket = new ServerSocket(port);
        listeningSocket.setSoTimeout(100000);
    }

    public static void main(String[] args) {

        try {
            SocketListenerWithThreads tester = new SocketListenerWithThreads(1337);
            tester.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
