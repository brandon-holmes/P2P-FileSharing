package P2P;

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
		
		try
		{
			Server server1 = new Server(20380,InetAddress.getByName("localhost").toString(),1);
			Server server2 = new Server(20382,InetAddress.getByName("localhost").toString(),2);
			Server server3 = new Server(20384,InetAddress.getByName("localhost").toString(),3);
			Server server4 = new Server(20386,InetAddress.getByName("localhost").toString(),4);
			
			server1.listenUDP();
			server2.listenUDP();
			server3.listenUDP();
			server4.listenUDP();
		}
		catch (IOException e)
		{
			System.out.println("Can't create server");
		}
		
		
		
		
		
		
		//these are supposed to connect each server in a circle with tcp connections
		//idk if these actually work
		/*
		server2.listenTCP();
		server1.connect(server2.ip, server2.port);
		server3.listenTCP();
		server2.connect(server3.ip, server3.port);
		server4.listenTCP();
		server3.connect(server4.ip, server4.port);
		server1.listenTCP();
		server4.connect(server1.ip, server1.port);
		*/
		
	}
	
	public static class Server
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
		Hashtable<String, String> keyValues = new Hashtable<String, String>();
		
		//constructor
		public Server(int port, String ip, int id)//each server in DHT has unique ip
		{
			this.port = port;
			this.ip = ip;
			this.id = id;
			
			try
			{
				udpSocket = new DatagramSocket(this.port);
			}
			catch (IOException e) 
			{
				System.out.println("Port not available");
			}
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
				System.out.println("Can't connect to ip");
			}
		}
		
		public void listenTCP()
		{
			Thread tcp = new Thread(tcpRunnable);
			tcp.start();
		}
		
		public void listenUDP()
		{
			Thread udp = new Thread(udpRunnable);
			udp.start();
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
		};
		
		Runnable udpRunnable = new Runnable()
		{
			public void run()
			{
				System.out.println("UDP Server starting");
				
				
				byte[] requestType = "1".getBytes();
				String request;
				byte[] file = new byte[1024];
				String filenameHash;
				
	            while(true)
	            {
	            	try
	            	{
	            		DatagramPacket recieveQuery = new DatagramPacket(requestType, requestType.length);
	            		udpSocket.receive(recieveQuery);		
		            	request = new String(recieveQuery.getData());
		            	InetAddress ipAddress = recieveQuery.getAddress();
		            	port = recieveQuery.getPort();
		            	if (request.equals("1"))//Store image hash
		            	{
		            		//confirm connection
		            		byte[] confirm = "1".getBytes();
		            		DatagramPacket confirmation = new DatagramPacket(confirm, confirm.length, ipAddress, port);
		    				udpSocket.send(confirmation);
		    				
		    				//receive hash
		    				DatagramPacket storeInfo = new DatagramPacket(file, request.length());
		    				udpSocket.receive(storeInfo);
		    				filenameHash = new String(storeInfo.getData());
		    				//store hash
		    				keyValues.put(filenameHash,ip);//ip should be ip of p2p server
		    				
		            	}
		            	
		            	if (request.equals("2"))//Query Hash
		            	{
		            		//confirm connection
		            		byte[] confirm = "1".getBytes();
		            		DatagramPacket confirmation = new DatagramPacket(confirm, confirm.length, ipAddress, port);
		    				udpSocket.send(confirmation);
		    				
		    				//receive hash
		    				DatagramPacket queryInfo = new DatagramPacket(file, request.length());
		    				udpSocket.receive(queryInfo);
		    				filenameHash = new String(queryInfo.getData());
		    				
		    				//find and return ip
		    				String ipLocationStr = keyValues.get(filenameHash);
		    				byte[] ipLocation = ipLocationStr.getBytes();
		    				DatagramPacket location = new DatagramPacket(ipLocation, ipLocation.length, ipAddress, port);
		            	}
		            	
		            	if (request.equals("3"))//Delete 
		            	{
		            		//confirm connection
		            		byte[] confirm = "1".getBytes();
		            		DatagramPacket confirmation = new DatagramPacket(confirm, confirm.length, ipAddress, port);
		    				udpSocket.send(confirmation);
		    				
		    				//receive hash
		    				DatagramPacket deleteInfo = new DatagramPacket(file, request.length());
		    				udpSocket.receive(deleteInfo);
		    				filenameHash = new String(deleteInfo.getData());
		    				
		    				//remove hash and pair
		    				keyValues.remove(filenameHash);
		            	}
		            	
	            	}
	            	catch (IOException e)
	            	{
	            		System.out.println("Failed to receive/send packet");
	            	}
	            }
			}
		};
		
		//None of these are really used
		public void addFile(String hash, String ip)
		{
			keyValues.put(hash, ip); 
		}
		
		public void removeFile(String hash)
		{
			keyValues.remove(hash);
		}
		
		public String sendInfo(int hash)
		{
			return keyValues.get(hash); 
		}
	}
}