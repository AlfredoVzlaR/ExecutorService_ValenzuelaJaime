/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author gilberto.borrego
 */
public class ClienteSocket {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            Socket kkSocket = new Socket("localhost", 4444);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Se crea un hilo para leer los mensajes del servidor
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String fromServer;
                    try {
                        while ((fromServer = in.readLine()) != null) {
                            System.out.println("Server: " + fromServer);
                            if (fromServer.equals("Bye.")) {
                                break;
                            }
                        }
                    } catch (IOException ex) {
                        System.err.println("Error al leer los datos del servidor: " + ex.getMessage());
                    }
                }
            });

            // Se crea un hilo para enviar los mensajes del cliente al servidor
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String fromUser;
                    try {
                        while ((fromUser = stdIn.readLine()) != null) {
                            System.out.println("Client: " + fromUser);
                            out.println(fromUser);
                        }
                    } catch (IOException ex) {
                        System.err.println("Error al enviar los datos al servidor: " + ex.getMessage());
                    }
                }
            });

            // Se espera a que ambos hilos terminen antes de cerrar las conexiones
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Esperar hasta que ambos hilos terminen
            }

            // Se cierran las conexiones
            out.close();
            in.close();
            stdIn.close();
            kkSocket.close();
        } catch (IOException ex) {
            System.err.println("Error al establecer la conexión con el servidor: " + ex.getMessage());
        }
    }
}
