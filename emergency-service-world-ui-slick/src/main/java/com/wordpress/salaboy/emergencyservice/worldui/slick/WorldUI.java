package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.AnimationFactory;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableFactory;
import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Vehicle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.renderer.Renderer;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.command.Command;
import com.wordpress.salaboy.model.messages.EmergencyDetailsMessage;
import com.wordpress.salaboy.model.messages.HospitalSelectedMessage;
import com.wordpress.salaboy.model.messages.IncomingCallMessage;
import com.wordpress.salaboy.model.messages.VehicleDispatchedMessage;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldUI extends BasicGame {

    // The map itself (created using Tiled)
    public BlockMap map;
    // Places for emergencies
    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private int[] ys = new int[]{1, 7, 13, 19, 25};
    private Map<Long, GraphicableEmergency> emergencies = new HashMap<Long, GraphicableEmergency>();
    public static SpriteSheet hospitalSheet;
    private List<Command> renderCommands = Collections.synchronizedList(new ArrayList<Command>());
    private EmergencyRenderer currentRenderer;
    private GlobalEmergenciesRenderer globalRenderer;
    private Map<Long, ParticularEmergencyRenderer> renderers = new HashMap<Long, ParticularEmergencyRenderer>();

    public WorldUI() {
        super("City Map");
        this.globalRenderer = new GlobalEmergenciesRenderer(this);
        this.currentRenderer = globalRenderer;
    }

    @Override
    public void init(GameContainer gc)
            throws SlickException {
        gc.setVSync(true);
        gc.setAlwaysRender(true);

       // hospitalSheet = new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);

        map = new BlockMap("data/cityMap.tmx");

        map.initializeCorners();
        
        AnimationFactory.getAmbulanceSpriteSheet();
        AnimationFactory.getFireTruckSpriteSheet();
        AnimationFactory.getPoliceCarSpriteSheet();
        
        gc.getInput().addKeyListener(new WorldKeyListener(this));
        gc.getInput().addMouseListener(new WorldMouseListener(this));
        
        //Initializing Distribtued Persistence Service
        DistributedPeristenceServerService.getInstance();
        
        registerMessageConsumers();

    }

    @Override
    public void update(GameContainer gc, int delta)
            throws SlickException {
        this.currentRenderer.update(gc, delta);
    }

    @Override
    public void render(GameContainer gc, Graphics g)
            throws SlickException {

        //Execute any renderCommands
        for (Command command : this.renderCommands) {
            command.execute();
        }
        //clear the renderCommands list
        renderCommands.clear();

        this.currentRenderer.renderPolygon(gc, g);

        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 1, true);

        this.currentRenderer.renderAnimation(gc, g);

        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 2, true);

    }

    public static void main(String[] args)
            throws SlickException {

        final WorldUI game = new WorldUI();
        Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
        Renderer.getLineStripRenderer().setLineCaps(true);

        AppGameContainer container =
                new AppGameContainer(game);
        container.start();

        System.out.println(" ->  -> -> Displaying City!");


    }

    public synchronized void removeEmergency(long id) {
        for (GraphicableEmergency emergency : this.emergencies.values()) {
            //TODO: GraphicalbeEmergency no longer has the emergency as attribute
            //if(emergency.getEmergency().getId().compareTo(id) == 0 ){
            //    this.emergencies.remove(emergency);
            //}
            throw new IllegalStateException("TODO: GraphicalbeEmergency no longer has the emergency as attribute");
        }
    }

    private void registerMessageConsumers() {
        //worldMessages queue
        MessageConsumerWorker emergencyDetailsWorker = new MessageConsumerWorker("emergencyDetailsWorldUI", new MessageConsumerWorkerHandler<EmergencyDetailsMessage>() {

            @Override
            public void handleMessage(final EmergencyDetailsMessage message) {
                //Changes emergency animation
                renderCommands.add(new Command() {

                    public void execute() {
                        System.out.println("EmergencyDetailsMessage received");
                        if (emergencies.get(message.getCallId())==null){
                            System.out.println("Unknown emergency for call Id "+message.getCallId());
                            return;
                        }
                        emergencies.get(message.getCallId()).setAnimation(AnimationFactory.getEmergencyAnimation(message.getType(), message.getNumberOfPeople()));
                    }
                });
            }
        });
        
        MessageConsumerWorker vehicleDispatchedWorker = new MessageConsumerWorker("vehicleDispatchedWorldUI", new MessageConsumerWorkerHandler<VehicleDispatchedMessage>() {

            @Override
            public void handleMessage(final VehicleDispatchedMessage message) {
                if (emergencies.get(message.getCallId())==null){
                    System.out.println("Unknown emergency for call Id "+message.getCallId());
                    return;
                }
                Vehicle vehicle = DistributedPeristenceServerService.getInstance().loadVehicle(message.getVehicleId());
                switchToEmergency(message.getCallId());
                assignVehicleToEmergency(message.getCallId(), vehicle);
            }
        });

        
        MessageConsumerWorker hospitalSelectedWorker = new MessageConsumerWorker("hospitalSelectedWorldUI", new MessageConsumerWorkerHandler<HospitalSelectedMessage>() {

            @Override
            public void handleMessage(final HospitalSelectedMessage message) {
                //Changes emergency animation
                renderCommands.add(new Command() {

                    public void execute() {
                       
                        if (emergencies.get(message.getCallId())==null){
                            System.out.println("Unknown emergency for call Id "+message.getCallId());
                            return;
                        }
                        selectHospitalForEmergency(message.getCallId(), message.getHospital());
                        
                    }

                });
            }
        });
        hospitalSelectedWorker.start();
        emergencyDetailsWorker.start();
        vehicleDispatchedWorker.start();
    }

    public void addRandomEmergency() {
        
        int randomx = 0;
        int randomy = 0;
        
        boolean isFreeSpace = false;
        //Avoid duplicated places
        do{
            randomx = (int) (Math.random() * 10) % 7;
            randomy = (int) (Math.random() * 10) % 5;
            if (randomx == 1 && randomy == 25) {
                randomx = 19;
            }

            isFreeSpace = true;
            for (GraphicableEmergency graphicableEmergency : emergencies.values()) {
                if (graphicableEmergency.getCallX() == randomx && graphicableEmergency.getCallY() == randomy){
                   isFreeSpace = false; 
                }
            }
        } while (!isFreeSpace);
        
        System.out.println("x = " + xs[randomx] + " -> y =" + ys[randomy]);
        Call call = new Call(randomx, randomy, new Date(System.currentTimeMillis()));
        int callSquare[] = {1, 1, 31, 1, 31, 31, 1, 31};
        BlockMap.emergencies.add(new Block(xs[call.getX()] * 16, ys[call.getY()] * 16, callSquare, "callId:" + call.getId()));
        
        GraphicableEmergency newEmergency = GraphicableFactory.newEmergency(call);
        emergencies.put(call.getId(), newEmergency);

        renderers.put(call.getId(), new ParticularEmergencyRenderer(this,newEmergency));
        
        try {
            MessageFactory.sendMessage(new IncomingCallMessage(call));
        } catch (HornetQException ex) {
            Logger.getLogger(WorldUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<Long, GraphicableEmergency> getEmergencies() {
        return emergencies;
    }

    public void emergencyClicked(Long callId) {
        this.switchToEmergency(callId);
    }
    
    private void switchToEmergency(Long callId){
        this.currentRenderer = renderers.get(callId);
    }
    
    public void goToGlobalMap(){
        this.currentRenderer = this.globalRenderer;
    }
    
    public void assignVehicleToEmergency(long callId, Vehicle vehicle){
        this.renderers.get(callId).addVehicle(vehicle);
    }

    public EmergencyRenderer getCurrentRenderer() {
        return currentRenderer;
    }
    
    public void selectHospitalForEmergency(Long callId, Hospital hospital) {
        this.renderers.get(callId).selectHospital(hospital);               
    }
    
    
}