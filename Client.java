import java.io.*;
import java.net.*;

public class Client {

    // Write JSON data to socket output stream
    public static void sendJsonData(PrintWriter out, String json) throws Exception {
        // Adding custom delimiter
        out.print(json + '#');
        out.flush();
    }

    public static String receiveJsonData(BufferedReader in) throws Exception {
        // Read response from server
        StringBuilder response = new StringBuilder("");
        char tmp;
        while ((tmp = (char)in.read()) != '#') {
            response.append(tmp);
        }

        return response.toString();
    }
}