#!/usr/bin/env ruby
require 'rbbt-util'
require 'rbbt/sources/organism'
require 'cgi'

END_POINT="http://147.96.80.204:8183/sparql" 
def sparql_query(query, nocache = false)
  query.gsub!(/\s+/,' ')
  Log.debug query
  Open.open(END_POINT + "?query=#{CGI.escape(query)}", :nice => 1, :nocache => nocache).read.split(/\n/).select{|line| line =~ /<uri|literal>/}.collect{|l| l.gsub(/<\/?uri>/,'').strip}
end

def get_proteins_by_GO(go)
  proteins = sparql_query("select distinct ?protein where {
    ?protein <http://chem2bio2rdf.org/uniprot/resource/GO_ID> <http://bio2rdf.org/#{go}>}").collect{|prot| prot.chomp.sub(/.*\//,'')}
end

#    cas = sparql_query("select distinct ?cas ?gene where {\
#                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_unigene_id> ?gene .\
#                      ?gene  <http://chem2bio2rdf.org/uniprot/resource/GO_ID> <http://bio2rdf.org/#{go}> . \
#                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?cas . \
#                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_ki_value> ?kiv . \
#                      ?kiv <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?ki . filter(?ki > #{threshold})
#                      }")
#

def get_compounds_by_ki_and_go(go, threshold, species = "HUMAN")
    cas = sparql_query("select distinct ?cas ?gene where {\
                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_unigene_id> ?gene .\
                      ?gene  <http://chem2bio2rdf.org/uniprot/resource/GO_ID> <http://bio2rdf.org/#{go}> . \
                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?cas . \
                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_ki_value> ?kiv . \
                      ?kiv <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?ki . filter(?ki > #{threshold})
                      } limit 100", true)




   cas
end

def get_compounds_by_ki_old(proteins, threshold, species = "HUMAN")

  compound = proteins.collect do |protein|
    interactions = sparql_query("select distinct ?interaction where {\
                      ?interaction  <http://wiki.openphacts.org/index.php/PDSP_DB#has_unigene_id> \"#{protein}\" }")

    next if interactions.empty?
                      
    interactions = interactions.select{|interaction| 
      kis = sparql_query("select distinct ?ki where {\
                      <#{ interaction.strip }>  <http://wiki.openphacts.org/index.php/PDSP_DB#has_ki_value> ?kiv . \
                      ?kiv <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?ki . filter(?ki > #{threshold})}", true).any?
    }

    interactions.collect{|interaction|
      sparql_query("select distinct ?compound where {\
                      <#{ interaction}>  <http://wiki.openphacts.org/index.php/PDSP_DB#has_cas_num> ?compound}")
    }
  end
end

go = "GO:0016491"

compounds = get_compounds_by_ki_and_go(go, 10, "HUMAN")
ddd compounds
