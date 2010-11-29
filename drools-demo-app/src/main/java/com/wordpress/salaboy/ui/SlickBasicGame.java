package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.MyDroolsService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.drools.runtime.StatefulKnowledgeSession;

import org.newdawn.slick.Animation;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.plugtree.training.model.Call;

public class SlickBasicGame extends BasicGame {

    public StatefulKnowledgeSession ksession = MyDroolsService.createSession();
    private float playerX = 32;
    private float playerY = 400;
    //private    TiledMap map;	
    private Animation player;
    private Animation emergency;
    private Animation hospital;
    public BlockMap map;
    private Polygon playerPoly;
    private Polygon emergencyPoly;
    private Polygon hospitalPoly;
    private SpriteSheet emergencySheet;
    private int[] xs = new int[]{1, 2, 7, 8, 13, 14, 19, 20, 25, 26, 31, 32, 37, 38};
    private int[] ys = new int[]{1, 2, 7, 8, 13, 14, 19, 20, 25, 26};
    private int randomx;
    private int randomy;
    private int[] hospitals = new int[]{10, 13, //Hospital 1 = X, Y 
        11, 13,//Hospital 2 = X, Y 
        34, 13,
        35, 13,
        16, 25,
        17, 25};//Hospital 3 = X, Y
    private static UserUI ui;
    
    private MapEventsNotifier mapEventsNotifier= new MapEventsNotifier();

    public SlickBasicGame() {
        super("Emergency City!!");


    }

    @Override
    public void init(GameContainer gc)
            throws SlickException {
        gc.setVSync(true);
        SpriteSheet sheet = new SpriteSheet("data/sprites/sprites-ambulancia.png", 32, 32, Color.magenta);
        emergencySheet = new SpriteSheet("data/sprites/spot.png", 16, 16, Color.magenta);
        map = new BlockMap("data/cityMap.tmx");





        player = new Animation();
        player.setLooping(false);
        player.setAutoUpdate(false);
        for (int row = 0; row < 4; row++) {
            for (int frame = 0; frame < 4; frame++) {

                player.addFrame(sheet.getSprite(frame, row), 250);

            }
        }


        playerPoly = new Polygon(new float[]{
                    playerX, playerY,
                    playerX + 30, playerY,
                    playerX + 30, playerY + 30,
                    playerX, playerY + 30
                });



    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException {
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            int current = player.getFrame();
            if (current < 7) {
                current += 1;
            } else {
                current = 4;
            }

            player.setCurrentFrame(current);

            playerX--;
            playerPoly.setX(playerX);
            if (entityCollisionWith()) {
                playerX++;
                playerPoly.setX(playerX);
            }
            if (emergencyCollision()) {
                emergencyPointReachedCreateHospital();
            }
            if (hospitalCollision()) {
                hospitalPointReached();
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {

            int current = player.getFrame();
            if (current < 3) {
                current += 1;
            } else {
                current = 0;
            }

            player.setCurrentFrame(current);
            playerX++;
            playerPoly.setX(playerX);
            if (entityCollisionWith()) {
                playerX--;
                playerPoly.setX(playerX);


            }
            if (emergencyCollision()) {
                emergencyPointReachedCreateHospital();
            }
            if (hospitalCollision()) {
                hospitalPointReached();
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            int current = player.getFrame();
            if (current < 15) {
                current += 1;
            } else {
                current = 12;
            }
            player.setCurrentFrame(current);
            playerY--;
            playerPoly.setY(playerY);
            if (entityCollisionWith()) {
                playerY++;
                playerPoly.setY(playerY);
            }
            if (emergencyCollision()) {
                emergencyPointReachedCreateHospital();
            }
            if (hospitalCollision()) {
                hospitalPointReached();
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            int current = player.getFrame();
            if (current < 11) {
                current += 1;
            } else {
                current = 8;
            }
            player.setCurrentFrame(current);
            playerY++;
            playerPoly.setY(playerY);
            if (entityCollisionWith()) {
                playerY--;
                playerPoly.setY(playerY);

            }
            if (emergencyCollision()) {
                emergencyPointReachedCreateHospital();
            }
            if (hospitalCollision()) {
                hospitalPointReached();
            }
        }

        if (gc.getInput().isKeyDown(Input.KEY_SPACE)) {

            if (emergency == null) {
                randomx = (int) (Math.random() * 10) % 14;
                randomy = (int) (Math.random() * 10) % 10;
                emergencyPoly = new Polygon(new float[]{
                            xs[randomx] * 16, ys[randomy] * 16,
                            xs[randomx] * 16 + 16, ys[randomy] * 16,
                            xs[randomx] * 16 + 16, ys[randomy] * 16 + 16,
                            xs[randomx] * 16, ys[randomy] * 16 + 16
                        });

                System.out.println("x = " + xs[randomx] + " -> y =" + ys[randomy]);
                int emergencysquare[] = {1, 1, 15, 1, 15, 15, 1, 15};
                BlockMap.emergencies.add(new Block(xs[randomx] * 16, ys[randomy] * 16, emergencysquare, "emergency"));
                emergency = new Animation();
                emergency.setAutoUpdate(true);
                for (int frame = 0; frame < 6; frame++) {
                    emergency.addFrame(emergencySheet.getSprite(frame, 0), 150);
                }


                MyDroolsService.registerHandlers(ksession);
                MyDroolsService.setGlobals(ksession);

                ksession.insert(new Call(new Date()));

                new Thread(new Runnable()   {

                    public void run() {
                        ksession.fireUntilHalt();
                    }
                }).start();
            }


        }
    }

    private void emergencyPointReachedCreateHospital() {
        System.out.println("Check Point Hit!");
        emergency = null;
        BlockMap.emergencies = new ArrayList<Object>();
        int hospitasquare[] = {1, 1, 15, 1, 15, 15, 1, 15};
        BlockMap.hospitals.add(new Block(hospitals[0] * 16, hospitals[1] * 16, hospitasquare, "hospital"));
        hospitalPoly = new Polygon(new float[]{
                    hospitals[0] * 16, hospitals[1] * 16,
                    hospitals[0] * 16 + 16, hospitals[1] * 16,
                    hospitals[0] * 16 + 16, hospitals[1] * 16 + 16,
                    hospitals[0] * 16, hospitals[1] * 16 + 16
                });
        hospital = new Animation();
        hospital.setAutoUpdate(true);
        hospital.addFrame(emergencySheet.getSprite(0, 0), 150);

    }

    public void render(GameContainer gc, Graphics g)
            throws SlickException {

        g.draw(playerPoly);
        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 1, true);
        g.drawAnimation(player, playerX, playerY);

        if (emergency != null) {
            g.drawAnimation(emergency, xs[randomx] * 16, ys[randomy] * 16);
            g.draw(emergencyPoly);


        }
        if (hospital != null) {
            g.drawAnimation(hospital, hospitals[0] * 16, hospitals[1] * 16);
            g.draw(hospitalPoly);
        }
        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 2, true);

    }

    public static void main(String[] args)
            throws SlickException {

        final SlickBasicGame game = new SlickBasicGame();
        Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
        Renderer.getLineStripRenderer().setLineCaps(true);
        java.awt.EventQueue.invokeLater(new Runnable()   {

            @Override
            public void run() {
                ui = new UserUI();
                ui.setVisible(true);
                ui.setUIMap(game);
            }
        });
        AppGameContainer container =
                new AppGameContainer(game);
        container.start();

    }

    public boolean entityCollisionWith() throws SlickException {
        for (int i = 0; i < BlockMap.entities.size(); i++) {
            Block entity1 = (Block) BlockMap.entities.get(i);
            if (playerPoly.intersects(entity1.poly)) {
                return true;
            }
        }
        return false;
    }

    public boolean emergencyCollision() throws SlickException {
        for (int i = 0; i < BlockMap.emergencies.size(); i++) {
            Block entity1 = (Block) BlockMap.emergencies.get(i);
            if (playerPoly.intersects(entity1.poly)) {
                this.mapEventsNotifier.notifyMapEventsListeners(MapEventsNotifier.EVENT_TYPE.EMERGENCY_REACHED, entity1);
                return true;
            }
        }
        return false;
    }

    public boolean hospitalCollision() throws SlickException {
        for (int i = 0; i < BlockMap.hospitals.size(); i++) {
            Block entity1 = (Block) BlockMap.hospitals.get(i);
            if (playerPoly.intersects(entity1.poly)) {
                this.mapEventsNotifier.notifyMapEventsListeners(MapEventsNotifier.EVENT_TYPE.HOSPITAL_REACHED, entity1);
                return true;
            }
        }
        return false;
    }

    private void hospitalPointReached() {
        System.out.println("Hospital Point Hit!");
    }
    
    public void addMapEventsListener(MapEventsListener listener){
        this.mapEventsNotifier.addMapEventsListener(listener);
    }
}
