
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class worker_1 {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String CLIENT_HOST = config.CLIENT_IP;
        int CLIENT_PORT = config.CLIENT_PORT;
        int WORKER0PORT = config.WORKER_0_PORT;
        int WORKER1PORT = config.WORKER_1_PORT;

        try (ServerSocket server = new ServerSocket(WORKER1PORT)) {
            while (true) {
                try (Socket workerSocket = server.accept(); ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream())) {
                    // Recibir y procesar el mensaje
                    Sort messageReceived = (Sort) in.readObject();
                    messageReceived.sort(1); // Intentamos ordenar el vector

                    // Si el ordenamiento no se completó, lo pasamos a worker_0
                    if (!messageReceived.isFinished()) {
                        try {
                            Socket nextWorkerSocket = new Socket(CLIENT_HOST, WORKER0PORT);
                            ObjectOutputStream out = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                            out.writeObject(messageReceived);
                            System.out.println("Enviando a worker_0 para continuar el ordenamiento..." + messageReceived.isFinished());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Socket clientSocket = new Socket(CLIENT_HOST, CLIENT_PORT);
                            ObjectOutputStream out1 = new ObjectOutputStream(clientSocket.getOutputStream());

                            out1.writeObject(messageReceived);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break; // Salir del ciclo si el ordenamiento ha terminado 
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            throw ex;
        }
    }
}
