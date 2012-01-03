package eu.ops.plugin.irssparqlexpand;

import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 * This class preforms a look ahead to see if a single context is shared by the sub tree.
 * 
 * @author Christian
 */
public class ContextFinderVisitor extends QueryModelVisitorBase<QueryExpansionException>{
    
    private Var context = null;
    private boolean nullContext = false;
    private boolean multipleContexts = false;
        
    @Override
    public void meet(StatementPattern sp) throws QueryExpansionException {
        //ystem.out.println(sp);
        Var localContext = sp.getContextVar();
        System.out.println(localContext);
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
     * Obtrains the single context from a query or sub query.
     * <p>
     * As the OpenRdf parse assigns two indices of equal literals different names, 
     *     this class considers then as unequal.
     * 
     * @return The context is all Statements in this query or subquery have the same none null Context. 
     *    Otherwise null.
     */
    public Var getContext(){
        if (multipleContexts) {
            //ystem.out.println("Multiple Contexts");
            return null;
        } 
        if (nullContext) {
            //ystem.out.println("null");
            return null;
        }
        return context;
    }
}
