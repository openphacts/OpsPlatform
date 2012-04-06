package eu.ops.plugin.imssparqlexpand.querywriter;

/**
 * Represents the different types that a SPARQL query can have.
 * <p>
 * Used by the QueryWriterModelVisitor to establish what type of query it is dealing with and therefor what to write.
 * @author Christian
 */
public enum QueryType {

    SELECT, CONSTRUCT, ASK, DESCRIBE
}
