package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.MyDroolsService;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

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
import org.plugtree.training.model.Ambulance;
import org.plugtree.training.model.Call;
import org.plugtree.training.model.Emergency.EmergencyType;
import org.plugtree.training.model.Hospital;
import org.plugtree.training.model.Patient;
import org.plugtree.training.model.events.PatientAtTheHospitalEvent;
import org.plugtree.training.model.events.PatientPickUpEvent;

public class SlickBasicGame extends BasicGame implements MapEventsListener {

    public StatefulKnowledgeSession ksession = MyDroolsService.createSession();
    private float playerX = 32;
    private float playerY = 400;
    private Animation player;
    private Animation emergency;
    private Animation hospital;
    public BlockMap map;
    private Hospital selectedHospital = null;
    private Polygon playerPoly;
    private Polygon emergencyPoly;
    private Polygon hospitalPoly;
    private SpriteSheet emergencySheet;
    private SpriteSheet hospitalSheet;
    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private int[] ys = new int[]{1, 7, 13, 19, 25};
    private int randomx;
    private int randomy;
    //private int[] hospitals = new int[]{9, 8, 33, 8, 15, 20};
    private static UserUI ui;
    public boolean ambulanceDispatched = false;
    private Ambulance ambulance;
    public Long ambulanceSelectedId = 0L;
    public EmergencyType emergencyTypeSelected;
    private MapEventsNotifier mapEventsNotifier;
    //FIXME: It only supports one ambulance!
    private AmbulanceMonitorService ambulanceMonitorService;

    public SlickBasicGame() {
        super("City Map");

        this.mapEventsNotifier = new MapEventsNotifier(MyDroolsService.logger);
        this.mapEventsNotifier.addMapEventsListener(this);
        

        // initWiiMote();
    }

    @Override
    public void init(GameContainer gc)
            throws SlickException {
        gc.setVSync(true);
        SpriteSheet sheet = new SpriteSheet("data/sprites/sprites-ambulancia.png", 32, 32, Color.magenta);
        emergencySheet = new SpriteSheet("data/sprites/alert.png", 32, 32, Color.magenta);
        hospitalSheet = new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);
        map = new BlockMap("data/cityMap.tmx");
        map.initializeCorners();
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
            if (ambulance != null) {
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
               
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {

            if (ambulance != null) {
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
               
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            if (ambulance != null) {
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
              
            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            if (ambulance != null) {
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


            }
        }

        if (gc.getInput().isKeyDown(Input.KEY_SPACE)) {

            if (emergency == null) {
                
                
                randomx = (int) (Math.random() * 10) % 7;
                randomy = (int) (Math.random() * 10) % 5;
                if(randomx == 1 && randomy == 25){
                    randomx = 19;
                }
                
                emergencyPoly = new Polygon(new float[]{
                            xs[randomx] * 16, ys[randomy] * 16,
                            xs[randomx] * 16 + 32, ys[randomy] * 16,
                            xs[randomx] * 16 + 32, ys[randomy] * 16 + 32,
                            xs[randomx] * 16, ys[randomy] * 16 + 32
                        });

                System.out.println("x = " + xs[randomx] + " -> y =" + ys[randomy]);
                int emergencysquare[] = {1, 1, 31, 1, 31, 31, 1, 31};
                BlockMap.emergencies.add(new Block(xs[randomx] * 16, ys[randomy] * 16, emergencysquare, "emergency"));
                emergency = new Animation();
                emergency.setAutoUpdate(true);
                for (int frame = 0; frame < 5; frame++) {
                    emergency.addFrame(emergencySheet.getSprite(frame, 0), 150);
                }


                MyDroolsService.registerHandlers(ksession);
                MyDroolsService.setGlobals(ksession);
                MyDroolsService.setNotifier(ksession, mapEventsNotifier);
                ksession.insert(new Call(new Date(System.currentTimeMillis())));
                new Thread(new Runnable()     {

                    public void run() {
                        ksession.fireUntilHalt();
                    }
                }).start();
            }
        }
        cornerCollision();
        if (emergency != null) {
            emergencyCollision();
        }

        if (hospital != null) {
            hospitalCollision();
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g)
            throws SlickException {

        g.draw(playerPoly);
        if (emergency != null) {
            g.draw(emergencyPoly);
        }
        if (hospital != null) {
            g.draw(hospitalPoly);
        }
        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 1, true);
        if (ambulanceDispatched) {
            this.ambulance = getAmbulanceById(emergencyTypeSelected, ambulanceSelectedId);
            g.drawAnimation(player, playerX, playerY);
        }

        if (emergency != null) {
            g.drawAnimation(emergency, xs[randomx] * 16, ys[randomy] * 16);
        }

        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 2, true);
        if (hospital != null && selectedHospital != null) {
            g.drawAnimation(hospital, (selectedHospital.getPositionX()-2)*16 , (selectedHospital.getPositionY()-5)*16);

        }
    }

    public static void main(String[] args)
            throws SlickException {

        final SlickBasicGame game = new SlickBasicGame();
        Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
        Renderer.getLineStripRenderer().setLineCaps(true);
        java.awt.EventQueue.invokeLater(new Runnable()     {

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

    public boolean cornerCollision() throws SlickException {
        for (int i = 0; i < BlockMap.corners.size(); i++) {
            Block entity1 = (Block) BlockMap.corners.get(i);
            if (playerPoly.intersects(entity1.poly)) {
                this.mapEventsNotifier.notifyMapEventsListeners(MapEventsNotifier.EVENT_TYPE.AMBULANCE_POSITION, entity1);
                return true;
            }
        }
        return false;
    }

    public void addMapEventsListener(MapEventsListener listener) {
        this.mapEventsNotifier.addMapEventsListener(listener);
    }

    private Ambulance getAmbulanceById(EmergencyType type, Long id) {
        List<Ambulance> ambulances = MyDroolsService.ambulances.get(type);
        for (Ambulance ambulancenow : ambulances) {
            if (ambulancenow.getId() == id) {
                return ambulancenow;
            }
        }
        return null;
    }

    @Override
    public synchronized void hospitalReached(Block hospitalBlock) {
        if (this.hospital != null) {
            this.hospital = null;
            System.out.println("HOSPITAL REACHED TIME TO SIGNAL The PATIENT AT THE HOSPITAL EVENT!!!");
            ksession.signalEvent("org.plugtree.training.model.events.PatientAtTheHospitalEvent", new PatientAtTheHospitalEvent());
            
            ambulanceMonitorService.stop();
            
            Hospital myhospital = MyDroolsService.getHospitalByCoordinates(hospitalBlock);
            QueryResults results = ksession.getQueryResults("getPatient");
            
            Iterator<QueryResultsRow> it = results.iterator();
            Patient patient = null;
            while(it.hasNext()){
                Object o = it.next().get(results.getIdentifiers()[0]);
                patient = (Patient)o;
            }
            myhospital.addPatient(patient);
        }
    }

    @Override
    public synchronized void emergencyReached(Block emergencyBlock) {
        if(emergency != null){
            emergency = null;
            System.out.println("EMERGENCY REACHED TIME TO SIGNAL DE PATIENT PICK UP EVENT!!!");
            ksession.signalEvent("com.wordpress.salaboy.PickUpPatientEvent", new PatientPickUpEvent(new Date()));
        }
    }

    @Override
    public void positionReceived(Block corner) {
        float newX = Math.round(corner.poly.getX() / 16);
        float newY = Math.round(corner.poly.getY() / 16);
        if(newX != ambulance.getPositionX() || newY != ambulance.getPositionY()){
            ambulance.setPositionX(newX);
            ambulance.setPositionY(newY);
            FactHandle handle = ksession.getFactHandle(ambulance);
            ksession.update(handle, ambulance);
        }
    }

    @Override
    public void heartBeatReceived(double value) {
    }

    @Override
    public void hospitalSelected(Long id) {
        
        BlockMap.emergencies.clear();
        int hospitasquare[] = {1, 1, 15, 1, 15, 15, 1, 15};
        
        for (Hospital hospitalnow : MyDroolsService.hospitals.values()) {
            if (hospitalnow.getId() == id) {
                selectedHospital = hospitalnow;
            }
        }
        BlockMap.hospitals.clear();
        BlockMap.hospitals.add(new Block(Math.round(selectedHospital.getPositionX()) * 16, Math.round(selectedHospital.getPositionY()) * 16, hospitasquare, "hospital"));
        hospitalPoly = new Polygon(new float[]{
                    Math.round(selectedHospital.getPositionX()) * 16, Math.round(selectedHospital.getPositionY()) * 16,
                    Math.round(selectedHospital.getPositionX()) * 16 + 16, Math.round(selectedHospital.getPositionY()) * 16,
                    Math.round(selectedHospital.getPositionX()) * 16 + 16, Math.round(selectedHospital.getPositionY()) * 16 + 16,
                    Math.round(selectedHospital.getPositionX()) * 16, Math.round(selectedHospital.getPositionY()) * 16 + 16
                });
        hospital = new Animation();
        hospital.setAutoUpdate(true);
        for (int frame = 0; frame < 5; frame++) {
            hospital.addFrame(hospitalSheet.getSprite(frame, 0), 150);
        }
        ambulanceMonitorService = new AmbulanceMonitorService(ksession, mapEventsNotifier);
        ambulanceMonitorService.start();
    }

    @Override
    public void monitorAlertReceived(String string) {
    }

    public AmbulanceMonitorService getAmbulanceMonitorService() {
        return ambulanceMonitorService;
    }

    
    

   
}
