import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

	ArrayList<Integer> clientsNums = new ArrayList<>();

//	ArrayList<Integer> numClients = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;


	Server(Consumer<Serializable> call){

		callback = call;
		server = new TheServer();
		server.start();
	}


	public class TheServer extends Thread{

		public void run() {
			messageInfo mi = new messageInfo();

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");
				mi.data = "Server is waiting for a client!";
				callback.accept(mi);


				while(true) {
					ClientThread c = new ClientThread(mysocket.accept(), count);
					mi.data = "client has connected to server: " + "client #" + count;
					callback.accept(mi);
					clients.add(c);
					c.start();

					count++;
					mi.numClients.add((count-1));

				}
			}//end of try
			catch(Exception e) {
				mi.data = "Server socket did not launch";
				callback.accept(mi);
			}
		}//end of while
	}


	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
		}

		public void updateClients2(messageInfo mi, ArrayList<Integer> values) {

			for (int i = 0; i < values.size(); i++) {
				for (int j = 0; j < clientsNums.size(); j++) {
					ClientThread t = clients.get(j);
					if(clientsNums.get(j).equals(values.get(i))){
						try {
							t.out.writeObject(mi);
						} catch (Exception e) {}
					}

				}
			}
		}


		public void updateClients(messageInfo mi) {
			for (int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					t.out.writeObject(mi);
				} catch (Exception e) {
				}
			}
		}

		public void getOnlineClients(ClientThread client) throws IOException {
			messageInfo mi3 = new messageInfo();
			for (int i = 0; i < clients.size(); i++) {
				mi3.onlineClients.add(clients.get(i).count);
			}

			// Update activeClients ListView for all connected clients
			for (ClientThread connectedClient : clients) {
				messageInfo mi4 = new messageInfo();
				mi4.onlineClients = mi3.onlineClients;
				connectedClient.out.writeObject(mi4);
			}
		}


		public void run(){

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			messageInfo mi3 = new messageInfo();
			mi3.data = "client #"+count + " has joined the server";
			updateClients(mi3);

			try {
				getOnlineClients(this);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}


			clientsNums.add(count);
			while(true) {
				try {
					mi3 = (messageInfo) in.readObject();
					if (mi3.sendALl) {
						mi3.data = "client #" + count + " sent: " + mi3.data;
						callback.accept(mi3);
						updateClients(mi3);
					} else {
						String myString = mi3.data;
						mi3.data = mi3.data.replaceAll("at\\s+\\d+", "");
						int index = 0;
						ArrayList<Integer> values = new ArrayList<>();

						while (index != -1) {
							index = myString.indexOf("at", index);
							if (index != -1) {
								index += 3;
								int endIndex = myString.indexOf(' ', index);
								if (endIndex == -1) {
									endIndex = myString.length();
								}
								String valueStr = myString.substring(index, endIndex);
								int value = Integer.parseInt(valueStr); // Convert the extracted string to an integer
								values.add(value);
							}
						}
						mi3.data = "client #" + count + " sent: " + mi3.data;
						callback.accept(mi3);
						updateClients2(mi3, values);
					}
				} catch (Exception e) {
					mi3.data = "OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!";
					callback.accept(mi3);
					mi3.data = "Client #" + count + " has left the server!";
					updateClients(mi3);
					clients.remove(this);
					break;
				}
			}
		}//end of run
	}//end of client thread
}