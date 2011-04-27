package com.wordpress.salaboy.emergencyservice.worldui.slick;


import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class DELETE_MECityMapUI extends BasicGame {

    public DELETE_MECityMapUI(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void render(GameContainer gc, Graphics grphcs) throws SlickException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    //Initial Emergency Service Spot
//    private float playerInitialX = 32;
//    private float playerInitialY = 400;
//    // The map itself (created using Tiled)
//    public BlockMap map;
//    // Places for emergencies
//    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
//    private int[] ys = new int[]{1, 7, 13, 19, 25};
//    private int randomx;
//    private int randomy;
//    private List<GraphicableAmbulance> ambulances = new ArrayList<GraphicableAmbulance>();
//    
//    //Call.id -> GraphicableEmergency map
//    private Map<Long,GraphicableEmergency> emergencies = new HashMap<Long, GraphicableEmergency>();
//    private List<GraphicableHighlightedHospital> hospitals = new CopyOnWriteArrayList<GraphicableHighlightedHospital>();
//    private boolean turbo;
//    public static SpriteSheet hospitalSheet;
//    
//    //Commands to be executed inside update() method
//    private List<Command> renderCommands = Collections.synchronizedList(new ArrayList<Command>());
//
//    public CityMapUI() {
//        super("City Map");
//
//    }
//
//    @Override
//    public void init(GameContainer gc)
//            throws SlickException {
//        gc.setVSync(true);
//        gc.setAlwaysRender(true);
//
//        hospitalSheet = new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);
//
//        map = new BlockMap("data/cityMap.tmx");
//
//        map.initializeCorners();
//
//        gc.getInput().addKeyListener(new CityMapKeyListener(this));
//
//        registerMessageConsumers();
//
//    }
//
//    @Override
//    public void update(GameContainer gc, int delta)
//            throws SlickException {
//        //@TODO:  Keys are defined for Player 1 .. add support for multiple controllers        
//        // HACK -> I'm just selecting the first ambulance to bind it to these keys
//        GraphicableAmbulance ambulance = null;
//        if (ambulances.size() >= 1) {
//            ambulance = ambulances.iterator().next();
//        }
//
//        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
//            if (ambulance != null) {
//                int current = ambulance.getAnimation().getFrame();
//                if (current < 7) {
//                    current += 1;
//                } else {
//                    current = 4;
//                }
//
//                ambulance.getAnimation().setCurrentFrame(current);
//
//                if (turbo) {
//                    playerInitialX -= 5;
//                } else {
//                    playerInitialX--;
//                }
//                ambulance.getPolygon().setX(playerInitialX);
//                if (entityCollisionWith(ambulance)) {
//                    if (turbo) {
//                        playerInitialX += 5;
//                    } else {
//                        playerInitialX++;
//                    }
//                    ambulance.getPolygon().setX(playerInitialX);
//                }
//
//            }
//        }
//        if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
//
//            if (ambulance != null) {
//                int current = ambulance.getAnimation().getFrame();
//                if (current < 3) {
//                    current += 1;
//                } else {
//                    current = 0;
//                }
//
//                ambulance.getAnimation().setCurrentFrame(current);
//                if (turbo) {
//                    playerInitialX += 5;
//                } else {
//                    playerInitialX++;
//                }
//                ambulance.getPolygon().setX(playerInitialX);
//                if (entityCollisionWith(ambulance)) {
//                    if (turbo) {
//                        playerInitialX -= 5;
//                    } else {
//                        playerInitialX--;
//                    }
//                    ambulance.getPolygon().setX(playerInitialX);
//
//
//                }
//
//            }
//        }
//        if (gc.getInput().isKeyDown(Input.KEY_UP)) {
//            if (ambulance != null) {
//                int current = ambulance.getAnimation().getFrame();
//                if (current < 15) {
//                    current += 1;
//                } else {
//                    current = 12;
//                }
//                ambulance.getAnimation().setCurrentFrame(current);
//                if (turbo) {
//                    playerInitialY -= 5;
//                } else {
//                    playerInitialY--;
//                }
//                ambulance.getPolygon().setY(playerInitialY);
//                if (entityCollisionWith(ambulance)) {
//                    if (turbo) {
//                        playerInitialY += 5;
//                    } else {
//                        playerInitialY++;
//                    }
//                    ambulance.getPolygon().setY(playerInitialY);
//                }
//
//            }
//        }
//        if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
//            if (ambulance != null) {
//                int current = ambulance.getAnimation().getFrame();
//                if (current < 11) {
//                    current += 1;
//                } else {
//                    current = 8;
//                }
//                ambulance.getAnimation().setCurrentFrame(current);
//
//                if (turbo) {
//                    playerInitialY += 5;
//                } else {
//                    playerInitialY++;
//                }
//                ambulance.getPolygon().setY(playerInitialY);
//                if (entityCollisionWith(ambulance)) {
//                    if (turbo) {
//                        playerInitialY -= 5;
//                    } else {
//                        playerInitialY--;
//                    }
//                    ambulance.getPolygon().setY(playerInitialY);
//                }
//
//
//            }
//        }
//
//        if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
//            randomx = (int) (Math.random() * 10) % 7;
//            randomy = (int) (Math.random() * 10) % 5;
//            if (randomx == 1 && randomy == 25) {
//                randomx = 19;
//            }
//            System.out.println("x = " + xs[randomx] + " -> y =" + ys[randomy]);
//            Call call = new Call(randomx, randomy, new Date(System.currentTimeMillis()));
//            int callSquare[] = {1, 1, 31, 1, 31, 31, 1, 31};
//            BlockMap.emergencies.add(new Block(xs[call.getX()] * 16, ys[call.getY()] * 16, callSquare, "callId:" + call.getId()));
//            emergencies.put(call.getId(),GraphicableFactory.newEmergency(call));
//
//            try {
//                MessageProducer messageProducer = MessageFactory.createMessageProducer("phoneCalls");
//                messageProducer.sendMessage(new IncomingCallMessage(call));
//                messageProducer.stop();
//            } catch (HornetQException ex) {
//                Logger.getLogger(CityMapUI.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//
//        //if at least have one player
//        if (ambulances.size() >= 1) {
//            for (GraphicableAmbulance collisionambulance : ambulances) {
//                cornerCollision(collisionambulance);
//                emergencyCollision(collisionambulance);
//                if (hospitals.size() >= 1) {
//                    hospitalCollision(collisionambulance);
//                }
//            }
//        }
//        
//    }
//
//    @Override
//    public void render(GameContainer gc, Graphics g)
//            throws SlickException {
//
//        //Execute any renderCommands
//        for (Command command : this.renderCommands) {
//            command.execute();
//        }
//        //clear the renderCommands list
//        renderCommands.clear();
//
//        for (GraphicableAmbulance renderAmbulance : ambulances) {
//            g.draw(renderAmbulance.getPolygon());
//        }
//
//        for (GraphicableEmergency renderEmergency : emergencies.values()) {
//            g.draw(renderEmergency.getPolygon());
//        }
//
//        for (GraphicableHighlightedHospital renderHospitalHightlight : hospitals) {
//            g.draw(renderHospitalHightlight.getPolygon());
//        }
//
//        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 1, true);
//
//        for (GraphicableEmergency renderEmergency : emergencies.values()) {
//            g.drawAnimation(renderEmergency.getAnimation(), renderEmergency.getPolygon().getX(), renderEmergency.getPolygon().getY());
//        }
//
//
//        for (GraphicableAmbulance renderAmbulance : ambulances) {
//            g.drawAnimation(renderAmbulance.getAnimation(), renderAmbulance.getPolygon().getX(), renderAmbulance.getPolygon().getY());
//        }
//
//        BlockMap.tmap.render(0, 0, 0, 0, BlockMap.tmap.getWidth(), BlockMap.tmap.getHeight(), 2, true);
//
//        for (GraphicableHighlightedHospital renderHospitalHightlight : hospitals) {
//            g.drawAnimation(renderHospitalHightlight.getAnimation(), renderHospitalHightlight.getPolygon().getX() - 32, renderHospitalHightlight.getPolygon().getY() - 80);
//        }
//
//    }
//
//    public static void main(String[] args)
//            throws SlickException {
//
//        final CityMapUI game = new CityMapUI();
//        Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
//        Renderer.getLineStripRenderer().setLineCaps(true);
//
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
////                UserTaskListUI.getInstance().setVisible(true);
////                UserTaskListUI.getInstance().setUIMap(game);
//            }
//        });
//
//        AppGameContainer container =
//                new AppGameContainer(game);
//        container.start();
//
//        System.out.println(" ->  -> -> Displaying City!");
//
//
//    }
//
//    public synchronized boolean entityCollisionWith(GraphicableAmbulance ambulance) throws SlickException {
//        for (int i = 0; i < BlockMap.entities.size(); i++) {
//            Block entity1 = (Block) BlockMap.entities.get(i);
//            if (ambulance.getPolygon().intersects(entity1.poly)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public synchronized boolean emergencyCollision(GraphicableAmbulance ambulance) throws SlickException {
//        for (int i = 0; i < BlockMap.emergencies.size(); i++) {
//            Block entity1 = (Block) BlockMap.emergencies.get(i);
//            if (ambulance.getPolygon().intersects(entity1.poly) && !GridEmergencyService.getInstance().getEmergencyReachedNotified().get(ambulance.getAmbulance().getId())) {
//                GridEmergencyService.getInstance().getEmergencyReachedNotified().put(ambulance.getAmbulance().getId(), true);
//                GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_SELECTED, new MapHospitalSelectedEventNotifier());
//                GridEmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.EMERGENCY_REACHED, new EmergencyReachedNotifierEvent(ambulance.getAmbulance().getId()));
//                GridEmergencyService.getInstance().getHospitalReachedNotified().put(ambulance.getAmbulance().getId(), false);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public synchronized boolean hospitalCollision(GraphicableAmbulance ambulance) throws SlickException {
//        for (int i = 0; i < BlockMap.hospitals.size(); i++) {
//            Block entity1 = (Block) BlockMap.hospitals.get(i);
//            if (ambulance.getPolygon().intersects(entity1.poly) && !GridEmergencyService.getInstance().getHospitalReachedNotified().get(ambulance.getAmbulance().getId())) {
//                GridEmergencyService.getInstance().getHospitalReachedNotified().put(ambulance.getAmbulance().getId(), true);
//                GridEmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.HOSPITAL_REACHED, new HospitalReachedNotifierEvent(ambulance.getAmbulance().getId(), null));
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean cornerCollision(GraphicableAmbulance ambulance) throws SlickException {
//        for (int i = 0; i < BlockMap.corners.size(); i++) {
//            Block entity1 = (Block) BlockMap.corners.get(i);
//            if (ambulance.getPolygon().intersects(entity1.poly)) {
//                GridEmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.AMBULANCE_POSITION, new PositionUpdatedNotifierEvent(ambulance.getAmbulance().getId()));
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void addAmbulance(GraphicableAmbulance ambulance) {
//        this.ambulances.add(ambulance);
//    }
//
//    public void addHospital(GraphicableHighlightedHospital hospital) {
//        this.hospitals.add(hospital);
//    }
//
//    public void removeAmbulance(long id) {
//        for (GraphicableAmbulance ambulance : this.ambulances) {
//            if (ambulance.getAmbulance().getId().compareTo(id) == 0) {
//                this.ambulances.remove(ambulance);
//            }
//        }
//    }
//
//    public synchronized void removeEmergency(long id) {
//        for (GraphicableEmergency emergency : this.emergencies.values()) {
//            //TODO: GraphicalbeEmergency no longer has the emergency as attribute
//            //if(emergency.getEmergency().getId().compareTo(id) == 0 ){
//            //    this.emergencies.remove(emergency);
//            //}
//            throw new IllegalStateException("TODO: GraphicalbeEmergency no longer has the emergency as attribute");
//        }
//    }
//
//    public synchronized void removeHospital(long id) {
//        for (GraphicableHighlightedHospital hospital : this.hospitals) {
//            if (hospital.getHospital().getId().compareTo(id) == 0) {
//                this.hospitals.remove(hospital);
//            }
//        }
//    }
//
//    public GraphicableAmbulance getGraphicableAmbulanceById(Long id) {
//
//        for (GraphicableAmbulance ambulancenow : ambulances) {
//            if (ambulancenow.getAmbulance().getId().compareTo(id) == 0) {
//                return ambulancenow;
//            }
//        }
//        return null;
//    }
//
//    public void setTurbo(boolean turbo) {
//        this.turbo = turbo;
//    }
//
//    private void registerMessageConsumers() {
//        //worldMessages queue
//        MessageConsumerWorker phoneCallsWorker = new MessageConsumerWorker("worldMessages", new MessageConsumerWorkerHandler<EmergencyDetailsMessage>() {
//
//            @Override
//            public void handleMessage(final EmergencyDetailsMessage message) {
//                //Changes emergency animation
//                renderCommands.add(new Command() {
//                    public void execute() {
//                        System.out.println("EmergencyDetailsMessage received");
//                        emergencies.get(message.getCallId()).setAnimation(AnimationFactory.getEmergencyAnimation(message.getType(), message.getNumberOfPeople()));
//                    }
//                });
//            }
//        });
//
//        phoneCallsWorker.start();
//    }
}
