import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        /*
        Main function needs to handle socket creation and deletion
        Once a socket stream is closed it cannot be reopened
        */
        try {
            //Creating Json
            String json = "{\"Response\":\"B\"}";

            // Create socket object with server address and port
            Socket socket = new Socket("192.168.0.20", 8000);

            // Create PrintWriter object for writing to socket output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Sending Json to server
            Client.sendJsonData(out, json);

            // Create BufferedReader object for reading from socket input stream
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Receiving data from Server
            String response = Client.receiveJsonData(inputStream);
            System.out.println(response);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
