#!/usr/bin/env ruby
require 'rbbt-util'
require 'rbbt/sources/organism'
require 'cgi'
require 'xml'

END_POINT="http://147.96.80.204:8183/sparql" 
def sparql_query(query, nocache = false)
  query.gsub!(/\s+/,' ')
  Log.debug query
  xml = Open.open(END_POINT + "?query=#{CGI.escape(query)}", :nice => 1, :nocache => nocache).read
  puts xml
  xml_doc = XML::Document.string(xml)

  ddd xml_doc.find('//result').size
  xml_doc.find('result').each do |result|
    puts result
  end

  xml_doc
end

                      #?kiv <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> ?ki . filter(?ki > #{threshold})
def get_inhibitors(enzyme_class, threshold, species = "HUMAN")
  cas = sparql_query("PREFIX brenda: <http://brenda-enzymes.info/> \
                       PREFIX uniprot: <http://purl.uniprot.org/enzymes/> \
                       PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \
                       select distinct ?ic50 ?inhibitor ?species ?name where{
                         ?ic50experiment brenda:has_ic50_value_of ?ic50 . filter(?ic50 < #{threshold}) . \
                         ?ic50experiment brenda:has_inhibitor ?inhibitor . \
                         ?ic50experiment brenda:species ?species_code . \
                         ?brenda_entry brenda:is_inhibited_by ?ic50experiment . \
                         ?brenda_entry brenda:has_ec_number ?uniprot_entry. \
                         ?uniprot_entry rdfs:subClassOf ?uniprot_top_level_entry . \
                         ?uniprot_top_level_entry <http://purl.uniprot.org/core/name> \"#{enzyme_class}\" \
                       } limit 100", true)


   cas
end

compounds = get_inhibitors( "With NAD(+) or NADP(+) as acceptor", 1001000.2, "HUMAN")

