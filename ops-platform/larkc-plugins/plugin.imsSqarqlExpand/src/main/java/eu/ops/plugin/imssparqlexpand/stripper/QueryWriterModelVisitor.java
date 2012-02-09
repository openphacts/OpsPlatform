package eu.ops.plugin.imssparqlexpand.stripper;

import eu.ops.plugin.imssparqlexpand.ContextFinderVisitor;
import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import eu.ops.plugin.imssparqlexpand.querywriter.QueryType;
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
 * First attempt at stripping out unrequired attributes.
 * 
 * Currently niavely strips out the Statements with unrequired attributes.
 * 
 * Old code so incorrectly handles graph and optional clauses.
 * 
 * @author Christian
 */
public class QueryWriterModelVisitor implements QueryModelVisitor<QueryExpansionException>{
    
    StringBuilder queryString = new StringBuilder();
    Dataset originalDataSet;
    boolean inContext = false;
    List<String>  requiredAttributes;
    Set<String> eliminatedAttributes;

    QueryWriterModelVisitor(Dataset dataSet, List<String> requiredAttributes){
        originalDataSet = dataSet;
        eliminatedAttributes = new HashSet<String>();
        if (requiredAttributes == null || requiredAttributes.isEmpty()) {
            this.requiredAttributes = null;
        } else {
            this.requiredAttributes = requiredAttributes;
        }
    }

    QueryWriterModelVisitor(Dataset dataSet){
        this(dataSet, null);
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
        //ystem.out.println("join");
        boolean newContext = startContext(join); 
        join.getLeftArg().visit(this);
        join.getRightArg().visit(this);
        closeContext(newContext);
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

    @Override
    public void meet(LeftJoin lj) throws QueryExpansionException {
        boolean newContext = startContext(lj); 
        lj.getLeftArg().visit(this);
        newLine();
        queryString.append("OPTIONAL {");
        lj.getRightArg().visit(this);
        if (lj.hasCondition()){
            newLine();
            queryString.append("    FILTER ");
            lj.getCondition().visit(this);
        }
        queryString.append("}");
        closeContext(newContext);
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

    private void closeProjectionUnlessOrderHas(TupleExpr expr){
        if (expr instanceof Order) return;
        if (expr instanceof Extension){
            Extension extnsn = (Extension) expr;
            if (extnsn.getArg() instanceof Order) return;
        }
        queryString.append("}");
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
     * Note; This method is built from a few test cases and is probably far from complete.
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
    private void meet(List<ProjectionElemList> pels, HashMap<String, ValueExpr> mappedExstensionElements) throws QueryExpansionException {
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
    private void meet(ProjectionElemList pel, HashMap<String, ValueExpr> mappedExstensionElements) throws QueryExpansionException {
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
    private void meet(ProjectionElem pe, HashMap<String, ValueExpr> mappedExstensionElements) throws QueryExpansionException {
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

    private void writeDescribe(Filter filter) throws QueryExpansionException {
        queryString.append ("DESCRIBE ");
        ValueExpr condition = filter.getCondition();
        writeDescribeVariable(condition);
        TupleExpr arg = filter.getArg();
        if (arg instanceof StatementPattern){
            //Do nothing as only statement patter is the automatically added -descr-subj -descr-pred -descr-obj
        } else {
            queryString.append (" {");
            filter.getArg().visit(this);
            queryString.append (" }");
        }
    }

    private void writeDescribeVariable(ValueExpr condition) throws QueryExpansionException {
        if (condition instanceof Or){
           Or or = (Or)condition;
           writeDescribeVariable(or.getLeftArg());
           writeDescribeVariable(or.getRightArg());
        } else if (condition instanceof SameTerm) {
           SameTerm term = (SameTerm)condition;
           String leftName = extractName(term.getLeftArg());
           if (" ?-descr-subj".equals(leftName)){
               queryString.append(extractName(term.getRightArg()));
           } else {
               System.out.println(leftName);
           }
        } else {
            throw new QueryExpansionException ("Expected Or when extracting DescribeVariable");
        }
    }
    
    private String extractName(ValueExpr expr) throws QueryExpansionException{
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
         return (" <" + uri.stringValue() + ">"); 
    }
    
    @Override
    public void meet(Filter filter) throws QueryExpansionException {
        newLine();
        queryString.append("FILTER ");
        filter.getCondition().visit(this);
        //queryString.append(")");
        filter.getArg().visit(this);
    }

    @Override
    public void meet(SingletonSet ss) throws QueryExpansionException {
        //Expected no children but just to be sure.
        ss.visitChildren(this);
    }

    boolean startContext(TupleExpr expr) throws QueryExpansionException{
        if (inContext) return false;
        ContextFinderVisitor contextFinder = new ContextFinderVisitor();
        expr.visit(contextFinder);
        Var context = contextFinder.getContext();   
        if (context == null) {
            return false;
        } else {
            queryString.append(" GRAPH ");
            context.visit(this);
            queryString.append(" {");   
            inContext = true;
            return true;
        }
    }
    
    void closeContext (boolean startedHere){
        if (startedHere){
            queryString.append(" } ");
            inContext = false;
        }
    }
    //Make sure to add changes to QueryExpandAndWriteVisititor too!
    @Override
    public void meet(StatementPattern sp) throws QueryExpansionException {
        if (isDescribePattern(sp)) return;
        if (canEliminate(sp)) return;
        newLine();
        boolean newContext = startContext(sp); 
        sp.getSubjectVar().visit(this);
        queryString.append(" ");
        sp.getPredicateVar().visit(this);
        queryString.append(" ");
        sp.getObjectVar().visit(this);
        closeContext(newContext);
        queryString.append(". ");
    }

    boolean canEliminate(StatementPattern sp) throws QueryExpansionException{
        System.out.println(sp);
        if (sp.getSubjectVar().isAnonymous()){
            //We don't think subject variables can be literals but just in case.
            if (sp.getObjectVar().isAnonymous()) {
                //literal predicate literal
                System.out.println("literal predicate literal");
                return false;
            } else {
                if (canEliminate(sp.getObjectVar().getName())){
                     //literal predicate remove
                    System.out.println("literal predicate remove");
                    return true;
                } else {
                    //literal predicate keep
                    System.out.println("literal predicate keep");
                    return false;
                }
            }
        } else {
            if (canEliminate(sp.getSubjectVar().getName())){
                if (sp.getObjectVar().isAnonymous()) {
                    //remove predicate literal
                    System.out.println("remove predicate literal");
                    return true;
                } else {
                    if (canEliminate(sp.getObjectVar().getName())){
                         //remove predicate remove
                        System.out.println("remove predicate remove");
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
                    //keep predicate literal
                    System.out.println("keep predicate literal");
                    return false;
                } else {
                    if (canEliminate(sp.getObjectVar().getName())){
                         //keep predicate remove
                        throw new QueryExpansionException ("Statement has a Keep variable ( " + 
                                sp.getObjectVar().getName() +") and an Eliminate variable (" + 
                                sp.getObjectVar().getName() + ")");
                    } else {
                        //keep predicate keep
                        System.out.println("keep predicate keep");
                        return false;
                    }
                }
            }
        } 
    }
    
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

    boolean isDescribePattern(StatementPattern sp){
        if (!(sp.getSubjectVar().isAnonymous())) return false;
        if (!(sp.getPredicateVar().isAnonymous())) return false;
        if (!(sp.getObjectVar().isAnonymous())) return false;
        if (!("-descr-subj".equals(sp.getSubjectVar().getName()))) return false;
        if (!("-descr-pred".equals(sp.getPredicateVar().getName()))) return false;
        if (!("-descr-obj".equals(sp.getObjectVar().getName()))) return false;
        return true;        
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
    public String getQuery() throws QueryExpansionException {
        return queryString.toString();
    }

}
