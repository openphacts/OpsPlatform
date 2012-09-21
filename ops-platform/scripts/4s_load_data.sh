###DATA LOADING

4s-import OPS_March -v --model http://www.chem2bio2rdf.org/ChEMBL ../../datasets/chem2bio2rdf/chembl.nt -f ntriples
4s-import OPS_March -v --add --model http://www.chem2bio2rdf.org/ChEMBL ../../../openphacts_SVN/linksets/chemspider/cs-chemblcompounds_linkset.ttl
 -f turtle
4s-import OPS_March -v --add --model http://www.chem2bio2rdf.org/ChEMBL ../../datasets/OPS-DS-TTL/ChEMBL_assays2activities.nt -f ntriple
s
4s-import OPS_March -v --add --model http://www.chem2bio2rdf.org/ChEMBL ../../datasets/uniprotrdf/c2b2r_enzyme.ttl -f turtle
4s-import OPS_March -v --model http://larkc.eu#Fixedcontext ../../../openphacts_SVN/linksets/conceptwiki/conceptwiki_url_preflabel_nonew
lines.ttl
4s-import OPS_March -v --add --model http://larkc.eu#Fixedcontext ../../../openphacts_SVN/linksets/conceptwiki/cw-cs_linkset.ttl
4s-import OPS_March -v --model http://www.chemspider.com ../../datasets/OPS-DS-TTL/chemspider.ttl  -f turtle
4s-import OPS_March -v ../../datasets/lld/drugbank.trig -f trig 
4s-import OPS_March -v --add --model http://linkedlifedata.com/resource/drugbank ../../datasets/lld/drug_type_labels.ttl
4s-import OPS_March -v --add --model http://linkedlifedata.com/resource/drugbank ../../../openphacts_SVN/linksets/chemspider/cs-drugbankdrugs_linkset.ttl
