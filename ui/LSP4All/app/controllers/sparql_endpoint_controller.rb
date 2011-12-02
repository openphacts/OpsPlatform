########################################################################################
#  
#  Copyright H. Lundbeck A/S
#  This file is part of LSP4All.
#  
#  LSP4All is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or (at
#  your option) any later version.
#  
#  LSP4All IS MADE AVAILABLE FOR DISTRIBUTION WITHOUT ANY FORM OF WARRANTY TO THE 
#  EXTENT PERMITTED BY APPLICABLE LAW.  THE COPYRIGHT HOLDER PROVIDES THE PROGRAM \"AS IS\" 
#  WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT  
#  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
#  PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM LIES
#  WITH THE USER.  SHOULD THE PROGRAM PROVE DEFECTIVE IN ANY WAY, THE USER ASSUMES THE
#  COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION. THE COPYRIGHT HOLDER IS NOT 
#  RESPONSIBLE FOR ANY AMENDMENT, MODIFICATION OR OTHER ENHANCEMENT MADE TO THE PROGRAM 
#  BY ANY USER WHO REDISTRIBUTES THE PROGRAM SO AMENDED, MODIFIED OR ENHANCED.
#  
#  IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL THE 
#  COPYRIGHT HOLDER BE LIABLE TO ANY USER FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL,
#  INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE
#  PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE
#  OR LOSSES SUSTAINED BY THE USER OR THIRD PARTIES OR A FAILURE OF THE PROGRAM TO 
#  OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER HAS BEEN ADVISED OF THE 
#  POSSIBILITY OF SUCH DAMAGES.
#  
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#  
########################################################################################

# Our controller that both creates the SPARQL and formats the result as it comes back
class SparqlEndpointController < ApplicationController

  def index
    puts "index"
  end

  def query
     query_str = params[:query]
     @endpoint = SparqlEndpoint.new(session[:endpoint])             
     results = @endpoint.find_by_sparql(query_str)
     render :json => construct_column_objects(results).to_json, :layout => false
  end
  
  def pharm_enzyme_fam
  
  puts session.inspect
  
      puts params.inspect
      species = [params[:species_1],params[:species_2],params[:species_3],params[:species_4]]
      species.compact!
      pharm_enzyme_query = "PREFIX brenda: <http://brenda-enzymes.info/>\n" 
      pharm_enzyme_query +=  "PREFIX uniprot: <http://purl.uniprot.org/enzymes/>\n" 
      pharm_enzyme_query += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
      pharm_enzyme_query += "select  ?ic50 ?inhibitor ?species  where {\n"
      pharm_enzyme_query += "?ic50experiment brenda:has_ic50_value_of ?ic50 .\n"
      if not params[:min_filter] == "" and not params[:max_filter] == "" then  
        pharm_enzyme_query += "filter(?ic50 > #{params[:min_filter]} && ?ic50 < #{params[:max_filter]}) .\n" 
      elsif not params[:min_filter] == "" and params[:max_filter] == "" then  
        pharm_enzyme_query += "filter(?ic50 > #{params[:min_filter]}) .\n"
      elsif params[:min_filter] == "" and not params[:max_filter] == "" then
        pharm_enzyme_query += "filter(?ic50 < #{params[:max_filter]}) .\n" 
      end
      pharm_enzyme_query += "?ic50experiment brenda:has_inhibitor ?inhibitor .\n" 
      pharm_enzyme_query += "?ic50experiment brenda:species ?species_code .\n"
      pharm_enzyme_query += "?species_code <http://w3.org/2000/01/rdf-schema#label> ?species .\n" 
      if species.length >= 1 then
        pharm_enzyme_query += "filter(?species = \"#{species.join('" || ?species = "')}\") .\n"
      end
      pharm_enzyme_query += "?brenda_entry brenda:is_inhibited_by ?ic50experiment .\n" 
      pharm_enzyme_query += "?brenda_entry brenda:has_ec_number ?uniprot_entry_url .\n" 
      pharm_enzyme_query += "?uniprot_entry_url rdfs:subClassOf ?uniprot_top_level_entry .\n"
      pharm_enzyme_query += "?uniprot_top_level_entry <http://purl.uniprot.org/core/name> \"#{params[:enz_name]}\"}" 
                          
     puts pharm_enzyme_query
     puts session[:endpoint]
     @endpoint = SparqlEndpoint.new(session[:endpoint]) 
     results = @endpoint.find_by_sparql(pharm_enzyme_query)
   puts results.inspect 
     render :json => construct_column_objects((results)).to_json, :layout => false  
  end
 
  def similar2smiles
     smiles = params[:smiles]
     @endpoint = SparqlEndpoint.new(session[:endpoint]) 
     sim2smiles_query = 'SELECT * WHERE { ?csid_uri <http://wiki.openphacts.org/index.php/ext_function#has_similar> "' + smiles + '"}'
     results = @endpoint.find_by_sparql(sim2smiles_query)
     render :json => construct_column_objects(format_chemspider_results(results)).to_json, :layout => false
  end
  
  def format_chemspider_results(input_arr)
    output_arr = Array.new
    input_arr.each do |record|
       uri = record[:csid_uri]
       csid = nil
       cs_image = nil
       if uri =~ /Chemical-Structure\.(\d+)\.html/ then
         csid = $1
         cs_image = '<img src="http://www.chemspider.com/ImagesHandler.ashx?id=' + csid + '&w=200&h=160" alt="CSID:' + csid + '"/>'
       end
       record[:csid] = csid
       record[:chemspider_id] = '<a href ="' + uri + '" target="_blank">' + csid + '</a>'
       record[:structure] = cs_image
       puts record.inspect
    end
    return input_arr
  end
  
  
  ##
  # This function formats SPARQL query results to json column objects for ExtJS dynamicgrid widget
  #
  def construct_column_objects(input_arr)

    if input_arr.length > 0 and input_arr.first.is_a?(Hash) then
      header_keys = input_arr.first.keys     
      header_strings = header_keys.collect{|sym| sym.to_s}
      
      # we devide header variable names into url+label pairs or singletons 
      url_label_pairs = Hash.new
      singleton_vars = Hash.new
      tpl_headers = Array.new
      header_idx = 0
      header_strings.each do |header|
          if header =~ /(\w+)_(url|label)/ then        
            if $2 == 'url' then
              if not url_label_pairs.has_key?($1) then
                url_label_pairs[$1] = Array.new(2)
              end 
              url_label_pairs[$1][0] = header_idx   
              tpl_headers.push($1)
            elsif $2 == 'label' then
              if not url_label_pairs.has_key?($1) then
                url_label_pairs[$1] = Array.new(2)
              end 
              url_label_pairs[$1][1] = header_idx
            end
          else
            singleton_vars[header] = header_idx
          end
          puts url_label_pairs.keys
          header_idx += 1
      end
      columns = Array.new

      url_label_pairs.each_pair do |key, idx_pair|
         col = Hash.new
         col[:text] = key
         col[:xtype] = 'templatecolumn'  
         col[:tpl] = '<a href ="{' + header_strings[idx_pair.last] + '}" target="_blank">{' + header_strings[idx_pair.first] + '}</a>'
         col[:hidden] = false
         col[:groupable] = true
         columns.push(col)
         columns.push(:text => header_strings[idx_pair.first], :dataIndex => header_strings[idx_pair.first], :hidden => true)
         columns.push(:text => header_strings[idx_pair.last], :dataIndex => header_strings[idx_pair.last], :hidden => true)
      end
      singleton_vars.each do |key, idx|
          col = Hash.new
          col[:text] = key.gsub(/_/,' ').capitalize
          col[:dataIndex] = key
          col[:hidden] = false
          if key =~ /structure/ then
            col[:width] = 200
          end
          if key == 'ic50' then
             col[:type] = 'number'
          end
          columns.push(col)
       
      end
      fields = header_strings + tpl_headers
      field_aofh = Array.new
      fields.each do |field|
         type = 'auto'
         if field == 'ic50' then
            type = 'float'
         end
         field_aofh.push({:name => field, :type => type})
      end 
      @col_objs = {
            :objects => input_arr,
            :totalCount => input_arr.size,
            :metaData => { :fields => field_aofh, :root => 'objects' },
            :columns => columns }
      return @col_objs
    else
        return {  :objects => input_arr,
                  :totalCount => [],
                  :metaData => { :fields => [], :root => 'objects' },
                  :columns => [] }
    end       
  end
  
  def settings
     endpoint = params[:endpoint]  
     session[:endpoint] = endpoint
     settings_arr = Array.new
     fields = Array.new
     endpoint = session[:endpoint]
     if endpoint.nil? then
        endpoint = "Not yet configured!"
     end
     settings_arr.push({:endpoint => endpoint})
     fields.push('endpoint') 
     settings_objs = {
            :objects => settings_arr,
            :metaData => { :fields => fields, :root => 'objects' }}
     
     render :json => settings_objs.to_json, :layout => false  
  end
    
end
