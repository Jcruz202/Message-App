import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;



public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	Server server;

	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call){

		callback = call;
	}

	public void run() {

		try {
			socketClient= new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}

		while(true) {

			try {
				messageInfo mi = (messageInfo) in.readObject();
				callback.accept(mi);
			}
			catch(Exception e) {}
		}

	}

	public void send(messageInfo mi) {

		try {
			out.writeObject(mi);
//			out.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}