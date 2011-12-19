package eu.ops.plugin.irssparqlexpand;

/**
 * This is thrown if a valid sparql query can not be handed by the existing IRS code.
 * 
 * @author Christian
 */
public class UnexpectedQueryException extends QueryExpansionException {

    /**
     * Constructs an instance of <code>UnexpectedQueryException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnexpectedQueryException(String msg) {
        super(msg);
    }

    public UnexpectedQueryException(String msg, Exception ex) {
        super(msg, ex);
    }
}
