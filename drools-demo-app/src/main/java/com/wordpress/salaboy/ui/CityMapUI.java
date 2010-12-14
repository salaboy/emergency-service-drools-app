package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.EmergencyService;
import com.wordpress.salaboy.events.MapHospitalReachedEventNotifier;
import com.wordpress.salaboy.events.MapHospitalSelectedEventNotifier;
import com.wordpress.salaboy.ui.MapEventsNotifier.EventType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.plugtree.training.model.Ambulance;
import org.plugtree.training.model.Call;
import org.plugtree.training.model.Emergency;
import org.plugtree.training.model.Hospital;

public class CityMapUI extends BasicGame {

    private float playerInitialX = 32;
    private float playerInitialY = 400;
    
    
    // The map itself (created using Tiled)
    public BlockMap map;
    
    
    // Places for emergencies
    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private int[] ys = new int[]{1, 7, 13, 19, 25};
    private int randomx;
    private int randomy;
    
    
    private List<Ambulance> ambulances = new ArrayList<Ambulance>();
    private List<Emergency> emergencies = new CopyOnWriteArrayList<Emergency>();
    private List<Hospital> hospitals = new CopyOnWriteArrayList<Hospital>();
    
    
    private boolean turbo;
    
    
    public static SpriteSheet alertSheet;
    public static SpriteSheet hospitalSheet;

    public CityMapUI() {
        super("City Map");

    }

    @Override
    public void init(GameContainer gc)
            throws SlickException {
        gc.setVSync(true);

        alertSheet = new SpriteSheet("data/sprites/alert.png", 32, 32, Color.magenta);
        hospitalSheet = new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);

        map = new BlockMap("data/cityMap.tmx");
        
        map.initializeCorners();

    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException {
        //@TODO:  Keys are defined for Player 1 .. add support for multiple controllers        
        // HACK -> I'm just selecting the first ambulance to bind it to these keys
        Ambulance ambulance = null;
        if (ambulances.size() >= 1) {
            ambulance = ambulances.iterator().next();
        }

        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            if (ambulance != null) {
                int current = ambulance.getAnimation().getFrame();
                if (current < 7) {
                    current += 1;
                } else {
                    current = 4;
                }

                ambulance.getAnimation().setCurrentFrame(current);

                if (turbo) {
                    playerInitialX -= 5;
                } else {
                    playerInitialX--;
                }
                ambulance.getPolygon().setX(playerInitialX);
                if (entityCollisionWith(ambulance)) {
                    if (turbo) {
                        playerInitialX += 5;
                    } else {
                        playerInitialX++;
                    }
                    ambulance.getPolygon().setX(playerInitialX);
                }

            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {

            if (ambulance != null) {
                int current = ambulance.getAnimation().getFrame();
                if (current < 3) {
                    current += 1;
                } else {
                    current = 0;
                }

                ambulance.getAnimation().setCurrentFrame(current);
                if (turbo) {
                    playerInitialX += 5;
                } else {
                    playerInitialX++;
                }
                ambulance.getPolygon().setX(playerInitialX);
                if (entityCollisionWith(ambulance)) {
                    if (turbo) {
                        playerInitialX -= 5;
                    } else {
                        playerInitialX--;
                    }
                    ambulance.getPolygon().setX(playerInitialX);


                }

            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            if (ambulance != null) {
                int current = ambulance.getAnimation().getFrame();
                if (current < 15) {
                    current += 1;
                } else {
                    current = 12;
                }
                ambulance.getAnimation().setCurrentFrame(current);
                if (turbo) {
                    playerInitialY -= 5;
                } else {
                    playerInitialY--;
                }
                ambulance.getPolygon().setY(playerInitialY);
                if (entityCollisionWith(ambulance)) {
                    if (turbo) {
                        playerInitialY += 5;
                    } else {
                        playerInitialY++;
                    }
                    ambulance.getPolygon().setY(playerInitialY);
                }

            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            if (ambulance != null) {
                int current = ambulance.getAnimation().getFrame();
                if (current < 11) {
                    current += 1;
                } else {
                    current = 8;
                }
                ambulance.getAnimation().setCurrentFrame(current);

                if (turbo) {
                    playerInitialY += 5;
                } else {
                    playerInitialY++;
                }
                ambulance.getPolygon().setY(playerInitialY);
                if (entityCollisionWith(ambulance)) {
                    if (turbo) {
                        playerInitialY -= 5;
                    } else {
                        playerInitialY--;
                    }
                    ambulance.getPolygon().setY(playerInitialY);
                }


            }
        }
        if (gc.getInput().isKeyDown(Input.KEY_CAPITAL)) {
            if (turbo) {
                turbo = false;
            } else {
                turbo = true;
            }
        }


        if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
            randomx = (int) (Math.random() * 10) % 7;
            randomy = (int) (Math.random() * 10) % 5;
            if (randomx == 1 && randomy == 25) {
                randomx = 19;
            }
            System.out.println("x = " + xs[randomx] + " -> y =" + ys[randomy]);
            Call call = new Call(randomx, randomy, new Date(System.currentTimeMillis()));
            int callSquare[] = {1, 1, 31, 1, 31, 31, 1, 31};
            BlockMap.emergencies.add(new Block(xs[call.getX()] * 16, ys[call.getY()] * 16, callSquare, "callId:" + call.getId()));
            emergencies.add(EmergencyService.getInstance().newCall(call));

        }
        //if at least have one player
        if (ambulances.size() >= 1) {
            for (Ambulance collisionambulance : ambulances) {
                cornerCollision(collisionambulance);
                emergencyCollision(collisionambulance);
                 if (hospitals.size() >= 1) {
                    hospitalCollision(collisionambulance);
                }
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g)
            throws SlickException {



        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 1, true);

        for (Emergency renderEmergency : emergencies) {
            g.drawAnimation(renderEmergency.getAnimation(), renderEmergency.getPolygon().getX(), renderEmergency.getPolygon().getY());
            g.draw(renderEmergency.getPolygon());
        }


        for (Ambulance renderAmbulance : ambulances) {
            g.drawAnimation(renderAmbulance.getAnimation(), renderAmbulance.getPolygon().getX(), renderAmbulance.getPolygon().getY());
            g.draw(renderAmbulance.getPolygon());
        }
        
        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 2, true);
        
        for (Hospital renderHospitalHightlight : hospitals) {
            g.drawAnimation(renderHospitalHightlight.getAnimation(), renderHospitalHightlight.getPolygon().getX() - 32, renderHospitalHightlight.getPolygon().getY() - 80);
            g.draw(renderHospitalHightlight.getPolygon());
        }

        

    }

    public static void main(String[] args)
            throws SlickException {

        final CityMapUI game = new CityMapUI();
        Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
        Renderer.getLineStripRenderer().setLineCaps(true);
        java.awt.EventQueue.invokeLater(new Runnable()       {

            @Override
            public void run() {
                UserTaskListUI.getInstance().setVisible(true);
                UserTaskListUI.getInstance().setUIMap(game);
            }
        });
        AppGameContainer container =
                new AppGameContainer(game);
        container.start();



    }

    public synchronized boolean entityCollisionWith(Ambulance ambulance) throws SlickException {
        for (int i = 0; i < BlockMap.entities.size(); i++) {
            Block entity1 = (Block) BlockMap.entities.get(i);
            if (ambulance.getPolygon().intersects(entity1.poly)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean emergencyCollision(Ambulance ambulance) throws SlickException {
        for (int i = 0; i < BlockMap.emergencies.size(); i++) {
            Block entity1 = (Block) BlockMap.emergencies.get(i);
            if (ambulance.getPolygon().intersects(entity1.poly) && !EmergencyService.getInstance().getEmergencyReachedNotified().get(ambulance.getId())) {
                EmergencyService.getInstance().getEmergencyReachedNotified().put(ambulance.getId(), true);
                System.out.println("EmergencyService.getInstance().getEmergencyReachedNotified().get(ambulance.getId()) = "+EmergencyService.getInstance().getEmergencyReachedNotified().get(ambulance.getId()));
                
                EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_SELECTED, new MapHospitalSelectedEventNotifier());
                EmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.EMERGENCY_REACHED, ambulance.getId());
                EmergencyService.getInstance().getHospitalReachedNotified().put(ambulance.getId(), false);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hospitalCollision(Ambulance ambulance) throws SlickException {
        for (int i = 0; i < BlockMap.hospitals.size(); i++) {
            Block entity1 = (Block) BlockMap.hospitals.get(i);
            if (ambulance.getPolygon().intersects(entity1.poly) && !EmergencyService.getInstance().getHospitalReachedNotified().get(ambulance.getId())) {
                EmergencyService.getInstance().getHospitalReachedNotified().put(ambulance.getId(), true);
                System.out.println("EmergencyService.getInstance().getHospitalReachedNotified().get(ambulance.getId())"+EmergencyService.getInstance().getHospitalReachedNotified().get(ambulance.getId()));
                
                EmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.HOSPITAL_REACHED, ambulance.getId());
                
                return true;
            }
        }
        return false;
    }

    public boolean cornerCollision(Ambulance ambulance) throws SlickException {
        for (int i = 0; i < BlockMap.corners.size(); i++) {
            Block entity1 = (Block) BlockMap.corners.get(i);
            if (ambulance.getPolygon().intersects(entity1.poly)) {
                EmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.AMBULANCE_POSITION, ambulance.getId());
                return true;
            }
        }
        return false;
    }

    public void addAmbulance(Ambulance ambulance) {
        this.ambulances.add(ambulance);
    }
    
    public void addHospital(Hospital hospital){
        this.hospitals.add(hospital);
    }

    public void removeAmbulance(long id) {
        for(Ambulance ambulance : this.ambulances){
            if(ambulance.getId() == id){
                this.ambulances.remove(ambulance);
            }
        }
    }
    public synchronized void removeEmergency(long id){
         for(Emergency emergency : this.emergencies){
            if(emergency.getId() == id){
                this.emergencies.remove(emergency);
            }
        }
    }
    public synchronized void removeHospital(long id){
         for(Hospital hospital : this.hospitals){
            if(hospital.getId() == id){
                this.hospitals.remove(hospital);
            }
        }
    }
}
