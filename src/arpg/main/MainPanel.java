package arpg.main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import arpg.base.event.map.AbstractEvent;
import arpg.base.event.map.MapChengePoint;
import arpg.base.event.map.WorldMapOP;
import arpg.base.event.story.Story;
import arpg.base.item.Item;
import arpg.base.magic.Magic;
import arpg.base.map.GameMap;
import arpg.base.map.WorldMap;
import arpg.base.message.MessageWindow;
import arpg.personae.Hero;
import arpg.personae.Monster;
import arpg.prameter.Display;
import arpg.shop.AbstractShop;
import arpg.sound.Sound;
import arpg.sound.Sound.Bgm;
import arpg.sound.Sound.SoundEffect;
import arpg.system.NameInput;
import arpg.ui.CommandWindow;
import arpg.ui.Manifest;
import arpg.ui.key.ActionKey;
import arpg.ui.key.ActionKey.InputMode;

import static arpg.main.Common.*;
import static arpg.main.Common.FontOption.*;

public class MainPanel extends JPanel implements KeyListener, Runnable {

    private static final GameMap DEFAULT = new GameMap();

    public static int gameFlag;
    public static int money;
    public static boolean isAction;

    
    private static Map<String, GameMap> area;
    private static GameMap currentMap;
    private ReadXml readXml;
    
    
    private static Hero hero;
    private Display disp;
    private MessageWindow window;
    private CommandWindow command;
    private AbstractShop shop;
    private Story story;
    private WorldMap worldMap;
    private NameInput input;
    private Manifest manifest;
    private int moveCount;
    //private String mapId;

    private int offsetX;
    private int offsetY;

    private ActionKey upKey;
    private ActionKey downKey;
    private ActionKey rightKey;
    private ActionKey leftKey;
    private ActionKey spaceKey;
    private ActionKey shiftKey;
    private ActionKey controlKey;
    private ActionKey zKey;
    private ActionKey mKey;
    private ActionKey fkey;

    private boolean eventSwitch;
    private boolean mapMode;
    private boolean manifestMode;

    private Sound sound;
    private int soundNumber;

    private static ExecutorService gameLoop;
    private static Thread main;
    private static boolean gameSwitch;

    static {
        area = new HashMap<>();
    }

    public MainPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        gameSwitch = true;

        readXml = new ReadXml();
        story = new Story(this);
        disp = new Display();
        input = new NameInput();
        sound = new Sound();
        // mapId = "OPNING_A";
        //mapId = "SEPO";

        hero = new Hero("トーマス", 25, 26, 0, Direction.DOWN, MoveType.PLAYER, 100, DEFAULT);
        hero.setDirection(Direction.RIGHT);

        debugSetting();

        ///if (area == null) {
            // createGameMap();
        //}
        // currentMap = new GameMap(readXml.readFile("OPENING_A"), this);
        currentMap = new GameMap(readXml.readFile("SEPO"), this);
        // currentMap = area.get(mapId);
        currentMap.addCharacter(hero);

        window = new MessageWindow(this.sound, this);
        command = new CommandWindow(this);
        worldMap = new WorldMap(this);
        manifest = new Manifest();

        setOffset();
        keyInit();

        gameLoop = Executors.newFixedThreadPool(2);
        main = new Thread(this);
        gameLoop.execute(main);
        bgmLoop();

        setBgm(currentMap.getSoundNumber());
        story.lordMainEvent();
    }

    @Test
    private void debugSetting() {

        // 主人公初期配置座標
        // hero = new Hero("トーマス", 21, 32, 0, Direction.DOWN, MoveType.PLAYER, 100,
        // DEFAULT); //オープニングB
        // hero = new Hero("トーマス", 38, 13, 0, Direction.DOWN, MoveType.PLAYER, 100,
        // DEFAULT); //セポ
        hero = new Hero("トーマス", 38, 23, 0, Direction.DOWN, MoveType.PLAYER, 100, DEFAULT); // セポ

        // 初期配置map
        //mapId = "SEPO";

        // 主人公向き
        // hero.setDirection(Direction.UP);
        // hero.setDirection(Direction.LEFT);

        // 所持金
        money = 500;

        // ストーリー進行度
        gameFlag = 5;

        // 名前入力
        // input.setNameInputMode(true);

        // 所持アイテム
        hero.getStatus().addItemBag(Item.getReference("薬草"));
        hero.getStatus().addItemBag(Item.getReference("鉄の斧"));
        hero.getStatus().addItemBag(Item.getReference("ブロンズソード"));
        hero.getStatus().addItemBag(Item.getReference("ブロンズランス"));
        hero.getStatus().addItemBag(Item.getReference("ショートボウ"));
        hero.getStatus().addItemBag(Item.getReference("レザーヘルム"));
        hero.getStatus().addItemBag(Item.getReference("レザーアーマー"));
        hero.getStatus().addItemBag(Item.getReference("ライトシールド"));

        // 所持重要アイテム
        hero.getStatus().addKeyItem(Item.getReference("銀の鍵"));
        // hero.getStatus().addKeyItem(Item.getReference("魔導士の杖"));

        // 習得魔法
        hero.getStatus().masterMagic(Magic.getReference("フィアド"));
        hero.getStatus().masterMagic(Magic.getReference("スラッシュ"));
        hero.getStatus().masterMagic(Magic.getReference("ガイア"));
        hero.getStatus().masterMagic(Magic.getReference("アクエル"));
        hero.getStatus().masterMagic(Magic.getReference("キュレ"));
        hero.getStatus().masterMagic(Magic.getReference("リドーテ"));
        hero.getStatus().masterMagic(Magic.getReference("リパイア"));
        hero.getStatus().masterMagic(Magic.getReference("リブラ"));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        currentMap.drawMap(offsetX, offsetY, g);
        currentMap.drawBattalMessage(g);
        if (window.isGhost()) {
            window.drawMessage(140, 200, FontAndColor.STANDARD, g);
        } else {
            window.drawMessage(FontAndColor.STANDARD, g);
        }
        if (!eventSwitch) {
            disp.drawData(hero, g);
        }

        command.drawCommand(g);
        if (mapMode) {
            worldMap.drawWorldMap(g);
        }
        if (input.isNameInputMode()) {
            input.draw(g);
        }
        if (manifestMode) {
            manifest.drawManifestWindow(g);
        }
    }

    public void setOffset() {
        offsetX = PANEL_WIDTH / 2 - hero.getPx();
        offsetY = PANEL_HEIGHT / 2 - hero.getPy();

        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, PANEL_WIDTH - currentMap.getWidth());

        offsetY = Math.min(offsetY, 0);
        offsetY = Math.max(offsetY, PANEL_HEIGHT - currentMap.getHeight());
    }

    private void createGameMap() {

        new GameMap(readXml.readFile("辺境の町セポ"), this);
        /*
         * area = Map.ofEntries(
         * Map.entry("OPNING_A", new GameMap(readXml.readFile("オープニングA"), this)),
         * Map.entry("OPNING_B", new GameMap(readXml.readFile("オープニングA"), this)),
         * Map.entry("SEPO", new GameMap(readXml.readFile("辺境の町セポ"), this)),
         * Map.entry("SEPO_B1", new GameMap(readXml.readFile("辺境の町セポB1"), this)),
         * Map.entry("SEPO_DJB1", new GameMap(readXml.readFile("辺境の町セポ道場B1"), this)),
         * Map.entry("SEPO_INN2F", new GameMap(readXml.readFile("辺境の町セポ宿屋2F"), this)),
         * Map.entry("EMERIV", new GameMap(readXml.readFile("エメリヴ森林"), this)),
         * Map.entry("EMERIV_2", new GameMap(readXml.readFile("エメリヴ森林2"), this)),
         * Map.entry("EMERIV_3", new GameMap(readXml.readFile("エメリヴ森林3"), this)),
         * Map.entry("EMERIV_4", new GameMap(readXml.readFile("エメリヴ森林4"), this)),
         * Map.entry("EMERIV_5", new GameMap(readXml.readFile("エメリヴ森林5"), this)),
         * Map.entry("EMERIV_B1", new GameMap(readXml.readFile("エメリヴ森林B1"), this)),
         * Map.entry("EMERIV_IDO", new GameMap(readXml.readFile("エメリヴ森林井戸"), this))
         * );
         * /*area = new GameMap[] {
         * new GameMap(readXml.readFile("オープニングA"), this),
         * new GameMap(readXml.readFile("オープニングB"), this),
         * new GameMap(readXml.readFile("辺境の町セポ"), this),
         * new GameMap(readXml.readFile("辺境の町セポB1"), this),
         * new GameMap(readXml.readFile("辺境の町セポ道場B1"), this),
         * new GameMap(readXml.readFile("辺境の町セポ宿屋2F"), this),
         * new GameMap(readXml.readFile("エメリヴ森林"), this),
         * new GameMap(readXml.readFile("エメリヴ森林2"), this),
         * new GameMap(readXml.readFile("エメリヴ森林3"), this),
         * new GameMap(readXml.readFile("エメリヴ森林4"), this),
         * new GameMap(readXml.readFile("エメリヴ森林5"), this),
         * new GameMap(readXml.readFile("エメリヴ森林B1"), this),
         * new GameMap(readXml.readFile("エメリヴ森林井戸"), this),
         * 
         * new GameMap(MapDataPath.SEPO, this),
         * new GameMap(MapDataPath.SEPO_B1, this),
         * new GameMap(MapDataPath.SEPO_DJ_B1, this),
         * new GameMap(MapDataPath.SEPO_INN_F2, this),
         * new GameMap(MapDataPath, panel)
         */
    }

    @Override
    public void run() {

        while (gameSwitch) {

            if (mapMode) {
                worldMap.move();
            } else {
                windowIsOpen();
                magicShotMoving();
                currentMap.autoMove();
                if (eventSwitch) {
                    currentMap.eventNotifyAll();
                }
                if (!pausing()) {
                    heroMove();
                    currentMap.charaMove();
                }
            }
            sleep(20);
            repaint();
        }
    }

    private void sleep(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private void heroMove() {

        if (hero.isMoving()) {
            if (hero.move()) {
                if (hero.getStatus().fieldDamage(moveCount)) {
                    moveCount = 0;
                }
                warpEntrance();
                story.lordMainEvent();
                if (hero.getStatus().getCondition().isPoizon()) {
                    moveCount++;
                } else {
                    if (moveCount != 0) {
                        moveCount = 0;
                    }
                }
            }
            setOffset();
        }
    }

    private void warpEntrance() {

        AbstractEvent event = currentMap.eventCheck(hero.getX(), hero.getY());

        if (event instanceof WorldMapOP) {
            mapMode = true;
            keyReset();
        } else if (event instanceof MapChengePoint) {
            hero.warp(this, (MapChengePoint) event);
        }
    }

    private void input() {

        if (upKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(Direction.UP);
            }
        } else if (downKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(Direction.DOWN);
            }
        } else if (rightKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(Direction.RIGHT);
            }
        } else if (leftKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(Direction.LEFT);
            }
        }

        if (spaceKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.action();
            }
        } else if (zKey.isPressed()) {
            if (!shopping()) {
                performance();
            }
        } else if (controlKey.isPressed()) {
            if (!mapMode && !manifestMode) {
                if (!command.isVisible()) {
                    command.open();
                    sound.soundEffectStart(SoundEffect.DECISION);
                } else {
                    command.close();
                    sound.soundEffectStart(SoundEffect.CANCEL);
                }
            }
        } else if (shiftKey.isPressed()) {
            hero.shot();
        } else if (mKey.isPressed()) {
            if (!command.isVisible() && gameFlag > 3) {
                if (!mapMode) {
                    mapMode = true;
                }
            }
        } else if (fkey.isPressed()) {
            if (!command.isVisible() && gameFlag > 3) {
                if (!manifestMode) {
                    manifestMode = true;
                }
            }
        }
    }

    private boolean pausing() {
        if (window.isVisible() || eventSwitch || command.isVisible() || hero.isExecution() || mapMode
                || input.isNameInputMode() || manifestMode) {
            return true;
        }
        /*
         * if(eventSwitch) {
         * return true;
         * }
         * if(command.isVisible()) {
         * return true;
         * }
         */
        if (shop != null) {
            if (shop.isUsed()) {
                return true;
            }
        }
        /*
         * if(hero.isExecution()) {
         * return true;
         * }
         * if(mapMode) {
         * return true;
         * }
         * if(input.isNameInputMode()) {
         * return true;
         * }
         * if(manifestMode) {
         * return true;
         * }
         */
        return false;
    }

    private void windowIsOpen() {

        if (window.isVisible()) {
            if (window.isRightClose()) {
                if (upKey.isPressed() || downKey.isPressed() || rightKey.isPressed() || leftKey.isPressed()
                        || zKey.isPressed()) {
                    if (window.nextMessage()) {
                        window.close();
                    }
                }
            } else {
                if (zKey.isPressed()) {
                    if (window.nextMessage()) {
                        window.close();
                    }
                }
            }
        } else {
            input();
        }
    }

    private void performance() {

        if (hero.isMoving()) {
            return;
        }
        if (!window.isVisible()) {
            String message = hero.getMessage();
            if (!message.equals("")) {
                window.setting(message);
                window.open();
            }
        }
    }

    private void magicShotMoving() {
        currentMap.getCharaList().stream().forEach(v -> {
            if (v instanceof Hero) {
                hero.magicShotMoving();
                hero.arrowMoving();
            }
            if (v instanceof Monster) {
                Monster monster = (Monster) v;
                monster.magicShotMoving();
            }
        });
    }

    private boolean shopping() {
        shop = hero.serchShop();
        if (shop != null) {
            shop.startingCustomerService();
            return true;
        }
        return false;
    }

    private void cursorOperation(KeyEvent e) {

        int key = e.getKeyCode();

        if (!window.isVisible()) {

            switch (key) {
                case KeyEvent.VK_UP -> {
                    sound.soundEffectStart(SoundEffect.CURSOR);
                    if (command.isVisible()) {
                        command.moveCursor(KeyCode.UP, hero);
                    } else if (shop != null) {
                        if (shop.isUsed()) {
                            shop.moveCursor(KeyCode.UP);
                        }
                    } else if (mapMode) {
                        worldMap.movePointer(KeyCode.UP);
                    } else if (input.isNameInputMode()) {
                        input.moveCursor(KeyCode.UP);
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    sound.soundEffectStart(SoundEffect.CURSOR);
                    if (command.isVisible()) {
                        command.moveCursor(KeyCode.DOWN, hero);
                    } else if (shop != null) {
                        if (shop.isUsed()) {
                            shop.moveCursor(KeyCode.DOWN);
                        }
                    } else if (mapMode) {
                        worldMap.movePointer(KeyCode.DOWN);
                    } else if (input.isNameInputMode()) {
                        input.moveCursor(KeyCode.DOWN);
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    sound.soundEffectStart(SoundEffect.CURSOR);
                    if (mapMode) {
                        worldMap.movePointer(KeyCode.RIGHT);
                    } else if (input.isNameInputMode()) {
                        input.moveCursor(KeyCode.RIGHT);
                    }
                }
                case KeyEvent.VK_LEFT -> {
                    sound.soundEffectStart(SoundEffect.CURSOR);
                    if (mapMode) {
                        worldMap.movePointer(KeyCode.LEFT);
                    } else if (input.isNameInputMode()) {
                        input.moveCursor(KeyCode.LEFT);
                    }
                }
                case KeyEvent.VK_Z -> {
                    sound.soundEffectStart(SoundEffect.BOTTUN);
                    if (command.isVisible()) {
                        command.moveCursor(KeyCode.Z, hero);
                    } else if (shop != null) {
                        if (shop.isUsed()) {
                            shop.moveCursor(KeyCode.Z);
                        }
                    } else if (input.isNameInputMode()) {
                        input.moveCursor(KeyCode.Z);
                    } else if (mapMode) {
                        worldMap.movePointer(KeyCode.Z);
                    }
                }
                case KeyEvent.VK_CONTROL -> {
                    sound.soundEffectStart(SoundEffect.CANCEL);
                    if (shop != null) {
                        if (shop.isUsed()) {
                            shop.close();
                        }
                    } else if (input.isNameInputMode()) {

                    } else {
                        controlKey.press();
                    }
                }
                case KeyEvent.VK_M -> {
                    if (mapMode) {
                        WorldMapOP op = (WorldMapOP) currentMap.eventCheck(hero.getX(), hero.getY());
                        if (op != null) {
                            hero.setX(op.getReverseX());
                            hero.setY(op.getReverseY());
                        }
                        mapMode = false;
                        sound.soundEffectStart(SoundEffect.CURSOR);
                    }
                }
                case KeyEvent.VK_F -> {
                    if (manifestMode) {
                        manifestMode = false;
                        sound.soundEffectStart(SoundEffect.CURSOR);
                    }
                }
            }
        } else {
            if (window.getSelecter()) {
                switch (key) {
                    case KeyEvent.VK_UP -> window.cursorMove(KeyCode.UP);
                    case KeyEvent.VK_DOWN -> window.cursorMove(KeyCode.DOWN);
                    case KeyEvent.VK_Z -> window.cursorMove(KeyCode.Z);
                }
            } else {
                if (window.isRightClose()) {
                    switch (key) {
                        case KeyEvent.VK_UP -> upKey.press();
                        case KeyEvent.VK_DOWN -> downKey.press();
                        case KeyEvent.VK_RIGHT -> rightKey.press();
                        case KeyEvent.VK_LEFT -> leftKey.press();
                        case KeyEvent.VK_Z -> zKey.press();
                    }
                } else {
                    if (key == KeyEvent.VK_Z) {
                        zKey.press();
                    }
                }
            }
        }
    }

    private void keyInit() {

        upKey = new ActionKey();
        downKey = new ActionKey();
        rightKey = new ActionKey();
        leftKey = new ActionKey();
        spaceKey = new ActionKey(InputMode.DETECT_INTIAL_PRESS_ONLY);
        shiftKey = new ActionKey(InputMode.DETECT_INTIAL_PRESS_ONLY);
        controlKey = new ActionKey(InputMode.DETECT_INTIAL_PRESS_ONLY);
        zKey = new ActionKey(InputMode.DETECT_INTIAL_PRESS_ONLY);
        mKey = new ActionKey(InputMode.DETECT_INTIAL_PRESS_ONLY);
        fkey = new ActionKey(InputMode.DETECT_INTIAL_PRESS_ONLY);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int keyCode = e.getKeyCode();

        if (!pausing()) {
            switch (keyCode) {
                case KeyEvent.VK_UP -> upKey.press();
                case KeyEvent.VK_DOWN -> downKey.press();
                case KeyEvent.VK_RIGHT -> rightKey.press();
                case KeyEvent.VK_LEFT -> leftKey.press();
                case KeyEvent.VK_SPACE -> spaceKey.press();
                case KeyEvent.VK_SHIFT -> shiftKey.press();
                case KeyEvent.VK_CONTROL -> controlKey.press();
                case KeyEvent.VK_Z -> zKey.press();
                case KeyEvent.VK_M -> mKey.press();
                case KeyEvent.VK_F -> fkey.press();
            }
        } else {
            cursorOperation(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP -> upKey.release();
            case KeyEvent.VK_DOWN -> downKey.release();
            case KeyEvent.VK_RIGHT -> rightKey.release();
            case KeyEvent.VK_LEFT -> leftKey.release();
            case KeyEvent.VK_SPACE -> spaceKey.release();
            case KeyEvent.VK_SHIFT -> shiftKey.release();
            case KeyEvent.VK_CONTROL -> controlKey.release();
            case KeyEvent.VK_Z -> zKey.release();
            case KeyEvent.VK_M -> mKey.release();
            case KeyEvent.VK_F -> fkey.release();
        }
    }

    private void keyReset() {
        upKey.reset();
        downKey.reset();
        rightKey.reset();
        leftKey.reset();
    }

    public static void setHeroName(String newHeroName) {
        hero.setName(newHeroName);
    }

    public static boolean isGameSwitch() {
        return gameSwitch;
    }

    public boolean isEventSwitch() {
        return this.eventSwitch;
    }

    public void eventStart() {
        eventSwitch = true;
    }

    public void eventEnd() {
        eventSwitch = false;
    }

    public Hero getHero() {
        return hero;
    }

    public void lordMap(String id) {
        String currentId = currentMap.getSelectMap().getId();
        if (!currentId.equals(id)) {
            if(area.containsKey(id)) {
                currentMap = area.get(id);
            }
            else {
                currentMap = new GameMap(readXml.readFile(id), this);
                area.put(id, currentMap);
            }
            currentMap.addCharacter(hero);
        }
    }

    public GameMap getCurrentMap() {
        return this.currentMap;
    }

    public void setCurrentMap(GameMap map) {
        this.currentMap = map;
    }

    public WorldMap getWorlMap() {
        return this.worldMap;
    }

    public Story getStory() {
        return this.story;
    }

    public MessageWindow getWindow() {
        return this.window;
    }

    public Thread getMain() {
        return main;
    }

    public void nameInputModeStart() {
        this.input.setNameInputMode(true);
    }

    public void nameInputModeEnd() {
        this.input.setNameInputMode(false);
    }

    public NameInput getNameInput() {
        return this.input;
    }

    public void mapModeOn() {
        mapMode = true;
    }

    public void mapModeOff() {
        mapMode = false;
    }

    public Sound getSound() {
        return this.sound;
    }

    public void jukebox() {
        switch (soundNumber) {
            case -1 -> sound.stop();
            default -> sound.bgmPlayer(Bgm.values()[soundNumber]);
        }
    }

    public void setBgm(int soundNumber) {
        sound.stop();
        this.soundNumber = soundNumber;
    }

    public int getSoundNumber() {
        return this.soundNumber;
    }

    private void bgmLoop() {

        gameLoop.execute(() -> {
            try {
                while (gameSwitch) {
                    jukebox();
                }
            } finally {
                gameLoop.shutdownNow();
            }
        });
    }
}
