package gamedev.screen;

import gamedev.input.GDInputProcessor;

import com.badlogic.gdx.Screen;

public abstract class GDScreen implements Screen{
	protected GDInputProcessor inputProcessor;

	public GDInputProcessor getInputProcessor() {
		return inputProcessor;
	}
	

}
