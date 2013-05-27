package com.focused.projectf.input;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.focused.projectf.Point;
import com.focused.projectf.graphics.Canvas;
import com.focused.projectf.interfaces.IInputReciever;

public class Input {

	private static Vector<IInputReciever> Recievers;
	public static Hashtable<Integer, KeyEvent> keys;

	private static MouseEvent mEvent;
	private static ButtonState[] mStates;
	private static float[] mDownTimes;

	public static void initialize() {
		try {
			Mouse.create();
			Keyboard.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		Recievers 	= new Vector<IInputReciever>();
		mEvent 		= new MouseEvent();
		keys 		= new Hashtable<Integer, KeyEvent>();
		mStates		= new ButtonState[Mouse.getButtonCount()];
		mDownTimes	= new float[Mouse.getButtonCount()];

		for(int i = 0; i < mStates.length; i++) {
			mStates[i] = ButtonState.Depressed;
			mDownTimes[i] = -1;
		}
	}

	public static void Update(float time) {
		boolean[] events = new boolean[Mouse.getButtonCount()];

		for(int i = 0; i < events.length; i++) {
			events[i] = false;
		}

		while(Mouse.next()) {
			if(Mouse.getEventButton() != MouseEvent.BUTTON_NONE) {
				int b = Mouse.getEventButton();				
				mEvent.Button = b;
				mStates[b] = ButtonState.get(Mouse.getEventButtonState(), mStates[b].Down);
				mEvent.State = mStates[b];
				mEvent.ScrollWheel = Mouse.getEventDWheel();
				mEvent.Position.X = Mouse.getEventX();
				mEvent.Position.Y = Display.getHeight() - Mouse.getEventY();
				events[b] = true;
				passEvent(mEvent);
			}
		} 

		for(int b = 0; b < Mouse.getButtonCount(); b++) {

			if(!events[b]) {
				if (mStates[b] == ButtonState.Released) {
					mStates[b] = ButtonState.Depressed;
					continue;
				}
				if(mStates[b] == ButtonState.Pressed)
					mStates[b] = ButtonState.Held;
				mEvent.Button = b;
				mEvent.State = mStates[b];
				mEvent.ScrollWheel = Mouse.getDWheel();
				mEvent.Position.X = Mouse.getX();
				mEvent.Position.Y = Display.getHeight() - Mouse.getY();
				passEvent(mEvent);	
			}

			if(Mouse.getEventButtonState())
				mDownTimes[b] = (mStates[b].Down) ? mDownTimes[b] + time : 0;

			else if(!Mouse.getEventButtonState() && !mStates[b].Down)
				mDownTimes[b] = -1;
		}



		Vector<Integer> eventsSent = new Vector<Integer>();

		KeyEvent event = null;
		while(Keyboard.next()) {
			int key = Keyboard.getEventKey();
			boolean down = Keyboard.getEventKeyState();
			KeyEvent obj = keys.get(new Integer(key));
			ButtonState newState = ButtonState.Pressed;

			if(obj != null)
				newState = ButtonState.get(down, obj.State.Down);

			event = new KeyEvent(key, Keyboard.getEventCharacter(), newState, Keyboard.getEventNanoseconds());

			if(obj == null)
				keys.put(key, event);

			if(event != null) {
				eventsSent.add(event.KeyId);
				for(IInputReciever r : Recievers) 
					r.onKeyEvent(event);
			}
		}
		Object[] ents = keys.entrySet().toArray();
		for(int i = 0; i < ents.length; i++) {
			@SuppressWarnings("unchecked")
			Entry<Integer, KeyEvent> ent = (Map.Entry<Integer, KeyEvent>)ents[i];
			if(!eventsSent.contains(ent.getKey())) {
				if(Keyboard.isKeyDown(ent.getKey())) {
					event = new KeyEvent(ent.getKey().intValue(), ent.getValue().KeyChar, ButtonState.Held, System.nanoTime());
					keys.put(ent.getKey(), event);
					for(IInputReciever r : Recievers)
						r.onKeyEvent(event);
				}
				else {
					event = new KeyEvent(ent.getKey().intValue(), ent.getValue().KeyChar, ButtonState.Released, System.nanoTime());
					keys.remove(ent.getKey());
					for(IInputReciever r : Recievers)
						r.onKeyEvent(event);
				}
			}	
		}
	}

	protected static boolean passEvent(MouseEvent event) {
		for(int i = 0; i < Recievers.size(); i++)
			if(!Recievers.get(i).onMouseEvent(event))
				return true;

		return false;
	}

	public static boolean getShift() { 
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	public static boolean getCtrl() { 
		return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}
	public static boolean getAlt() { 
		return Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
	}

	public static void registerInputReciever(IInputReciever reciever) {
		Recievers.add(reciever);
	}
	public static void unregisterInputReciever(IInputReciever reciever) {
		Recievers.remove(reciever);
	}

	public static Point getMousePosition() {
		return new Point(Mouse.getX(), Canvas.getHeight() - Mouse.getY());
	}

	public static float getMouseButtonDownTime(int mouseButton) {
		if(mDownTimes[mouseButton] != -1)
			return mDownTimes[mouseButton];

		return 0;
	}

	public static ButtonState getMouseButtonState(int mouseButton) {
		return mStates[mouseButton];
	}
}