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
		Server server2 = new Server(20382,null,1);
		Server server3 = new Server(20383,null,1);
		Server server4 = new Server(20384,null,1);
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
		
		//constructor
		public Server(int port, String ip, int id)//each server in DHT has unique ip
		{
			port = this.port;
			ip = this.ip;
			id = this.id;
			
			System.out.print("Server starting");
			try 
			{
				//create/accept socket and listen
				serverSocket = new ServerSocket(port);//reserves and creates socket on port #
				clientSocket = serverSocket.accept();//accepts connection with client
				
				//input and output i think?
				inputToServer = clientSocket.getInputStream();
				outputFromServer = clientSocket.getOutputStream();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();//error if cant use specified port
			}
			
		}
	}
}
