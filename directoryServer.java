import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class directoryServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server server1 = new Server(20381,"141.117.117.212",1);
		Server server2 = new Server(20382,"141.117.117.213",2);
		//Server server3 = new Server(20383,null,3);
		//Server server4 = new Server(20384,null,4);
	}
	
	public static class Server
	{
		int port;//range is 20380-20389
		String ip;
		int id;//1-4
		int nextPort;
		int nextIp;
		ServerSocket serverSocket;
		Socket clientSocket;
		InputStream inputToServer;
		OutputStream outputFromServer;
		
		//first String = name of file, second String = ip file is located at
		Hashtable<String, String> keyValues = new Hashtable<String, String>();
		
		
		//constructor
		public Server(int port, String ip, int id)//each server in DHT has unique ip
		{
			port = this.port;
			ip = this.ip;
			id = this.id;
			
		}
		/*
		public void connect(String ip, int port)
		{
			nextPort = port;
			try
			{
				clientSocket = new Socket(ip, port);
			}
			catch (IOException e)
			{
				//error if ip unknown
			}
		}
		*/
		
		public void startServer()
		{
			Thread serverThread1 = new Thread(new Runnable() 
			{

				@Override
				public void run()
				{
					System.out.print("Server starting");
					try 
					{
						serverSocket = new ServerSocket(port);//reserves and creates socket on port #
					} 
					catch (IOException e) 
					{
						e.printStackTrace();//error if cant use specified port
					}
					
					try 
					{
						clientSocket = serverSocket.accept();//listens and accepts connection with client
					} 
					catch (IOException e) 
					{
						e.printStackTrace();//error if cant accept
					}
					
					try 
					{
						//input and output i think?
						inputToServer = clientSocket.getInputStream();
						outputFromServer = clientSocket.getOutputStream();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();//error if read failed
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
							e.printStackTrace();//error if read failed
						}
					}
				}	
			});
		}
		
		public void addFile(String file, String ip)
		{
			keyValues.put(file, ip);
		}
	}
}
