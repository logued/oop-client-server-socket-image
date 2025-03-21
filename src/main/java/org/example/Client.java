package org.example;        // March 2025
import java.io.*;
import java.net.Socket;

// Client program
// This client attempts to connect to a Server (on a particular Port).
// If successful, this client sends the following to the server:
// 1. An 8-byte long integer value which is the count of the number of bytes
//    in the image file that we are sending.
// 2. The contents of the image file is sent as a stream of bytes
//     in data chunks of size [4 x 1024] bytes.
//

public class Client {

    final int SERVER_PORT = 1024;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        // Create Client Socket connect to SERVER_PORT
        try (Socket socket = new Socket("localhost", SERVER_PORT)) {

            DataOutputStream dataOutputStream = new DataOutputStream( socket.getOutputStream());
            System.out.println("Sending the File to the Server");
            // Call SendFile Method
            sendFile("images/parrot_image.jpg", dataOutputStream);   // hardcode location for convenience
            dataOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read data from a file and write that data to the socket output stream.
     * @param fileName
     * @param dataOutputStream - output stream of the socket
     * @throws Exception
     */
    private void sendFile(String fileName, DataOutputStream dataOutputStream) throws Exception
    {
        int numberOfBytes = 0;
        // Open the File at the specified location (path)
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send the length (in bytes) of the file to the server as a "long"
        // The server expects this, as this is the Protocol.
        dataOutputStream.writeLong(file.length());

        // Here we break file into chunks by using a buffer
        byte[] buffer = new byte[4 * 1024]; // 4 kilobyte buffer

        // read bytes from file into the buffer until buffer is full,
        // or until we have reached the end of the image file
        while ((numberOfBytes = fileInputStream.read(buffer))!= -1) {
            // write "numberOfBytes" bytes from the buffer to the socket output stream
            dataOutputStream.write(buffer, 0, numberOfBytes);
            dataOutputStream.flush();   // force the data into the stream
        }
        // close the file
        fileInputStream.close();
    }
}
