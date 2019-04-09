import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import javax.imageio.ImageIO;


public class DummyP2PClient {
	
	public static void main(String[] args) throws Exception {
		//Socket and Data Stream for sending file name to server
		//You'll have to modify the "localhost" with the IP address you get from the dht server
		Socket clientSocket = new Socket("localhost", 20381);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		//Scanner to read from user
		Scanner scanner = new Scanner(System.in);
		//Save and send file name to server
		String fileName = scanner.nextLine();
		outToServer.writeBytes(fileName + '\n');
		//Creates empty file for image to be stored in
		File imageFile = new File("." + File.separator + "Recieved" + File.separator + fileName);
		imageFile.createNewFile();
		//Reads image from server output stream
        BufferedImage image = ImageIO.read(clientSocket.getInputStream());
        if(image != null) {
        	//Writes image content to empty file created earlier
            ImageIO.write(image, "jpg", imageFile);
        } else {
        	//Error if image was not downloaded properly
            System.out.println("failed to download");
        }
	}
}
