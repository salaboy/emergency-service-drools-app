/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.oupls.jung.GraphJung;



import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import java.awt.Dimension;
import java.util.UUID;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.ImpermanentGraphDatabase;




/**
 *
 * @author salaboy
 */
public class GraphTest {

    public enum MyRelationshipTypes implements RelationshipType {

        KNOWS, CREATES, INSTANCIATE, USE
    }
    public static void main(String[] args) {
        
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase("var/base");
        Transaction tx = graphDb.beginTx();
        Node call = null;
        String vehicle2Id = "";
        try {
            call = graphDb.createNode();

            call.setProperty("callId", "Call:" + UUID.randomUUID().toString());
            call.setProperty("name","Call:" + UUID.randomUUID().toString());
            call.setProperty("phoneNumber", "555-1234");
            Node emergency = graphDb.createNode();
            emergency.setProperty("emergencyId", "Emergency:" + UUID.randomUUID().toString());
            emergency.setProperty("name", "Emergency:" + UUID.randomUUID().toString());
            Relationship callEmergencyRelationship = call.createRelationshipTo(emergency, MyRelationshipTypes.CREATES);
            callEmergencyRelationship.setProperty("time", System.currentTimeMillis());

            Node procedureOne = graphDb.createNode();
            procedureOne.setProperty("procedureName", "DefaultHeartAttack");
            procedureOne.setProperty("procedureId", "Procedure:" + UUID.randomUUID().toString());
            procedureOne.setProperty("name", "Procedure:" + UUID.randomUUID().toString());

            Relationship emergencyProcedureOneRelationship = emergency.createRelationshipTo(procedureOne, MyRelationshipTypes.INSTANCIATE);

            Node procedureTwo = graphDb.createNode();
            procedureTwo.setProperty("procedureName", "GenericBankRobbery");
            procedureTwo.setProperty("procedureId", "Procedure:" + UUID.randomUUID().toString());
            procedureTwo.setProperty("name", "Procedure:" + UUID.randomUUID().toString());

            Relationship emergencyProcedureTwoRelationship = emergency.createRelationshipTo(procedureTwo, MyRelationshipTypes.INSTANCIATE);

            Node vehicleOne = graphDb.createNode();
            vehicleOne.setProperty("vehicleId", "Vehicle:" + UUID.randomUUID().toString());
            vehicleOne.setProperty("name", "Vehicle:" + UUID.randomUUID().toString());
            vehicleOne.setProperty("vehicleType", "Ambulance");

            Relationship procedureVehicleOne = procedureOne.createRelationshipTo(vehicleOne, MyRelationshipTypes.USE);

            Node vehicleTwo = graphDb.createNode();
            vehicleTwo.setProperty("vehicleId", "Vehicle:" + UUID.randomUUID().toString());
            vehicleTwo.setProperty("name", "Vehicle:" + UUID.randomUUID().toString());
            vehicleTwo.setProperty("vehicleType", "Police Car");
            vehicle2Id = (String) vehicleTwo.getProperty("vehicleId");
            Relationship procedureVehicleTwo = procedureOne.createRelationshipTo(vehicleTwo, MyRelationshipTypes.USE);


            tx.success();

//
//            


        } finally {
            tx.finish();
            
        }
        Graph graphStandard = new Neo4jGraph(graphDb);
        GraphJung graph = new GraphJung(graphStandard);
        
        Layout<Vertex, Edge> layout =  new  FRLayout2<Vertex, Edge>(graph);
        layout.setSize(new Dimension(400, 400));
        BasicVisualizationServer<Vertex, Edge> viz = new BasicVisualizationServer<Vertex, Edge>(layout);
        viz.setPreferredSize(new Dimension(450, 450));

        Transformer<Vertex, String> vertexLabelTransformer = new Transformer<Vertex, String>() {

            public String transform(Vertex vertex) {
                return (String) vertex.getProperty("name");
            }
        };

        Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {

            public String transform(Edge edge) {
                return edge.getLabel();
            }
        };

        viz.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);
        viz.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);

        JFrame frame = new JFrame("TinkerPop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(viz);
        frame.pack();
        frame.setVisible(true);
    }
}
