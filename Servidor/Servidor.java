package Servidor;

import java.net.ServerSocket;
import java.net.Socket;

import Cliente.ClientHandler;

public class Servidor {

    private static ServerSocket serverSocket;

    public Servidor(ServerSocket serverSocket) {
        Servidor.serverSocket = serverSocket;
    }

    public void startServer() {
        try {

            while (!serverSocket.isClosed()) {

                System.out.println("Esperando a conexão de algum cliente");
                Socket socket = serverSocket.accept();
                System.out.println("Cliente Conectou");

                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {

            Servidor servidor = new Servidor(new ServerSocket(1234));

            servidor.startServer();

        } catch (Exception e) {
            closeServerSocket();
        }

    }

}
