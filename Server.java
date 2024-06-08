import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    public static void main(String[] args) throws IOException{
        
        // creating all relevant variables
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        ServerSocket serverSocket = null;
        
        serverSocket = new ServerSocket(1234);

        while (true) {
            try{
                //accepts a new client connection
                socket = serverSocket.accept();

                // creating streams for server client to communicate
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                // wraps in buffer to improve efficiency
                bufferedReader =  new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                //sending messages
                while (true) {

                    String msgFromClient = bufferedReader.readLine();

                    System.out.println("Client: "+msgFromClient);

                    //sends message back to client
                    bufferedWriter.write("MSG Recieved.");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();


                    if (msgFromClient.equalsIgnoreCase("BYE"))
                        break;

                }

                socket.close();
                inputStreamReader.close();
                outputStreamWriter.close();
                bufferedReader.close();
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            } 

        }

    }
}
