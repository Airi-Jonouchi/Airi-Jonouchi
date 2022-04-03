package arpg.main;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import arpg.main.path.MapDataPath;

public class ReadXml {

	private static Document document;

	static {
		initDocument();
	}

	private static void initDocument() {
		
		try(BufferedInputStream is = new BufferedInputStream(ReadXml.class.getResourceAsStream("path/data/path.xml"));) {		 
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public MapDataPath readFile(String data) {
		Element maps = document.getDocumentElement();
		NodeList mapList = maps.getElementsByTagName("map");
		
		for(int i = 0; i < mapList.getLength(); i++) {
			Element map = (Element)mapList.item(i);
			if(map.getAttribute("id").equals(data)) {
				String id = map.getAttribute("id");
				String name = map.getAttribute("name");
				String bgm = map.getAttribute("bgm");
				String imagePath = getChildren(map, "image").getTextContent();
				String eventPath = getChildren(map, "event").getTextContent();
				String messagePath = getChildren(map, "message").getTextContent();
				MapDataPath mapDataPath = new MapDataPath(id, name, imagePath, eventPath, messagePath, bgm);
				return mapDataPath;
			}
		}
		return null;
	}

	private Element getChildren(Element element, String tag) {

		NodeList children = element.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			if(children.item(i) instanceof Element) {
				Element childElement = (Element)children.item(i);
				if(childElement.getNodeName().equals(tag)) {
					return childElement;
				}
			}
		};
		return null;
	}
}
