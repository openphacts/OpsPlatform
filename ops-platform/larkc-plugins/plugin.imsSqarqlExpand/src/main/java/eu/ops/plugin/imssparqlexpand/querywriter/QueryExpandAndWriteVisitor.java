package eu.ops.plugin.imssparqlexpand.querywriter;

import eu.ops.plugin.imssparqlexpand.ContextListerVisitor;
import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import eu.ops.plugin.imssparqlexpand.ims.IMSMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Dataset;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.Compare.CompareOp;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;

/**
 * Extends the QueryWriterModelVisitor to insert replacement URI received from an IMSMapper.
 * 
 * @author Christian
 */
public class QueryExpandAndWriteVisitor extends QueryWriterModelVisitor{
    
    //FULL_EXPAND currently untested with the Option nesting.
    //private static final boolean DO_FULL_EXPAND = false;
    
    //Maps the URIs found in the original query to the values used in the outputQuery 
    private Map<URI,String> contextUriVariables = new HashMap<URI,String>();
   
    //Maps the Values used for a URI with the uriSet of URIs that this value could represent.
    private Map<String,Set<URI>> mappings = new  HashMap<String,Set<URI>>(); 
    
    //Service that provides the URI mappings.
    private IMSMapper mapper;
    
    //Counter to ensure that all the temporary variables have unique names.
    private int variableCounter =  0;
    
    /**
     * Sets up the visitor for writing the query.
     * 
     * @param dataSet dataSets listed in the original Queries FROM clause.
     * @param requiredAttributes List of attributes to be used or null to include all.
     *     WARNING: this functionality is in an early stage of development 
     *     so can only handle queries where each attribute comes from exactly one statement.
     * @param mapper The service that will offers replacement URIs for each (none predicate) URI found in the query.
     * @param contexts List of Contexts retrieved using the ContextListerVisitor.
     */
    QueryExpandAndWriteVisitor (Dataset dataset, List<String> requiredAttributes, IMSMapper mapper, 
            ArrayList<Var> contexts){
        super(dataset, requiredAttributes, contexts);
        this.mapper = mapper;    
    }
    
    /**
     * Retreives the mappings from this URI is any from the service.
     * <p>
     * Where applicable this function will attempt to retreive context specific mappings.
     * Where there is no context for example the Statement is not inside a Graph claus,
     * or the context is a variable all mapped URIs are retreived.
     * 
     * @param uri The URI for which replacements are to be found.
     * @return A List of replacement URIs or NULL is not replacement are returned by the mapper.
     * @throws QueryExpansionException Some expection thrown by the mapping service.
     */
    private Set<URI> getMappings(URI uri) throws QueryExpansionException{
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
    
    /**
     * Converts the Value Expr into an URI and calls getMappings(URI uri).
     * 
     * @param uriArg A ValueExpr that MUST contain a URI
     * @return A List of replacement URIs or NULL is not replacement are returned by the mapper.
     * @throws QueryExpansionException Thrown if the ValueExpr does not contain a URI or 
     *   some expection thrown by the mapping service.
     */
    private Set<URI> getMappings(ValueExpr uriArg) throws QueryExpansionException {
        Value value;
        if (uriArg instanceof ValueConstant){
            ValueConstant vc = (ValueConstant)uriArg;
            value = vc.getValue();
        } else if (uriArg instanceof Var){
            Var var = (Var)uriArg;
            if (var.hasValue()){
               value = var.getValue();
            } else {
                throw new QueryExpansionException ("Expected a URI but found : " + uriArg);        
            }
        } else {
            throw new QueryExpansionException ("Expected a URI but found : " + uriArg);
        }
        if (value instanceof URI){
            return getMappings((URI)value);
        } else {
            throw new QueryExpansionException ("Expected a URI but found : " + uriArg);            
        }
    }

    @Override                 
     /**
     * Close the context (GRAPH clause) and any optional clauses opened inside the graph anding a filter if required.
     * <p>
     * This method has three steps.
     * <ol>
     * <li> Close any optionals opened inside the Graph Clause. </li>
     * <li> Add any required URI filters </li>
     * <li> Close the context. </li>
     * </ol>    
     * <p>
     * Subclasses with overwrite this method to add behavior such as adding URi replacement filters.
     */
    void closeContext (){
        //Close any optionals opened inside the Graph Clause.
        //optionsInGraph will never be > 0 outside of a context.
        //optionsIngraph is only used if there are more statements in the context than in the optional
        //   So closing the optional first is cleaner.
        while (optionInGraph > 0){
            newLine();
            queryString.append(" } #OPTION from close context");   
            //reduce the count so it is not closed again.
            optionInGraph--;
        }
        //Add any required URI filters
        //if there are non no filters need to be added.
        if (!(mappings.isEmpty())){
            for (String variableName:mappings.keySet()){
                Set<URI> uriSet = mappings.get(variableName);
                Iterator<URI> uris = uriSet.iterator();
                newLine();
                queryString.append("FILTER (");
                queryString.append(variableName);
                queryString.append(" = <");
                queryString.append(uris.next());
                queryString.append(">");
                while (uris.hasNext()){
                    queryString.append(" || ");
                    queryString.append(variableName);
                    queryString.append(" = <");
                    queryString.append(uris.next());
                    queryString.append(">");            
                }
                queryString.append(")");
            }
            //Clear the mappings so they are no closed again.
            mappings = new  HashMap<String,Set<URI>>(); 
            //Clear the URI Mappings so new variables are used if the same URI is sean again
            contextUriVariables = new HashMap<URI,String>();
        }
        //Call super class to do the actual closing.
        super.closeContext();
    }

    /**
     * Gets the String that will be used for this URI in the expanded Query.
     * <p>
     * This could be:
     * <ol>
     * <li> The URI itself within the angle brackets &lt; &gt; </li>.
     * <li> A Single replacement URI within the angle brackets &lt; &gt; </li>.
     * <li> A temporary variable name including the ?. </li>
     * </ol>
     * <p>
     * A tempororay variable is returned when the URI mapps to more than one other URI. 
     * In this case the method.
     * <ol>
     * <li> Generates a new temporay variable </li>
     * <li> Maps the URI to this temporary variable </li>
     * <li> Maps the uriSet of URIs to this temporary variable </li>
     * <li> Returns the temporary variable</li>
     * </ol>
     * @param uri URI to map from.
     * @return String that will be used for this URI in the expanded Query.
     * @throws QueryExpansionException Thrown by the mapping service.
     */
    private String getURIVariable(URI uri) throws QueryExpansionException{
        //if there is already a variable mapped to this URI return it
        if (contextUriVariables.containsKey(uri)){
            return contextUriVariables.get(uri);
        }
        Set<URI> uriSet = getMappings(uri);
        //If there are no mappings just use the original URI.
        //This keeps the URI that had no mappings in the query for logging and bug testing purposes.
        if (uriSet == null || uriSet.isEmpty()){
            return "<" + uri.stringValue() + ">";
        }
        //Exactly one URI found so us it.
        //This could be the orignal URI but may be a graph specific one to one mapping replacement.
        if (uriSet.size()== 1){
            String variable = "<" + uriSet.iterator().next().stringValue() + ">";
            return variable;
        }
        
        //Ok so there must be more than one Mappinhg
        
        //Get a new temporay variable
        variableCounter++;
        String variableName = "?replacedURI" + variableCounter;
        //Store the variable for reuse
        contextUriVariables.put(uri,variableName); 
        //Store the uriSet for adding the filter.
        mappings.put(variableName, uriSet);
        return variableName;
    }
    
    @Override
    /**
     * Write the var, or for a URI the replacement.
     * <p>
     * Called by meet(StatementPattern sp) for the subject and the object.
     * <p> 
     * Checks to see if the var is a URI.
     * If it is a URI getURIVariable is used to find a possible replacement.
     * If it is not a URI the normal write method is used.
     * 
     * @param var Var to be written.
     * @throws QueryExpansionException 
     */
    void writeStatementPart(Var var) throws QueryExpansionException{
        if (var.isAnonymous()){
            Value value = var.getValue();
            if (value instanceof URI){
                queryString.append(getURIVariable((URI)value));
            } else {
                meet(var);
            }
        } else {
            meet(var);         
        }
    }

    /**
     * Writes a compare where one of the values is known to be a URI which may need replacing.
     * <p>
     * <ol>
     * <li> Write the non URI arguement. </li>
     * <li> Writes the operator. </li>
     * <li> Gets the List of replacement URIs (if any) </li>
     * <ol>
     * <li> If the List is null or empty: Just writes the uriSet </li>
     * <li> If the uriSet has one URI: Just write that URI inclduing the &lt; and &gt; </li>
     * <li> If the uriSet has more than one URI: Expands the filter to include each of the mapped URIs
     *     Seperated by AND or OR as appropriate. </li>
     * </ol></ol>
     * @param compareOp The comparison operator. Note: Only Equals and  Not equals make sence.
     * @param normalArg The none URI arguement.
     * @param uriArg The URI arguement
     * @throws QueryExpansionException If uriArg is not a URI, compareOp is not "=" or "!=" or a mapping exception.
     */
    private void writeCompareWithURI(CompareOp compareOp, ValueExpr normalArg, ValueExpr uriArg) throws QueryExpansionException {
        queryString.append("(");
        normalArg.visit(this);
        queryString.append(" ");
        queryString.append(compareOp.getSymbol());
        Set<URI> uriSet = this.getMappings(uriArg);
        if (uriSet == null || uriSet.isEmpty()){
            uriArg.visit(this);
        } else {
            Iterator<URI> uris = uriSet.iterator();
            queryString.append(" <");
            queryString.append(uris.next());
            queryString.append(">");
            while (uris.hasNext()){
                switch (compareOp){
                    case EQ: 
                        queryString.append(" || ");        
                        break;                    
                    case NE: 
                        queryString.append(" && ");        
                        break;
                    default:  //LT, LE, GE, GT do not make sense applied to a URI: 
                        throw new QueryExpansionException ("Did not expect " + compareOp + " in a Compare with URIs");
                }
                normalArg.visit(this);
                queryString.append(" ");
                queryString.append(compareOp.getSymbol());
                queryString.append(" <");
                queryString.append(uris.next());
                queryString.append(">");
            }
        }
        queryString.append(")");
   }

    /**
     * Write a compare that does not include a URi by writing the three parts. 
     */
    private void writeCompareWithoutURI (CompareOp operator, ValueExpr leftArg,  ValueExpr rightArg) 
            throws QueryExpansionException{
        queryString.append("(");
        leftArg.visit(this);    
        queryString.append(" ");
        queryString.append(operator.getSymbol());
        queryString.append(" ");
        rightArg.visit(this);
        queryString.append(")");
    }
    
    /**
     * Determines if a ValueExpr contains a URI.
     * @param valueExpr Any non null ValueExpr
     * @return True if and only if the expresssion holds a URI
     */
    private boolean isURI(ValueExpr valueExpr){
        //ystem.out.println(valueExpr);
        if (valueExpr instanceof ValueConstant){
            ValueConstant vc = (ValueConstant)valueExpr;
            Value value = vc.getValue();
            return value instanceof URI;
        } else if (valueExpr instanceof Var){
            Var var = (Var)valueExpr;
            if (var.hasValue()){
                Value value = var.getValue();
                return value instanceof URI;
            //} else {
                //ystem.out.println(" no value");
            }
        //} else {
            //ystem.out.println (" not a Var or ValueConstant");
        }
        return false;
    }
    
    @Override
    /**
     * Checks if the compare incluses a URi and calls the appropriate method writeCompareWithURI or writeCompareWithoutURI.
     * @param cmpr 
     * @throws QueryExpansionException 
     */
    public void meet(Compare cmpr) throws QueryExpansionException {
        //ystem.out.println(cmpr);
        if (isURI(cmpr.getRightArg())){
            if (isURI(cmpr.getLeftArg())) {
                throw new QueryExpansionException ("Unexpected compare with two URIs; " + cmpr);
            } else {
                writeCompareWithURI(cmpr.getOperator(), cmpr.getLeftArg(), cmpr.getRightArg());
            }
        } else {
            if (isURI(cmpr.getLeftArg())) {
                writeCompareWithURI( cmpr.getOperator(), cmpr.getRightArg(), cmpr.getLeftArg());
            } else {
                writeCompareWithoutURI(cmpr.getOperator(), cmpr.getLeftArg(), cmpr.getRightArg());
            } 
        }
    }

    @Override
    /**
     * Checks if the Variable is a URI and if so writes the mapped uriSet otherwise just writes the variable
     * @param decribeVariable Variable which may be a URI
     * @throws QueryExpansionException 
     */
    void writeDescribeVariable(ValueExpr decribeVariable) throws QueryExpansionException{
        if (isURI(decribeVariable)){
            //See if there are any mapped URIs
            Set<URI> mappedURIs = getMappings(decribeVariable);
            if (mappedURIs == null){
                //OK no mapped URIs so just fall back to the normal behavior.
                queryString.append(extractName(decribeVariable));
            } else {
                //Write the mapped URIs
                for (URI uri:mappedURIs){
                    queryString.append(getUriString(uri));
                }
            }
        } else {
            //OK not a URI so just fall back to the normal bahaviour
            queryString.append(extractName(decribeVariable));
        }
    }

    /**
     * Returns the query as a string.
     * <p>
     * Works if and only if the model was visited exactly once.
     * @return query as a String
     * @throws QueryExpansionException Declared as thrown to allow calling methods to catch it specifically.
     */
    private String getQuery() throws QueryExpansionException {
        return queryString.toString();
    }

    public static String convertToQueryString(TupleExpr tupleExpr, Dataset dataSet, IMSMapper mapper, 
            List<String> requiredAttributes) throws QueryExpansionException{
        ArrayList<Var> contexts = ContextListerVisitor.getContexts(tupleExpr);
       
        QueryExpandAndWriteVisitor writer = new QueryExpandAndWriteVisitor(dataSet, requiredAttributes, mapper, contexts);
        tupleExpr.visit(writer);
        return writer.getQuery();
    }
}
