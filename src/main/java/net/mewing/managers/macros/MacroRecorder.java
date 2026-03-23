/*
 * Mewing Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.mewing.managers.macros;

import java.util.HashSet;
import java.util.LinkedList;
import org.lwjgl.glfw.GLFW;
import net.mewing.Mewing;
import net.mewing.event.events.KeyDownEvent;
import net.mewing.event.events.KeyUpEvent;
import net.mewing.event.events.MouseClickEvent;
import net.mewing.event.events.MouseMoveEvent;
import net.mewing.event.events.MouseScrollEvent;
import net.mewing.event.listeners.KeyDownListener;
import net.mewing.event.listeners.KeyUpListener;
import net.mewing.event.listeners.MouseClickListener;
import net.mewing.event.listeners.MouseMoveListener;
import net.mewing.event.listeners.MouseScrollListener;
import net.mewing.managers.macros.actions.KeyClickMacroEvent;
import net.mewing.managers.macros.actions.MacroEvent;
import net.mewing.managers.macros.actions.MouseClickMacroEvent;
import net.mewing.managers.macros.actions.MouseMoveMacroEvent;
import net.mewing.managers.macros.actions.MouseScrollMacroEvent;

/**
 * Class responsible for recording Macros
 */
public class MacroRecorder
		implements MouseClickListener, MouseMoveListener, MouseScrollListener, KeyDownListener, KeyUpListener {

	private static final long SAMPLE_INTERVAL_NS = 4_000_000; // 1ms = 1000Hz, default 4000hz

	private LinkedList<MacroEvent> currentMacro = new LinkedList<MacroEvent>();
	private long startTime = 0;
	private long lastMoveTime = 0;
	private boolean recording = false;

	private final HashSet<Integer> heldKeys = new HashSet<>();
	private final HashSet<Integer> heldMouseButtons = new HashSet<>();

	/**
	 * Begins recording a Macro
	 */
	public void startRecording() {
		if (!recording) {
			currentMacro = new LinkedList<MacroEvent>();
			recording = true;
			startTime = System.nanoTime();

			Mewing.getInstance().eventManager.AddListener(MouseClickListener.class, this);
			Mewing.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
			Mewing.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
			Mewing.getInstance().eventManager.AddListener(KeyDownListener.class, this);
			Mewing.getInstance().eventManager.AddListener(KeyUpListener.class, this);
		}
	}

	/**
	 * Stops recording a Macro
	 */
	public void stopRecording() {
		if (recording) {
			long timeStamp = System.nanoTime() - startTime;

			// Set the macro to release all keys currently pressed at the end.
			for (int key : heldKeys)
				currentMacro.add(new KeyClickMacroEvent(timeStamp, key, 0, GLFW.GLFW_RELEASE, 0));
			for (int button : heldMouseButtons)
				currentMacro.add(new MouseClickMacroEvent(timeStamp, button, GLFW.GLFW_RELEASE, 0));
			heldKeys.clear();
			heldMouseButtons.clear();

			recording = false;
			startTime = 0;

			Mewing.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
			Mewing.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
			Mewing.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
			Mewing.getInstance().eventManager.RemoveListener(KeyDownListener.class, this);
			Mewing.getInstance().eventManager.RemoveListener(KeyUpListener.class, this);
		}
	}

	/**
	 * Returns the recorder to its default state.
	 */
	public void reset() {
		heldKeys.clear();
		heldMouseButtons.clear();
		recording = false;
		startTime = 0;
		currentMacro.clear();
		currentMacro = null;
	}
	
	/**
	 * Constructs and returns a macro built from this recorder.
	 */
	public Macro constructMacro() {
		if (!recording && currentMacro != null) {
			Macro macro = new Macro(currentMacro);
			return macro;
		}
		return null;
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (event.GetKey() != Mewing.getInstance().guiManager.clickGuiButton.getValue().getValue()
				&& event.GetKey() != 256
				&& !Mewing.getInstance().guiManager.isClickGuiOpen()) {
			heldKeys.remove(Integer.valueOf(event.GetKey()));
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new KeyClickMacroEvent(timeStamp, event.GetKey(), event.GetScanCode(), event.GetAction(),
					event.GetModifiers()));
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.GetKey() != Mewing.getInstance().guiManager.clickGuiButton.getValue().getValue()
				&& event.GetKey() != 256
				&& !Mewing.getInstance().guiManager.isClickGuiOpen()) {
			heldKeys.add(event.GetKey());
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new KeyClickMacroEvent(timeStamp, event.GetKey(), event.GetScanCode(), event.GetAction(),
					event.GetModifiers()));
		}
	}


	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (!Mewing.getInstance().guiManager.isClickGuiOpen()) {
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseScrollMacroEvent(timeStamp, event.GetHorizontal(), event.GetVertical()));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (!Mewing.getInstance().guiManager.isClickGuiOpen()) {
			if (mouseMoveEvent.getX() == 0 && mouseMoveEvent.getY() == 0)
				return;

			long now = System.nanoTime();
			if (now - lastMoveTime < SAMPLE_INTERVAL_NS)
				return;

			lastMoveTime = now;
			long timeStamp = now - startTime;
			currentMacro.add(new MouseMoveMacroEvent(timeStamp, mouseMoveEvent.getX(), mouseMoveEvent.getY()));
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent mouseClickEvent) {
		if (!Mewing.getInstance().guiManager.isClickGuiOpen()) {
			if (mouseClickEvent.action == GLFW.GLFW_PRESS)
				heldMouseButtons.add(mouseClickEvent.button);
			else if (mouseClickEvent.action == GLFW.GLFW_RELEASE)
				heldMouseButtons.remove(Integer.valueOf(mouseClickEvent.button));
			long timeStamp = System.nanoTime() - startTime;
			currentMacro.add(new MouseClickMacroEvent(timeStamp, mouseClickEvent.button, mouseClickEvent.action,
					mouseClickEvent.mods));
		}
	}
}
