package com.focused.projectf.global;

import java.io.Serializable;
import java.util.Vector;

import org.lwjgl.input.Keyboard;

import com.focused.projectf.Point;
import com.focused.projectf.Rect;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.graphics.Color;
import com.focused.projectf.gui.TextBox;
import com.focused.projectf.gui.TextBox.KeyPressListener;
import com.focused.projectf.input.ButtonState;
import com.focused.projectf.input.KeyEvent;
import com.focused.projectf.players.Player;
import com.focused.projectf.resources.Content;
import com.focused.projectf.resources.TTFont;

public class ChatLog {

	public static Vector<ChatMessage> Messages;
	public static Vector<Player> talkingTo;

	private static TextBox input;	
	public static TTFont Font;
	
	private final static KeyPressListener listener = new KeyPressListener() {
		@Override
		public boolean onKeyPressed(KeyEvent event) {
			if(event.KeyId == Keyboard.KEY_RETURN && event.State == ButtonState.Pressed) {
				sendMessage(input.getText());
				input.setText("");
				return false;
			}
			return true;
		}
	};
	
	static {
		Messages = new Vector<ChatMessage>();
		talkingTo = new Vector<Player>();
		Font = Content.getFont("Arial", 14, false, false);
	}

	public static void setTalkingTo(Player... recievers) {
		talkingTo.clear();
		for(Player p : recievers)
			talkingTo.add(p);
	}

	public static void setTextBox(TextBox box) {
		if(input != null)
			input.setKeypressListener(null);
		input = box;
		input.setKeypressListener(listener);
	}
	
	public static void sendMessage(String message) {
		if(message == null)
			return;
		if(message.length() == 0)
			return;
		ChatMessage msg = new ChatMessage();
		msg.Message = message;
		msg.Player = Player.getThisPlayer();
		msg.ICanSeeIt = true;
		Messages.add(msg);
		//TODO Make it so that it sends out to everyone
		//Assigned to Austin Bolstridge
	}
	
	public static void render(float elapsed, Rect region, Color bgColor) {
		if(Font == null)
			Font = Content.getFont("Arial", 14, false, false);
		
		Canvas.pushClip(region);
		Canvas.fillRectangle(region, Color.HALF_BLACK);
		
		int yShift = 0, message = Messages.size() - 1;
		while(yShift <= region.getHeight() && message >= 0) {
			ChatMessage msg = Messages.get(message); 
			msg.timeAlive  += elapsed;
			Rect rect = Font.drawMultiLineText(msg.Message, new Point(region.getX(), region.getY() + yShift), region.getWidth(), msg.getColor());
			yShift += rect.getHeight();
			message--;
		}
		
		Canvas.popClip();
	}
	
	public static class ChatMessage implements Serializable {
		
		private static final long serialVersionUID = 8345370377461751535L;
		
		public float timeAlive = 0;
		public String Message;
		public Player Player;
		public boolean ICanSeeIt;

		public Color getColor() {
			return Player.MyTeam.MainColor;
		}
	}
}