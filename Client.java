import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);

            // Create input and output streams for communication with the server
            ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

            // Receive the prompt message from the server
            String promptMessage = (String) inFromServer.readObject();
            System.out.println(promptMessage);

            // Read the roll number from the user
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String rollNumber = userInputReader.readLine();

            // Send the roll number to the server
            outToServer.writeObject(rollNumber);

            // Receive the response from the server
            Object receivedObject = inFromServer.readObject();

            if (receivedObject instanceof String) {
                String xmlContent = (String) receivedObject;
                if (xmlContent.equals("Student not found.")) {
                    System.out.println(xmlContent);
                } else {
                    System.out.println("XML file received:\n" + xmlContent);
                }
            }

            // Close the input and output streams and socket connection
            inFromServer.close();
            outToServer.close();
            userInputReader.close();
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
