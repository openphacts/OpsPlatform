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
import time

#Utility folder for openpahcts

#If you use http get rather than SPARQLWrapper you have to replace some characters in the query.
def urlClean(theUrl):
    theUrl = theUrl.replace(" ","%20");
    theUrl = theUrl.replace("{","%7B");
    theUrl = theUrl.replace("}","%7D");
    theUrl = theUrl.replace('"',"%22");
    #theUrl = theUrl.replace('/',"%2F");
    theUrl = theUrl.replace('<',"%3C");
    theUrl = theUrl.replace('>',"%3F");
    return theUrl

#Runs a sparql query to the hardcode larkc endponit
#Converts the query to and dom xml document
def runQuery(query):
    #creates sparql endpoint
    sparql = SPARQLWrapper('http://localhost:8183/sparql')
    #run the query
    sparql.setQuery(query)
    print "running" + query
    results = sparql.query()
    #There is a TODO on Larkc to produce Jason but currently they only produce XML
    dom = parse(results.response) 
    #Larkc appeared so suffer if hit too fast too often.
    #So sleeping for 1 second. - Hope this will not be neeed!
    time.sleep(1)
    return dom

#HACK to print what the returned XML looks like
#Does enough to show the Larkc xml
def printTree(node, prefix):
    if node:
        if node.nodeType == node.ELEMENT_NODE:
            print prefix + "ELEM: " + node.tagName
        elif node.nodeType == node.ATTRIBUTE_NODE:
            print node
            print "ATTRIBUTE_NODE"
        elif node.nodeType == node.TEXT_NODE:
            #Ignore any text nodes with just whitespace
            if len(node.data.strip()) > 0:
                print prefix + "TEXT: " + bin(len(node.data.strip())) + "^" + node.data + "^"
        elif node.nodeType == node.CDATA_SECTION_NODE:
            print node
            print "CDATA_SECTION_NODE"
        elif node.nodeType == node.ENTITY_NODE:
            print node
            print "ENTITY_NODE"
        elif node.nodeType == node.PROCESSING_INSTRUCTION_NODE:
            print node
            print "PROCESSING_INSTRUCTION_NODE"
        elif node.nodeType == node.COMMENT_NODE:
            print node
            print "COMMENT_NODE"
        elif node.nodeType == node.DOCUMENT_NODE:
            print prefix + "DOC: " + node.nodeName
        elif node.nodeType == node.DOCUMENT_TYPE_NODE:
            print node
            print "DOCUMENT_TYPE_NODE"
        elif node.nodeType == node.NOTATION_NODE:
            print node
            print "NOTATION_NODE"
        children = node.childNodes
        for child in children:
            printTree(child, prefix + "   ")

#HACK Gets the text in an xml node and its children
#trunctates uri to fit in utopia sidebar
def getTexts(node):
    rt = ""
    children = node.childNodes
    for child in children:
        if child.nodeType == node.TEXT_NODE:
            #Strip out tabs, newlines and spaces as larkc result full of text with just this
            text = child.data.strip()
            #The full uri is too long for the utopia sidebar so just showing the interesting bit.
            if text.find("#") > 0: 
               text = "uri:" +text.partition("#")[2]
            rt+= text
        #This allow the method to be called on bindings that have uri/literal element which has text children    
        elif child.nodeType == node.ELEMENT_NODE:
            rt+= getTexts(child)
    return rt

#HACK Gets the text in an xml node and its children
#Does not trunctate uri to fit in utopia sidebar
def getFullTexts(node):
    rt = ""
    children = node.childNodes
    for child in children:
        if child.nodeType == node.TEXT_NODE:
            #Strip out tabs, newlines and spaces as larkc result full of text with just this
            text = child.data.strip()
            rt+= text
        #This allow the method to be called on bindings that have uri/literal element which has text children    
        elif child.nodeType == node.ELEMENT_NODE:
            rt+= getTexts(child)
    return rt

#HACK This finds the information in a single larkc xml field, in an "binding" element.
def getBindings(dom):
    rt = ""

    #Each line of the sparql result is in a "result" element
    results = dom.getElementsByTagName("result")
    for result in results:
        #Each column in the sparql result is in a "binding" tag
        bindings = result.getElementsByTagName("binding")
        for binding in bindings:
           #Each binding as a "name" attribute with the sparql ?xxxx in it (no ?)
           attribute = binding.getAttribute("name")
           #get the text inside ignoring the rest
           text = getTexts(binding)
           rt+= "<p>" + attribute+ ": " + text + "</p>"
    return rt

#Untested HACK - internet died
#The idea here is to find what other perdicates and objects a subject uri connects to
#Should avoid the triple that found the subject url but never got that far.
#Maybe ignore non interesting triples but what are they. Again never got that far
def getNextLevel(theUri):
    query = 'Select DISTINCT ?predicate ?object where { %s ?predicate ?object } LIMIT 10' %theUri 
    print query 
    dom = runQuery(query)
    return getBindings(dom)

#Untested HACK - internet died
#The idea here is to document what is found and what other perdicates and objects a subject uri connects to
def getDetails(node):
    rt = ""
    #process the singleton result which will be either a "uri" or a "literal" tag.
    #have to use these lists methods due to (my) limited xml ability
    #get the uri type result
    uris = binding.getElementsByTagName("uri")
    for uri in uris:
       theUri = getFullTexts(uri)
       #document the subject value
       rt = "<p>" + getTexts(uri) + "</p>"
       #find out what else is know about this subject
       #Need to avoid doing this with a predicate uri - never got that far
       rt = rt + getNextLevel(theUri)
    #get the literal result
    literals = node.getElementsByTagName("literal")
    for literal in literals:
       #document the value
       rt = "<p>" + getTexts(literal) + "</p>"
    return rt   

#The idea here is to document what is found and what other perdicates and objects a subject uri connects to
def doubleBindings(dom):
    rt = ""
    #Each line of the sparql result is in a "result" element
    results = dom.getElementsByTagName("result")
    for result in results:
        #Each column in the sparql result is in a "binding" tag
        bindings = result.getElementsByTagName("binding")
        for binding in bindings:
           #Each binding as a "name" attribute with the sparql ?xxxx in it (no ?)
           attribute = binding.getAttribute("name")
           rt+= "<h4>" + attribute + "<h4>"
           rt+= getDetails(binding)

#Given a pubmed id find the names of the receptors that point to this pubmed id.
#Worked last time I had internet
def pubMedLookup(pmid):
        query = 'Select DISTINCT ?receptor where { ?r <http://wiki.openphacts.org/index.php/PDSP_DB#pubmed_id> "%s"; <http://wiki.openphacts.org/index.php/PDSP_DB#has_receptor_name> ?receptor} LIMIT 100' % pmid
        dom = runQuery(query)
	#convert dom to sidebar text
        return getBindings(dom)

#given a term find the triples which has this term as the object
#Worked last time I had internet
def objectLookup(text):
        query = 'Select DISTINCT ?subject ?predicate where {?subject ?predicate "%s" } LIMIT 100' %text  
        dom = runQuery(query)
	#convert dom to sidebar text
        return getBindings(dom)

#Untested HACK - internet died
#given a term find the triples which has this term as the object
#Then find the other triples that have the same subject
def doubleLookup(text):
        query = 'Select DISTINCT ?subject ?predicate where {?subject ?predicate "%s" } LIMIT 100' %text 
        dom = runQuery(query)
        return doubleBindings(dom)
