
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner read = new Scanner(System.in);
        String HOST = config.CLIENT_IP;
        int WORKER0PORT = config.WORKER_0_PORT;
        int CLIENT_PORT = config.CLIENT_PORT;

        System.out.print("Digite el tamaño del vector a ordenar: ");
        int n = read.nextInt();
        int[] vector = new int[n];
        for (int i = 0; i < n; i++) {
            vector[i] = new Random().nextInt(10000);
        }

        System.out.print("\nDigite el tiempo que debe tardar cada worker (en milisegundos): ");
        long time = read.nextLong();
        while (time <= 0) {
            System.out.println("\nEl tiempo debe ser un número positivo, digite un tiempo válido: ");
            time = read.nextLong();
        }
     

        System.out.println("\nElija el tipo de ordenamiento a aplicar:\n1. Mergesort\n2. Heapsort\n3. Quicksort");
        int sort = read.nextInt();
        while (sort > 3 || sort < 1) {
            System.out.println("\nEl número digitado está fuera del rango. Elija una de las opciones siguientes:\n1. Mergesort\n2. Heapsort\n3. Quicksort");
            sort = read.nextInt();
        }

        // Creación del mensaje a enviar
        Sort message = new Sort(vector, sort,time);

        try (
                ServerSocket server = new ServerSocket(CLIENT_PORT); Socket workerSocket = new Socket(HOST, WORKER0PORT); ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream())) {
            // Envía el objeto Sort al worker0
            out.writeObject(message);
            out.flush();

            // Recepción de respuesta del worker
            try (
                    Socket socket = server.accept(); ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                Sort messageReceived = (Sort) in.readObject();

                // Verificación de terminación de ordenamiento según el worker
                if (messageReceived.getWorkerId() == 0) {
                    System.out.println("Ordenamiento finalizado por worker_0 en " + messageReceived.getTime() + " milisegundos.");
                } else {

                    System.out.println("Ordenamiento finalizado por worker_1 en " + messageReceived.getTime() + " milisegundos.");

                }
                
                for (int a : messageReceived.getVector()) {
                    System.out.print(a + ", ");
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            throw ex;
        }
    }
}
