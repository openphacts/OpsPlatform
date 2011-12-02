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


class SparqlEndpointController < ApplicationController

  def index
    puts "-----index"
  end

  def query
     query_str = params[:query]
     @endpoint = SparqlEndpoint.new(params[:endpoint_url])
              
     results = @endpoint.find_by_sparql(query_str)
     #results = expand_url2label(results)
     render :json => construct_column_objects(results).to_json, :layout => false
  end
  
  def similar2smiles
     smiles = params[:smiles]
     @endpoint = SparqlEndpoint.new(params[:endpoint_url])
  
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
         cs_image = '<img src="http://www.chemspider.com/ImagesHandler.ashx?id=' + csid + '&w=200&h=150" alt="CSID:' + csid + '"/>'
       end
       record[:csid] = csid
       record["chemspider_id"] = '<a href ="' + uri + '" target="_blank">' + csid + '</a>'
       record["cs_image"] = cs_image
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
      value_sample = input_arr.first.values
      columns = Array.new
      header_strings.each do |hs|
        #if input_arr.first[hs.to_sym].match(/http:\/\//) then 
       #   columns.push({:text => hs.gsub(/_/,' ').capitalize, :dataIndex => hs, :hidden => false, :xtype => 'templatecolumn', :tpl =>"<a href =\"{#{hs}}\" target=\"_blank\">{#{hs}}<\\a>"})                                                                                                       
       # else
          columns.push({:text => hs.gsub(/_/,' ').capitalize, :dataIndex => hs, :hidden => false}) 
       # end  
      end
      @col_objs = {
            :objects => input_arr,
            :totalCount => input_arr.size,
            :metaData => { :fields => header_strings, :root => 'objects' },
            :columns => columns }
      return @col_objs
    else
        return {  :objects => input_arr,
                  :totalCount => [],
                  :metaData => { :fields => [], :root => 'objects' },
                  :columns => [] }
    end       
  end
  
  def expand_url2label (input_arr)
    input_arr.each do |row|
       row.each do |key, value|
         if value.match(/http:\/\//) then
            label = @endpoint.find_rdfs_label(value)
            unless label.nil? 
              row[key] = "<a href =\"#{value}\" target=\"_blank\">#{label}</a>"
            end
         end
      end
    end  
    input_arr
  end
  
end
