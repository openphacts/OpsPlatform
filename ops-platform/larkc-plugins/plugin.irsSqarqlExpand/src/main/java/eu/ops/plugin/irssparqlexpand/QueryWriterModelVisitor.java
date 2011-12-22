/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import java.util.HashMap;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
public class QueryWriterModelVisitor implements QueryModelVisitor<UnexpectedQueryException>{
    
    StringBuilder queryString = new StringBuilder();
    
    @Override
    public void meet(QueryRoot qr) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("QueryRoot not supported yet.");
    }

    @Override
    public void meet(And and) throws UnexpectedQueryException {
        queryString.append("(");
        and.getLeftArg().visit(this);
        queryString.append(" && ");
        and.getRightArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(BNodeGenerator bng) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("BNodeGenerator not supported yet.");
    }

    @Override
    public void meet(Bound bound) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Bound not supported yet.");
    }

    @Override
    public void meet(Compare cmpr) throws UnexpectedQueryException {
        queryString.append("(");
        cmpr.getLeftArg().visit(this);
        queryString.append(" ");
        queryString.append(cmpr.getOperator().getSymbol());
        queryString.append(" ");
        cmpr.getRightArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(CompareAll ca) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("CompareAl not supported yet.");
    }

    @Override
    public void meet(CompareAny ca) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("CompareAny not supported yet.");
    }

    @Override
    public void meet(Count count) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Count not supported yet.");
    }

    @Override
    public void meet(Datatype dtp) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Datatype not supported yet.");
    }

    @Override
    public void meet(Difference dfrnc) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Difference not supported yet.");
    }

    @Override
    public void meet(Distinct dstnct) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Distinct  not supported yet.");
    }

    @Override
    public void meet(EmptySet es) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("EmptySet not supported yet.");
    }

    @Override
    public void meet(Exists exists) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Exists not supported yet.");
    }

    @Override
    public void meet(Extension extnsn) throws UnexpectedQueryException {
        //I assume that this is also part of the ProjectionElemList so not required again.
        //extnsn.getElements();
        extnsn.getArg().visit(this);
    }

    @Override
    public void meet(ExtensionElem ee) throws UnexpectedQueryException {
        //possibly never called as meet(Extension extnsn) ignores this part
        throw new UnexpectedQueryException("ExtensionElem not supported yet.");
    }

    @Override
    public void meet(FunctionCall fc) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("FunctionCall not supported yet.");
    }

    @Override
    public void meet(Group group) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Group not supported yet.");
    }

    @Override
    public void meet(GroupElem ge) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("GroupElem not supported yet.");
    }

    @Override
    public void meet(In in) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("In not supported yet.");
    }

    @Override
    public void meet(Intersection i) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Intersection not supported yet.");
    }

    @Override
    public void meet(IsBNode ibn) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("IsBNode not supported yet.");
    }

    @Override
    public void meet(IsLiteral il) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("IsLiteral not supported yet.");
    }

    @Override
    public void meet(IsResource ir) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("IsResource not supported yet.");
    }

    @Override
    public void meet(IsURI isuri) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("IsURI not supported yet.");
    }

    @Override
    public void meet(Join join) throws UnexpectedQueryException {
        join.getLeftArg().visit(this);
        join.getRightArg().visit(this);
    }

    @Override
    public void meet(Label label) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Label not supported yet.");
    }

    @Override
    public void meet(Lang lang) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Lang not supported yet.");
    }

    @Override
    public void meet(LangMatches lm) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("LangMatches not supported yet.");
    }

    @Override
    public void meet(Like like) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Like not supported yet.");
    }

    @Override
    public void meet(LocalName ln) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("LocalName not supported yet.");
    }

    @Override
    public void meet(MathExpr me) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("MathExpr not supported yet.");
    }

    @Override
    public void meet(Max max) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Max not supported yet.");
    }

    @Override
    public void meet(Min min) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Min not supported yet.");
    }

    @Override
    public void meet(MultiProjection mp) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("MultiProjection not supported yet.");
    }

    @Override
    public void meet(Namespace nmspc) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Namespace not supported yet.");
    }

    @Override
    public void meet(Not not) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("\"Not\" not supported yet.");
    }

    @Override
    public void meet(LeftJoin lj) throws UnexpectedQueryException {
        lj.getLeftArg().visit(this);
        queryString.append("OPTIONAL {");
        lj.getRightArg().visit(this);
        queryString.append("}");
    }

    @Override
    public void meet(Or or) throws UnexpectedQueryException {
        queryString.append("(");
        or.getLeftArg().visit(this);
        queryString.append(" || ");
        or.getRightArg().visit(this);
        queryString.append(")");
    }

    @Override
    public void meet(Order order) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Order not supported yet.");
    }

    @Override
    public void meet(OrderElem oe) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("OrderElem not supported yet.");
    }

    @Override
    public void meet(Projection prjctn) throws UnexpectedQueryException {
        queryString.append("SELECT ");
        prjctn.getProjectionElemList().visit(this);
        newLine();
        queryString.append("{");
        prjctn.getArg().visit(this);
        queryString.append("}");
    }

    @Override
    public void meet(ProjectionElemList pel) throws UnexpectedQueryException {
        List<ProjectionElem> elements = pel.getElements();
        for (ProjectionElem element:elements){
            element.visit(this);
        }
    }

    /**
     * Used by the Reduce (Construction query) to add looked ahead ExtensionElems
     *
     * @param pel
     * @param mappedExstensionElements
     * @throws UnexpectedQueryException 
     */
    private void meet(ProjectionElemList pel, HashMap<String, ValueExpr> mappedExstensionElements) throws UnexpectedQueryException {
        List<ProjectionElem> elements = pel.getElements();
        for (ProjectionElem element:elements){
            meet(element, mappedExstensionElements);
        }
    }

    @Override
    public void meet(ProjectionElem pe) throws UnexpectedQueryException {
        queryString.append(" ?");
        queryString.append(pe.getSourceName());
    }

    /**
     * Used by the Reduce (Construction query) to add looked ahead ExtensionElems
     * 
     * @param pe
     * @param mappedExstensionElements
     * @throws UnexpectedQueryException 
     */
    private void meet(ProjectionElem pe, HashMap<String, ValueExpr> mappedExstensionElements) throws UnexpectedQueryException {
        String name = pe.getSourceName();
        ValueExpr mapped = mappedExstensionElements.get(name);
        if (mapped == null){
            queryString.append(" ?");
            queryString.append(pe.getSourceName());
        } else {
            mapped.visit(this);
        }
    }

    @Override
    public void meet(Reduced rdcd) throws UnexpectedQueryException {
        queryString.append("CONSTRUCT {");
        TupleExpr arg = rdcd.getArg();
        if (arg instanceof Projection){
            Projection prjctn = (Projection)arg;
            HashMap<String, ValueExpr> mappedExstensionElements = mapExensionElements(prjctn);
            meet (prjctn.getProjectionElemList(), mappedExstensionElements);
            queryString.append("}");
            newLine();
            queryString.append("{");
            prjctn.getArg().visit(this);
            queryString.append("}");
        } else {
            throw new UnexpectedQueryException("Reduced with non projection child supported yet.");
        }
    }

    //Look ahead function to match names ProjectionElem to ExtensionElem
    private HashMap<String, ValueExpr> mapExensionElements(Projection prjctn) throws UnexpectedQueryException{
        HashMap<String, ValueExpr> mappedExstensionElements = new HashMap<String, ValueExpr>();
        TupleExpr prjctnArg = prjctn.getArg();
        if (prjctnArg instanceof Extension){
            Extension extnsn = (Extension) prjctnArg;
            List<ExtensionElem> ees =  extnsn.getElements();
            for (ExtensionElem ee:ees) {
                mappedExstensionElements.put(ee.getName(), ee.getExpr());
            }
        } else {
            throw new UnexpectedQueryException ("Project Arguement of Reduce(Consttruct query) was not an Extension.");
        }
        return  mappedExstensionElements;
    }
    
    @Override
    public void meet(Regex regex) throws UnexpectedQueryException {
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
    public void meet(Slice slice) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Slice not supported yet.");
    }

    @Override
    public void meet(SameTerm st) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("SameTerm not supported yet.");
    }

    @Override
    public void meet(Filter filter) throws UnexpectedQueryException {
        newLine();
        queryString.append("FILTER (");
        filter.getCondition().visit(this);
        queryString.append(")");
        filter.getArg().visit(this);
    }

    @Override
    public void meet(SingletonSet ss) throws UnexpectedQueryException {
        //Expected no children but just to be sure.
        ss.visitChildren(this);
    }

    @Override
    public void meet(StatementPattern sp) throws UnexpectedQueryException {
        newLine();
        sp.getSubjectVar().visit(this);
        queryString.append(" ");
        sp.getPredicateVar().visit(this);
        queryString.append(" ");
        sp.getObjectVar().visit(this);
        queryString.append(" .");
    }

    @Override
    public void meet(Str str) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Str not supported yet.");
    }

    @Override
    public void meet(Union union) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("Union not supported yet.");
    }

    @Override
    public void meet(ValueConstant vc) throws UnexpectedQueryException {
        Value value = vc.getValue();
        addValue(value);
    }

    @Override
    public void meet(Var var) throws UnexpectedQueryException {
         if (var.hasValue()){
            Value value = var.getValue();
            addValue(value);
        } else {
            queryString.append(" ?");
            queryString.append(var.getName());
        }
    }

    @Override
    public void meetOther(QueryModelNode qmn) throws UnexpectedQueryException {
        throw new UnexpectedQueryException("meetOther not supported yet.");
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
     * @throws UnexpectedQueryException Declared as thrown to allow calling methods to catch it specifically.
     */
    public String getQuery() throws UnexpectedQueryException {
        return queryString.toString();
    }
}
