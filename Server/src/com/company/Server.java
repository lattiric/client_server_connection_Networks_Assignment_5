package com.company;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;


public class Server{

    //function to get the same key for both
    public static SecretKey getKeyFromPassword(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256); // 65536 iterations, 256-bit key
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    //function to decrypt password
    public static String decryptString(String strToDecrypt, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        return new String(decrypted, "UTF-8");
    }

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

                String password = "Password";
                String salt = "salt";

                SecretKey secretKey = getKeyFromPassword(password, salt);

                //sending messages
                while (true) {

                    String msgFromClient = bufferedReader.readLine();

                    System.out.println("Encrypted client: "+msgFromClient);

                    //sends message back to client
                    bufferedWriter.write("MSG Recieved.");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    String decryptedString = decryptString(msgFromClient, secretKey);
                    System.out.println("Plain Text Client: "+decryptedString);

                    if (msgFromClient.equalsIgnoreCase("STOP"))
                        break;
                }

                socket.close();
                inputStreamReader.close();
                outputStreamWriter.close();
                bufferedReader.close();
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
