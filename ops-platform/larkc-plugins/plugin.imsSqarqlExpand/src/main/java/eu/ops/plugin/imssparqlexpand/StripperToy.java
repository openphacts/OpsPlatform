/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.imssparqlexpand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.TupleExpr;

/**
 *
 * @author Christian
 */
public class StripperToy {
    
    public static void main1(String[] args) throws MalformedQueryException, Exception {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
                + "SELECT ?foo ?bar "
                + "{ "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:name ?foo . "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:age ?bar . "
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
                + "SELECT ?foo "
                + "{ "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:name ?foo . "
               + "}";
        TupleExpr tupleExpr = QueryUtils.queryStringToTupleExpr(inputQuery);
        System.out.println(tupleExpr);
        List<String> requiredAttributes = new ArrayList<String>();
        requiredAttributes.add("foo");
        String newQuery = QueryUtils.tupleExprToQueryString(tupleExpr, requiredAttributes);
        System.out.println(newQuery);
        if (QueryUtils.sameTupleExpr(expectedQuery,  newQuery)) {
             System.out.println("ok");
        }
    }

    public static void main(String[] args) throws MalformedQueryException, Exception {
        String inputQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
                + "SELECT ?foo ?bar "
                + "{ "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:name ?foo . "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:age ?bar . "
                + "}";
        String expectedQuery = "PREFIX foaf:   <http://xmlns.com/foaf/0.1/> "
                + "SELECT ?bar ?foo "
                + "{ "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:name ?foo . "
                + "<http://brenda-enzymes.info/1.1.1.1> foaf:age ?bar . "
                + "}";
        TupleExpr tupleExpr = QueryUtils.queryStringToTupleExpr(inputQuery);
        System.out.println(tupleExpr);
        List<String> requiredAttributes = new ArrayList<String>();
        requiredAttributes.add("bar");
        requiredAttributes.add("foo");
        String newQuery = QueryUtils.tupleExprToQueryString(tupleExpr, requiredAttributes);
        System.out.println(newQuery);
        if (QueryUtils.sameTupleExpr(expectedQuery,  newQuery)) {
             System.out.println("ok");
        }
    }
    

}
