package de.uxnr.tsoexpert.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import de.uxnr.tsoexpert.game.communication.vo.BackgroundTileVO;
import de.uxnr.tsoexpert.game.communication.vo.BuildingVO;
import de.uxnr.tsoexpert.game.communication.vo.FreeLandscapeVO;
import de.uxnr.tsoexpert.game.communication.vo.LandscapeVO;
import de.uxnr.tsoexpert.game.communication.vo.MapValueItemVO;
import de.uxnr.tsoexpert.game.communication.vo.PathVO;
import de.uxnr.tsoexpert.game.communication.vo.ResourceCreationVO;
import de.uxnr.tsoexpert.game.communication.vo.ZoneVO;
import de.uxnr.tsoexpert.resource.GameSetting;
import de.uxnr.tsoexpert.resource.XMLHandler;

public class ZoneMap {
	private static final Map<String, Sprite> backgrounds = new HashMap<String, Sprite>();
	private static final Map<String, Sprite> landscapes = new HashMap<String, Sprite>();
	private static final Map<String, Sprite> buildings = new HashMap<String, Sprite>();

	private static final Map<String, Double> nofUpgrades = new HashMap<String, Double>();

	private final int backgroundGridWidth;
	private final int backgroundGridHeight;

	private final int isoGridWidth;
	private final int isoGridHeight;

	private final Document gfxDoc;
	private final ZoneVO zoneVO;

	private BufferedImage doubleBuffer = null;
	private double zoomFactor = 1;
	private int offsetX = 0;
	private int offsetY = 0;
	private int minOffsetX = 0;
	private int minOffsetY = 0;
	private int maxOffsetX = 0;
	private int maxOffsetY = 0;

	private boolean showBackground = true;
	private boolean showFreeLandscape = false;
	private boolean showLandscape = false;
	private boolean showBuilding = false;
	private boolean debugBackground = false;
	private boolean debugFreeLandscape = true;
	private boolean debugLandscape = true;
	private boolean debugBuilding = true;
	private boolean debugResourceCreations = false;
	private boolean debugMapValues = false;

	public ZoneMap(ZoneVO zoneVO) {
		this.zoneVO = zoneVO;
		this.gfxDoc = XMLHandler.getDocument(GameSetting.gfx_settings);
		this.backgroundGridWidth = GameSetting.getNumber(this.gfxDoc, "//Globals/BackgroundGrid/@w").intValue();
		this.backgroundGridHeight = GameSetting.getNumber(this.gfxDoc, "//Globals/BackgroundGrid/@h").intValue();
		this.isoGridWidth = GameSetting.getNumber(this.gfxDoc, "//Globals/IsoGrid/@w").intValue();
		this.isoGridHeight = GameSetting.getNumber(this.gfxDoc, "//Globals/IsoGrid/@h").intValue();
	}

	public void updateZoomFactor(int count) {
		if (count > 0 && this.zoomFactor < 1.5) {
			this.zoomFactor *= 1.1;
			this.offsetX *= 1.1;
			this.offsetY *= 1.1;
		} else if (count < 0 && this.zoomFactor > 0.5) {
			this.zoomFactor *= 0.9;
			this.offsetX *= 0.9;
			this.offsetY *= 0.9;
		}
	}

	public void updateOffsetX(int offsetX) {
		this.offsetX += offsetX;
		this.offsetX = Math.max(this.offsetX, this.minOffsetX);
		this.offsetX = Math.min(this.offsetX, this.maxOffsetX);
	}

	public void updateOffsetY(int offsetY) {
		this.offsetY += offsetY;
		this.offsetY = Math.max(this.offsetY, this.minOffsetY);
		this.offsetY = Math.min(this.offsetY, this.maxOffsetY);
	}

	public void setShowBackground(boolean showBackground) {
		this.showBackground = showBackground;
	}

	public void setShowFreeLandscape(boolean showFreeLandscape) {
		this.showFreeLandscape = showFreeLandscape;
	}

	public void setShowLandscape(boolean showLandscape) {
		this.showLandscape = showLandscape;
	}

	public void setShowBuilding(boolean showBuilding) {
		this.showBuilding = showBuilding;
	}

	public void setDebugBackground(boolean debugBackground) {
		this.debugBackground = debugBackground;
	}

	public void setDebugFreeLandscape(boolean debugFreeLandscape) {
		this.debugFreeLandscape = debugFreeLandscape;
	}

	public void setDebugLandscape(boolean debugLandscape) {
		this.debugLandscape = debugLandscape;
	}

	public void setDebugBuilding(boolean debugBuilding) {
		this.debugBuilding = debugBuilding;
	}

	public void setDebugResourceCreations(boolean debugResourceCreations) {
		this.debugResourceCreations = debugResourceCreations;
	}

	public void setDebugMapValues(boolean debugMapValues) {
		this.debugMapValues = debugMapValues;
	}

	public void draw(Dimension size, Graphics2D graphics) {
		int width = 0;
		int height = 0;

		if (this.doubleBuffer != null) {
			width = this.doubleBuffer.getWidth(null);
			height = this.doubleBuffer.getHeight(null);
		}
		if (this.doubleBuffer == null || size.width != width || size.height != height) {
			this.doubleBuffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		}

		Graphics2D g = this.doubleBuffer.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setBackground(graphics.getBackground());
		g.setColor(graphics.getColor());
		g.setFont(graphics.getFont());
		g.fillRect(0, 0, size.width, size.height);

		Rectangle clip = new Rectangle(size);

		this.minOffsetX = 0;
		this.minOffsetY = 0;
		this.maxOffsetX = 0;
		this.maxOffsetY = 0;

		if (this.showBackground || this.debugBackground) {
			this.drawBackground(g, clip);
		}
		if (this.showFreeLandscape || this.debugFreeLandscape) {
			this.drawFreeLandscape(g, clip);
		}
		if (this.showLandscape || this.debugLandscape) {
			this.drawLandscape(g, clip);
		}
		if (this.showBuilding || this.debugBuilding) {
			this.drawBuildings(g, clip);
		}
		if (this.debugResourceCreations) {
			this.drawResourceCreations(g, clip);
		}
		if (this.debugMapValues) {
			this.drawMapValues(g, clip);
		}

		this.offsetX = Math.max(this.offsetX, this.minOffsetX);
		this.offsetY = Math.max(this.offsetY, this.minOffsetY);
		this.offsetX = Math.min(this.offsetX, this.maxOffsetX);
		this.offsetY = Math.min(this.offsetY, this.maxOffsetY);

		graphics.drawImage(this.doubleBuffer, 0, 0, null);

		g.dispose();
	}

	private void drawBackground(Graphics2D g, Rectangle clip) {
		int width = this.backgroundGridWidth;
		int height = this.backgroundGridHeight;
		int length = 34;

		int index = 0;
		for (BackgroundTileVO backgroundTile : this.zoneVO.getBackgroundTiles()) {
			String name = backgroundTile.getName_string();
			Sprite sprite = this.getBackground(name);

			if (sprite != null) {
				Image image = sprite.getImage();
				Rectangle src = sprite.getBounds();
				Rectangle dst = sprite.getBounds();

				dst.x = (int) ((((index % length) * width) + sprite.getOffsetX()) * this.zoomFactor);
				dst.y = (int) (((Math.floor(index / length) * height) + sprite.getOffsetY()) * this.zoomFactor);
				dst.width = (int) (src.width * this.zoomFactor);
				dst.height = (int) (src.height * this.zoomFactor);

				this.minOffsetX = Math.min(this.minOffsetX, dst.x);
				this.minOffsetY = Math.min(this.minOffsetY, dst.y);
				this.maxOffsetX = Math.max(this.maxOffsetX, (dst.x + dst.width) - clip.width);
				this.maxOffsetY = Math.max(this.maxOffsetY, (dst.y + dst.height) - clip.height);

				dst.x -= this.offsetX;
				dst.y -= this.offsetY;

				if (clip.intersects(dst)) {
					this.drawImage(g, image, dst, src);

					if (this.debugBackground) {
						g.draw(dst);
						g.drawString("BG: "+name, dst.x, dst.y);
					}
				}

				index++;
			}
		}
	}

	private void drawFreeLandscape(Graphics2D g, Rectangle clip) {
		for (FreeLandscapeVO freeLandscape : this.zoneVO.getFreeLandscapes()) {
			String name = freeLandscape.getName_string();
			Sprite sprite = this.getLandscape(name);

			if (sprite != null) {
				Image image = sprite.getImage();
				Rectangle src = sprite.getBounds();
				Rectangle dst = sprite.getBounds();

				dst.x = (int) ((freeLandscape.getX() + sprite.getOffsetX()) * this.zoomFactor);
				dst.y = (int) ((freeLandscape.getY() + sprite.getOffsetY()) * this.zoomFactor);
				dst.width = (int) (src.width * this.zoomFactor);
				dst.height = (int) (src.height * this.zoomFactor);

				dst.x -= this.offsetX;
				dst.y -= this.offsetY;

				if (clip.intersects(dst)) {
					this.drawImage(g, image, dst, src);

					if (this.debugFreeLandscape) {
						g.draw(dst);
						g.drawString("FL: "+name, dst.x, dst.y);
					}
				}
			}
		}
	}

	private void drawLandscape(Graphics2D g, Rectangle clip) {
		int width = this.isoGridWidth;
		int height = this.isoGridHeight / 2;
		int length = 64;

		for (LandscapeVO landscape : this.zoneVO.getLandscapes()) {
			String name = landscape.getName_string();
			Sprite sprite = this.getLandscape(name);
			int index = landscape.getGrid();

			if (sprite != null) {
				Image image = sprite.getImage();
				Rectangle src = sprite.getBounds();
				Rectangle dst = sprite.getBounds();

				double margin = (Math.floor(index / length) % 2) / 2;

				dst.x = (int) (((((index % length) + margin) * width) + sprite.getOffsetX()) * this.zoomFactor);
				dst.y = (int) (((Math.floor(index / length) * height) + sprite.getOffsetY()) * this.zoomFactor);
				dst.width = (int) (src.width * this.zoomFactor);
				dst.height = (int) (src.height * this.zoomFactor);

				dst.x -= this.offsetX;
				dst.y -= this.offsetY;

				if (clip.intersects(dst)) {
					this.drawImage(g, image, dst, src);

					if (this.debugLandscape) {
						g.draw(dst);
						g.drawString("L: "+name, dst.x, dst.y);
					}
				}
			}
		}
	}

	private void drawBuildings(Graphics2D g, Rectangle clip) {
		int width = this.isoGridWidth;
		int height = this.isoGridHeight / 2;
		int length = 64;

		for (BuildingVO building : this.zoneVO.getBuildings()) {
			String name = building.getBuildingName_string();
			Sprite sprite = this.getBuilding(name, building.getUpgradeLevel(), 0);
			int index = building.getBuildingGrid();

			if (sprite != null) {
				Image image = sprite.getImage();
				Rectangle src = sprite.getBounds();
				Rectangle dst = sprite.getBounds();

				double margin = (Math.floor(index / length) % 2) / 2;

				dst.x = (int) (((((index % length) + margin) * width) + sprite.getOffsetX()) * this.zoomFactor);
				dst.y = (int) (((Math.floor(index / length) * height) + sprite.getOffsetY()) * this.zoomFactor);
				dst.width = (int) (src.width * this.zoomFactor);
				dst.height = (int) (src.height * this.zoomFactor);

				dst.x -= this.offsetX;
				dst.y -= this.offsetY;

				if (clip.intersects(dst)) {
					this.drawImage(g, image, dst, src);

					if (this.debugBuilding) {
						g.draw(dst);
						g.drawString("B: "+name, dst.x, dst.y);
					}

					for (ResourceCreationVO resourceCreation : this.zoneVO.getResourceCreations()) {
						int house = resourceCreation.getResourceCreationHouseGrid();
						if (house == index) {
							PathVO path = resourceCreation.getPathVO();
							if (path == null)
								continue;
							
							List<de.uxnr.amf.v3.type.Integer> list = path.getPath();
							if (list == null)
								continue;
							
							int length1 = list.size() * 10000;
//							int length2 = length1 * 2;

//							int mWayWarehouseToWorkyard = 0;
							int mWayWorkyardToDeposit = (length1 / 5) / 100;
//							int mProductionTime = 0;
//							int mOverallTime = 0;
							
							g.drawString("B: "+name+" ("+mWayWorkyardToDeposit+")", dst.x, dst.y);
						}
					}
					
					dst.x = (int) ((((index % length) + margin) * width) * this.zoomFactor);
					dst.y = (int) ((Math.floor(index / length) * height) * this.zoomFactor);
					dst.width = 4;
					dst.height = 4;
					
					dst.x -= this.offsetX + 2;
					dst.y -= this.offsetY + 2;
					
					if (clip.intersects(dst)) {
						g.draw(dst);
					}
				}
			}
		}
	}

	private void drawMapValues(Graphics2D g, Rectangle clip) {
		int width = this.isoGridWidth;
		int height = this.isoGridHeight / 2;
		int length = 62;

		Color foreground = g.getColor();

		int index = 0;
		for (MapValueItemVO mapValueItemVO : this.zoneVO.getMapValues()) {
			double margin = (Math.floor(index / length) % 2) / 2;

			int x = (int) (((((index % length) + margin) * width) * this.zoomFactor) - this.offsetX);
			int y = (int) (((Math.floor(index / length) * height) * this.zoomFactor) - this.offsetY);
			int w = (int) (width * this.zoomFactor);
			int h = (int) (height * this.zoomFactor);

			switch (mapValueItemVO.getBackgroundBlocking()) {
				case 0: // BLOCK_TYPE_ALLOW_ALL
					g.setColor(Color.GREEN);
					break;
				case 1: // BLOCK_TYPE_ALLOW_NOTHING
					g.setColor(Color.RED);
					break;
				case 2: // BLOCK_TYPE_ALLOW_STREETS
					g.setColor(Color.BLUE);
					break;
			}

			Rectangle dst = new Rectangle(x, y, w, h);

			if (clip.intersects(dst)) {
				g.draw(dst);
				g.drawString("S: "+mapValueItemVO.getSectorId(), dst.x, dst.y);
			}

			index++;
		}

		g.setColor(foreground);
	}

	private void drawResourceCreations(Graphics2D g, Rectangle clip) {
		int width = this.isoGridWidth;
		int height = this.isoGridHeight / 2;
		int length = 64;

		Color foreground = g.getColor();

		for (ResourceCreationVO resourceCreation : this.zoneVO.getResourceCreations()) {
			int index = resourceCreation.getResourceCreationHouseGrid();

			double margin = (Math.floor(index / length) % 2) / 2;
			int startX = (int) (((((index % length) + margin) * width) * this.zoomFactor) - this.offsetX);
			int startY = (int) (((Math.floor(index / length) * height) * this.zoomFactor) - this.offsetY);
			int endX = startX;
			int endY = startY;

			PathVO path = resourceCreation.getPathVO();
			if (path != null) {
				g.setColor(Color.RED);

				for (de.uxnr.amf.v3.type.Integer streetGrid : path.getPath()) {
					index = streetGrid.get();

					endX = (int) (((((index % length) + margin) * width) * this.zoomFactor) - this.offsetX);
					endY = (int) (((Math.floor(index / length) * height) * this.zoomFactor) - this.offsetY);

					g.setColor(Color.BLUE);
					g.drawLine(startX, startY, endX, endY);

					startX = endX;
					startY = endY;
				}
			}
		}

		g.setColor(foreground);
	}

	private Sprite getBackground(String name) {
		return this.getImage(name, -1, -1, "res/GFX/background_lib/", "//GameObjects/Backgrounds/Background[@name='"+name+"']/@filename", backgrounds);
	}

	private Sprite getLandscape(String name) {
		return this.getImage(name, -1, -1, "res/GFX/landscape_lib/", "//GameObjects/Landscapes/Landscape[@name='"+name+"']/@filename", landscapes);
	}

	private Sprite getBuilding(String name, int level, int type) {
		Double nofUpgrades;
		if (ZoneMap.nofUpgrades.containsKey(name)) {
			nofUpgrades = ZoneMap.nofUpgrades.get(name);
		} else {
			nofUpgrades = GameSetting.getNumber(this.gfxDoc, "//GameObjects/Buildings/Building[@name='"+name+"']/@nofUpgrades");
			ZoneMap.nofUpgrades.put(name, nofUpgrades);
		}
		if (nofUpgrades != null) {
			level = Math.min(level, nofUpgrades.intValue()) - 1;
		} else {
			level = 0;
		}
		return this.getImage(name, level, type, "res/GFX/building_lib/", "//GameObjects/Buildings/Building[@name='"+name+"']/@filename", buildings);
	}

	private Sprite getImage(String name, int level, int type, String path, String xpath, Map<String, Sprite> cache) {
		Sprite sprite = null;
		String key = name + level + type;
		if (cache.containsKey(key)) {
			sprite = cache.get(key);
		} else {
			String filename = GameSetting.getString(this.gfxDoc, xpath);
			if (filename != null) {
				if (level != -1 && type != -1) {
					filename = filename.replaceAll("\\.", "["+level+"_"+type+"].");
				}
				File file = new File(path+filename);
				if (file.exists() && file.isFile() && file.canRead()) {
					try {
						sprite = new Sprite(file);
					} catch (IOException e) {
						sprite = null;
					}
				}
			}
			cache.put(key, sprite);
		}
		return sprite;
	}

	private boolean drawImage(Graphics2D g, Image image, Rectangle dst, Rectangle src) {
		return g.drawImage(image, dst.x, dst.y, dst.x+dst.width, dst.y+dst.height, src.x, src.y, src.x+src.width, src.y+src.height, null);
	}
}