package eu.ops.plugin.imssparqlexpand;

import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
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
public class ContextFinderVisitor extends QueryModelVisitorBase<QueryExpansionException>{
    
    private Var context = null;
    private boolean nullContext = false;
    private boolean multipleContexts = false;
    
    /**
     * Blocks public access. Use static Var getContext(TupleExpr) method.
     */
    private ContextFinderVisitor(){
    }
        
    @Override
    public void meet(StatementPattern sp) throws QueryExpansionException {
        if (multipleContexts) {
            return;
        }
        //ystem.out.println(sp);
        Var localContext = sp.getContextVar();
        if (localContext == null){
            nullContext = true;
        } else {
            if (context == null){
                context = localContext;
            } else {
                multipleContexts = !(context.equals(localContext));
            }
        }
    }

    /**
     * Avoids that a a SingletonSet is considered part of a graph.
     * 
     * The only time a Singletonset is used is an Optional which has no matching none Optional part.
     * In this case the Filters need to be added inside the Optional.
     * By setting multipleContexts to true this avoids that the Filters will be added outside of the optional.
     * 
     * @param set
     * @throws QueryExpansionException 
     */
    public void meet(SingletonSet set) throws QueryExpansionException {
        //ystem.out.println("SingletonSet");
        multipleContexts = true;
    }
    
    /**
     * Obtrains the single context from a query or sub query.
     * <p>
     * As the OpenRdf parse assigns two indices of equal literals different names, 
     *     this class considers then as unequal.
     * 
     * @return The context is all Statements in this query or subquery have the same none null Context. 
     *    Otherwise null.
     */
    private Var getContext(){
        if (multipleContexts) {
            //ystem.out.println("Multiple Contexts");
            return null;
        } 
        if (nullContext) {
            //ystem.out.println("null");
            return null;
        }
        //ystem.out.println(context);
        return context;
    }
    
    public static Var getContext(TupleExpr tupleExpr) throws QueryExpansionException{
        ContextFinderVisitor visitor = new ContextFinderVisitor();
        tupleExpr.visit(visitor);
        return visitor.getContext();
    }
}
