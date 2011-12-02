#!/usr/bin/env ruby
require 'rbbt-util'

END_POINT="http://147.96.80.204:8183/sparql" 
def query(protein)
  uri = Open.open(END_POINT + "?query=select * where {?s \
                  <http://chem2bio2rdf.org/chemogenomics/resource/GENE>\
                  <http://chem2bio2rdf.org/uniprot/resource/gene/#{protein}>}").read.select{|line| line =~ /<uri>/}
  uri
end

puts query("BDH2")
