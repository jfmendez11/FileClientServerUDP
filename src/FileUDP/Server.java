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
public class Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el número de puerto");
        int port = sc.nextInt();
        DatagramSocket serverSocket = new DatagramSocket(port);
        while (true) {
        
        byte[] recieveData = new byte[1024];
        
        DatagramPacket recieveServerPack = new DatagramPacket(recieveData, recieveData.length);
        serverSocket.receive(recieveServerPack);
        InetAddress ip = recieveServerPack.getAddress();
        int cltPort = recieveServerPack.getPort();
        
        String fileName = new String(recieveServerPack.getData());
        int j = 0;
        
        for (int i = 0; i < fileName.length(); i++) {
            if (fileName.charAt(i) == ' ') {
                j = i;
                break;
            }
        }
        
        System.out.println(fileName.substring(0, j));
        
        File file = new File("./Archivos/" + fileName.substring(0, j));
        FileInputStream fis = new FileInputStream(file);
        
        int fileSize = fis.available();
        System.out.println(fileSize);
        
        
        byte[] sendData = new byte[1024];
        int noOfPackets = fileSize/1024;
        
        int offSet = noOfPackets*1024;
        
        int lastPacketLength = fileSize - offSet;
        
        byte[] lastPack = new byte[lastPacketLength-1];
        
        /*for (int i = 0; i < sendData.length; i++) {
            sendData[i] = (byte)fis.read();
        }*/
        int count = 0;
        while ((count = fis.read(sendData)) != -1) {
            if (noOfPackets <= 0) break;
            
            DatagramPacket sendServerPack = new DatagramPacket(sendData, sendData.length, ip, cltPort);
            serverSocket.send(sendServerPack);
            noOfPackets--;
        }
        
        DatagramPacket lastPackSend = new DatagramPacket(lastPack, lastPack.length, ip, cltPort);
        serverSocket.send(lastPackSend);
        
        
        byte[] hash = new byte[1024];
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        
        String checksum = getFileChecksum(md5Digest, file);
        hash = checksum.getBytes();
        
        DatagramPacket hashPack = new DatagramPacket(hash, hash.length, ip, cltPort);
        serverSocket.send(hashPack);
        
        System.out.println(checksum);
        }
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
