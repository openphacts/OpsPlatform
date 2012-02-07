package eu.ops.plugin.imssparqlexpand;

import java.util.ArrayList;
import java.util.HashMap;
import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 * The purpose of this visitor is to dettermine if the whole subtree has a single non null graph.
 * <p>
 * Dettermining if a whole subtree has a single non null graph is required for two reasons.
 * <ol>
 * <li>Placing a Single Graph statement around the whole block.</li>
 * <li>Placing a single block of filters just inside the whole block.</li> 
 * </ol
 * 
 * @author Christian
 */
public class ContextListerVisitor extends QueryModelVisitorBase<QueryExpansionException>{

    private ArrayList<Var> contexts = new ArrayList<Var>();
        
    @Override
    public void meet(StatementPattern sp) throws QueryExpansionException {
        contexts.add(sp.getContextVar());
    }

    public ArrayList<Var> getContexts(){
        return contexts;
    }
}
