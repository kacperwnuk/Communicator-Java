package atj;

import java.io.File;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Klasa UserInputHandler ma za zadanie zbierać od użytkownika informacje
 * odnośnie zapisywanych plików takie jak, wybór ścieżki czy chęć zapisania
 * pliku.
 * 
 *
 */

public class UserInputHandler extends Application {

	private String path;

	public UserInputHandler() {
		setPath(null);
	}

	private void chooseFileAndDirectory() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(
				"Wybierz lokalizację i nazwę pliku lub wybierz plik do zapisu");
		Stage tmpStage = new Stage();

		File directory = fileChooser.showSaveDialog(tmpStage);

		if (directory != null) {
			setPath(directory.toString());
			System.out.println("Zapisywanie pod ścieżką: " + directory);
		} else {
			System.out.println("Zrezygnowano z pobierania");
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Otrzymales nowy plik");
		alert.setContentText("Czy chcesz go pobrać?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			chooseFileAndDirectory();
		}

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
