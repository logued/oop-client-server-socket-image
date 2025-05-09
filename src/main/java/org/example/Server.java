package org.example;                // April 2025

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Server
// This server awaits a connection from a client, and when a client requests to connect,
// then a Socket is established between the client and server.
// The server then waits for (image) data to arrive from the client, and when it arrives,
// the data is read in batches and is written to a local ,jpg file.

// InputStream could also be buffered for efficiency (BufferedInputStream)

public class Server {

    final static int SERVER_PORT = 1024;

    private DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {

        // Create Server Socket to listen for a connection request on specific port number
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is Starting on Port "+SERVER_PORT);

            // Wait for a client to connect, and then set up a Socket to receive data
            Socket clientSocket = serverSocket.accept();
            System.out.println("Server has accepted connection from client.");



            // Get input and output streams on the Socket to send/receive binary data
            dataInputStream = new DataInputStream(clientSocket.getInputStream());

            System.out.println("Server is waiting for image data to arrive...");
            // call function to extract file data from the data input stream and write to file
            receiveFile("images/parrot_image_received.jpg");

            dataInputStream.close();
            clientSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Read data from the input stream (data sent by the client)
     * The first 8 bytes represents a long integer value which is a count of the number of
     * bytes that will follow in this stream.  i.e. the length of the image data in bytes.
     * We read a fixed number of bytes into a buffer (temporary storage), and then write the
     * contents of the buffer into a local file.  We repeat this until we have consumed
     * the number of bytes indicated by the count.
     * (In Java, a long data type is 8-bytes)
     *
     * @param fileName - name for new local file to store received data
     * @throws Exception
     */
    private void receiveFile(String fileName) throws Exception
    {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        // DataInputStream allows us to read Java primitive types from stream
        // e.g. readLong()

        // bytes remaining to be read (initially equal to file size),
        // but reduced by number of bytes read each time
        long numberOfBytesRemaining = dataInputStream.readLong();
        System.out.println("Server: size of image file (in bytes) = " + numberOfBytesRemaining);

        // Create a Buffer to receive a portion of the file from the socket
        // A buffer is simply an array that stores a certain amount of data temporarily.
        //
        byte[] buffer = new byte[4*1024];         // 4 kilobyte buffer
        System.out.println("Buffer size: " + (4*1024) + " bytes");
        int numberOfBytesRead = 0;    // number of bytes read from the socket so far (excluding the initial count)

        // next, read the incoming bytes in chunks (of buffer size) that make up the image file
        //
        while (numberOfBytesRemaining > 0 &&  (numberOfBytesRead =
                dataInputStream.read(buffer, 0,(int)Math.min(buffer.length, numberOfBytesRemaining))) != -1) {

            // above,
            // while there are bytes remaining to be read, then,
            // we read the number of bytes from stream to fill the buffer (if there are enough bytes remaining)
            // - the number of bytes we must read is the smallest (min) of:
            //    the buffer length and the remaining size of the file
            // - (note that the last chunk of data read will usually not fill the buffer)


            // write the buffer data into the local file
            fileOutputStream.write(buffer, 0, numberOfBytesRead);  // write N number of bytes from buffer into file

            // reduce the 'numberOfBytesRemaining' to be read by the number of bytes read in.
            // 'numberOfBytesRemaining' represents the number of bytes remaining to be read from
            // the input stream.
            // We repeat this until all the bytes are dealt with and the remaining size is reduced to zero
            numberOfBytesRemaining = numberOfBytesRemaining - numberOfBytesRead;

            System.out.print("Bytes read: " + numberOfBytesRead);
            System.out.println(" - Bytes remaining: " + numberOfBytesRemaining);
        }
        fileOutputStream.close();

        System.out.println("File was Received");
        System.out.println("Look in the images folder to see the transferred file: parrot_image_received.jpg");
    }
}
