package eu.ops.plugin.irssparqlexpand.version1;
import eu.ops.plugin.imssparqlexpand.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
/**
 * Old code park for paper writing.
 * @author Christian
 */
public class ReplacementVariableFinderVisitor extends QueryModelVisitorBase<QueryExpansionException>{
   
   int statements;
   Map<URI, List<URI>> uriMappings;
   ArrayList<String> variables = new ArrayList<String>();
   
   ReplacementVariableFinderVisitor(int startStatements, Map<URI, List<URI>> uriMappings){
       statements = startStatements;
       this.uriMappings = uriMappings;
   }
    
    private URI findMultipleMappedURI(ValueExpr valueExpr){
        Value value = null;
        if (valueExpr instanceof Var) {
            Var var = (Var)valueExpr;
            ValueConstant constant;
            if (var.hasValue()){
                value = var.getValue();
            }
        } else if (valueExpr instanceof ValueConstant){
            value = ((ValueConstant)valueExpr).getValue();
        }
        if (value == null) return null;
        Var var = (Var)valueExpr;
        if (value instanceof URI){
            URI uri = (URI) value;
            List<URI> uriList = getMappedList(uri);
            //Only care it is a URI if there is more than one mapping
            if (uriList.size() > 1){
               return uri;
            } 
        }
        return null;        
    }
    
    private List<URI> getMappedList(URI uri){
        List<URI> uriList = uriMappings.get(uri);
        if (uriList == null){
            throw new Error("Query has URI " + uri + " but it has no mapped set.");
        }
        //ystem.out.println(uriList);
        if (uriList.isEmpty()){
            throw new Error("Query has URI " + uri + " but mapped set is empty.");
        }
        return uriList;
    }

    //@Override
    public void meet(StatementPattern sp) {
        statements++;
        URI subjectURI = findMultipleMappedURI(sp.getSubjectVar());
        if (subjectURI != null) {
            variables.add("?subjectUri" + statements);
        } 
        URI objectURI = findMultipleMappedURI(sp.getObjectVar());
        if (objectURI != null) {
            variables.add("?objectUri" + statements);
        } 
    }
    
    public List<String> getVariables(){
        return this.variables;
    }
}
