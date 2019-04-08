package P2P;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class p2pclient {
	private static DatagramSocket socket;
	private static InetAddress address;
	private static int port = 2000;
	private byte[] buff;
	
	
	public static void main(String [] args) {
		//run init, getting ip addresses of DHT servers
		boolean running = true;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e2) {
			System.out.println("socket Failure");
		}
		try {
			address = InetAddress.getByName("192.168.2.18");
		} catch (UnknownHostException e1) {
		}
		
		while(running) {
			System.out.println("input 'S' to store content in DHT server \n"
							+ "input 'Q' to query for content \n"
							+ "input 'D' to download file \n"
							+ "input 'E' to exit \n"
				);
			System.out.println("round start");
			Scanner usrInput = new Scanner(System.in);
			String choice = usrInput.next();
			if( choice.equalsIgnoreCase("S")) { // Sends request for server to store data of IP and files
				
				System.out.println("Please type the exact name of the file you wish to upload: ");
				String file = usrInput.next();
				
				int fileHash = computeHash(file);
				int realPort = port + ((fileHash%4)*2-2);
				byte[] request = file.getBytes();
				byte[] requestType = "1".getBytes();
				DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address, realPort);
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
					System.out.println("Query Failure: No response from server \n");
					continue;
				}
				if(responseStr.equals("response")){//verifies server is ready for request
					DatagramPacket query = new DatagramPacket(request, request.length, address, realPort);
					try {
						socket.send(query);
					} catch (IOException e) {
						System.out.println("Query Failure: Unable to send query \n");
						continue;
					}
					DatagramPacket queryInfo = new DatagramPacket(request, request.length);
					try {
						socket.receive(queryInfo);
					} catch (IOException e) {
						System.out.println("Query Failure: Did not receive query information. \n");
						continue;
					}
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
					System.out.println("Query Failure: No response from server \n");
					continue;
				}
				if(responseStr.equals("response")){//verifies server is ready for request
					DatagramPacket query = new DatagramPacket(request, request.length, address,  realPort);
					try {
						socket.send(query);
					} catch (IOException e) {
						System.out.println("Query Failure: Unable to send query \n");
						continue;
					}
					DatagramPacket queryInfo = new DatagramPacket(request, request.length);
					try {
						socket.receive(queryInfo);
					} catch (IOException e) {
						System.out.println("Query Failure: Did not receive query information. \n");
						continue;
					}
					String queryInfoStr = new String(queryInfo.getData(),0, queryInfo.getLength());
					System.out.println(queryInfoStr);
				}
				//send 
			}//End of Query protocol
			else if (choice.equalsIgnoreCase("D")) { // searches 
				
			}
			else if (choice.equalsIgnoreCase("E")) { //Initiates exit protocol
				running = false;			
				byte[] requestType = "3".getBytes();
				DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address,  port);
				String responseStr;
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("Exit Failure: failed to send packet, information not removed from DHT server.\n");
					continue;
				}
				DatagramPacket response = new DatagramPacket(requestType, requestType.length);
				try {
					socket.receive(response);
					responseStr = new String(packet.getData(),0, packet.getLength());
				} catch (IOException e) {
					System.out.println("Query Failure: No response from server \n");
					continue;
				}
				if(responseStr.equals("response")){//verifies server has successfully removed client information 
					System.out.println("Succesfully removed information from server, shutting down");
				}
				usrInput.close();
			}//End of Exit protocol
			else {
				byte[] space = new byte[1024];
				System.out.println("Unrecognized input");
				byte[] requestType = "second try yea haw".getBytes();
				DatagramPacket packet = new DatagramPacket(requestType, requestType.length, address,  port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("Exit Failure: failed to send packet, information not removed from DHT server.\n");
					continue;
				}
				DatagramPacket response = new DatagramPacket(space, space.length);
				try {
					socket.receive(response);
					String responseStr = new String(response.getData(),0, response.getLength());
					System.out.println(responseStr +"response");
				} catch (IOException e) {
					System.out.println("Query Failure: No response from server \n");
					continue;
				}
				
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