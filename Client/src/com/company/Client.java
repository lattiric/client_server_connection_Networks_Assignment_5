package com.company;

//main method imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

//encryption imports
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class Client{
    //generates encryption key that is based on password
    public static SecretKey getKeyFromPassword(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256); // 65536 iterations, 256-bit key
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    //encrypts string
    public static String encryptString(String strToEncrypt, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static void main(String[] args){

        // creating all relevant variables
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            //socket telling client where the server is
            socket = new Socket("localhost", 1234);

            // creating streams for server client to communicate
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            // wraps in buffer to improve efficiency
            bufferedReader =  new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            // input stream connected to keyboard
            Scanner scanner = new Scanner(System.in);

            String password = "Password";
            String salt = "salt";

            SecretKey secretKey = getKeyFromPassword(password, salt);

            while(true){
                //gets
                String msgToSend = scanner.nextLine();
                String encryptedString = encryptString(msgToSend, secretKey);

                //sends message to server
                bufferedWriter.write(encryptedString);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                //recieves message from server
                System.out.println("Server: " + bufferedReader.readLine());

                if (msgToSend.equalsIgnoreCase("STOP"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(socket != null)
                    socket.close();
                if(inputStreamReader != null)
                    inputStreamReader.close();
                if(outputStreamWriter != null)
                    outputStreamWriter.close();
                if(bufferedReader != null)
                    bufferedReader.close();
                if(bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
