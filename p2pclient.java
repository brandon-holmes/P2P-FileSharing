//Brandon Holmes 500751878
//peer to peer client used to interact with the created DHT server and P2P server
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.imageio.ImageIO;


public class p2pclient {
	private static DatagramSocket socket; // UDP Socket
	private static InetAddress address; //DHT Pool IP Address
	private static InetAddress serverAddress;//Server IP
	private static int port = 2000; // DHT server 1 port
	private static int sPort = 20381; // P2P server port
	private static byte[] buff = new byte[1024]; //Input buffer
	private static String[] submissions = new String[15];//Maximum 15 entries from this client
	private static int subCount=0;
	
	public static void main(String [] args) throws IOException {
		//(yet to imnplement, might not be needed) run init, getting ip addresses of DHT servers
		boolean running = true;//GUI /user input run var
		try {//initialize UDP sockets
			socket = new DatagramSocket();
		} catch (SocketException e2) {
			System.out.println("socket Failure");
		}
		try {//initialize IP address for everything, currently just my local IP
			address = InetAddress.getByName(args[0]);// Change IP here
		} catch (UnknownHostException e1) {
		}
		
		while(running) {//main loop
			System.out.println("input 'S' to store content in DHT server \n"
							+ "input 'Q' to query for content \n"
							+ "input 'D' to download file \n"
							+ "input 'E' to exit \n"
				);
			Scanner usrInput = new Scanner(System.in);//gets usr choice
			String choice = usrInput.next();
			
			if( choice.equalsIgnoreCase("S")) { // Sends request for DHT pool to store data of IP and files
				
				System.out.println("Please type the exact name of the file you wish to upload: (including extension)");
				String file = usrInput.next();//file name
				
				int fileHash = computeHash(file);
				int realPort = port + ((fileHash%4)*2-2);
				byte[] request = file.getBytes();
				byte[] requestType = "1".getBytes();
				DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address, realPort);
				socket.send(packet);
				
				String responseStr;
				DatagramPacket response = new DatagramPacket(requestType, requestType.length);
				socket.receive(response);
				responseStr = new String(packet.getData(),0, packet.getLength());
				
					if(responseStr.equals("1")){ //verifies server is ready for request
						DatagramPacket query = new DatagramPacket(request, request.length, address, realPort);
						socket.send(query);
						submissions[subCount]=file;
						subCount++;
						
						DatagramPacket queryInfo = new DatagramPacket(request, request.length);
						socket.receive(queryInfo);
						String queryInfoStr = new String(queryInfo.getData(),0, queryInfo.getLength());
						System.out.println(queryInfoStr);
					}
					else {
						System.out.println("Server sent incorrect response");
					}
			}//End of info store protocol
			
			else if (choice.equalsIgnoreCase("Q")) { // asks server to give location of specific file (IP/port) 						
				System.out.println("Please type the exact name of the file you are looking for: \n");
				String file = usrInput.next();				
				
				int fileHash = computeHash(file);
				int realPort = port + ((fileHash%4)*2-2);
				byte[] request = file.getBytes();
				byte[] requestType = "2".getBytes();
				DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address,  realPort);
				socket.send(packet);
				
				String responseStr;
				DatagramPacket response = new DatagramPacket(requestType, requestType.length);	
				socket.receive(response);
				responseStr = new String(packet.getData(),0, packet.getLength());
				
					if(responseStr.equals("1")){//verifies server is ready for request
						DatagramPacket query = new DatagramPacket(request, request.length, address,  realPort);						
						socket.send(query);
						DatagramPacket queryInfo = new DatagramPacket(request, request.length);
						socket.receive(queryInfo);
						String queryInfoStr = new String(queryInfo.getData(),0, queryInfo.getLength());
						System.out.println(queryInfoStr);
					}
				//send 
			}//End of Query protocol
			
			else if (choice.equalsIgnoreCase("D")) { // Initiates TCP connection with 
				System.out.println("Please type the exact IP of the server you wish to download from (use query for server IP): ");
				String serverAdressStr = usrInput.next();
				serverAddress =  InetAddress.getByName(serverAdressStr);
				Socket sendSocket = new Socket(serverAddress, sPort);
				DataOutputStream toServer = new DataOutputStream(sendSocket.getOutputStream());
				
				//Asks for and sends file name
				System.out.println("Please type the exact name of the file you wish to download: ");
				String file = usrInput.next();
				toServer.writeBytes(file + '\n');
				
				//creates empty file for reception of JPEG from server
				File serverJPEG = new File("received" + file);
				serverJPEG.createNewFile();
				
				//receive file from server
				BufferedImage image = ImageIO.read(sendSocket.getInputStream());
					if(image != null) {
						//Writes image content to empty file created earlier
						ImageIO.write(image, "jpg", serverJPEG);
					} 
					else {
						//Error if image was not downloaded properly
						System.out.println("failed to download");
					}
				sendSocket.close();
			}
			
			else if (choice.equalsIgnoreCase("E")) { //Initiates exit protocol
				running = false;
				int i = 0;
				while(submissions[i] != null) {
					byte[] requestType = "3".getBytes();
					DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address,  port);
					socket.send(packet);
					
					String responseStr;
					DatagramPacket response = new DatagramPacket(requestType, requestType.length);
					socket.receive(response);
					responseStr = new String(packet.getData(),0, packet.getLength());
						if(responseStr.equals("1")){//verifies server has successfully removed client information 
							byte[] fileName = submissions[i].getBytes();
							DatagramPacket delInfo = new DatagramPacket(fileName, fileName.length, address,  port);
							socket.send(delInfo);
						}
					i++;
				}
				usrInput.close();
			}//End of Exit protocol
			else {
				System.out.println("Unrecognized input. Try Again.");				
			}
			
		}
	
	}

	public static int computeHash(String name) {
		int hash = 0; 
		char[] cArray = name.toCharArray();
		for (int i = 0; i< name.length(); i++) {
			int ascii = (int) cArray[i];
			System.out.println(ascii);
			hash = hash + ascii;
		}
		return hash;
	}
}
