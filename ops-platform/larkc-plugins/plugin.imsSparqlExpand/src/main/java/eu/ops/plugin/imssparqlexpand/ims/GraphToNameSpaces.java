/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.plugin.imssparqlexpand.ims;

import java.util.List;

/**
 *
 * @author Christian
 */
public interface GraphToNameSpaces {
    
    public List<String> getNameSpacesInGraph(String graph);
}
