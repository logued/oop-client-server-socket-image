package org.example;        // March 2025
import java.io.*;
import java.net.Socket;

// Client program
// This clients attempts to connect to a Server (on a particular Port).
// If successful, this client sends the following to the server:
// 1. An 8-byte long integer value which is the count of the number of bytes
//    in the image file, which is about to be sent.
// 2. The contents of the image file is send in data chunks of size [4 x 1024]
//

public class Client {
    final static int SERVER_PORT = 1024;

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args)
    {
        // Create Client Socket connect to port 900
        try (Socket socket = new Socket("localhost", SERVER_PORT)) {

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream( socket.getOutputStream());
            System.out.println("Sending the File to the Server");
            // Call SendFile Method
            sendFile("images/parrot_image.jpg");   // hardcode location for convenience
            dataInputStream.close();
            dataInputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sendFile function define here
    private static void sendFile(String path)
            throws Exception
    {
        int bytes = 0;
        // Open the File at the specified location (path)
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send the length (in bytes) of the file to the server.
        // The server expects this, as this is the Protocol.
        dataOutputStream.writeLong(file.length());

        // Here we break file into chunks
        byte[] buffer = new byte[4 * 1024]; // 4 kilobyte buffer

        // read bytes from file into the buffer until buffer is full, or we reached end of file
        while ((bytes = fileInputStream.read(buffer))!= -1) {
            // Send the buffer contents to the Server over our socket,
            // along with the count of the number of bytes we are sending.
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();   // force the data into the stream
        }
        // close the file
        fileInputStream.close();
    }
}
