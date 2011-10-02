package de.uxnr.tsoexpert.game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Game {

	private final Map<String, Resource> ResourceMap = new HashMap<String, Resource>();
	private final Map<String, Building> BuildingMap = new HashMap<String, Building>();
	private final Map<Integer, Creation> CrationMap = new HashMap<Integer, Creation>();
	private final Map<Integer, Buff> BuffMap = new HashMap<Integer, Buff>();

	public static void main(String args[]) throws Exception {
		File file = new File("res/GFX/game_settings.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		parse(document);
	}

	public static Game parse(Document d) throws InvalidGameSettingsException {
		Game game = new Game();
		parseResources(d, game);

		return null;
	}

	private static void parseResources(Document d, Game game) throws InvalidGameSettingsException {
		NodeList resourceDefinitions = d.getElementsByTagName("ResourceDefinitions");
		Map<String, Resource> resourceMap = new HashMap<String, Resource>();
		if (resourceDefinitions.getLength() == 1) {
			NodeList childNodes = resourceDefinitions.item(0).getChildNodes();
			for (int x = 0; x < childNodes.getLength(); x++) {
				Node node = childNodes.item(x);
				if (node.getNodeName().equalsIgnoreCase("ResourceDefinition")) {
					NamedNodeMap attributes = node.getAttributes();
					String name = null;
					for (int y = 0; y < attributes.getLength(); y++) {
						Node attribute = attributes.item(y);
						if (attribute.getNodeName().equalsIgnoreCase("name")) {
							name = attribute.getNodeValue();
							System.out.println(name);
						}
					}
					Resource r = new Resource(name);
					if (!game.ResourceMap.containsKey(r)) {
						resourceMap.put(r.getName(), r);
					}
					r.parse(node);
				}
			}
		} else {
			throw new InvalidGameSettingsException("No ResourceDefinitions present");
		}
	}

}
