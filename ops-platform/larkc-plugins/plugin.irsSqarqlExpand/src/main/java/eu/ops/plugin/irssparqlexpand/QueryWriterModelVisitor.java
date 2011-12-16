/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

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
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;

/**
 *
 * The methods here all throw Exception because QueryModelNode.visitChildren which most methods will need to call does.
 * <p>
 * The only Exception that could actually be thrown is UnexpectedQueryException
 * @author Christian
 */
public class QueryWriterModelVisitor implements QueryModelVisitor{
    
    StringBuilder queryString = new StringBuilder();
    boolean selectElementAdded = false;
    
    @Override
    public void meet(QueryRoot qr) throws Exception {
        throw new UnexpectedQueryException("QueryRoot not supported yet.");
    }

    @Override
    public void meet(And and) throws Exception {
        throw new UnexpectedQueryException("And not supported yet.");
    }

    @Override
    public void meet(BNodeGenerator bng) throws Exception {
        throw new UnexpectedQueryException("BNodeGenerator not supported yet.");
    }

    @Override
    public void meet(Bound bound) throws Exception {
        throw new UnexpectedQueryException("Bound not supported yet.");
    }

    @Override
    public void meet(Compare cmpr) throws Exception {
        throw new UnexpectedQueryException("Compare not supported yet.");
    }

    @Override
    public void meet(CompareAll ca) throws Exception {
        throw new UnexpectedQueryException("CompareAl not supported yet.");
    }

    @Override
    public void meet(CompareAny ca) throws Exception {
        throw new UnexpectedQueryException("CompareAny not supported yet.");
    }

    @Override
    public void meet(Count count) throws Exception {
        throw new UnexpectedQueryException("Count not supported yet.");
    }

    @Override
    public void meet(Datatype dtp) throws Exception {
        throw new UnexpectedQueryException("Datatype not supported yet.");
    }

    @Override
    public void meet(Difference dfrnc) throws Exception {
        throw new UnexpectedQueryException("Difference not supported yet.");
    }

    @Override
    public void meet(Distinct dstnct) throws Exception {
        throw new UnexpectedQueryException("Distinct  not supported yet.");
    }

    @Override
    public void meet(EmptySet es) throws Exception {
        throw new UnexpectedQueryException("EmptySet not supported yet.");
    }

    @Override
    public void meet(Exists exists) throws Exception {
        throw new UnexpectedQueryException("Exists not supported yet.");
    }

    @Override
    public void meet(Extension extnsn) throws Exception {
        throw new UnexpectedQueryException("Extension not supported yet.");
    }

    @Override
    public void meet(ExtensionElem ee) throws Exception {
        throw new UnexpectedQueryException("ExtensionElem not supported yet.");
    }

    @Override
    public void meet(FunctionCall fc) throws Exception {
        throw new UnexpectedQueryException("FunctionCall not supported yet.");
    }

    @Override
    public void meet(Group group) throws Exception {
        throw new UnexpectedQueryException("Group not supported yet.");
    }

    @Override
    public void meet(GroupElem ge) throws Exception {
        throw new UnexpectedQueryException("GroupElem not supported yet.");
    }

    @Override
    public void meet(In in) throws Exception {
        throw new UnexpectedQueryException("In not supported yet.");
    }

    @Override
    public void meet(Intersection i) throws Exception {
        throw new UnexpectedQueryException("Intersection not supported yet.");
    }

    @Override
    public void meet(IsBNode ibn) throws Exception {
        throw new UnexpectedQueryException("IsBNode not supported yet.");
    }

    @Override
    public void meet(IsLiteral il) throws Exception {
        throw new UnexpectedQueryException("IsLiteral not supported yet.");
    }

    @Override
    public void meet(IsResource ir) throws Exception {
        throw new UnexpectedQueryException("IsResource not supported yet.");
    }

    @Override
    public void meet(IsURI isuri) throws Exception {
        throw new UnexpectedQueryException("IsURI not supported yet.");
    }

    private void newLine(){
        queryString.append("\n");
    }
    
    private void addWhereIfNeeded(){
        if (this.selectElementAdded){
            newLine();
            queryString.append(" WHERE {");
            selectElementAdded = false;
        }
    }
    
    @Override
    public void meet(Join join) throws Exception {
        addWhereIfNeeded();
        join.visitChildren(this);
    }

    @Override
    public void meet(Label label) throws Exception {
        throw new UnexpectedQueryException("Label not supported yet.");
    }

    @Override
    public void meet(Lang lang) throws Exception {
        throw new UnexpectedQueryException("Lang not supported yet.");
    }

    @Override
    public void meet(LangMatches lm) throws Exception {
        throw new UnexpectedQueryException("LangMatches not supported yet.");
    }

    @Override
    public void meet(Like like) throws Exception {
        throw new UnexpectedQueryException("Like not supported yet.");
    }

    @Override
    public void meet(LocalName ln) throws Exception {
        throw new UnexpectedQueryException("LocalName not supported yet.");
    }

    @Override
    public void meet(MathExpr me) throws Exception {
        throw new UnexpectedQueryException("MathExpr not supported yet.");
    }

    @Override
    public void meet(Max max) throws Exception {
        throw new UnexpectedQueryException("Max not supported yet.");
    }

    @Override
    public void meet(Min min) throws Exception {
        throw new UnexpectedQueryException("Min not supported yet.");
    }

    @Override
    public void meet(MultiProjection mp) throws Exception {
        throw new UnexpectedQueryException("MultiProjection not supported yet.");
    }

    @Override
    public void meet(Namespace nmspc) throws Exception {
        throw new UnexpectedQueryException("Namespace not supported yet.");
    }

    @Override
    public void meet(Not not) throws Exception {
        throw new UnexpectedQueryException("\"Not\" not supported yet.");
    }

    @Override
    public void meet(LeftJoin lj) throws Exception {
        throw new UnexpectedQueryException("LeftJoin not supported yet.");
    }

    @Override
    public void meet(Or or) throws Exception {
        throw new UnexpectedQueryException("Or not supported yet.");
    }

    @Override
    public void meet(Order order) throws Exception {
        throw new UnexpectedQueryException("Order not supported yet.");
    }

    @Override
    public void meet(OrderElem oe) throws Exception {
        throw new UnexpectedQueryException("OrderElem not supported yet.");
    }

    @Override
    public void meet(Projection prjctn) throws Exception {
        System.out.println("projection");
        prjctn.visitChildren(this);
    }

    @Override
    public void meet(ProjectionElemList pel) throws Exception {
        queryString.append("SELECT ");
        selectElementAdded = false;
        pel.visitChildren(this);
    }

    @Override
    public void meet(ProjectionElem pe) throws Exception {
        if (selectElementAdded){
            queryString.append(", ");    
        } else {
            selectElementAdded = true;
        }
        queryString.append("?");
        queryString.append(pe.getSourceName());
        //queryString.append(" as ?");
        //queryString.append(pe.getTargetName() + "x");
        //queryString.append(")");
        //Don't expect any children but just in case
        pe.visitChildren(this);
    }

    @Override
    public void meet(Reduced rdcd) throws Exception {
        throw new UnexpectedQueryException("Reduced not supported yet.");
    }

    @Override
    public void meet(Regex regex) throws Exception {
        throw new UnexpectedQueryException("Regex not supported yet.");
    }

    @Override
    public void meet(Slice slice) throws Exception {
        throw new UnexpectedQueryException("Slice not supported yet.");
    }

    @Override
    public void meet(SameTerm st) throws Exception {
        throw new UnexpectedQueryException("SameTerm not supported yet.");
    }

    @Override
    public void meet(Filter filter) throws Exception {
        throw new UnexpectedQueryException("Filter not supported yet.");
    }

    @Override
    public void meet(SingletonSet ss) throws Exception {
        throw new UnexpectedQueryException("SingletonSet not supported yet.");
    }

    @Override
    public void meet(StatementPattern sp) throws Exception {
        newLine();
        sp.visitChildren(this);
        queryString.append(" .");
    }

    @Override
    public void meet(Str str) throws Exception {
        throw new UnexpectedQueryException("Str not supported yet.");
    }

    @Override
    public void meet(Union union) throws Exception {
        throw new UnexpectedQueryException("Union not supported yet.");
    }

    @Override
    public void meet(ValueConstant vc) throws Exception {
        throw new UnexpectedQueryException("ValueConstant not supported yet.");
    }

    @Override
    public void meet(Var var) throws Exception {
        System.out.print(var);
         if (var.hasValue()){
            queryString.append(" ");
            Value value = var.getValue();
            if (value instanceof URI){
                queryString.append("<");
                queryString.append(value.stringValue());
                queryString.append(">");                
            } else {
                queryString.append(var.getValue());
            }
        } else {
            queryString.append(" ?");
            queryString.append(var.getName());
        }
        //Don't expect any children but just in case
        var.visitChildren(this);
    }

    @Override
    public void meetOther(QueryModelNode qmn) throws Exception {
        throw new UnexpectedQueryException("meetOther not supported yet.");
    }
    
    /**
     * Returns the query as a string.
     * <p>
     * Works if and only if the model was visited exactly once.
     * @return query asa String
     * @throws UnexpectedQueryException Declared as thrown to allow calling methods to catch it specifically.
     */
    public String getQuery() throws UnexpectedQueryException {
        queryString.append("}");
        return queryString.toString();
    }
}
