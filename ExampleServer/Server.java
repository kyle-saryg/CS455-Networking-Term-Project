import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static final int PORT_NUMBER = 8000;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server started and listening on port " + PORT_NUMBER);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder output = new StringBuilder("");
                char tmp;

                System.out.println("Reading input from client");

                while ((tmp = (char)reader.read()) != '#') {
                    output.append(tmp);
//                    System.out.println(tmp);
                }
                System.out.println(output.toString());

                // ***process the JSON data here***

                PrintWriter writer = new PrintWriter(outputStream);
                writer.print("a whole lotta ligma#");
                writer.flush();
                System.out.println("Response sent to client.");

                clientSocket.close();
                System.out.println("Client disconnected.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
