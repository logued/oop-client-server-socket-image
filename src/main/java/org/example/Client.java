package org.example;        // April 2025
import java.io.*;
import java.net.Socket;

// Client program.  (Remember to run Server first)
// This client attempts to connect to a Server (on a particular Port).
// If successful, a socket is established between the client and server, and
// this client sends the following data to the server:
// 1. An 8-byte long integer value which is the count of the number of bytes
//    in the image file that we are sending.
// 2. The contents of the image file is sent as a stream of bytes
//     in data chunks of size [4 * 1024] bytes.
// (Sending the data as chunks is more efficient than sending data byte-by-byte)

public class Client {

    final static int SERVER_PORT = 1024;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        // Create Client Socket connect to SERVER_PORT
        try (Socket socket = new Socket("localhost", SERVER_PORT)) {

            // create a DataOutputStream on the socket in order to send data
            DataOutputStream dataOutputStream = new DataOutputStream( socket.getOutputStream() );
            System.out.println("Sending the File to the Server");

            // sendFile() method will send the file data
            sendFile("images/parrot_image.jpg", dataOutputStream);   // hardcode location for convenience

            dataOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read data from a file and write that data to the socket via data output stream.
     * @param fileName
     * @param dataOutputStream - data output stream of the socket
     * @throws Exception
     */
    private void sendFile(String fileName, DataOutputStream dataOutputStream) throws Exception
    {
        int numberOfBytes = 0;
        // Open the File at the specified location (path)
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send the length (in bytes) of the file to the server as a "long"
        // The server expects this value as the first piece of data, as this is the designed Protocol.
        dataOutputStream.writeLong( file.length() );

        // create a Buffer to store chunks of data to be sent on the socket
        byte[] buffer = new byte[4 * 1024]; // ~4 kilobyte buffer

        // read bytes from file into the buffer until the buffer is full,
        // or until we have reached the end of the image file
        while ((numberOfBytes = fileInputStream.read(buffer))!= -1) {
            // write "numberOfBytes" bytes from the buffer to the data output stream (on Socket)
            dataOutputStream.write(buffer, 0, numberOfBytes);
            dataOutputStream.flush();   // force the data into the stream
        }
        // close the file
        fileInputStream.close();
    }
}
