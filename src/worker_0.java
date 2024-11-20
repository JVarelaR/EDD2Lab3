
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class worker_0 {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String CLIENT_HOST = config.CLIENT_IP;
        int CLIENT_PORT = config.CLIENT_PORT;
        int WORKER0PORT = config.WORKER_0_PORT;
        int WORKER1PORT = config.WORKER_1_PORT;

        try (ServerSocket server = new ServerSocket(WORKER0PORT)) {
            while (true) {
                try {
                    Socket workerSocket = server.accept();
                    ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());

                    // Recibir y ordenar el vector
                    Sort messageReceived = (Sort) in.readObject();
                    messageReceived.sort(0); // Intentamos ordenar el vector
                    
                    // Si el ordenamiento se completó se pasa al cliente
                    if (messageReceived.isFinished()) {
                        try{
                            Socket clientSocket = new Socket(CLIENT_HOST, CLIENT_PORT); 
                            ObjectOutputStream out1 = new ObjectOutputStream(clientSocket.getOutputStream());
                            out1.writeObject(messageReceived);

                        } catch (IOException e) {
                            e.printStackTrace();

                        } finally{
                            break;
                        }
                        
                    } else {
                        try {
                            Socket nextWorkerSocket = new Socket(CLIENT_HOST, WORKER1PORT);
                            ObjectOutputStream out = new ObjectOutputStream(nextWorkerSocket.getOutputStream());
                            out.writeObject(messageReceived);

                            System.out.println("Enviando a worker_1 para continuar el ordenamiento...");
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
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
