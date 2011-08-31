/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import scala.collection.Iterator;

/**
 *
 * @author salaboy
 */
public class ContextTrackingSimpleGraphServiceImpl implements ContextTrackingGraphService {

    private GraphDatabaseService graphDb;

    public ContextTrackingSimpleGraphServiceImpl(GraphDatabaseService db) {
        this.graphDb = db;
    }

    @Override
    public String graphEmergency(String emergencyId) {
        Transaction tx = this.graphDb.beginTx();
        String resultString = "";
        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(emergencies, 'emergencyId:" + emergencyId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node emergency = n_column.next();
            resultString += emergency.getProperty("name") + "\n";
            for (Relationship rel : emergency.getRelationships(Direction.OUTGOING)) {
                resultString += graphRelationships( rel);
            }
            resultString += "--";

            tx.success();
        } finally {
            tx.finish();

        }

        return resultString;
    }

    private String graphRelationships( Relationship rel) {
        String resultString = "";
        resultString += rel.getType().toString() + "\n";
        resultString += rel.getEndNode().getProperty("name") + "\n";
        for (Relationship rel2 : rel.getEndNode().getRelationships(Direction.OUTGOING)) {
            resultString += graphRelationships(rel2);
        }
        return resultString;
    }
}
