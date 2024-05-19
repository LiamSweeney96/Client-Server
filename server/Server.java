import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
	This class opens a server socket and begins listening on
	a given port. It runs continuously and executes the client
	handler class to deal with the client input.
*/

public class Server
{
	public static void main( String[] args ) throws IOException {

		ServerSocket serverSocket = null;
		ExecutorService executorService;

		// Attempt to create a file to store accepted commands.
		try {
			File log = new File("log.txt");
			if (!log.createNewFile()){
				System.err.println("File creation failed.");
				System.exit(0);
			}
		} catch (IOException e){
			e.printStackTrace();
		}

		// Attempt to open a serverSocket and begin listening on the port.
		try {
			serverSocket = new ServerSocket(6201);
		} catch (Exception e){
			System.err.println("Could not listen on port: 6201.");
			e.printStackTrace();
		}
		executorService = Executors.newFixedThreadPool(30);

		// Run continuously
		while (true){
			try {
				assert serverSocket != null;
				Socket client = serverSocket.accept();
				executorService.execute(new ClientHandler(client));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}