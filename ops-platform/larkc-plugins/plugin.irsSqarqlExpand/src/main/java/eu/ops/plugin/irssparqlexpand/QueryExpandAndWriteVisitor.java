/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import java.util.List;
import java.util.Map;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Dataset;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;

/**
 *
 * @author Christian
 */
public class QueryExpandAndWriteVisitor extends QueryWriterModelVisitor{

    Map<URI, List<URI>> uriMappings;
    boolean showExpandedVariables;
    
    int statements = 0;
    QueryExpandAndWriteVisitor (Map<URI, List<URI>> uriMappings, Dataset dataset, boolean showExpandedVariables){
        super(dataset);
        this.uriMappings = uriMappings;
        this.showExpandedVariables = showExpandedVariables;
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
    
    private void writeFilterIfNeeded(URI uri, String variableName) throws QueryExpansionException {
        if (uri == null) return;
        //ystem.out.println(uriMappings);
        List<URI> uriList = getMappedList(uri);
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
 
    private URI writeVarOrGetURI(ValueExpr valueExpr) throws QueryExpansionException{
        if (valueExpr instanceof Var){
            Var var = (Var)valueExpr;
            if (var.hasValue()){
                Value value = var.getValue();
                return writeValueOrGetURI(value);
            } if (var.isAnonymous()){
                writeAnon(var.getName());
                return null;
            } else {
                queryString.append(" ?");
                queryString.append(var.getName());
                return null;
            }
        } else if (valueExpr instanceof ValueConstant){
            Value value = ((ValueConstant)valueExpr).getValue();
            return writeValueOrGetURI(value);
        } else {
            valueExpr.visit(this);
            return null;
        }
    }
    
    private URI writeValueOrGetURI(Value value) throws QueryExpansionException{
        if (value instanceof URI){
            URI uri = (URI) value;
            List<URI> uriList = uriMappings.get(uri);
            if (uriList == null || uriList.isEmpty()){
                queryString.append("<");
                queryString.append(uri.stringValue());
                queryString.append(">");                
                return null;
            } else if (uriList.size() == 1){
                queryString.append("<");
                queryString.append(uriList.get(0));
                queryString.append(">");                
                return null;
            } else {
                return uri;
            }
        } else {
            queryString.append(value);
            return null;
        }
    }

    //@Override
    public void meet(StatementPattern sp) throws QueryExpansionException  {
        if (isDescribePattern(sp)) return;
        statements++;
        newLine();
        boolean newContext = startContext(sp); 
        URI subjectURI = writeVarOrGetURI(sp.getSubjectVar());
        if (subjectURI != null) {
            queryString.append("?subjectUri");
            queryString.append(statements);
        }
        queryString.append(" ");
        sp.getPredicateVar().visit(this);
        queryString.append(" ");
        URI objectURI = writeVarOrGetURI(sp.getObjectVar());
        if (objectURI != null) {
            queryString.append("?objectUri");
            queryString.append(statements);
        }
        queryString.append(" .");
        writeFilterIfNeeded(subjectURI, "?subjectUri" + statements);
        writeFilterIfNeeded(objectURI, "?objectUri" + statements);
        closeContext(newContext);
    }

    @Override
    void addExpanded(Projection prjctn) throws QueryExpansionException {
        if (showExpandedVariables){
            ReplacementVariableFinderVisitor variableFinder = new ReplacementVariableFinderVisitor(statements, uriMappings);
            prjctn.getArg().visit(variableFinder);
            List<String> variables = variableFinder.getVariables();
            for(String variable:variables){
                queryString.append(variable);
                queryString.append(" ");
            }
        }
    }

   private void expandCompare(Compare cmpr, ValueExpr valueExpr,  List<URI> uriList) throws QueryExpansionException {
        valueExpr.visit(this);
        queryString.append(" ");
        queryString.append(cmpr.getOperator().getSymbol());
        queryString.append(" <");
        queryString.append(uriList.get(0));
        queryString.append(">");
        for (int i = 1; i< uriList.size(); i++){
            switch (cmpr.getOperator()){
                case EQ: 
                    queryString.append(" || ");        
                    break;                    
                case NE: 
                    queryString.append(" && ");        
                    break;
                default:  //LT, LE, GE, GT do not make sense applied to a URI: 
                    throw new QueryExpansionException ("Did not expect " + cmpr.getOperator() + " in a Compare with URIs");
            }
            valueExpr.visit(this);
            queryString.append(" ");
            queryString.append(cmpr.getOperator().getSymbol());
            queryString.append(" <");
            queryString.append(uriList.get(i));
            queryString.append(">");
        }
   }

    @Override
    public void meet(Compare cmpr) throws QueryExpansionException {
        queryString.append("(");
        URI rightURI = findMultipleMappedURI(cmpr.getRightArg());
        if (rightURI == null){
            URI leftURI = writeVarOrGetURI(cmpr.getLeftArg());
            if (leftURI == null){
                queryString.append(" ");
                queryString.append(cmpr.getOperator().getSymbol());
                queryString.append(" ");
                writeVarOrGetURI(cmpr.getRightArg());
            } else {
                expandCompare(cmpr, cmpr.getRightArg(), getMappedList(leftURI));  
            }
        } else {
            expandCompare(cmpr, cmpr.getLeftArg(), getMappedList(rightURI));                      
        }
        queryString.append(")");
    }

    @Override
    String getUriString(URI uri){
        List<URI> uriList = uriMappings.get(uri);
        if (uriList == null || uriList.isEmpty()) {
            return (" <" + uri.stringValue() + ">"); 
        } else {
            StringBuilder builder = new StringBuilder();
            for (URI mapped: uriList){
                builder.append(" <");
                builder.append(mapped.stringValue());
                builder.append(">");
            }
            return builder.toString();
        }
    }

}
