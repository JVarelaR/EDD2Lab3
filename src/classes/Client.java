package classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class Client {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner read = new Scanner(System.in);
        String CLIENT_HOST = config.CLIENT_IP;
        String WORKER0_HOST = config.WORKER0_IP;
        String WORKER1_HOST = config.WORKER1_IP;
        int WORKER0PORT = config.WORKER_0_PORT;
        int CLIENT_PORT = config.CLIENT_PORT;
        int[] array;

        
        /*
        //Creacion del array aleatorio
        int n = 50000;
        array=new int[n];
        for(int i=0;i<n;i++){
            array[i]= new Random().nextInt(10000);
        }
        */
        
        
        // Lectura del archivo
        FileReader fr = new FileReader("src/texts/array.txt");
        BufferedReader br = new BufferedReader(fr);
        String linea;
        int i = 0;
        while ((br.readLine()) != null) {
            i++;
        }
        br.close();
        
        fr = new FileReader("src/texts/array.txt");
        br = new BufferedReader(fr);
        array = new int[i];
        i = 0;
        //Creación del array
        while ((linea = br.readLine()) != null) {
            array[i] = (Integer.parseInt(linea));
            i++;
        }
        br.close();
        
        
        System.out.print("--------------------------- Ordenamiento -------------------------");
        
        //Establecimiento del tiempo para ordenar
        System.out.print("\nDigite el tiempo que debe tardar cada worker (en milisegundos): ");
        long time = read.nextLong();
        while (time <= 0) {
            System.out.println("\nEl tiempo debe ser un número positivo, digite un tiempo válido: ");
            time = read.nextLong();
        }

        //Establecimiento del ordenamiento
        System.out.println("\nElija el tipo de ordenamiento a aplicar:\n1. Mergesort\n2. Heapsort\n3. Quicksort");
        int sort = read.nextInt();
        while (sort > 3 || sort < 1) {
            System.out.println("\nEl número digitado está fuera del rango. Elija una de las opciones siguientes:\n1. Mergesort\n2. Heapsort\n3. Quicksort");
            sort = read.nextInt();
        }

        // Creación del mensaje a enviar
        Sort message = new Sort(array, sort, time);

        try (
                ServerSocket server = new ServerSocket(CLIENT_PORT); Socket workerSocket = new Socket(WORKER0_HOST, WORKER0PORT); ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream())) {
            // Envía el objeto Sort al worker0
            out.writeObject(message);
            out.flush();

            // Recepción de respuesta del worker
            try (
                    Socket socket = server.accept(); ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                Sort messageReceived = (Sort) in.readObject();

                // Verificación del worker que terminó el ordenamiento
                if (messageReceived.getWorkerId() == 0) {
                    System.out.println("Ordenamiento finalizado por worker_0 en " + messageReceived.getTime() + " milisegundos.");
                } else {

                    System.out.println("Ordenamiento finalizado por worker_1 en " + messageReceived.getTime() + " milisegundos.");

                }

                
                //Escribir un archivo con el vector ordenado
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("src/texts/sortedArray.txt"));
                    for(int j=0;j<messageReceived.getVector().length;j++){
                        writer.write(messageReceived.getVector()[j]+"\n");
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException ex) {
            throw ex;
        }
    }
}
