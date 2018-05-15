package atj;

import atj.Message;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoderTxt implements Decoder.Text<Message> {

	@Override
	public void destroy() {

	}

	@Override
	public void init(EndpointConfig arg0) {

	}

	@Override
	public Message decode(String message) throws DecodeException {
		Message mess = new Message();
		mess.setText(message);
		return mess;
	}

	@Override
	public boolean willDecode(String arg0) {
		return true;
	}

}
