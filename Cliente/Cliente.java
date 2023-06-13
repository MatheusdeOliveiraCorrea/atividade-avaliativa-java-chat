package Cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Cliente(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;

        } catch (Exception e) {
            FecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }

    public void EnviarMensagem() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String mensagem = scanner.nextLine();

                bufferedWriter.write(username + ": " + mensagem);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (Exception e) {
            FecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }

    public void escutarNovasMensagens() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String msgDoGrupo;

                while (socket.isConnected()) {
                    try {
                        msgDoGrupo = bufferedReader.readLine();

                        if (msgDoGrupo == null)
                            FecharTudo(socket, bufferedReader, bufferedWriter);

                        System.out.println(msgDoGrupo);
                    } catch (Exception e) {
                        FecharTudo(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void FecharTudo(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static String obterNomeUsuario() {

        String nome;
        boolean validacao;

        do {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Escreva seu nome de usuário");

            nome = scanner.nextLine();

            validacao = nome.equals("") || nome.length() <= 1;

            if (validacao) {
                System.out.println("Nome inválido: nome não pode ser vazio e deve ter no mínimo dois caracteres");
            }

        } while (validacao);

        return nome;
    }

    public static void main(String[] args) {

        try {
            String username = obterNomeUsuario();

            Socket socket;

            socket = new Socket("localhost", 1234);
            Cliente cliente = new Cliente(socket, username);

            cliente.escutarNovasMensagens();

            cliente.EnviarMensagem();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
