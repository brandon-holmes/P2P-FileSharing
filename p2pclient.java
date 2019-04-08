package P2P;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class p2pclient {
	private static DatagramSocket socket;
	private static InetAddress address;
	private static int port = 20380;
	private byte[] buff;
	
	public static void main(String [] args) {
		//run init, getting ip addresses of DHT servers
		boolean running = true;
		
		
		while(running) {
			System.out.println("input 'S' to store content in DHT server \n"
							+ "input 'Q' to query for content \n"
							+ "input 'D' to download file \n"
							+ "input 'E' to exit \n"
				);
			Scanner usrInput = new Scanner(System.in);
			String choice = usrInput.next();
			if( choice.equalsIgnoreCase("S")) { // Store
				
			}
			else if (choice.equalsIgnoreCase("Q")) { // searches 						
				System.out.println("Please type the exact name of the file you are looking for: ");
				String file = usrInput.next();
				
				try {
					address = InetAddress.getByName("10.10.10.10");
				} catch (UnknownHostException e) {
					System.out.println("Query Failure: Ip incorrect. \n");
					continue;
				}
				
				
				int fileHash = computeHash(file);
				int realPort = port + (fileHash%4);
				byte[] request = file.getBytes();
				byte[] requestType = "1".getBytes();
				DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address, port);
				String responseStr;
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("Query Failure: failed to send packet.\n");
					continue;
				}
				DatagramPacket response = new DatagramPacket(requestType, requestType.length);
				try {
					socket.receive(response);
					responseStr = new String(packet.getData(),0, packet.getLength());
				} catch (IOException e) {
					System.out.println("Query Failure: No response from server ");
					continue;
				}
				if(responseStr.equals("response")){
					DatagramPacket query = new DatagramPacket(request, request.length, address, port);
					try {
						socket.send(query);
					} catch (IOException e) {
						System.out.println("Query Failure: Unable to send query ");
						continue;
					}
					DatagramPacket queryInfo = new DatagramPacket(request, request.length);
					try {
						socket.receive(queryInfo);
					} catch (IOException e) {
						System.out.println("Query Failure: Did not receive query information. ");
						continue;
					}
					String queryInfoStr = new String(queryInfo.getData(),0, queryInfo.getLength());
					System.out.println(queryInfoStr);
				}
				//send 
			}
			else if (choice.equalsIgnoreCase("D")) { // searches 
				
			}
			else if (choice.equalsIgnoreCase("E")) { //Initiates exit protocol
				running = false;
			}
			else {
				System.out.println("Unrecognized input");
				usrInput.close();
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