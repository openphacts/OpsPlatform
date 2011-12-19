package eu.ops.plugin.irssparqlexpand;

import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
/**
 *
 * @author Christian
 */
public class ReplacementVariableFinderVisitor extends QueryModelVisitorBase<UnexpectedQueryException>{
   
   int statements;
   ArrayList<String> variables = new ArrayList<String>();
   
   ReplacementVariableFinderVisitor(int startStatements){
       statements = startStatements;
   }
    
   private URI findURI(Var var){
        if (var.hasValue()){
            Value value = var.getValue();
            if (value instanceof URI){
                return (URI)value;
            }
        }
        return null;        
    }
    
    //@Override
    public void meet(StatementPattern sp) {
        statements++;
        URI subjectURI = findURI(sp.getSubjectVar());
        if (subjectURI != null) {
            variables.add("?subjectUri" + statements);
        } 
        URI objectURI = findURI(sp.getObjectVar());
        if (objectURI != null) {
            variables.add("?objectUri" + statements);
        } 
    }
    
    public List<String> getVariables(){
        return this.variables;
    }
}
