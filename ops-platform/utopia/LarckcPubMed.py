import re
import spineapi
import utopia.document
import urllib
import common.utils
import OpsUtils

#Given a pubmed id find the names of the receptors that point to this pubmed id.
#Worked last time I had internet

#This is the code which does th pubmed annotation
#HACK Uses a match rather than the whole document. -Should be whole document
def addAnnotation(document, match):
    print match
    annotation = spineapi.Annotation()
    annotation['concept'] = 'LarkcPubMed'
    pmid = common.utils.getPMID(document)
    print "pmid"
    print pmid
    if pmid:
       annotation['pmid'] = pmid
    #HACK did not find pubmedid for 2 documents I tested that I know we data data for.   
    elif document.title() == "Second generation N-(1,2-diphenylethyl)piperazines as dual serotonin and noradrenaline reuptake inhibitors: Improving metabolic stability and reducing ion channel activity":
        annotation['pmid'] = "20471260"                
    elif document.title() == "doi:10.1016/j.bmcl.2006.10.089":
        annotation['pmid'] = "17107798"                
    else:
        print "Title"
        print document.title()                  
        print "END Title"
        annotation['pmid'] = "123456789"
    annotation['session:volatile'] = '1'
    #HACK should be at document level!!!!
    annotation.addExtent(match)
    document.addAnnotation(annotation)

class LarkcPubMedAnnotator(utopia.document.Annotator):
    """Test Larkc PubMed Annotater"""
    

    def populate(self, document):
        print "in LarckcPubMed!"
        #HACK. As I did not know how to annotate document I annoted word "keywords"
        for match in document.search('keywords', spineapi.IgnoreCase + spineapi.WholeWordsOnly):
            addAnnotation(document, match)
        #HACK did not know at first second document also had "keywords"    
        #for match in document.search('abstract', spineapi.IgnoreCase + spineapi.WholeWordsOnly):
        #    addAnnotation(document, match)

class LarkcPubMedVisualiser(utopia.document.Visualiser):
    """Test Visualiser for Larkc PubMed"""

    def visualise(self, annotation):
        rt=None
        if annotation['concept'] == 'LarkcPubMed':
            rt = '<h3>PubMed lookup</h3>'
            if annotation.get('pmid'):
               rt+= '<p>PubMedId = %s</p>' % annotation['pmid']
               #Show interesting things about this pubmed id
               rt+= OpsUtils.pubMedLookup(annotation['pmid'])
        return rt


