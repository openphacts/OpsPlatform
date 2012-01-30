/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.imssparqlexpand.idea;

/**
 *
 * @author Christian
 */
public enum AttributeType {
    LITTERAL, 
    PROJECTION, //Specific Projection
    EXISTENTIAL, //Specific Existential
    DELETED, //Specifically Deleted 
    NOTKEPT, //Not
    REMOVE,
    REMOVEABLE, UNDETTERMINED;
}
