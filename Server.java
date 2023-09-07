import java.io.*;
import java.net.*;
import java.sql.*;


public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started and waiting for client connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                // Create input and output streams for communication with the client
                ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());

                // Prompt the client to enter the roll number
                outToClient.writeObject("Enter Roll Number:");

                // Receive the roll number from the client
                String rollNumber = (String) inFromClient.readObject();
                System.out.println("Received Roll Number: " + rollNumber);

                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish a connection to the database
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "root");

                // Prepare the SQL statement to retrieve student data based on the roll number
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM student_info WHERE rollno = ?");
                statement.setString(1, rollNumber);

                // Execute the SQL query
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Retrieve the student data from the result set
                    String name = resultSet.getString("name");
                    int sub1Marks = resultSet.getInt("sub1_marks");
                    int sub2Marks = resultSet.getInt("sub2_marks");
                    int sub3Marks = resultSet.getInt("sub3_marks");
                    int sub4Marks = resultSet.getInt("sub4_marks");

                    // Calculate the average marks
                    double averageMarks = (sub1Marks + sub2Marks + sub3Marks + sub4Marks) / 4.0;

                    // Generate the XML content with the student data
                    String xmlContent = "<student>\n" +
                            "  <name>" + name + "</name>\n" +
                            "  <average_marks>" + averageMarks + "</average_marks>\n" +
                            "</student>";

                    // Send the XML content to the client
                    outToClient.writeObject(xmlContent);
                } else {
                    // Send a message indicating that the student was not found
                    outToClient.writeObject("Student not found.");
                }

                // Close the database resources and socket connection
                resultSet.close();
                statement.close();
                connection.close();
                inFromClient.close();
                outToClient.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
