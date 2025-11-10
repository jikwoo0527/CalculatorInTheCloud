import java.io.*;
import java.net.*;


public class CalClient {
    public static void main(String argv[]) throws Exception {
        String sentence;
        String answer;
        String serverIP = "";
        int nPort = 0;

        String status;

        // serverIP, nPort
        try (BufferedReader reader = new BufferedReader(new FileReader("src/server_info.dat"))) {
            serverIP = reader.readLine();
            String temp = reader.readLine();
            nPort = Integer.parseInt(temp);
        } finally {
            serverIP = "127.0.0.1";
            nPort = 6789;
        }

        // prepare I/O
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(serverIP, nPort); // TCP connection
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // send client input
        System.out.print("Enter the Sentence: ");
        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');

        status = inFromServer.readLine();
        answer = inFromServer.readLine();
        System.out.println("Status: " + status);
        System.out.println(answer);
        inFromServer.close();
        outToServer.close();
        clientSocket.close();
    }
}
