package atj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

import javafx.stage.Stage;

/**
 * Klasa FileHandler zajmuje się obsługą pliku który po odebraniu ma zostać
 * zapiasany.
 */

public class FileHandler {

	private File stream;	//file got from message
	private UserInputHandler userInputHandler;

	public FileHandler() {
		setStream(null);
		userInputHandler = new UserInputHandler();
	}

	public File getStream() {
		return stream;
	}

	public void setStream(File buffer) {
		this.stream = buffer;
	}

	public void update(File file) {

		setStream(file);

		Stage stage = new Stage();
		try {
			userInputHandler.start(stage);
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (userInputHandler.getPath() != null) {
			createFile();
		}
		else {
			try {
				Files.delete(stream.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void createFile() {

		FileChannel src;
		FileChannel dest;
		File file = new File(userInputHandler.getPath());
		try {

			src = new FileInputStream(stream).getChannel();
			dest = new FileOutputStream(file).getChannel();
			dest.transferFrom(src, 0, src.size());

			src.close();
			dest.close();
			Files.delete(stream.toPath());
			setStream(null);

		} catch (IOException e) {
			System.out.println("I/O exception");
			e.printStackTrace();
		}

		System.out.println("Zapisano pod ścieżką: " + userInputHandler
				.getPath());

	}

}
