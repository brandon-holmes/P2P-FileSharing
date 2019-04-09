/*
Client sends message containing only a file name
The server accepts a connection with this client on a separate thread so that it can listen for more connections
If the server finds the file that the client has requested it sends the file
If the server cannot find the image requested then it closes the connection
*/
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;


public class P2PServer {
	
	public static void main(String[] args) throws IOException {
		//Server Socket to listen for new connections
		ServerSocket listenSocket = new ServerSocket(20381);
		//Constantly listens for new connection
		//When connection accepted a new "ServerThread" is created
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
	//Socket and Data Streams
	Socket connectionSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	
	//Constructor
	public ServerThread(Socket socket, BufferedReader input, DataOutputStream output) {
		this.connectionSocket = socket;
		this.inFromClient = input;
		this.outToClient = output;
	}
	//Main method for each thread
	public void run(){
		//Waits for a client to send a file name
		String fileName = null;
		System.out.println("listening for message");
		try {
			//Stores file name sent from client
			fileName = inFromClient.readLine();
			File picture = new File(fileName);
			//Checks if file exists
			if (!picture.exists() || !picture.isFile())
	        {
				//If file does not exists, closes connection
	            System.out.println("Not a file");
	            connectionSocket.close();
	        }
	        else {
	        	//If file exists proceeds to send file
	        	//Converts image to BufferedImage
	        	BufferedImage image = ImageIO.read(picture);
	        	//Writes BufferedImage to DataOutputStream
	        	ImageIO.write(image, "jpg", outToClient);
	        	outToClient.flush();
	        	//Closes connection once file sent
	        	connectionSocket.close();
	        }
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
