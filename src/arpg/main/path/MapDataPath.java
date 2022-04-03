package arpg.main.path;

public class MapDataPath {

	private String id;
	private String mapName;
	private String imagePath;
	private String eventPath;
	private String messagePath;
	private String bgm;

	public MapDataPath(String id, String mapName, String imagePath, String eventPath, String messagePath, String bgm) {
		this.id = id;
		this.mapName = mapName;
		this.imagePath = imagePath;
		this.eventPath = eventPath;
		this.messagePath = messagePath;
		this.bgm = bgm;
	}

	public String getId() {
		return this.id;
	}

	public String getMapName() {
		return this.mapName;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	public String getEventPath() {
		return this.eventPath;
	}

	public String getMessagePath() {
		return this.messagePath;
	}

	public String getBgm() {
		return this.bgm;
	}
}
