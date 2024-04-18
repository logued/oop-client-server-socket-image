package org.example;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

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


    /**
     * Read file length from socket, using a buffer, and write the received bytes to a local file.
     *
     * @param fileName - name for new local file to store received data
     * @throws Exception
     */
    private static void receiveFile(String fileName)
            throws Exception
    {
     FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        // DataInputStream allows us to read Java primitive types from stream e.g. readLong()
        // read the size of the file in bytes (the file length)
        long bytes_remaining = dataInputStream.readLong(); // bytes remaining to be read (initially equal to file size)
        System.out.println("Server: file size in bytes = " + bytes_remaining);

        // create a buffer to receive the incoming bytes from the socket
        byte[] buffer = new byte[4 * 1024];         // 4 kilobyte buffer

        System.out.println("Server:  Bytes remaining to be read from socket: ");
        int bytes_read = 0;    // number of bytes read from the socket

        // next, read the raw bytes in chunks (buffer size) that make up the image file
        while (bytes_remaining > 0 &&  (bytes_read =
                dataInputStream.read(buffer, 0,(int)Math.min(buffer.length, bytes_remaining))) != -1) {

            // above, we read a number of bytes from stream to fill the buffer (if there are enough remaining)
            // - the number of bytes we must read is the smallest (min) of: the buffer length and the remaining size of the file
            //- (remember that the last chunk of data read will usually not fill the buffer)

            // Here we write the buffer data into the local file
            fileOutputStream.write(buffer, 0, bytes_read);

            // reduce the 'bytes_remaining' to be read by the number of bytes read in.
            // 'bytes_remaining' represents the number of bytes remaining to be read from the source file (via socket)
            // We repeat this until all the bytes are dealt with and the size is reduced to zero
            bytes_remaining = bytes_remaining - bytes_read;

            System.out.print(bytes_remaining + ", ");
        }

        System.out.println("File is Received");

        System.out.println("Look in the images folder to see the transferred file: parrot_image_received.jpg");
        fileOutputStream.close();
    }
}
