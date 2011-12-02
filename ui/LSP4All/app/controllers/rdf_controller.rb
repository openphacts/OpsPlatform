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


class RdfController < ApplicationController

  def index_old
    lld = LinkedLifeData.new
    @rdf = lld.find(50,nil,[:s,:p,:o])
    #json = '[{"appeId":"1","survId":"1","location":"","surveyDate":"2008-03-14","surveyTime":"12:19:47","inputUserId":"1","inputTime":"2008-03-14 12:21:51","modifyTime":"0000-00-00 00:00:00"},{"appeId":"2","survId":"32","location":"","surveyDate":"2008-03-14","surveyTime":"22:43:09","inputUserId":"32","inputTime":"2008-03-14 22:43:37","modifyTime":"0000-00-00 00:00:00"},{"appeId":"3","survId":"32","location":"","surveyDate":"2008-03-15","surveyTime":"07:59:33","inputUserId":"32","inputTime":"2008-03-15 08:00:44","modifyTime":"0000-00-00 00:00:00"},{"appeId":"4","survId":"1","location":"","surveyDate":"2008-03-15","surveyTime":"10:45:42","inputUserId":"1","inputTime":"2008-03-15 10:46:04","modifyTime":"0000-00-00 00:00:00"},{"appeId":"5","survId":"32","location":"","surveyDate":"2008-03-16","surveyTime":"08:04:49","inputUserId":"32","inputTime":"2008-03-16 08:05:26","modifyTime":"0000-00-00 00:00:00"},{"appeId":"6","survId":"32","location":"","surveyDate":"2008-03-20","surveyTime":"20:19:01","inputUserId":"32","inputTime":"2008-03-20 20:19:32","modifyTime":"0000-00-00 00:00:00"}]'
    render :json => {:objects => @rdf}.to_json, :layout => false
              
  end
  
  def index
  
     query1 = "PREFIX biopax2: <http://www.biopax.org/release/biopax-level2.owl#>
              PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

              SELECT DISTINCT *
              WHERE {
                  ?interactor biopax2:PARTICIPANTS ?participants .
                  ?participants biopax2:PHYSICAL-ENTITY ?protein .
                  ?protein biopax2:NAME \"Phytochrome A\" .
                  ?interactor biopax2:PARTICIPANTS ?participants1 .
                  ?participants1 biopax2:PHYSICAL-ENTITY ?interactor_uri .
                  ?interactor_uri rdf:type biopax2:protein .
                  ?interactor_uri biopax2:NAME ?interactor_name .
              FILTER (?interactor_name != \"Phytochrome A\")
              }"
              
     query2 = "SELECT ?s ?p ?o
              WHERE {
              ?s ?p ?o .
              } LIMIT 1000"         
    
              
     @rdf = LinkedLifeData.find_by_sparql(query1)
     
     render :json => construct_column_objects(@rdf).to_json, :layout => false
     
  end
  
  ##
  # This function
  #
  def construct_column_objects(input_arr)

    if input_arr.length > 0 and input_arr.first.is_a?(Hash) then
      header_keys = input_arr.first.keys     
      header_strings = header_keys.collect{|sym| sym.to_s}
      value_sample = input_arr.first.values
      columns = Array.new
      header_strings.each do |hs|
        if input_arr.first[hs.to_sym].match(/http:\/\//) then 
          columns.push({:text => hs.gsub(/_/,' ').capitalize, :dataIndex => hs, :hidden => true, :xtype => 'templatecolumn', :tpl =>"<a href =\"{#{hs}}\" target=\"_blank\">{#{hs}}<\\a>"})                                                                                                       
        else
          columns.push({:text => hs.gsub(/_/,' ').capitalize, :dataIndex => hs, :hidden => false}) 
        end  
      end
      @col_objs = {
            :objects => input_arr,
            :totalCount => @rdf.size,
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
  

end
