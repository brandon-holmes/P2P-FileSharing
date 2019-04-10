import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class directoryServer {

	public static void main(String[] args) {
		Server server1 = new Server(20382,"141.117.117.173",1);
		Server server2 = new Server(20384,"141.117.117.173",2);
		Server server3 = new Server(20386,"141.117.117.173",3);
		Server server4 = new Server(20388,"141.117.117.173",4);
		new Thread(server1).start();
		new Thread(server2).start();
		new Thread(server3).start();
		new Thread(server4).start();
		server2.listenTCP();
		server1.connect(server2.ip, server2.port);
		server3.listenTCP();
		server2.connect(server3.ip, server3.port);
		server4.listenTCP();
		server3.connect(server4.ip, server4.port);
		server1.listenTCP();
		server4.connect(server1.ip, server1.port);
		
	}
	
	public static class Server implements Runnable
	{
		int port;//range is 20380-20389
		String ip;
		int id;//1-4
		int nextPort;
		int nextIp;
		InetAddress inetAddress;
		
		ServerSocket serverSocket;
		Socket clientSocket;
		
		DatagramSocket udpSocket;
		DatagramPacket udpPacket;
		
		InputStream inputToServer;
		OutputStream outputFromServer;
		
		//first int = name of file(hashed), second String = ip file is located at
		Hashtable<Integer, String> keyValues = new Hashtable<Integer, String>();
		
		//constructor
		public Server(int port, String ip, int id)//each server in DHT has unique ip
		{
			this.port = port;
			this.ip = ip;
			this.id = id;
		}
		
		public void connect(String ip, int port)
		{
			nextPort = port;
			try
			{
				inetAddress = InetAddress.getByName(ip);
				clientSocket = new Socket(inetAddress, port);
			}
			catch (IOException e)
			{
				//error if ip unknown
			}
		}
		
		public void listenTCP()
		{
			try 
			{
				clientSocket = serverSocket.accept();//listens and accepts connection with client
				System.out.println("Connected");
			} 
			catch (IOException e) 
			{
				System.out.println("Can't accept connection");
			}
			
			try 
			{
				//input and output i think?
				inputToServer = clientSocket.getInputStream();
				outputFromServer = clientSocket.getOutputStream();
			} 
			catch (IOException e) 
			{
				System.out.println("Read Failed");
			}
			
			while(true)
			{
				try
				{
					//what to do with input and output
					int input = inputToServer.read();
					//String output = outputFromServer.write();//insert method here
				} 
				catch (IOException e) 
				{
					System.out.println("Read Failed");
				}
			}
		}
		
		Runnable tcpRunnable = new Runnable()
		{
			public void run()
			{
				System.out.println("TCP Server starting");
				try 
				{
					serverSocket = new ServerSocket(port);//reserves and creates socket on port #
					System.out.println("Server on port " + port);
				} 
				catch (IOException e) 
				{
					System.out.println("Port not available");
				}
			}
		};
		
		Runnable udpRunnable = new Runnable()
		{
			public void run()
			{
				System.out.println("UDP Server starting");
				try
				{
					DatagramSocket udpSocket = new DatagramSocket(port);
				}
				catch (IOException e) 
				{
					System.out.println("Port not available");
				}
				
				byte[] requestType = "1".getBytes();
				
	            while(true)
	            {
	            	try
	            	{
	            		DatagramPacket recieveQuery = new DatagramPacket(requestType, requestType.length, inetAddress, port);
		            	udpSocket.receive(recieveQuery);
	            	}
	            	catch (IOException e)
	            	{
	            		System.out.println("Failed to receive packet");
	            	}
	            }
			}
		};
		public void addFile(int hash, String ip)
		{
			keyValues.put(hash, ip); 
		}
		
		public String sendInfo(String hash)
		{
			return keyValues.get(hash); 
		}
	}
}
