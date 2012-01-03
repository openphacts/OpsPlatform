package eu.ops.plugin.irssparqlexpand;

import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
/**
 *
 * @author Christian
 */
public class ContextFinderVisitor extends QueryModelVisitorBase<UnexpectedQueryException>{
    
    private Var context = null;
    private boolean nullContext = false;
    private boolean multipleContexts = false;
        
    @Override
    public void meet(StatementPattern sp) throws UnexpectedQueryException {
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
