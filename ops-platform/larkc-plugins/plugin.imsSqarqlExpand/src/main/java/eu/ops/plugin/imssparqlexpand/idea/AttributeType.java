/*
 * Early work in progress for trying to find which part of the query to keep.
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
