/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileUDP;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

/**
 *
 * @author JuanFelipe
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        DatagramSocket clientSocket = new DatagramSocket();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Ingrese la dirección IP del servidor:");
        String hostname = sc.next();
        System.out.println("Ingrese el número de puerto");
        int port = sc.nextInt();
        
        InetAddress ip = InetAddress.getByName(hostname);
        byte [] send = new byte[1024];
        byte [] recieve = new byte [1024];
        System.out.println("Ingrese el numero del archivo que desea descargar del serivdor:\n\t1. archivoMenor2MB.txt\n\t2. archivoMenor10MB.txt\n\t3. archivoMenor100MB.txt\n\t4. prueba.txt");
        String sentence = "";
        
        boolean aproved = false;
        
        while (!aproved) {
            int fileOption = sc.nextInt();
            if (fileOption == 1) { sentence = "archivoMenor2MB.txt"; aproved = true;} 
            else if (fileOption == 2) { sentence = "archivoMenor10MB.txt"; aproved = true;}
            else if (fileOption == 3) { sentence = "archivoMenor100MB.txt"; aproved = true;}
            else if (fileOption == 4) { sentence = "prueba.txt"; aproved = true;}
            else System.out.println("Ingrese un número entre 1 y 3 dependiendo del archivo que desee descargar");
        }
        
        System.out.println(sentence);
        
        send = sentence.getBytes();
        
        DatagramPacket sendPacket = new DatagramPacket(send, send.length, ip, port);
        clientSocket.send(sendPacket);
        
        //DatagramPacket recievePacket = new DatagramPacket(recieve, recieve.length);
        //clientSocket.receive(recievePacket);
        
        FileWriter fil = new FileWriter("./ClientRepo/" + sentence);
        File f = new File("./ClientRepo/" + sentence);
        
        //byte [] recievedData = recievePacket.getData();
        
        FileOutputStream fos = new FileOutputStream(f);
        int packetSize = 1024;
        while(packetSize >= 1024) {
            DatagramPacket recievePacket = new DatagramPacket(recieve, recieve.length);
            clientSocket.receive(recievePacket);
            fos.write(recievePacket.getData(), 0, recievePacket.getLength());
            packetSize = recievePacket.getLength();
            fos.flush();
        }
              
        
        //fos.write(recievedData);
        
        byte[] hash = new byte [1024];
        DatagramPacket hashPacket = new DatagramPacket(hash, hash.length);
        clientSocket.receive(hashPacket);
        
        String checksumServer = new String(hashPacket.getData());
        
        int j = 0;
        for (int i = 0; i < checksumServer.length(); i++) {
            if (checksumServer.charAt(i) == ' ') {
                j = i;
                break;
            }
        }
        
        System.out.println(checksumServer.substring(0, j));
        
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        String checksumClient = getFileChecksum(md5Digest, f);
        System.out.println(checksumClient);
        
        if (checksumServer.equals(checksumClient)) System.out.println("El archivo llegó correctamente");
        else System.err.println("El archivo llegó incorrecto");
        
        fos.close();
        clientSocket.close();
        
        //TODO falta el hash
    }
    
        
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
       return sb.toString();
    }
    
}
