import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

/*
	This class reads the input from the client and passes it
	to the server through the socket. Then it receives the
	servers response and outputs it.
*/

public class Client
{
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	public void NewClient(String arguments) {

		// Attempt to open a socket, as well as input and output
		try{
			socket = new Socket("localhost", 6201);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(arguments + "\n");

		} catch (Exception e){
			e.printStackTrace();
		}

		// Attempt to read response from server
		try {
			String fromServer;
			while((fromServer=in.readLine()) != null) {
				System.out.println(fromServer);
			}
			// Close reader, writer, and socket
			out.close();
			in.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main( String[] args )
	{
		Client c = new Client();
		c.NewClient(Arrays.toString(args));
	}
}