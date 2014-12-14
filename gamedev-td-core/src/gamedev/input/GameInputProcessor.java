package gamedev.input;

import gamedev.entity.GameState;
import gamedev.entity.Tower;
import gamedev.entity.TowerFactory;
import gamedev.entity.TowerFactory.TowerType;
import gamedev.entity.tower.ArrowTower;
import gamedev.entity.tower.DirtTower;
import gamedev.screen.GameScreen;
import gamedev.screen.GameUserInterface;
import gamedev.td.Config;
import gamedev.td.GDSprite;
import gamedev.td.TowerDefense;

import java.awt.Point;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

public class GameInputProcessor extends GDInputProcessor {

	private GameScreen gameScreen;
	private Tower towerToBuild, selectedTower;

	private GDSprite selectedSprite;
	private GameUserInterface userInterface;

	Color red = new Color(1, 0, 0, .5f);
	Color white = new Color(1, 1, 1, .5f);

	public GameInputProcessor(TowerDefense towerDefense) {
		super(towerDefense);
		userInterface = new GameUserInterface();
		towerToBuild = null;
		selectedSprite = null;
		selectedTower = null;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {

		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			// Step 1
			selectTowerToBuild(x, y, pointer, button);

			// Step 2
			buildSelectedTower(x, y, pointer, button);

			upgradeTowers(x, y, pointer, button);
		} else if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			resetInteractions();

		}

		return false;
	}

	private void resetInteractions() {

	}

	private void upgradeTowers(int x, int y, int pointer, int button) {
		GameState state = GameState.getInstance();

		List<Tower> deployedTowers = state.getDeployedTowers();

		for (int i = 0; i < deployedTowers.size(); i++) {
			Tower tower = deployedTowers.get(i);
			GDSprite sprite = tower.getSprite();

			if (sprite.contains(x, y)) {
				selectedTower = tower;
				userInterface.setTowerToUpgrade(tower);
				System.out.println("[Input] User selected a tower, " + selectedTower.getTowerName() + " found at " + selectedTower.getPosition());
			}
		}
	}

	private void selectTowerToBuild(int x, int y, int pointer, int button) {
		List<GDSprite> availableTowers = userInterface.getBuildTowerButtons();

		for (int type = 0; type < availableTowers.size(); type++) {
			GDSprite sprite = availableTowers.get(type);
			if (sprite.contains(x, y)) {
				TowerType towerType = TowerFactory.interpretType(type);
				System.out.println("[Input] User is trying to build a " + towerType);
				towerToBuild = TowerFactory.createTower(towerType);
				userInterface.setTowerToBuild(towerToBuild, towerType);
			}
		}
	}

	private void buildSelectedTower(int x, int y, int pointer, int button) {
		if (towerToBuild == null)
			return; // Do nothing if there is no tower to build yet.

		GameState state = GameState.getInstance();
		Point point = getGridCoordinate(x, y);
		
		if (point != null && state.isTowerPlaceable(point)) {
			if (state.canBuyTower(towerToBuild)){
				System.out.println("[Input] User built a " + towerToBuild.getTowerName() + " on " + point);
				state.buildTower(towerToBuild, point);				
			}else{
				System.out.println("[Input] User cannot build a " + towerToBuild.getTowerName() + " because he/she does not have money.");
				towerToBuild = null;
			}
			userInterface.reset();
		}
	}
	
	
	//TODO: rework this method to follow the design
	private void upgradeButtonIntesersect(int screenX, int screenY) {
		if(selectedTower != null) {
	//		TowerInformation towerInfo = gameScreen.getUiInformation();
			if(selectedTower instanceof ArrowTower) {
				// TODO: Check if mouse intersects with upgradeToFireArrow or upgradeToIceArrow sprite in TowerInformation
			}
			else if(selectedTower instanceof DirtTower) {
				
			}
		}
		
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {

		Point point = getGridCoordinate(x, y);
		userInterface.setHighlightedCell(point);
		if (towerToBuild != null){
			userInterface.setGhostTowerToBeBuilt(point);
		}
		

// TODO: Don't know what this code is for
		
//		if (selectedTower == null) {
//			if (state.isTowerPlaceable(point) == false) {
//				gameScreen.getTowerRangeRenderer().setColor(red);
//			} else
//				gameScreen.getTowerRangeRenderer().setColor(white);
//		} else
//			gameScreen.getTowerRangeRenderer().setColor(white);


		List<GDSprite> towerSprites = userInterface.getBuildTowerButtons();
		for (int i = 0; i < towerSprites.size(); i++) {
			GDSprite sprite = towerSprites.get(i);
			if (sprite.contains(x, y)) {
				
				// assumes size of sprites of available towers is equal to the size of the available towers model
				boolean showTooltip = true;
				Point spritePoint = sprite.getPosition();

				TowerType towerType = TowerFactory.interpretType(i);
				towerToBuild = TowerFactory.createTower(towerType);
				
				userInterface.setTowerToBuild(towerToBuild, towerType);
			} else {
				userInterface.reset();
			}
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	private Point getGridCoordinate(int screenX, int screenY) {
		if (screenX < 0 || screenY < 0)
			return null;

		// TODO: Check if screen exceeds map, if so, return null.
		// if (screenX > )
		
		int truncateX = screenX / Config.tileSize;
		int truncateY = screenY / Config.tileSize;
		return new Point(truncateX * Config.tileSize, truncateY * Config.tileSize);
	}

	public GameUserInterface getUserInterface() {
		return userInterface;
	}
}
