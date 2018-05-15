package atj;

import java.io.File;

public class Message {

	private String user;
	private String text;
	private File file;
	private Boolean attached;

	

	public Message() {
		setUser(null);
		setText(null);
		addFile(null);
		attached = false;
	}

	public void addFile(File file) {

		if (file != null) {
			this.file = file;
			attached = true;
		} else {

			attached = false;
		}
	}
	

	public void setAttached(Boolean attached) {
		this.attached = attached;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setText(String message) {
		this.text = message;
	}

	public Boolean hasAttachment() {
		return attached;
	}

	public String getText() {
		return text;
	}

	public File getFile() {
		return file;
	}

}
