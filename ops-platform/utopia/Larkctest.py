import re
import spineapi
import utopia.document
import urllib
from coda_network import urllib2
from lxml import etree
from rdflib import Graph
import rdflib
from SPARQLWrapper import SPARQLWrapper 
from xml.dom.minidom import parse 
import OpsUtils
import time
    
class LarkcTest2Annotator(utopia.document.Annotator):
    """Find Larkc words"""

    #This method tries to find all the words in the text that Larkc knows something about.
    #Probably much too slow (too many query) to use without a much better filter on words to try
    def populate(self, document):

	print "in LarkcTest2Annotator"
	#Try any word with 4 or more characters
	atext = '[a-zA-Z0-9%s]' % re.escape("!#$%&'*+-/=?^_`{|}~")
	dot_atom = atext + atext + atext + atext + "+"
	#print dot_atom

        for match in document.search(dot_atom, spineapi.IgnoreCase + spineapi.WholeWordsOnly + spineapi.RegExp):
            #See if Larkc know the word
            dom  = OpsUtils.runQuery('Select * where { ?subject ?object "%s" } LIMIT 1' % match.text())
            results = dom.getElementsByTagName("result")
            #if there is a result the annotate the word including storing the word for later querying
            if len(results) > 0:
                annotation = spineapi.Annotation()
                annotation['concept'] = 'Larkc'
                annotation['property:text'] = match.text()
                annotation['session:volatile'] = '1'
                annotation.addExtent(match)
                document.addAnnotation(annotation) 

class LarkcTest2Visualiser(utopia.document.Visualiser):
    """Show Larkc words"""

    #Now that we have a word larkc knows about find out what it knows.
    def visualise(self, annotation):
        rt=None

        if annotation['concept'] == 'Larkc':
            rt=""
            if 'property:text' in annotation:
                rt += '<h3>%s</h3>' % annotation['property:text']
                #Get the Sobjects and predictates for which this word is the Object
                #Was working last time I had internet
                rt += OpsUtils.objectLookup(annotation['property:text'])    
                #Idea here is to get the subject and predicate of triples this word is the object off
                #Then for each subject find its other predicates and objects
                #Untested due to internet death
                #rt += OpsUtils.doubleLookup(annotation['property:text'])    
        return rt

#This never worked what I am I doing wrong!
class Larkc2LevelAnnotator(utopia.document.Annotator):
    """Look up to a second level"""

    def lookup(self, phrase):
        print "lookup started"
        return "<h3>Level 2 started</h3>"
    
