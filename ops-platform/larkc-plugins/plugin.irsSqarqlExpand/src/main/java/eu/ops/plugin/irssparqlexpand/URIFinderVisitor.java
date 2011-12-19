package eu.ops.plugin.irssparqlexpand;

import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
/**
 *
 * @author Christian
 */
public class URIFinderVisitor extends QueryModelVisitorBase<QueryModelExpanderException>{
    
    HashSet<URI> uris = new HashSet<URI>();
        
    @Override
    public void meet(Var var){
         if (var.hasValue()){
            Value value = var.getValue();
            findUri(value);
        } 
    }
    
    @Override
    public void meet(ValueConstant vc) {
        Value value = vc.getValue();
        findUri(value);
    }
    
    @Override
    public void meet(StatementPattern sp) throws QueryModelExpanderException {
        sp.getSubjectVar().visit(this);
        //We don't want the predicate URIs so don't visit predicated
        //sp.getPredicateVar().visit(this);
        sp.getObjectVar().visit(this);
    }

    private void findUri(Value value){
        if (value instanceof URI){
            uris.add((URI)value);
        }
    }

    public Set<URI> getURIS(){
        return uris;
    }
}
