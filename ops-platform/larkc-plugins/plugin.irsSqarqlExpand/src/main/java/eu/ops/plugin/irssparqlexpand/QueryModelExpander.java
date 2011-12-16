/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.irssparqlexpand;

import java.util.List;
import java.util.Map;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.algebra.And;
import org.openrdf.query.algebra.BinaryValueOperator;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.Or;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.ValueExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

/**
 *
 */
class QueryModelExpander extends QueryModelVisitorBase<QueryModelExpanderException> {
    
    private Map<URI, List<URI>> uriMappings;
    private ValueFactory valueFactory = new ValueFactoryImpl();
    private int uriNumber = 1;

    public QueryModelExpander(Map<URI, List<URI>> uriMappings) {
        super();
        this.uriMappings = uriMappings;
    }
    
    @Override
    public void meet(StatementPattern sp) {
        StatementPattern newSp = sp.clone();
        BinaryValueOperator subjectFilter = null, objectFilter = null;
        Var s = sp.getSubjectVar();
        if (s.hasValue() && s.getValue() instanceof URI) {
            URI uri = (URI) s.getValue();
            Var subjectVariable = new Var("subjectUri" + uriNumber);
            uriNumber++;
            final List<URI> mappings = uriMappings.get(uri);
            mappings.add(uri);
            subjectFilter = constructCompareDisjunction(subjectVariable, mappings);
            newSp.setSubjectVar(subjectVariable);
        }
        Var o = sp.getObjectVar();
        if (o.hasValue() && o.getValue() instanceof URI) {
            URI uri = (URI) o.getValue();
            Var objectVariable = new Var("objectUri" + uriNumber);
            uriNumber++;
            final List<URI> mappings = uriMappings.get(uri);
            mappings.add(uri);
            objectFilter = constructCompareDisjunction(objectVariable, mappings);
            newSp.setObjectVar(objectVariable);
        }
        QueryModelNode parentNode = sp.getParentNode();
        if (subjectFilter != null && objectFilter != null) {
            And compareAndNode = new And(subjectFilter, objectFilter);
            if (parentNode instanceof Filter) {
                Filter filterNode = (Filter) parentNode;
                ValueExpr valueExpr = filterNode.getCondition();
                And andNode = new And(compareAndNode, valueExpr);
                filterNode.replaceChildNode(valueExpr, andNode);
                filterNode.replaceChildNode(sp, newSp);
            } else if (parentNode instanceof LeftJoin) {
                LeftJoin leftJoinNode = (LeftJoin) parentNode;
                ValueExpr valueExpr = leftJoinNode.getCondition();
                And andNode = new And(compareAndNode, valueExpr);
                leftJoinNode.replaceChildNode(valueExpr, andNode);
            } else if (parentNode instanceof Join || parentNode instanceof Projection) {
                Filter filterNode = new Filter(newSp, compareAndNode);
                parentNode.replaceChildNode(sp, filterNode);
            }
        } else if (subjectFilter != null && objectFilter == null) {
            if (parentNode instanceof Filter) {
                Filter filterNode = (Filter) parentNode;
                ValueExpr valueExpr = filterNode.getCondition();
                And andNode = new And(subjectFilter, valueExpr);
                filterNode.replaceChildNode(valueExpr, andNode);
                filterNode.replaceChildNode(sp, newSp);
            } else if (parentNode instanceof LeftJoin) {
                LeftJoin leftJoinNode = (LeftJoin) parentNode;
                ValueExpr valueExpr = leftJoinNode.getCondition();
                And andNode = new And(subjectFilter, valueExpr);
                leftJoinNode.replaceChildNode(valueExpr, andNode);
            } else if (parentNode instanceof Join || parentNode instanceof Projection) {
                Filter filterNode = new Filter(newSp, subjectFilter);
                parentNode.replaceChildNode(sp, filterNode);
            }
        } else if (subjectFilter == null && objectFilter != null) {
            if (parentNode instanceof Filter) {
                Filter filterNode = (Filter) parentNode;
                ValueExpr valueExpr = filterNode.getCondition();
                And andNode = new And(objectFilter, valueExpr);
                filterNode.replaceChildNode(valueExpr, andNode);
                filterNode.replaceChildNode(sp, newSp);
            } else if (parentNode instanceof LeftJoin) {
                LeftJoin leftJoinNode = (LeftJoin) parentNode;
                ValueExpr valueExpr = leftJoinNode.getCondition();
                And andNode = new And(objectFilter, valueExpr);
                leftJoinNode.replaceChildNode(valueExpr, andNode);
            } else if (parentNode instanceof Join || parentNode instanceof Projection) {
                Filter filterNode = new Filter(newSp, objectFilter);
                parentNode.replaceChildNode(sp, filterNode);
            }
        }
    }

    /**
     * Constructs the tree structure to represent the filter clause to check 
     * the given variable with all permutations of equivalent URIs.
     * 
     * @param variableName name of the new variable in the query
     * @param mappings List of equivalent URIs
     * @return tree representing filter clause checking the disjunction of the URIs
     */
    private BinaryValueOperator constructCompareDisjunction(Var variableName, List<URI> mappings) {
        BinaryValueOperator qmn;
        if (mappings.size() == 1) {
            qmn = constructCompare(variableName, mappings.get(0));
        } else {
            qmn = null;
            for (URI uri : mappings) {
                Compare compareNode = constructCompare(variableName, uri);
                if (qmn == null) {
                    qmn = compareNode;
                } else {
                    Or orNode = new Or(qmn, compareNode);
                    qmn = orNode;
                }
            }
        }
//System.out.print(qmn);        
        return qmn;
    }
    
    /**
     * Construct the leaf node for comparing the new query variable with one
     * of the equivalent URI values.
     * 
     * @param variable query variable introduced for a URI in the query
     * @param uri one of the equivalent URIs in the returned mappings
     * @return Compare object representing the disjunct
     */
    private Compare constructCompare(Var variable, URI uri) {
        ValueConstant value = new ValueConstant(uri);
        Compare compare = new Compare(variable, value, Compare.CompareOp.EQ);
//System.out.println(compare);
        return compare;
    }

}
