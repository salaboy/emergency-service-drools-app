package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.emergencyservice.worldui.slick.listener.WorldMouseListener;
import com.wordpress.salaboy.emergencyservice.worldui.slick.listener.WorldKeyListener;
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
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.renderer.Renderer;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FirefightersDepartment;
import com.wordpress.salaboy.model.command.Command;
import com.wordpress.salaboy.model.messages.*;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldUI extends BasicGame {

    // The map itself (created using Tiled)
    public BlockMap map;
    // Places for emergencies
    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private int[] ys = new int[]{1, 7, 13, 19, 25};
    private Map<String, GraphicableEmergency> emergencies = new HashMap<String, GraphicableEmergency>();
    public static SpriteSheet hospitalSheet;
    private List<Command> renderCommands = Collections.synchronizedList(new ArrayList<Command>());
    private EmergencyRenderer currentRenderer;
    private GlobalEmergenciesRenderer globalRenderer;
    private Map<String, ParticularEmergencyRenderer> renderers = new HashMap<String, ParticularEmergencyRenderer>();
    private PersistenceService persistenceService;
    private ContextTrackingService trackingService;

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
        
        persistenceService = PersistenceServiceProvider.getPersistenceService();
        trackingService = ContextTrackingProvider.getTrackingService();
        
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

    public synchronized void removeEmergency(String callId) {
        this.emergencies.remove(callId);
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
                        if (emergencies.get(message.getEmergency().getCall().getId())==null){
                            System.out.println("Unknown emergency for call "+message.getEmergency().getCall().getId());
                            return;
                        }
                        emergencies.get(message.getEmergency().getCall().getId()).setAnimation(AnimationFactory.getEmergencyAnimation(message.getType(), message.getNumberOfPeople()));
                    }
                });
            }
        });
        
        MessageConsumerWorker vehicleDispatchedWorker = new MessageConsumerWorker("vehicleDispatchedWorldUI", new MessageConsumerWorkerHandler<VehicleDispatchedMessage>() {

            @Override
            public void handleMessage(final VehicleDispatchedMessage message) {
                String callId = trackingService.getCallAttachedToEmergency(message.getEmergencyId());
                
                if (callId == null){
                    throw new IllegalArgumentException("No Call attached to Emergency "+message.getEmergencyId());
                }
                
                if (emergencies.get(callId)==null){
                    throw new IllegalArgumentException("Unknown emergency for call Id "+callId);
                }
                
                Vehicle vehicle = persistenceService.loadVehicle(message.getVehicleId());
                switchToEmergency(callId);
                assignVehicleToEmergency(callId, vehicle);
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
        
        MessageConsumerWorker firefigthersDepartmentWorker = new MessageConsumerWorker("firefigthersDepartmentWorldUI", new MessageConsumerWorkerHandler<FirefightersDepartmentSelectedMessage>() {

            @Override
            public void handleMessage(final FirefightersDepartmentSelectedMessage message) {
                //Changes emergency animation
                renderCommands.add(new Command() {

                    public void execute() {
                       
                        if (emergencies.get(message.getCallId())==null){
                            System.out.println("Unknown emergency for call Id "+message.getCallId());
                            return;
                        }
                        selectFirefighterDepartmentForEmergency(message.getCallId(), message.getFirefightersDepartment());
                        
                    }

                });
            }
        });
        
        hospitalSelectedWorker.start();
        emergencyDetailsWorker.start();
        vehicleDispatchedWorker.start();
        firefigthersDepartmentWorker.start();
    }

    
    public void addRandomGenericEmergency() throws IOException {
        this.addRandomEmergency(Emergency.EmergencyType.UNDEFINED, null);

    }
    
    public void addRandomEmergency(Emergency.EmergencyType emergencyType, Integer numberOfPeople) throws IOException {
        
        int randomx = 0;
        int randomy = 0;
        
        boolean isFreeSpace = false;
        //Avoid duplicated places
        do{
            randomx = (int) (Math.random() * 10) % 7;
            randomy = (int) (Math.random() * 10) % 5;
            if (randomx == 0 && randomy == 4) {
                randomx = 4;
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
        
        PersistenceServiceProvider.getPersistenceService().storeCall(call); 
        
        int callSquare[] = {1, 1, 31, 1, 31, 31, 1, 31};
        BlockMap.emergencies.add(new Block(xs[call.getX()] * 16, ys[call.getY()] * 16, callSquare, "callId:" + call.getId()));
        
        GraphicableEmergency newEmergency = null;
        
        if (emergencyType == Emergency.EmergencyType.UNDEFINED){
            newEmergency = GraphicableFactory.newGenericEmergency(call);
        }else{
            newEmergency = GraphicableFactory.newEmergency(call, emergencyType, numberOfPeople);
        }
        
        emergencies.put(call.getId(), newEmergency);

        renderers.put(call.getId(), new ParticularEmergencyRenderer(this,newEmergency));
        
        try {
            MessageFactory.sendMessage(new IncomingCallMessage(call));
        } catch (HornetQException ex) {
            Logger.getLogger(WorldUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, GraphicableEmergency> getEmergencies() {
        return emergencies;
    }

    public void emergencyClicked(String callId) {
        this.switchToEmergency(callId);
    }
    
    private void switchToEmergency(String callId){
        this.currentRenderer = renderers.get(callId);
    }
    
    public void goToGlobalMap(){
        this.currentRenderer = this.globalRenderer;
    }
    
    public void assignVehicleToEmergency(String callId, Vehicle vehicle){
        this.renderers.get(callId).addVehicle(vehicle);
    }

    public EmergencyRenderer getCurrentRenderer() {
        return currentRenderer;
    }
    
    public void selectHospitalForEmergency(String callId, Hospital hospital) {
        this.renderers.get(callId).selectHospital(hospital);               
    }
    
    public void selectFirefighterDepartmentForEmergency(String callId, FirefightersDepartment firefigthersDepartment) {
        this.renderers.get(callId).selectFirefighterDepartment(firefigthersDepartment);               
    }

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public ContextTrackingService getTrackingService() {
        return trackingService;
    }

    public void addRenderCommand(Command element) {
        renderCommands.add(element);
    }
    
    
    
}