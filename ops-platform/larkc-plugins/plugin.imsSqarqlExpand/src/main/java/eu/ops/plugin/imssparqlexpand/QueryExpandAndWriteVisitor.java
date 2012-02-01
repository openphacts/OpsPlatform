package eu.ops.plugin.imssparqlexpand;

import eu.ops.plugin.imssparqlexpand.ims.IMSMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Dataset;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;

/**
 *
 * @author Christian
 */
public class QueryExpandAndWriteVisitor extends QueryWriterModelVisitor{
    
    private static final boolean DO_FULL_EXPAND = false;
    private Map<URI,String> contextUriVariables = new HashMap<URI,String>();
    private Map<String,List<URI>> mappings = new  HashMap<String,List<URI>>(); 
    private IMSMapper mapper;
    int variableCounter =  0;
    
    QueryExpandAndWriteVisitor (Map<URI, List<URI>> uriMappings, Dataset dataset, List<String> requiredAttributes, 
            IMSMapper mapper){
        super(dataset, requiredAttributes);
        this.mapper = mapper;
    }
    
    private List<URI> getMappings(URI uri){
        if (context == null){
            return mapper.getMatchesForURI(uri);            
        } else {
            if (context.hasValue()){
                return mapper.getSpecificMatchesForURI(uri, context.getValue().stringValue());
            } else {
                return mapper.getMatchesForURI(uri);   
            }
        }
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
            List<URI> uriList = getMappings(uri);;
            if (uriList == null){
                //unmapped.
                return null;
            }
            //Only care it is a URI if there is more than one mapping
            if (uriList.size() > 1){
               return uri;
            } 
        }
        return null;        
    }
    
   private List<URI> getMappedList(URI uri){
        List<URI> uriList = getMappings(uri);
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
            List<URI> uriList = getMappings(uri);
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

    void setupNewContext(){
        contextUriVariables = new HashMap<URI,String>();
        mappings = new  HashMap<String,List<URI>>(); 
    }

    void closeContext (boolean startedHere){
        if (DO_FULL_EXPAND || context == null || startedHere ){
            for (String variableName:mappings.keySet()){
                List<URI> uriList = mappings.get(variableName);
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
            mappings = new  HashMap<String,List<URI>>(); 
        }
        if (startedHere){
            contextUriVariables = new HashMap<URI,String>();
            super.closeContext(startedHere);
        }
    }

    private String getURIVariable(URI uri){
        System.out.println(uri);
        if (contextUriVariables.containsKey(uri)){
            return contextUriVariables.get(uri);
        }
        List<URI> list = getMappings(uri);
        System.out.println(list);
        if (list == null || list.isEmpty()){
            return "<" + uri.stringValue() + ">";
        }
        if (list.size()== 1){
            String variable = "<" + list.get(0).stringValue() + ">";
            if (context != null){
               contextUriVariables.put(uri,variable); 
            }
            return variable;
        }
        variableCounter++;
        String variableName = "?replacedURI" + variableCounter;
        if (context != null){
           contextUriVariables.put(uri,variableName); 
        }
        mappings.put(variableName, list);
        return variableName;
    }
    
    @Override
    void writeStatementPart(Var var) throws QueryExpansionException{
//        System.out.println(var);
        if (var.isAnonymous()){
            Value value = var.getValue();
            if (value instanceof URI){
                queryString.append(getURIVariable((URI)value));
            } else {
//                System.out.println(value.getClass());
                meet(var);
            }
        } else {
//            System.out.println("annon");
            meet(var);         
        }
    }

/*    @Override
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
*/
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

   //TODO there must be a better way
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

 /*   @Override
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
*/
}
