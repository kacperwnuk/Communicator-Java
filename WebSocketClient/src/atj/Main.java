package atj;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
					"ChatWindow.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 550, 500);

			primaryStage.setScene(scene);
			primaryStage.setTitle("JavaFX WebSocketClient");
			primaryStage.setOnHiding(e -> primaryStage_Hiding(e, fxmlLoader));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void primaryStage_Hiding(WindowEvent e, FXMLLoader fxmlLoader) {
		((WebSocketChatStageControler) fxmlLoader.getController()).closeSession(
				new CloseReason(CloseCodes.NORMAL_CLOSURE, "Stage hiding"));
	}

}
