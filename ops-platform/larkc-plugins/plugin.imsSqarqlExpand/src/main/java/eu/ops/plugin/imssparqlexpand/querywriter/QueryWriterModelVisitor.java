package eu.ops.plugin.imssparqlexpand.querywriter;

import eu.ops.plugin.imssparqlexpand.ContextListerVisitor;
import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Dataset;
import org.openrdf.query.algebra.And;
import org.openrdf.query.algebra.BNodeGenerator;
import org.openrdf.query.algebra.Bound;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.CompareAll;
import org.openrdf.query.algebra.CompareAny;
import org.openrdf.query.algebra.Count;
import org.openrdf.query.algebra.Datatype;
import org.openrdf.query.algebra.Difference;
import org.openrdf.query.algebra.Distinct;
import org.openrdf.query.algebra.EmptySet;
import org.openrdf.query.algebra.Exists;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.FunctionCall;
import org.openrdf.query.algebra.Group;
import org.openrdf.query.algebra.GroupElem;
import org.openrdf.query.algebra.In;
import org.openrdf.query.algebra.Intersection;
import org.openrdf.query.algebra.IsBNode;
import org.openrdf.query.algebra.IsLiteral;
import org.openrdf.query.algebra.IsResource;
import org.openrdf.query.algebra.IsURI;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.Label;
import org.openrdf.query.algebra.Lang;
import org.openrdf.query.algebra.LangMatches;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.Like;
import org.openrdf.query.algebra.LocalName;
import org.openrdf.query.algebra.MathExpr;
import org.openrdf.query.algebra.Max;
import org.openrdf.query.algebra.Min;
import org.openrdf.query.algebra.MultiProjection;
import org.openrdf.query.algebra.Namespace;
import org.openrdf.query.algebra.Not;
import org.openrdf.query.algebra.Or;
import org.openrdf.query.algebra.Order;
import org.openrdf.query.algebra.OrderElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryModelVisitor;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.Regex;
import org.openrdf.query.algebra.SameTerm;
import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Str;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;

/**
 *
 * @author Christian
 */
public class QueryWriterModelVisitor implements QueryModelVisitor<QueryExpansionException>{
    
    //Builder to write the query to bit by bit
    StringBuilder queryString = new StringBuilder();
   
    //Used to add the FROM clause
    private Dataset originalDataSet;
    
    //The current context (GRAPH clause) the writer is in. (or null if not in a context.
    Var context = null;
    
    //List of attributes to be used or null to include all.
    //     WARNING: this functionality is in an early stage of development 
    //     so can only handle queries where each attribute comes from exactly one statement.
    private List<String>  requiredAttributes;
    
    //List of attributes to be removed. See requiredAttributes warning!
    private Set<String> eliminatedAttributes;
    
    //Flag that the writing of an Optional has been delayed until a Graph clause is added
    boolean swapGraphAndOptional = false;
    
    //List of the Contexts (including null) for all the Statements not yet met.
    ArrayList<Var> contexts;
    
    //List of the number of options clauses pushed under the graph clause.
    int optionInGraph = 0;
    
    /**
     * Sets up the visitor for writing the query.
     * 
     * @param dataSet dataSets listed in the original Queries FROM clause.
     * @param requiredAttributes List of attributes to be used or null to include all.
     *     WARNING: this functionality is in an early stage of development 
     *     so can only handle queries where each attribute comes from exactly one statement.
     * @param contexts List of Contexts retrieved using the ContextListerVisitor.
     */
    QueryWriterModelVisitor(Dataset dataSet, List<String> requiredAttributes,  ArrayList<Var> contexts){
        originalDataSet = dataSet;
        eliminatedAttributes = new HashSet<String>();
        if (requiredAttributes == null || requiredAttributes.isEmpty()) {
            this.requiredAttributes = null;
        } else {
            this.requiredAttributes = requiredAttributes;
        }
        this.contexts = contexts;
    }

   /**
     * Sets up the visitor for writing the query.
     * 
     * @param dataSet dataSets listed in the original Queries FROM clause.
     * @param contexts List of Contexts retrieved using the ContextListerVisitor.
     */
    QueryWriterModelVisitor(Dataset dataSet, ArrayList<Var> contexts){
        this(dataSet, null, contexts);
    }
    
    @Override
    public void meet(QueryRoot qr) throws QueryExpansionException {
        throw new QueryExpansionException("QueryRoot not supported yet.");
    }

    @Override
    public void meet(And and) throws QueryExpansionException {
        queryString.append("(");
        and.getLeftArg().visit(this);
        queryString.append(" && ");
        and.getRightArg().visit(this);
        queryString.append(")");
    }

    /**
     * Replaces an annonomous variable with a named variable.
     * <p>
     * The reason for this is that the semantic sugar ; method of writing statements 
     *    allows the same annonous variable to be used more than once.
     * @param name 
     */
    void writeAnon(String name){
        //-anon-1
        String numberPart = name.substring(6);
        short anonNumber = Short.parseShort(numberPart); 
        short anonBig = (short) (anonNumber / 26);
        short anonSmall = (short) (anonNumber % 26);
        char[] ending = Character.toChars(65 + anonSmall);
        queryString.append("_:");
        queryString.append(ending);
        if (anonBig > 0) {
            ending = Character.toChars(64 + anonBig);
            queryString.append(ending);        
        }
    }
    
    @Override
    public void meet(BNodeGenerator bng) throws QueryExpansionException {
        //queryString.append(" [] ");
        //queryString.append(" _:hjk ");
        throw new QueryExpansionException("BNodeGenerator not supported yet.");
    }

    @Override
    public void meet(Bound bound) throws QueryExpansionException {
        queryString.append("BOUND(");
        bound.getArg().visit(this);
        queryString.append(") ");        
    }

    @Override
    public void meet(Compare cmpr) throws QueryExpansionException {
        queryString.append("(");
        cmpr.getLeftArg().visit(this);
        queryString.append(" ");
        queryString.append(cmpr.getOperator().getSymbol());
        queryString.append(" ");
        cmpr.getRightArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(CompareAll ca) throws QueryExpansionException {
        throw new QueryExpansionException("CompareAl not supported yet.");
    }

    @Override
    public void meet(CompareAny ca) throws QueryExpansionException {
        throw new QueryExpansionException("CompareAny not supported yet.");
    }

    @Override
    public void meet(Count count) throws QueryExpansionException {
        throw new QueryExpansionException("Count not supported yet.");
    }

    @Override
    public void meet(Datatype dtp) throws QueryExpansionException {
        queryString.append(" DATATYPE(");
        dtp.getArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(Difference dfrnc) throws QueryExpansionException {
        throw new QueryExpansionException("Difference not supported yet.");
    }

    @Override
    public void meet(Distinct dstnct) throws QueryExpansionException {
        TupleExpr tupleExpr = dstnct.getArg();
        if (tupleExpr instanceof Projection){
            meet ((Projection)tupleExpr, " DISTINCT");
        } else {
            throw new QueryExpansionException("Distinct only supported followed by Projection.");
        }
    }

    @Override
    public void meet(EmptySet es) throws QueryExpansionException {
        throw new QueryExpansionException("EmptySet not supported yet.");
    }

    @Override
    public void meet(Exists exists) throws QueryExpansionException {
        throw new QueryExpansionException("Exists not supported yet.");
    }

    @Override
    public void meet(Extension extnsn) throws QueryExpansionException {
        //I assume that this is also part of the ProjectionElemList so not required again.
        //extnsn.getElements();
        extnsn.getArg().visit(this);
    }

    @Override
    public void meet(ExtensionElem ee) throws QueryExpansionException {
        //possibly never called as meet(Extension extnsn) ignores this part
        throw new QueryExpansionException("ExtensionElem not supported yet.");
    }

    @Override
    public void meet(FunctionCall fc) throws QueryExpansionException {
        queryString.append("<");
        queryString.append(fc.getURI());
        queryString.append(">(");
        List<ValueExpr> args = fc.getArgs();
        if (!args.isEmpty()) {
           args.get(0).visit(this);
        }
        for (int i = 1; i < args.size(); i++){
            queryString.append(" ,");
            args.get(i).visit(this);
        }
        queryString.append(")");
    }

    @Override
    public void meet(Group group) throws QueryExpansionException {
        throw new QueryExpansionException("Group not supported yet.");
    }

    @Override
    public void meet(GroupElem ge) throws QueryExpansionException {
        throw new QueryExpansionException("GroupElem not supported yet.");
    }

    @Override
    public void meet(In in) throws QueryExpansionException {
        throw new QueryExpansionException("In not supported yet.");
    }

    @Override
    public void meet(Intersection i) throws QueryExpansionException {
        throw new QueryExpansionException("Intersection not supported yet.");
    }

    @Override
    public void meet(IsBNode ibn) throws QueryExpansionException {
        queryString.append(" ISBLANK(");
        ibn.getArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(IsLiteral il) throws QueryExpansionException {
        queryString.append(" ISLITERAL(");
        il.getArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(IsResource ir) throws QueryExpansionException {
        throw new QueryExpansionException("IsResource not supported yet.");
    }

    @Override
    public void meet(IsURI isuri) throws QueryExpansionException {
        queryString.append(" ISIRI(");
        isuri.getArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(Join join) throws QueryExpansionException {
        join.getLeftArg().visit(this);
        join.getRightArg().visit(this);
    }

    @Override
    public void meet(Label label) throws QueryExpansionException {
        throw new QueryExpansionException("Label not supported yet.");
    }

    @Override
    public void meet(Lang lang) throws QueryExpansionException {
        queryString.append(" LANG(");
        lang.getArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(LangMatches lm) throws QueryExpansionException {
        queryString.append(" LANGMATCHES(");
        lm.getLeftArg().visit(this);
        queryString.append(" ,");
        lm.getRightArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(Like like) throws QueryExpansionException {
        throw new QueryExpansionException("Like not supported yet.");
    }

    @Override
    public void meet(LocalName ln) throws QueryExpansionException {
        throw new QueryExpansionException("LocalName not supported yet.");
    }

    @Override
    public void meet(MathExpr me) throws QueryExpansionException {
        throw new QueryExpansionException("MathExpr not supported yet.");
    }

    @Override
    public void meet(Max max) throws QueryExpansionException {
        throw new QueryExpansionException("Max not supported yet.");
    }

    @Override
    public void meet(Min min) throws QueryExpansionException {
        throw new QueryExpansionException("Min not supported yet.");
    }

    @Override
    public void meet(MultiProjection mp) throws QueryExpansionException {
        throw new QueryExpansionException("MultiProjection not supported yet.");
    }

    @Override
    public void meet(Namespace nmspc) throws QueryExpansionException {
        throw new QueryExpansionException("Namespace not supported yet.");
    }

    @Override
    public void meet(Not not) throws QueryExpansionException {
        queryString.append(" !(");
        not.getArg().visit(this);
        queryString.append(")");
    }

    //@Override
    /**
     * 
     * @param lj
     * @throws QueryExpansionException 
     */
    public void meet(LeftJoin lj) throws QueryExpansionException {
        
        //The leftArg is the stuff outside of the optional.
        //May be a SingletonSet in which case nothing is written
        lj.getLeftArg().visit(this);

        //If context is null no GRAPH clause is open 
        if (context == null){
            if (statementsInNextGraph() > statementsInExpression(lj.getRightArg())){
                //There are statements in the graph which will be written after the optional is closed
                //For example this happens if there is mmore than one Optional clause in a single graph.
                //So the wrting of the Optional is delayed until the GRAPH clause is added.
                swapGraphAndOptional = true;
            } else {
                //No need to delay so write the OPTIONAL clause
                newLine();
                queryString.append("OPTIONAL { #left join ");
            }
            //Write the Optional part
            lj.getRightArg().visit(this);
            //Write any filters in the OPTIONAL clause
            //This is Filters in the original query not URI replacement filters.
            if (lj.hasCondition()){
                newLine();
                queryString.append("    FILTER ");
                lj.getCondition().visit(this);
            }
            //Close the Optional
            newLine();
            queryString.append("  } #OPTIONAL leftJoin");
            newLine();
        } else {
            //Already in a Context (GRAPH clause)
            //For example because there are non optional statements, or more than one optional clause.
            //So open the optional
            newLine();
            queryString.append("OPTIONAL { #leftJoin");
            //Record that we opened the optional in side the graph so graph closes it first
            optionInGraph++;
            //Write the Optional part
            lj.getRightArg().visit(this);
            //Write any filters in the OPTIONAL clause
            //This is Filters in the original query not URI replacement filters.
            if (lj.hasCondition()){
                newLine();
                queryString.append("    FILTER ");
                lj.getCondition().visit(this);
            }
        }
        //If there is an optional open close it.
        if (optionInGraph > 0){
            newLine();
            queryString.append(" } #OPTIONAL by optionInGraph"); 
            //This may be an optional inside another optional so only close one.
            optionInGraph--;
            newLine();
        }

    }
    
    /**
     * Preforms a look ahead to see how many statements will be in the next Graph clause.
     * <p>
     * In practice it uses the list of contexts passed in the constructor to do the look ahead.
     * This was done as the next statements may be in a completely different branch of the tree.
     * @return Number of statements in the Graph Clause
     */
    private int statementsInNextGraph(){
        //Starts at the 0 element of the contexts list as meet(Statement) removes context that have been met.
        //If you are not in a grahp just return 0
        if (contexts.get(0) == null){
            return 0;
        }
        //Use the list of count how many are the same.
        int i;
        for (i = 0; i < contexts.size(); i++){
            if (!(contexts.get(0).equals(contexts.get(i)))){
                //Found one different so return the count
                return i;
            }
        }
        //End of the list so return the count.
        return i;
    }

    /**
     * Counts the number of statements in this query or sub query.
     * @param tupleExpr query or sub query
     * @return Number of statements met.
     * @throws QueryExpansionException Unlikely but just in case.
     */
    private int statementsInExpression(TupleExpr tupleExpr) throws QueryExpansionException{
        //Could be done with a lister that counts instead of lists but this reuses code.
        ContextListerVisitor counter = new ContextListerVisitor();
        tupleExpr.visit(counter);
        ArrayList<Var> contexts = counter.getContexts();
        return contexts.size();
    }
    
    @Override
    public void meet(Or or) throws QueryExpansionException {
        queryString.append("(");
        or.getLeftArg().visit(this);
        queryString.append(" || ");
        or.getRightArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(Order order) throws QueryExpansionException {
        order.getArg().visit(this);
        queryString.append(" } ");
        newLine();
        queryString.append("ORDER BY ");
        List<OrderElem> orderElems = order.getElements();
        for (OrderElem orderElem: orderElems){
            meet(orderElem);
        }
    }

    @Override
    public void meet(OrderElem oe) throws QueryExpansionException {
        if (oe.isAscending()){
            queryString.append("ASC(");    
        } else {
            queryString.append("DESC(");                
        }
        oe.getExpr().visit(this);
        queryString.append(") ");    
    }

    @Override
    public void meet(Projection prjctn) throws QueryExpansionException {
        meet (prjctn, "");
    }

    /**
     * Used by sub classes to add expaned list if required.
     * 
     * Currently not used but left in if required again.
     */
    void addExpanded(Projection prjctn) throws QueryExpansionException{
    }
    
    public void meet(Projection prjctn, String modifier) throws QueryExpansionException {
        queryString.append("SELECT ");
        queryString.append(modifier);
        addExpanded(prjctn);
        if (this.requiredAttributes != null){
            for (String requiredAttribute:requiredAttributes){
                queryString.append(" ?");
                queryString.append(requiredAttribute);
            }
        }
        //Call the ProjectionElementList even if there are requiredAttributes as this sets eliminatedAttributes
        prjctn.getProjectionElemList().visit(this);
        newLine();
        printDataset();
        queryString.append("WHERE {");
        prjctn.getArg().visit(this);
        closeProjectionUnlessOrderHas(prjctn.getArg());
    }

    /**
     * Add the Where's } unless it has already been done.
     * 
     * As the Order statement is added after the Where's }, the meet(Order) method has to close it.
     * In which case another } would cause an invalid query
     * @param expr 
     */
    private void closeProjectionUnlessOrderHas(TupleExpr expr){
        if (expr instanceof Order) return;
        if (expr instanceof Extension){
            Extension extnsn = (Extension) expr;
            if (extnsn.getArg() instanceof Order) return;
        }
        newLine();
        queryString.append(" } # WHERE");
        newLine();
    }
    
    private void printDataset(){
        if (originalDataSet == null) return;
        queryString.append(originalDataSet);
    }
    
    @Override
    public void meet(ProjectionElemList pel) throws QueryExpansionException {
        List<ProjectionElem> elements = pel.getElements();
        for (ProjectionElem element:elements){
            element.visit(this);
        }
    }

    /**
     * Lookahead function to see what query type this ProjectionElemList.
     * <p>
     * Note: This method is built from a few test cases and is probably far from complete.
     * It will err on the side of SELECT.
     * 
     * @param pel A ProjectionElemList
     * @return CONSTRUCT or DESCRIBE if it fits the pattenr seen so far. Otherwise SELECT
     */
    private QueryType workoutQueryType(ProjectionElemList pel){
        List<ProjectionElem> elements = pel.getElements();
        if (elements.size() != 3) return QueryType.SELECT;
        if (!(elements.get(0).getTargetName().equals("subject"))) return QueryType.SELECT;
        if (!(elements.get(1).getTargetName().equals("predicate"))) return QueryType.SELECT;
        if (!(elements.get(2).getTargetName().equals("object"))) return QueryType.SELECT;
        if (!(elements.get(0).getSourceName().equals("-descr-subj"))) return QueryType.CONSTRUCT;
        if (!(elements.get(1).getSourceName().equals("-descr-pred"))) return QueryType.CONSTRUCT;
        if (!(elements.get(2).getSourceName().equals("-descr-obj"))) return QueryType.CONSTRUCT;
        return QueryType.DESCRIBE;
    }
    
    /**
     * Used by the Reduce (Construction query) to add looked ahead ExtensionElems
     *
     * @param pel
     * @param mappedExstensionElements
     * @throws QueryExpansionException 
     */
    private void meet(List<ProjectionElemList> pels, HashMap<String, ValueExpr> mappedExstensionElements) 
            throws QueryExpansionException {
        for (ProjectionElemList pel:pels){
            newLine();
            meet(pel, mappedExstensionElements);
            queryString.append(" . ");
        }
    }

    /**
     * Used by the Reduce (Construction query) to add looked ahead ExtensionElems
     *
     * @param pel
     * @param mappedExstensionElements
     * @throws QueryExpansionException 
     */
    private void meet(ProjectionElemList pel, HashMap<String, ValueExpr> mappedExstensionElements) 
            throws QueryExpansionException {
        List<ProjectionElem> elements = pel.getElements();
        for (ProjectionElem element:elements){
            meet(element, mappedExstensionElements);
            queryString.append(" ");
        }
    }

    @Override
    public void meet(ProjectionElem pe) throws QueryExpansionException {
        String sourceName = pe.getSourceName();
        if (requiredAttributes != null){
            if (!(requiredAttributes.contains(sourceName))){
                 eliminatedAttributes.add(sourceName);
                //requiredAttributes are written by so not written here..
            }
        } else {
            queryString.append(" ?");
            queryString.append(sourceName);
        }
    }

    /**
     * Used by the Reduce (Construction query) to add looked ahead ExtensionElems
     * 
     * @param pe
     * @param mappedExstensionElements
     * @throws QueryExpansionException 
     */
    private void meet(ProjectionElem pe, HashMap<String, ValueExpr> mappedExstensionElements) 
            throws QueryExpansionException {
        String name = pe.getSourceName();
        ValueExpr mapped = mappedExstensionElements.get(name);
        if (mapped == null){
            queryString.append(" ?");
            queryString.append(pe.getSourceName());
        } else if (mapped instanceof BNodeGenerator) {
            writeAnon(name);
        } else {
            mapped.visit(this);
        }
    }

    //REDUCE is found both in CONSTRUCT AND SELECT QUERIES
    @Override
    public void meet(Reduced rdcd) throws QueryExpansionException {
        TupleExpr arg = rdcd.getArg();
        if (arg instanceof Projection){
            Projection prjctn = (Projection)arg;
            TupleExpr prjctnArg = prjctn.getArg();
            switch (workoutQueryType(prjctn.getProjectionElemList())){
                case CONSTRUCT:
                    queryString.append("CONSTRUCT {");
                    HashMap<String, ValueExpr> mappedExstensionElements = mapExensionElements(prjctnArg);
                    meet (prjctn.getProjectionElemList(), mappedExstensionElements);
                    queryString.append("}");
                    newLine();
                    queryString.append("{");
                    prjctn.getArg().visit(this);
                    closeProjectionUnlessOrderHas(prjctn.getArg());
                    break;
                case DESCRIBE:
                    if (prjctnArg instanceof Filter){
                        writeDescribe((Filter)prjctnArg);
                    } else {
                        throw new QueryExpansionException ("Reduced assumed to be a Describe but "
                                + "Projection element is not a Filter");
                    }
                    break;
                case SELECT:    
                    meet (prjctn, " REDUCED");
                    break;
                default: 
                    throw new QueryExpansionException("Unexpected QueryType");
            }
        }  else if (arg instanceof MultiProjection){
            //Assuming it must be construct
            MultiProjection mp = (MultiProjection)arg;
            TupleExpr prjctnArg = mp.getArg();
            queryString.append("CONSTRUCT {");
            HashMap<String, ValueExpr> mappedExstensionElements = mapExensionElements(prjctnArg);
            meet (mp.getProjections(), mappedExstensionElements);
            queryString.append("}");
            newLine();
            queryString.append("{");
            mp.getArg().visit(this);
            queryString.append("}");
        } else {
            throw new QueryExpansionException("Reduced with non Projection/ MultiProjection child not supported yet.");
        }
    }

    //Look ahead function to match names ProjectionElem to ExtensionElem
    //Used by reduce
    private HashMap<String, ValueExpr> mapExensionElements(TupleExpr tupleExpr) throws QueryExpansionException{
        HashMap<String, ValueExpr> mappedExstensionElements = new HashMap<String, ValueExpr>();
        if (tupleExpr instanceof Extension){
            Extension extnsn = (Extension) tupleExpr;
            List<ExtensionElem> ees =  extnsn.getElements();
            for (ExtensionElem ee:ees) {
                mappedExstensionElements.put(ee.getName(), ee.getExpr());
            }
        }
        return  mappedExstensionElements;
    }
    
    @Override
    public void meet(Regex regex) throws QueryExpansionException {
        queryString.append("regex(");
        regex.getArg().visit(this);
        queryString.append(",");
        regex.getPatternArg().visit(this);
        ValueExpr flag = regex.getFlagsArg();
        if (flag != null){
            queryString.append(",");
            flag.visit(this);
        }
        queryString.append(")");
    }

    @Override
    public void meet(Slice slice) throws QueryExpansionException {
        if (isAsk(slice)){
            queryString.append("ASK  {");
            slice.getArg().visit(this);
            queryString.append("}");
        } else {
            slice.getArg().visit(this);
            if (slice.hasLimit()){
                newLine();
                queryString.append("LIMIT ");
                queryString.append(slice.getLimit());     
            }
            if (slice.hasOffset()){
                newLine();
                queryString.append("OFFSET ");
                queryString.append(slice.getOffset());     
            }
        }
    }

    private boolean isAsk(Slice slice){
        if (!(slice.hasLimit())) return false;
        if (slice.getLimit() > 1) return false;
        TupleExpr arg = slice.getArg();
        if (arg instanceof Reduced){
            arg = ((Reduced)arg).getArg();
        } else if (arg instanceof Distinct){
            arg = ((Distinct)arg).getArg();
        } 
        if (arg instanceof Projection){
            return false;
        }
        return true;
    }
    
    @Override
    public void meet(SameTerm st) throws QueryExpansionException {
        queryString.append(" SAMETERM(");
        st.getLeftArg().visit(this);
        queryString.append(" ,");
        st.getRightArg().visit(this);
        queryString.append(")");
    }

    /**
     * Writes a Describe query based on a Filter that has been dettermined to be a Describe.
     * 
     * @param filter Filter tree representing a describe query
     * @throws QueryExpansionException 
     */
    private void writeDescribe(Filter filter) throws QueryExpansionException {
        queryString.append ("DESCRIBE ");
        ValueExpr condition = filter.getCondition();
        findandWriteDescribeVariable(condition);
        //Write the where bit of the decribe query
        TupleExpr arg = filter.getArg();
        if (arg instanceof StatementPattern){
            //Do nothing as only statement patter is the automatically added -descr-subj -descr-pred -descr-obj
        } else {
            newLine();
            queryString.append (" WHERE {");
            filter.getArg().visit(this);
            queryString.append (" }");
        }
    }

    /**
     * Writes the describe Variable.
     * <p>
     * Subclasses may replace a URI with mapped URIs
     * @param decribeVariable
     * @throws QueryExpansionException 
     */
    void writeDescribeVariable(ValueExpr decribeVariable) throws QueryExpansionException{
        queryString.append(extractName(decribeVariable));
    }
    
    /**
     * Looks through the condition for the describe variable and calls writeDescribeVariable(ValueExpr).
     * <p>
     * In a Describe query there is the statement ?-descr-subj ?-descr-pred ?-descr-obj.
     * Then there are filter conditions SameTerm(?-descr-subj, describeVariable) 
     *     and sameTerm(?-descr-obj, describeVariable) 
     * <p> 
     * This method looks for the SameTerm(?-descr-subj, describeVariable) then extracts the describeVariable
     * and calls writeDescribeVariable(ValueExpr) to do the actual writting. (So that sub classes can replace URIs)
     * @param condition
     * @throws QueryExpansionException 
     */
    private void findandWriteDescribeVariable(ValueExpr condition) throws QueryExpansionException {
        if (condition instanceof Or){
           Or or = (Or)condition;
           findandWriteDescribeVariable(or.getLeftArg());
           findandWriteDescribeVariable(or.getRightArg());
        } else if (condition instanceof SameTerm) {
           SameTerm term = (SameTerm)condition;
           String leftName = extractName(term.getLeftArg());
           if (" ?-descr-subj".equals(leftName)){
               writeDescribeVariable(term.getRightArg());
           } else {
               //System.out.println(leftName);
           }
        } else {
            throw new QueryExpansionException ("Expected Or when extracting DescribeVariable");
        }
    }
    
    //currently unit test fails.
    String extractName(ValueExpr expr) throws QueryExpansionException{
        if (expr instanceof Var){
            Var var = (Var)expr;
            String name = var.getName();
            if (var != null){
                return " ?" + name;
            } else {
                throw new QueryExpansionException ("Expected null name when extracting a name");
            }
        } if (expr instanceof ValueConstant){
            Value value = ((ValueConstant)expr).getValue();
            if (value instanceof URI){
                return getUriString((URI) value);
               
            } else {
                return value.stringValue();
            }
        } else {
            throw new QueryExpansionException ("Expected Var when extracting a name");
        }
    }
    
    /**
     * Gets the String for a URI.
     * 
     * Designed to be overwritten by a method that can get mapped uris.
     * 
     * @param uri
     * @return 
     */
    String getUriString(URI uri){
         return (" <" + uri.stringValue() + "> "); 
    }
    
    @Override
    public void meet(Filter filter) throws QueryExpansionException {
        newLine();
        queryString.append("FILTER ");
        filter.getCondition().visit(this);
        //Arguements add the brackets
        filter.getArg().visit(this);
    }

    @Override
    public void meet(SingletonSet ss) throws QueryExpansionException {
        //Expected no children but just to be sure.
        ss.visitChildren(this);
    }

    /**
     * Write the var.
     * 
     * Sub classes will do fancy things here.
     * 
     * @param var Var to be written.
     * @throws QueryExpansionException 
     */
    void writeStatementPart(Var var) throws QueryExpansionException{
        meet(var);
    }

    //@Override
    public void meet(StatementPattern sp) throws QueryExpansionException  {
        //Double check that then statement has the expected context 
        if (contexts.get(0) == null){
            if (sp.getContextVar() != null) {
                throw new QueryExpansionException ("Expected null context in statement: " + sp);
            }
        } else {
            if (!(contexts.get(0).equals(sp.getContextVar()))) {
               throw new QueryExpansionException ("Expected context  " + contexts.get(0) + " in statement: " + sp); 
            }
        }
        
        //Remove the context from the list, so it only has future contexts in it.
        contexts.remove(sp.getContextVar());       
        
        openNewContextIfRequired(sp); 
        
        //Add an optional pushed down if required.
        if (swapGraphAndOptional) {
            newLine();
            queryString.append("OPTIONAL { #meet(StatementPattern sp)");
            swapGraphAndOptional = false;
        }
        
        if (isDescribePattern(sp)) {
            //No need to write the describe pattern the parser will do that
        } else if (canEliminate(sp)) {
            //No need to write a pattern for eliminated elements.
        } else {
            //write the actual statement.
            newLine();
            writeStatementPart(sp.getSubjectVar());
            sp.getPredicateVar().visit(this);
            writeStatementPart(sp.getObjectVar());
            queryString.append(". ");
        }
        //Now use the look ahead provided by the context list. 
        if (contexts.isEmpty()){
            //Last Statement so close and flush filters
            closeContext();
        } else if (context == null){
            //Not in a context so flush replacement filters        
           closeContext(); 
        } else if (context.equals(contexts.get(0))){
            //staying in context so keep it open
        } else {
            //New context coming so close the context
            closeContext();
        }
    }

    /**
     * Close the context (GRAPH clause) and any optional clauses opened inside the graph.
     * 
     * If this method is called while not in a context no action is taken.
     * <p>
     * Subclasses with overwrite this method to add behavior such as adding URi replacement filters.
     */
    void closeContext(){
       //Only do something is inside a context
       if (context != null){
            //If any optional clauses where opened inside the graph these need to be closed first.
            //Not strictly required here but keeps it similar to overwritten methods.
            while (optionInGraph > 0){
                newLine();
                queryString.append(" } #OPTIONAL from close context optionInGraph");   
                //reduce the count so it is not closed again.
                optionInGraph--;
                newLine();
            }
            newLine();
            queryString.append(" } # close Context");
            newLine();
            //Set context to null so it is not closed again.
            context = null;
        }
    }

    /**
     * Checks to see if the statement can be removed from the query.
     * 
     * WARNING: Underdevelopment so uses the niave system of if the statement conatins a project element not in the 
     *    attribute list remove the statement. Otherwise keep it
     * @param sp Statement to check
     * @return If the statement is predicted to be removed.
     * @throws QueryExpansionException 
     */
    boolean canEliminate(StatementPattern sp) throws QueryExpansionException {
        //ystem.out.println(sp);
        if (sp.getSubjectVar().isAnonymous()){
            //We don't think subject variables can be literals but just in case.
            if (sp.getObjectVar().isAnonymous()) {
                //ystem.out.println("literal predicate literal");
                return false;
            } else {
                if (canEliminate(sp.getObjectVar().getName())){
                    //ystem.out.println("literal predicate remove");
                    return true;
                } else {
                    //ystem.out.println("literal predicate keep");
                    return false;
                }
            }
        } else {
            if (canEliminate(sp.getSubjectVar().getName())){
                if (sp.getObjectVar().isAnonymous()) {
                    //ystem.out.println("remove predicate literal");
                    return true;
                } else {
                    if (canEliminate(sp.getObjectVar().getName())){
                        //ystem.out.println("remove predicate remove");
                        return true;
                    } else {
                        //remove predicate keep
                        throw new QueryExpansionException ("Statement has an Eliminate variable ( " + 
                                sp.getObjectVar().getName() +") and a Keep variable (" + 
                                sp.getObjectVar().getName() + ")");
                    }
                }
            } else {
                if (sp.getObjectVar().isAnonymous()) {
                    //ystem.out.println("keep predicate literal");
                    return false;
                } else {
                    if (canEliminate(sp.getObjectVar().getName())){
                         //keep predicate remove
                        throw new QueryExpansionException ("Statement has a Keep variable ( " + 
                                sp.getObjectVar().getName() +") and an Eliminate variable (" + 
                                sp.getObjectVar().getName() + ")");
                    } else {
                        //stem.out.println("keep predicate keep");
                        return false;
                    }
                }
            }
        } 
    }
    
    /**
     * Checks to see if the name can be removed from the query.
     * 
     * WARNING: Underdevelopment so uses the niave system of if the statement conatins a project element not in the 
     *    attribute list remove the statement. Otherwise keep it
     * 
     * @param name Name of the Attribute
     * @return 
     */
    private boolean canEliminate(String name) {
        if (requiredAttributes == null) {
            return false;
        }
        if (requiredAttributes.contains(name)){
            return false;
        }
        if (eliminatedAttributes.contains(name)){
            return true;
        }
        String[] parts = name.split("_");
        for (String part:parts){
            if (!eliminatedAttributes.contains(part)){
                return false;
            }
        }
        return false;
    }

    /**
     * Identifies if this Statement is the one added by a design query. 
     * <p>
     * This method was built based on a few example queries so may be incomplete.
     * 
     * @param sp Statement to check for the design signature. 
     * @return True if and only if the Statement appears to be one added by a Design query.
     */
    boolean isDescribePattern(StatementPattern sp){
        if (!(sp.getSubjectVar().isAnonymous())) return false;
        if (!(sp.getPredicateVar().isAnonymous())) return false;
        if (!(sp.getObjectVar().isAnonymous())) return false;
        if (!("-descr-subj".equals(sp.getSubjectVar().getName()))) return false;
        if (!("-descr-pred".equals(sp.getPredicateVar().getName()))) return false;
        if (!("-descr-obj".equals(sp.getObjectVar().getName()))) return false;
        return true;        
    }
    
    /**
     * Checks if a new context GRAPH) needs to be opend and does so if required.
     * 
     * @param sp Statement about to be written.
     * @throws QueryExpansionException Not expected but just in case.
     */
    private void openNewContextIfRequired(StatementPattern sp) throws QueryExpansionException {
        //Check not already in a context
        if (context == null) {
           //Check statement is part of a context 
           if (sp.getContextVar() != null) {
                context = sp.getContextVar();
                newLine();
                queryString.append(" GRAPH ");
                context.visit(this);
                queryString.append(" {");   
            }
        }
    }

    @Override
    public void meet(Str str) throws QueryExpansionException {
        queryString.append(" STR(");
        str.getArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(Union union) throws QueryExpansionException {
        queryString.append("{");
        union.getLeftArg().visit(this);
        newLine();
        queryString.append("} UNION {");
        union.getRightArg().visit(this);
        newLine();
        queryString.append("}");
    }

    @Override
    public void meet(ValueConstant vc) throws QueryExpansionException {
        Value value = vc.getValue();
        addValue(value);
    }

    @Override
    public void meet(Var var) throws QueryExpansionException {
        if (var.hasValue()){
            Value value = var.getValue();
            addValue(value);
        } else if (var.isAnonymous()){
            writeAnon(var.getName());
        } else {
            queryString.append(" ?");
            queryString.append(var.getName());
        }
    }

    @Override
    public void meetOther(QueryModelNode qmn) throws QueryExpansionException {
        throw new QueryExpansionException("meetOther not supported yet.");
    }
    
    private void addValue(Value value){
        if (value instanceof URI){
            queryString.append("<");
            queryString.append(value.stringValue());
            queryString.append(">"); 
        } else {
            queryString.append(value);
        }
    }
    
    void newLine(){
        queryString.append("\n");
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

    public static String convertToQueryString(TupleExpr tupleExpr) throws QueryExpansionException{
        return convertToQueryString(tupleExpr, null, null);
    } 
    
    public static String convertToQueryString(TupleExpr tupleExpr, List<String> requiredAttributes) 
            throws QueryExpansionException{
        return convertToQueryString(tupleExpr, null, requiredAttributes);
    }
    
    public static String convertToQueryString(TupleExpr tupleExpr, Dataset dataSet, List<String> requiredAttributes) 
            throws QueryExpansionException{
        ContextListerVisitor counter = new ContextListerVisitor();
        tupleExpr.visit(counter);
        ArrayList<Var> contexts = counter.getContexts();
       
        QueryWriterModelVisitor writer = new QueryWriterModelVisitor(dataSet, requiredAttributes, contexts);
        tupleExpr.visit(writer);
        return writer.getQuery();
    }
}
