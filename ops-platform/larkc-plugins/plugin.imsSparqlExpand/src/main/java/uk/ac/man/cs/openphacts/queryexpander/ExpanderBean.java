package uk.ac.man.cs.openphacts.queryexpander;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the XML/ Jason object returned by the WebService.
 * 
 * Future plans include adding still to be determined provenance information here.
 * 
 * @author Christian
 */
@XmlRootElement(name="URL")
public class ExpanderBean {
    
    private String orginalQuery;
    
    private String expandedQuery;
    
    /**
     * Default constructor for bean creator
     */
    public ExpanderBean(){
    }

    /**
     * @return The orginal unexpanded Query.
     */
    public String getOrginalQuery() {
        return orginalQuery;
    }

    /**
     * @param orginalQuery the orginalQuery to set
     */
    public void setOrginalQuery(String orginalQuery) {
        this.orginalQuery = orginalQuery;
    }

    /**
     * @return the expandedQuery
     */
    public String getExpandedQuery() {
        return expandedQuery;
    }

    /**
     * @param expandedQuery the expandedQuery to set
     */
    public void setExpandedQuery(String expandedQuery) {
        this.expandedQuery = expandedQuery;
    }
}
