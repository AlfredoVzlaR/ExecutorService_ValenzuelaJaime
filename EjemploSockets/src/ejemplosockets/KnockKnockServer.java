/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejemplosockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author gilberto.borrego
 */
public class KnockKnockServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(3); // Número de hilos a utilizar
        try {
            ServerSocket serverSocket = new ServerSocket(4444);
            System.out.println("Servidor iniciado");

            while (true) { // Ciclo infinito para esperar conexiones
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexión aceptada de " + clientSocket.getInetAddress());

                // Crear un nuevo hilo para manejar la conexión
                pool.execute(new Runnable() {

                    public void run() {
                        try {
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                            String inputLine, outputLine;
                            KnockKnockProtocol kkp = new KnockKnockProtocol();

                            outputLine = kkp.processInput(null);
                            out.println(outputLine);

                            while ((inputLine = in.readLine()) != null) {
                                outputLine = kkp.processInput(inputLine);
                                out.println(outputLine);
                                if (outputLine.equals("Bye.")) {
                                    break;
                                }
                            }
                            out.close();
                            in.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }
    }
}
