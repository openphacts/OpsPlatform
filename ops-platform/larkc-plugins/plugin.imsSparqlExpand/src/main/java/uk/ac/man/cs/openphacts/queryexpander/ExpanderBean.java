package uk.ac.man.cs.openphacts.queryexpander;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URL")
public class ExpanderBean {
    
    private String orginalQuery;
    
    private String expandedQuery;
    
    public ExpanderBean(){
    }

    /**
     * @return the orginalQuery
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
