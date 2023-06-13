package Cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clienthandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clienthandlers.add(this);

            broadcastMessage("SERVER: " + clientUserName + " entrou");

        } catch (Exception e) {
            FecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String mensagemDoCliente;

        while (socket.isConnected()) {
            try {
                mensagemDoCliente = bufferedReader.readLine();

                String usernameMensagem = clientUserName + ": ";

                if (mensagemDoCliente.startsWith(usernameMensagem + "/")) {

                    if (mensagemDoCliente.equalsIgnoreCase(usernameMensagem + "/Sair")) {
                        this.socket.close();
                        removerCliente();
                    }
                }

                broadcastMessage(mensagemDoCliente);
            } catch (Exception e) {
                FecharTudo(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String mensagem) {
        for (ClientHandler clientHandler : clienthandlers) {
            try {
                if (!clientHandler.clientUserName.equals(clientUserName)) {
                    clientHandler.bufferedWriter.write(mensagem);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                FecharTudo(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removerCliente() {
        clienthandlers.remove(this);
        broadcastMessage("SERVER: " + clientUserName + " saiu do chat");
    }

    public void FecharTudo(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removerCliente();

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
