import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;


public class P2PServer {
	
	public static void main(String[] args) throws IOException {
		ServerSocket listenSocket = new ServerSocket(6789);
		while (true) {
			Socket connectionSocket = listenSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			Thread process = new ServerThread(connectionSocket, inFromClient, outToClient);
			process.start();
		}
    }
}

class ServerThread extends Thread {
	
	Socket connectionSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	
	public ServerThread(Socket socket, BufferedReader input, DataOutputStream output) {
		this.connectionSocket = socket;
		this.inFromClient = input;
		this.outToClient = output;
	}
	
	public void run(){
		String fileName = null;
		System.out.println("listening for message");
		try {
			fileName = inFromClient.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File picture = new File(fileName);
		if (!picture.exists() || !picture.isFile())
        {
            System.out.println("Not a file");
        }
        else { 
        	System.out.println("Is a file");
        }
	}
}
