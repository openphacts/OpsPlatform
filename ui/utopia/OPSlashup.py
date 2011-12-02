# -*- coding: utf-8 -*-

import utopia.document
import urllib
import urllib2
from lxml import etree
from SPARQLWrapper import SPARQLWrapper
import time

class OPSLashupAnnotator(utopia.document.Annotator):
    """OPS Lashup DOI/PMID Finder"""
    
    def populate(self, document):
        from spineapi import Annotation, IgnoreCase, RegExp
        import re
        
        
        def simpleChemspiderSearch(query):
            print "Querying chemspider for " , query
            #namespaces = {'c': 'http://www.chemspider.com/'}
            qs = { 'query': query, 'token': '70e8ac16-c6dc-4679-b5ac-b363e3a74fd5' }
            url = 'http://www.chemspider.com/Search.asmx/SimpleSearch?%s' % urllib.urlencode(qs)
            results = urllib2.urlopen(url).read()
            root = etree.fromstring(results)
            results = root.findall('./{http://www.chemspider.com/}int')
            if len(results) == 1:
                data = {}
                data['csid'] = int(results[0].text)
                data['imageUrl'] = 'http://www.chemspider.com/ImagesHandler.ashx?id=%s&w=200&h=200' % results[0].text
                data['smiles'] = query
                
                #note we can't use the smiles string from chemspider because they are proper stereochemistry
                
                qs = { 'csid': data['csid'], 'token': '70e8ac16-c6dc-4679-b5ac-b363e3a74fd5' }
                url = 'http://www.chemspider.com/Search.asmx/GetCompoundInfo?%s' % urllib.urlencode(qs)
                csrecord = urllib2.urlopen(url).read()
                root = etree.fromstring(csrecord)
                for field in ('InChI', 'InChIKey',):
                    param = './{http://www.chemspider.com/}%s' % field
                    value = root.find(param)
                    if value is not None:
                        data[field.lower()] = value.text
                return data
            else:  
                return None
    
        def parseLarkcResults(resultString):
            root = etree.fromstring(resultString)
            
            variables = root.findall("./{http://www.w3.org/2005/sparql-results#}head/{http://www.w3.org/2005/sparql-results#}variable")
            
            # a get the variables that we later want to find bindings for
            #for variable in variables:
            #    print variable.attrib['name']
                
            results = root.findall("./{http://www.w3.org/2005/sparql-results#}results/{http://www.w3.org/2005/sparql-results#}result")
            
            
            resultList = []
            for result in results:
                data = {}
                bindings = result.findall("./{http://www.w3.org/2005/sparql-results#}binding")
                for binding in bindings:
                    # print binding.attrib['name'],":",
                    literals = binding.findall("./{http://www.w3.org/2005/sparql-results#}literal")
                    for literal in literals:
                        # print literal.text
                        if binding.attrib['name'] == "smiles":
                            csinfo = simpleChemspiderSearch(literal.text)
                            data['csinfo'] = csinfo
                        data[binding.attrib['name']] = literal.text
                resultList.append(data)
            return resultList

        def parseLarkcKIPMIDResults(resultString):
            root = etree.fromstring(resultString)
            
            variables = root.findall("./{http://www.w3.org/2005/sparql-results#}head/{http://www.w3.org/2005/sparql-results#}variable")
            
            # a get the variables that we later want to find bindings for
            #for variable in variables:
            #    print variable.attrib['name']
                
            results = root.findall("./{http://www.w3.org/2005/sparql-results#}results/{http://www.w3.org/2005/sparql-results#}result")
            
            
            resultList = []
            for result in results:
                bindings = result.findall("./{http://www.w3.org/2005/sparql-results#}binding")
                kivalue = None
                pmid = None
                for binding in bindings:
                    # print binding.attrib['name'],":",
                    literals = binding.findall("./{http://www.w3.org/2005/sparql-results#}literal")
                    print binding.attrib['name']
                    for literal in literals:
                        if binding.attrib['name'] == 'kivalue':
                            kivalue = literal.text
                        if binding.attrib['name'] == 'pubmed':
                            pmid = literal.text
                print pmid, kivalue    
                if (pmid is not None) and (kivalue is not None):
                    resultList.append( (kivalue, pmid) )
            return resultList

        
        def PMIDfromDOI(doi):            
            toolName = 'UtopiaDocuments'
            contactEmail = 'utopia@cs.man.ac.uk'
            
            params = {  'db':'pubmed',
                        'tool': toolName,
                        'email':contactEmail,
                        'term': doi,
                        'usehistory':'y',
                        'retmax':'1',
                     }
            
            url = 'http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?%s' % urllib.urlencode(params)
            searchresult = urllib2.urlopen(url).read()
            root = etree.fromstring(searchresult)
            
            idlist = []
            ids = root.findall("./IdList/Id")
            for id in ids:
                idlist.append(id.text)
                
            
            pmid = None
            if idlist == []:
                print "Couldn't map DOI to PMID"
            else:
                pmid = idlist[0]
                print "Found PMID : " , pmid
            return pmid
    
        def finddoi(doc):
          result = None
          doiRegExp = '(?:DOI|doi|[Dd]igital\\s+[Oo]bject\\s+[Ii][Dd](?:entifier))\\s*:?\\s*(10\\.\\d+/[^%"\\#\\x20\\t\\r\\n]+)'
          matches = doc.search(doiRegExp, IgnoreCase + RegExp)

          if len(matches) >= 1:
            doi = re.search('(10\\.\\d+/[^%"\\#\\x20\\t\\r\\n]+)', matches[0].text())
            result=doi.group(0)
          return result

        pmid = None
        doi = finddoi(document).lower()
        print "OPS DOI:", doi
        pmid = PMIDfromDOI(doi)
        print "OPD PMID:", pmid
        
       # PAUL, REPLACE THIS WITH STUFF THAT TALKS TO LARKC ************************
        #file = open("/Users/srp/Desktop/larkc-results.xml","r")
        #results = file.read()
        
        query = '''select ?smiles ?kivalue ?testligandname ?species where {?s <http://wiki.openphacts.org/index.php/PDSP_DB#pubmed_id> "%s". ?s <http://wiki.openphacts.org/index.php/PDSP_DB#has_smiles_code> ?smiles. 
                   ?s <http://wiki.openphacts.org/index.php/PDSP_DB#has_test_ligand_name> ?testligandname.
                   ?s <http://wiki.openphacts.org/index.php/PDSP_DB#species> ?species.
                   ?s <http://wiki.openphacts.org/index.php/PDSP_DB#has_ki_value> ?ki. ?ki  <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?kivalue } LIMIT 5'''  % pmid
        
        print query
        sparql = SPARQLWrapper('http://localhost:8183/sparql')
        #run the query
        sparql.setQuery(query)
        print "running " + query
        res = sparql.query()
        resultstring = res.response.read()
        
        ligands = parseLarkcResults(resultstring)
        # STOP REPLACING STUFF HERE!!!! ********************************************
        
        matches = document.search(doi, IgnoreCase)

        # as long as we found a DOI to anchor the annotations to
        if len(matches) > 0:        
            for ligand in ligands:
                ligandName = ligand['testligandname']
                kivalue = ligand['kivalue']
                species = ligand['species']
                inchi = ligand['csinfo']['inchi']
                smiles = ligand['csinfo']['smiles']
                imageUrl = ligand['csinfo']['imageUrl']
                csid = ligand['csinfo']['csid']

                print ligandName, imageUrl

                # PAUL, REPLACE THIS WITH STUFF THAT TALKS TO LARKC TOO ************************
                #file = open("/Users/srp/Desktop/per-compound.xml","r")
                #results = file.read()
                
                 
               
                query = '''select ?pubmed ?kivalue where {?s <http://wiki.openphacts.org/index.php/PDSP_DB#pubmed_id> ?pubmed. ?s <http://wiki.openphacts.org/index.php/PDSP_DB#has_smiles_code> "%s". 
                            ?s <http://wiki.openphacts.org/index.php/PDSP_DB#has_ki_value> ?ki. ?ki  <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?kivalue }	'''  % smiles.replace("\\","\\\\").upper()
                print query
                sparql = SPARQLWrapper('http://localhost:8183/sparql')
                #run the query
                sparql.setMethod("POST")
                sparql.setQuery(query)
                print "running " + query
                res = sparql.query()
                resultstring = res.response.read()
                time.sleep(1)
                
                print resultstring
                kiToPMID = parseLarkcKIPMIDResults(resultstring)
                print "KIPMID List : ", kiToPMID
                # STOP REPLACING STUFF HERE!!!! ********************************************
                
                kitext = '<br>'
                for ki in kiToPMID:
                    print "Adding stuff", ki[0], ki[1]
                    kitext = kitext + 'Ki of ' + ki[0] + (' in <a href="http://www.ncbi.nlm.nih.gov/pubmed/%s">' % ki[1]) + ki[1] + '</a><br>'
                kitext = kitext + '<br>'

                # take the first match, and add all the annotations to it
                match = matches[0]
                annotation = Annotation()
                annotation['concept'] = 'Definition'
                annotation['property:name'] = ligandName
                description = ('<br><a href="http://www.chemspider.com/Chemical-Structure.%s.html">[More...]</a>' % csid) + '<br><br>Ki Value : ' + kivalue + '<br>' + kitext + '<img src="' + imageUrl + '">' 
                print description
                annotation['property:description'] = description
                annotation.addExtent(match)
                document.addAnnotation(annotation)

