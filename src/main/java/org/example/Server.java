package org.example;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Server            April 2024
// InputStream could also be buffered for efficiency (BufferedInputStream)

public class Server {

    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args)
    {
        // Create Server Socket running on port 900
        try (ServerSocket serverSocket = new ServerSocket(900)) {
            System.out.println("Server is Starting in Port 900");
            // Accept the Client request using accept method
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected");
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream( clientSocket.getOutputStream());
            // Here we call receiveFile define new for that file
            receiveFile("images/parrot_image_received.jpg");

            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void receiveFile(String fileName)
            throws Exception
    {
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);


        // DataInputStream allows us to read Java primitive types from stream e.g. readLong()
        // read the size of the file in bytes (the file length)
        long size = dataInputStream.readLong();
        System.out.println("Server: file size in bytes = " + size);


        // create a buffer to receive the incoming bytes from the socket
        byte[] buffer = new byte[4 * 1024];         // 4 kilobyte buffer

        System.out.println("Server:  Bytes remaining to be read from socket: ");

        // next, read the raw bytes in chunks (buffer size) that make up the image file
        while (size > 0 &&
                (bytes = dataInputStream.read(buffer, 0,(int)Math.min(buffer.length, size))) != -1) {

            // above, we read a number of bytes from stream to fill the buffer (if there are enough remaining)
            // - the number of bytes we must read is the smallest (min) of: the buffer length and the remaining size of the file
            //- (remember that the last chunk of data read will usually not fill the buffer)

            // Here we write the buffer data into the local file
            fileOutputStream.write(buffer, 0, bytes);

            // reduce the 'size' by the number of bytes read in.
            // 'size' represents the number of bytes remaining to be read from the socket stream.
            // We repeat this until all the bytes are dealt with and the size is reduced to zero
            size = size - bytes;
            System.out.print(size + ", ");
        }

        System.out.println("File is Received");

        fileOutputStream.close();
    }
}
