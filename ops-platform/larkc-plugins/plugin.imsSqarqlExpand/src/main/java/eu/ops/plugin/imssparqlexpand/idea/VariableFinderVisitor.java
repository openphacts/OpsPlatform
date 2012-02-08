/*
 * Early work in progress for trying to find which part of the query to keep.
 */
package eu.ops.plugin.imssparqlexpand.idea;

import eu.ops.plugin.imssparqlexpand.QueryExpansionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 *
 * @author Christian
 */
public class VariableFinderVisitor extends QueryModelVisitorBase<QueryExpansionException>{
 
    List<String> projectAttributes = new ArrayList<String>();
    Set<String> deleteAttributes = new HashSet<String>();
    Set<String> existentialAttributes = new HashSet<String>();
    
    @Override
    public void meet(ProjectionElem pe) throws QueryExpansionException {
        projectAttributes.add(pe.getSourceName());
    }
    
    @Override
    public void meet(StatementPattern sp) throws QueryExpansionException {
        Var subject = sp.getSubjectVar();
        AttributeType subjectType = getAttributeType(subject);       
        Var object = sp.getObjectVar();
        AttributeType objectType = getAttributeType(object);
        switch (subjectType){
            case LITTERAL: 
                switch (objectType){
                    case LITTERAL: 
                        return;
                    case PROJECTION:  
                        return;
                    case EXISTENTIAL:  
                        return;
                    case REMOVE:  
                        return;
                    case UNDETTERMINED:  
                        System.out.println (subject);
                        return;
                }
            case PROJECTION: 
                switch (objectType){
                    case LITTERAL: 
                        return;
                    case PROJECTION:  
                        return;
                    case EXISTENTIAL:  
                        return;
                    case REMOVE:  
                        throw new QueryExpansionException("Illegal mix of a Projection and an a Remove in the same statement "+ sp);
                    case UNDETTERMINED: 
                        existentialAttributes.add(object.getName());
                        return;
                }
            case EXISTENTIAL: 
                switch (objectType){
                    case LITTERAL: 
                        return;
                    case PROJECTION:  
                        return;
                    case EXISTENTIAL:  
                        return;
                    case REMOVE:  
                        return;
                    case UNDETTERMINED:  
                        return;
                }
            case REMOVE: 
                switch (objectType){
                    case LITTERAL: 
                        return;
                    case PROJECTION:  
                        return;
                    case EXISTENTIAL:  
                        return;
                    case REMOVE:  
                        return;
                    case UNDETTERMINED:  
                        return;
                }
            case UNDETTERMINED:
                switch (objectType){
                    case LITTERAL: 
                        return;
                    case PROJECTION:  
                        return;
                    case EXISTENTIAL:  
                        return;
                    case REMOVE:  
                        return;
                    case UNDETTERMINED:  
                        return;
                }
        }
    }

    private AttributeType getAttributeType (Var var){
        if (var.isAnonymous()) {
            return AttributeType.LITTERAL;
        }
        if (projectAttributes.contains(var.getName())){
            return AttributeType.PROJECTION;
        }
        if (deleteAttributes.contains(var.getName())){
            return AttributeType.REMOVE;
        }
        if (existentialAttributes.contains(var.getName())){
            return AttributeType.EXISTENTIAL;
        }
        return AttributeType.UNDETTERMINED
        ;
    }
    
    public static void main(String[] args) throws Exception {
        String queryStr = "PREFIX  fred:     <http://fred.org/types#> "
              + "PREFIX joe:    <http://joe.com/stuff#> "
              + "SELECT ?name ?Name ?sub1 ?sub2 "
              + "WHERE {"
              + "    ?sub1 fred:name ?temp ."
              + "    ?sub2 joe:name ?temp ."
              + "}";
        SPARQLParser parser = new SPARQLParser();
        ParsedQuery parsedQuery = parser.parseQuery(queryStr, null); 
        TupleExpr tupleExpr = parsedQuery.getTupleExpr();
        VariableFinderVisitor visitor = new VariableFinderVisitor();
        tupleExpr.visit(visitor);
    }
}
