package classes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class worker_0 {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String CLIENT_HOST = config.CLIENT_IP;
        String WORKER0_HOST = config.WORKER0_IP;
        String WORKER1_HOST = config.WORKER1_IP;
        int CLIENT_PORT = config.CLIENT_PORT;
        int WORKER0PORT = config.WORKER_0_PORT;
        int WORKER1PORT = config.WORKER_1_PORT;

        try (ServerSocket server = new ServerSocket(WORKER0PORT)) {
            
            while (true) {
                try (Socket workerSocket = server.accept(); ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream())) {

                    // Recibir y ordenar el vector
                    Sort messageReceived = (Sort) in.readObject();

                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    // Crear la tarea a ejecutar
                    Runnable tarea = () -> {
                        messageReceived.sort(0);
                    };

                    // Ejecutar la tarea en un hilo separado
                    Future<?> future = executor.submit(tarea);
                    long tiempoInicio = System.currentTimeMillis();

                    // Monitorear el tiempo y el estado de la tarea
                    boolean completado = false;
                    long tiempoMaximo = messageReceived.getFinishTime(); // Tiempo límite
                    long tiempoActual;

                    while ((tiempoActual = System.currentTimeMillis() - tiempoInicio) < tiempoMaximo) {
                        if (future.isDone()) {
                            completado = true;
                            messageReceived.time += tiempoActual;
                            System.out.println("Vector ordenado exitosamente");
                            // Enviar al cliente
                            try (Socket clientSocket = new Socket(CLIENT_HOST, CLIENT_PORT); ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                                out.writeObject(messageReceived);
                            }
                            break;
                        }
                    }

                    // Si no se ordenó se envia al siguiente worker
                    if (!completado) {
                        future.cancel(true);
                        messageReceived.time += tiempoMaximo;
                        System.out.println("Tiempo agotado. Pasando al siguiente worker.");
                        //messageReceived.updateArray();
                        try (Socket nextWorkerSocket = new Socket(WORKER1_HOST, WORKER1PORT); ObjectOutputStream out = new ObjectOutputStream(nextWorkerSocket.getOutputStream())) {
                            out.writeObject(messageReceived);
                        }
                    }

                    // Apagar el executor
                    executor.shutdown();

                    // Salir del bucle si se completó correctamente
                    if (completado) {
                        break;
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            server.close();

        } catch (IOException ex) {
            throw ex;
        }
    }
}
