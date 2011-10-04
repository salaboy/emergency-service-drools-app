/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.context.tracking;

import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author salaboy
 */
public enum EmergencyRelationshipType implements RelationshipType {
    CREATES, INSTANTIATE, USE, CONSUME
}
