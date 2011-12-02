#!/usr/bin/env ruby
require 'rbbt-util'
require 'rbbt/sources/organism'

protein = ARGV[0]

END_POINT="http://147.96.80.204:8183/sparql" 
def sparql_query(query)
  Open.open(END_POINT + "?query=#{query.gsub(/\s+/,' ')}", :nice => 2).read.select{|line| line =~ /<uri>/}.collect{|l| l.gsub(/<\/?uri>/,'')}
end
                 
def get_other_proteins(protein)
  pathway_uris = sparql_query("select distinct ?s where {?s \
                  <http://chem2bio2rdf.org/kegg/resource/protein>\
                  <http://chem2bio2rdf.org/uniprot/resource/uniprot/#{protein}>}")
                 
  other_proteins = pathway_uris.collect{|pathway_uri|
    sparql_query("select distinct ?o where {<#{pathway_uri.strip}> \
                  <http://chem2bio2rdf.org/kegg/resource/protein>\
                  ?o}")
  }
  other_proteins.flatten.collect{|line| line.strip.sub(/.*\//,'')}
end

def get_compounds_for_gene(genes)
  compounds = genes.collect{|gene|
    sparql_query("select distinct ?compounds where {?interaction a <http://chem2bio2rdf.org/kegg/resource/kegg_interaction> .\
                 ?interaction <http://chem2bio2rdf.org/kegg/resource/GENE> <http://chem2bio2rdf.org/uniprot/resource/gene/#{gene}> . 
                 ?interaction <http://chem2bio2rdf.org/kegg/resource/compound_id> ?compound}")
  }
end

protein = Organism::Hsa.normalize([protein], "UniProt/SwissProt Accession").first
other = get_other_proteins(protein)

other = Organism::Hsa.normalize(other, "Associated Gene Name").compact

compounds = get_compounds_for_gene(other)
puts compounds
