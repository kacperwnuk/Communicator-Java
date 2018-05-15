package atj;

import atj.Message;
import atj.FileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.websocket.*;
import javax.websocket.Session;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class WebSocketChatStageControler {

	@FXML
	TextField userTextField;
	@FXML
	TextArea chatTextArea;
	@FXML
	TextField messageTextField;
	@FXML
	Button btnSet;
	@FXML
	Button btnSend;
	@FXML
	Button btnAddFile;
	private Message message;
	private WebSocketClient webSocketClient;
	private static final int BUFFER_SIZE = 1024 * 1024; // ilość bajtów
														// przesyłana
														// jednorazowo poprzez
														// serwer

	@FXML
	private void initialize() {
		System.out.println("init");

		message = new Message();
		webSocketClient = new WebSocketClient();
		message.setUser(userTextField.getText());
		btnSend.setDisable(true);
	}

	@FXML
	private void btnSet_Click() {

		if (userTextField.getText().isEmpty()) {
			btnSend.setDisable(true);
			return;
		}
		message.setUser(userTextField.getText());
		btnSend.setDisable(false);

		System.out.println("Ustawiono nick: " + message.getUser());
	}

	@FXML
	private void btnSend_Click() {

		message.setText(messageTextField.getText());
		webSocketClient.sendMessage(message);
	}

	@FXML
	private void btnAddFile_Click() {

		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Wybierz plik do wysłania");

		message.addFile(fileChooser.showOpenDialog(stage));
	}

	public void closeSession(CloseReason closeReason) {

		try {
			webSocketClient.session.close(closeReason);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ClientEndpoint
	public class WebSocketClient {

		private Session session;

		public WebSocketClient() {
			connectToWebSocket();
		}

		@OnOpen
		public void onOpen(Session session) {
			System.out.println("Connection opened");
			this.session = session;

		}

		@OnClose
		public void onClose(CloseReason closeReason) {
			System.out.println("Connection closed: " + closeReason
					.getReasonPhrase());

		}

		@OnError
		public void onError(Throwable throwable) {
			System.out.println("Error occured" + throwable.getMessage());
		}

		@OnMessage
		public void onMessage(String message, Session session) {
			System.out.println("Message received.");
			chatTextArea.setText(chatTextArea.getText() + message + "\n");
		}

		/**
		 * Tworzę plik tmp w którym zbieram dane. Jeśli użytkownik wyrazi chęć
		 * zapisania pliku to zostanie on skopiowany w docelowe miejsce. W
		 * przeciwnym razie zostaje usunięty.
		 *
		 * Każdy ByteBuffer ma na końcu jeden bajt mówiący czy jest to ostatni
		 * fragment pliku czy też nie.
		 */

		@OnMessage
		public void onMessage(ByteBuffer stream, Session session) {

			byte data = stream.get();
			System.out.println(data);
			String fName = session.getId() + "/tmp";
			File file = new File(fName);
			file.getParentFile().mkdirs();

			FileOutputStream str;
			FileChannel channel;
			try {
				str = new FileOutputStream(file, true);
				channel = str.getChannel();
				channel.write(stream);
				channel.close();
				str.close();
			} catch (IOException i) {
				i.printStackTrace();
			}

			if (data == 1) {
				System.out.println("Przyszla czesc pliku");
			} else {
				try {
					System.out.println("Ostatni fragment pliku");
					FileHandler fileHandler = new FileHandler();
					Platform.runLater(() -> fileHandler.update(file));

				} catch (Throwable ex) {
					System.out.println("FileHandler error");
					ex.printStackTrace();
				}

				System.out.println("File received.");
			}

		}

		private void connectToWebSocket() {
			WebSocketContainer webSocketContainer = ContainerProvider
					.getWebSocketContainer();
			try {
				URI uri = URI.create(
						"ws://localhost:8080/WebSocket/websocketendpoint");
				webSocketContainer.connectToServer(this, uri);

			} catch (DeploymentException | IOException e) {
				e.printStackTrace();
			}

		}



		public void sendMessage(Message message) {

			 Task<Void> task = new Task<Void>() {
		         @Override protected Void call() throws Exception {


		 				try {
		 					long fileSize = message.getFile().length();

		 					if (fileSize > BUFFER_SIZE) {

		 						InputStream is = new FileInputStream(message.getFile());
		 						ByteBuffer buf = ByteBuffer.allocateDirect(BUFFER_SIZE
		 								+ 1);
		 						byte[] bufor = new byte[BUFFER_SIZE];

		 						while (fileSize > 0) {

		 							if (fileSize > BUFFER_SIZE) {
		 								buf.put((byte) 1); // part

		 								while (is.read(bufor) != -1) {
		 									buf.put(bufor);
		 									if (buf.position() == BUFFER_SIZE + 1) {
		 										break;
		 									}
		 								}

		 								fileSize -= BUFFER_SIZE;
		 							} else {
		 								buf = ByteBuffer.allocateDirect((int) fileSize
		 										+ 1);
		 								buf.put((byte) 2); // end

		 								bufor = new byte[(int) fileSize];
		 								while (is.read(bufor) != -1) {
		 									buf.put(bufor);
		 									fileSize = 0;
		 								}

		 							}

		 							buf.flip();
		 							session.getBasicRemote().sendBinary(buf);
		 							buf.clear();

		 						}

		 						is.close();
		 						session.getBasicRemote().sendText(message.getUser()
		 								+ " is sending a file: " + message.getFile()
		 										.getName());

		 					} else {
		 						System.out.println("Maly plik");
		 						ByteBuffer buf = ByteBuffer.allocateDirect((int) message
		 								.getFile().length() + 1);
		 						InputStream is = new FileInputStream(message.getFile());
		 						int b;
		 						buf.put((byte) 2);
		 						while ((b = is.read()) != -1) {
		 							buf.put((byte) b);
		 						}

		 						is.close();
		 						buf.flip();

		 						session.getBasicRemote().sendBinary(buf);
		 						session.getBasicRemote().sendText(message.getUser()
		 								+ " is sending a file: " + message.getFile()
		 										.getName());
		 					}

		 				} catch (IOException ex) {
		 					System.out.println("Stream error");
		 				}

		 				File temp = null;
		 				message.addFile(temp);
		 				System.out.println("Plik bin wyslany: ");


		             return null;
		         }
		     };

		     if(message.hasAttachment()) {
		    	 Thread th = new Thread(task);
		         th.setDaemon(true);
		         message.setAttached(false);
		         th.start();
		     }
		     else if (!message.getText().equals("")){
		    	 try {
	 					System.out.println("Message sent: " + message.getText());
	 					session.getBasicRemote().sendText(message.getUser() + ": "
	 							+ message.getText());
	 				} catch (IOException e) {
	 					e.printStackTrace();
	 				}

		     }


		}

	}

}
