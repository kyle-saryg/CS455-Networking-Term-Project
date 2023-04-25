import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int PORT_NUMBER = 8000;
    private static final int MAX_NUMBER_OF_PLAYERS = 2;


    public static void main(String[] args) {
        // Used to read data from every client
        BufferedReader[] readers = new BufferedReader[MAX_NUMBER_OF_PLAYERS];
        // Used to send data to every client
        PrintWriter[] writers = new PrintWriter[MAX_NUMBER_OF_PLAYERS];

        try {
            // Creating socket connection
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server started and listening on port " + PORT_NUMBER);

            int connectionCtr = 0;
            while (connectionCtr < MAX_NUMBER_OF_PLAYERS) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                // Creating reader and writer from client
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer = new PrintWriter(outputStream);

                readers[connectionCtr] = reader;
                writers[connectionCtr] = writer;

                connectionCtr += 1;
            }

            System.out.println("RECEIVED " + MAX_NUMBER_OF_PLAYERS + " CONNECTIONS");

            // Reading data from clients
            for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i += 1) {
                System.out.println(receiveFromClient(readers[i]));
            }

            //Sending data to clients
            for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i += 1) {
                sendToClient(writers[i], "WHOLE LOTTA LIGMA" + i);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToClient(PrintWriter writer, String data) {
        data = data + '#';
        writer.print(data);
        writer.flush();
    }

    private static String receiveFromClient(BufferedReader reader) {
        // Reading one character at a time, using StringBuilder
        StringBuilder output = new StringBuilder();

        try {
            char tmp;
            while ((tmp = (char) reader.read()) != '#') {
                output.append(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
