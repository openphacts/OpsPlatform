/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import java.util.List;
import java.util.Map;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Var;

/**
 *
 * @author Christian
 */
public class QueryExpandAndWriteVisitor extends QueryWriterModelVisitor{

    Map<URI, List<URI>> uriMappings;
    boolean showExpandedVariables;
    
    int statements = 0;
    QueryExpandAndWriteVisitor (Map<URI, List<URI>> uriMappings, boolean showExpandedVariables){
        this.uriMappings = uriMappings;
        this.showExpandedVariables = showExpandedVariables;
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
    
    private void writeFilterIfNeeded(URI uri, String variableName) throws UnexpectedQueryException{
        if (uri == null) return;
        //ystem.out.println(uriMappings);
        List<URI> uriList = uriMappings.get(uri);
        if (uriList == null){
            throw new Error("Query has URI " + uri + " but it has no mapped set.");
        }
        //ystem.out.println(uriList);
        if (uriList.isEmpty()){
            throw new Error("Query has URI " + uri + " but mapped set is empty.");
        }
        newLine();
        queryString.append("FILTER (");
        queryString.append(variableName);
        queryString.append(" = <");
        queryString.append(uriList.get(0));
        queryString.append(">");
        for (int i = 1; i < uriList.size(); i++){
            queryString.append(" || ");
            queryString.append(variableName);
            queryString.append(" = <");
            queryString.append(uriList.get(i));
            queryString.append(">");            
        }
        queryString.append(")");
    }
    
    //@Override
    public void meet(StatementPattern sp) throws UnexpectedQueryException  {
        statements++;
        newLine();
        URI subjectURI = findURI(sp.getSubjectVar());
        if (subjectURI == null) {
            sp.getSubjectVar().visit(this);
        } else {
            queryString.append("?subjectUri");
            queryString.append(statements);
        }
        queryString.append(" ");
        sp.getPredicateVar().visit(this);
        queryString.append(" ");
        URI objectURI = findURI(sp.getObjectVar());
        if (objectURI == null) {
            sp.getObjectVar().visit(this);
        } else {
            queryString.append("?objectUri");
            queryString.append(statements);
        }
        queryString.append(" .");
        writeFilterIfNeeded(subjectURI, "?subjectUri" + statements);
        writeFilterIfNeeded(objectURI, "?objectUri" + statements);
    }

        @Override
    public void meet(Projection prjctn) throws UnexpectedQueryException {
        queryString.append("SELECT ");
        if (showExpandedVariables){
            ReplacementVariableFinderVisitor variableFinder = new ReplacementVariableFinderVisitor(statements);
            prjctn.getArg().visit(variableFinder);
            List<String> variables = variableFinder.getVariables();
            for(String variable:variables){
                queryString.append(variable);
                queryString.append(" ");
            }
        }
        prjctn.getProjectionElemList().visit(this);
        newLine();
        queryString.append("{");
        prjctn.getArg().visit(this);
        queryString.append("}");
    }


}
