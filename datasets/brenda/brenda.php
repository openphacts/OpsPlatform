<?php

define("BRENDA_BAD_PROTEIN_ID", "@bad_id@");
define("BRENDA_EMPTY_STRING", "@empty_string@");

$file = "brenda.txt";
//$file = "brenda.small.txt";

$n3file = "brenda2.ttl";
if(!file_exists($file)) {
	trigger_error("File $file doesn't exists");
	exit;
}
$n3fp = fopen($n3file,"w+");

//fwrite($n3fp,"@prefix brenda: <http://brenda-enzymes.info/> .\n");
//fwrite($n3fp,"@prefix uniprot: <http://purl.uniprot.org/enzyme/> .\n");
fwrite($n3fp,"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n");
fwrite($n3fp,"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n");
fwrite($n3fp,"\n");

$id = '';
$fnx = '';
$ll = '';

$species = array();

$fp = fopen($file,"r");
while($l = fgets($fp)) {
	// get clean line
	$l = rtrim($l);
	//
	// if we see * - this is the copyright notice, so just ignore
	if($l[0] == '*') 
		continue;
	//
	// if there is nothing in the line, then process the last line
	if(strlen($l) == 0 && $ll != '' && $fnx != '') {
		if(function_exists($fnx)) {
			$b = $fnx($ll,$id);
			if ($b !== false && $id != BRENDA_BAD_PROTEIN_ID)
				fwrite($n3fp,$b);
		} 
		
		$fnx = '';
		$ll = '';
		continue;
	}
	
	if($l=='///') {
		// start of a new record
		if($id == '') continue;
		// store the record into the database
		// @todo
	}
	
	$a = explode("\t",$l);
	//
	// if we see only one element in the array, then this corresponds to the function name
	if(count($a) == 1) {
		// function name
		$fnx = $a[0];
		continue;
	}
	
	if(count($a) == 2 && strlen($a[0]) == 0) {
		// add this to the previous line
		$ll .= " ".$a[1];
		continue; 
	} else {
		if($ll) {
			// process this line
			if(function_exists($fnx)) {
				$b = $fnx($ll,$id);
				if ($b !== false && $id != BRENDA_BAD_PROTEIN_ID) {
					fwrite($n3fp,$b);
				} 
			} 
			$ll = $l;
		} else {
			$ll = $l;
		}
		
		if($a[0] == 'ID') {
			$id = $a[1];
			// lets do a little sanity checkhere.
			$found_match = preg_match("/(\d+)\.(\d+)\.(\d+)\.(\d+)/", $id, $m);
			if (!$found_match) {
				// found a bad ID ..
				$id = BRENDA_BAD_PROTEIN_ID;
				continue;
			}
			
			$b = "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/has_ec_number> <http://purl.uniprot.org/enzyme/$id> .".PHP_EOL;
			fwrite($n3fp,$b);
		}
	}
	// if the text corresponds to a function, then call the function with the file pointer so as to
	// process the text for the block
}


fclose($n3fp);
//*****************************************************************************************************************************************************//
//*****************************************************************************************************************************************************//

//************//
// FUNCTIONS: //
//************//

//******************************************************** PROTIEN ************************************************************************************//
function PROTEIN($l,$id) {		
	//
	// match the line
	$found_match = preg_match("/(PR\t#(\d+)#\s+(\w+\s+\w*\.*)\s+(\w*)\s+.*)+/", $l, $m);
	if (!$found_match) 
		return false;
	//	
	// define species ID
	$sid = "<http://brenda-enzymes.info/".clean_string($id)."/species".clean_string($m[2]).">";
	//
	// create triples
	$b = '';
	$b .= "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/species> $sid.".PHP_EOL;
	$b .= "$sid rdf:type <http://brenda-enzymes.info/Species> .".PHP_EOL;
	$b .= "$sid rdfs:label \"".clean_string($m[3])."\".".PHP_EOL;
	//
	// store the species assingment for later reference
	global $species;
	$species[trim($m[2])] = $sid;
	//
	return $b;
}

//******************************************************** RECOMMENDED NAME ***************************************************************************//
function RECOMMENDED_NAME($l,$id)
{
 $a = explode("\t",$l);  
 $b = "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/recommended_name> \"$a[1]\".".PHP_EOL;
 return $b;
}

//******************************************************** SYSTEMATIC NAME ****************************************************************************//
function SYSTEMATIC_NAME($l,$id)
{
 $a = explode("\t",$l);  
 $b = "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/systematic_name> \"$a[1]\".".PHP_EOL;
 return $b;
}

//******************************************************** CAS REGISTERY NUMBER ***********************************************************************//
function CAS_REGISTRY_NUMBER($l,$id)
{
 $a = explode("\t",$l);  
 $b = "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/cas_registry_number> \"$a[1]\".".PHP_EOL;
 return $b;
}

//************************************************************** REACTION *****************************************************************************//
function REACTION($l,$id)
{
	return;
 	$rx = explode("\t",$l);  
 	$reaction = $rx[1];
 	$b = "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/reaction_description> \"$reaction\".".PHP_EOL;
 	return $b;
}


//******************************************************** PROTIEN ************************************************************************************//
function IC50_VALUE($l,$id)
{	
	global $species;
	
	$found_match = preg_match("/^IC50\s+#(\d+)#\s+([-+]?[0-9]*\.?[0-9]+(?:[eE][-+]?[0-9]+)?)\s+\{(.*)\}/", $l, $m);
	if (!$found_match) 
		return false;
 	// create a unique hash per entry
 	$iid = "<http://brenda-enzymes.info/$id/ic50/".md5($l).">";
    $sid = $species[$m[1]];

	if ($sid == "" || !count($species)) {
		print_r("NO SPECIES: ".$sid."\nLine: ".$l."\nSpecies: ".$species."\n");
		return false;
	}
		
    $b = '';
    $b .= "<http://brenda-enzymes.info/$id> <http://brenda-enzymes.info/is_inhibited_by> $iid.".PHP_EOL;
    $b .= "$iid <http://brenda-enzymes.info/species> $sid.".PHP_EOL;
    $b .= "$iid <http://brenda-enzymes.info/has_ic50_value_of> $m[2] .".PHP_EOL;
    $b .= "$iid <http://brenda-enzymes.info/has_inhibitor> \"$m[3]\".".PHP_EOL;
	
    return $b;
}


//***********************************************************  REACTION_TYPE **************************************************************************//
function REACTION_TYPE($l,$id)
{
  $a = explode("\t",$l);
  $rxid  = "<http://brenda-enzymes.info/$id/reaction>";
  $rtype = "<http://brenda-enzymes.info/".md5($a[1]).">";

  $n3 .= "$rxid rdf:type $rtype .".PHP_EOL;
  $n3 .= "$rtype rdfs:label \"".clean_string($a[1]." [".$rtype."]")."\".".PHP_EOL;

  return $n3;
}

//******************************************************** TURN OVER NUMBER ***************************************************************************//
function TURNOVER_NUMBER($l,$id) {
	// BOOOOO
	return false;
	$tid = "<http://brenda-enzymes.info/$id/turnover".md5($l).">";
	$b .= "$tid rdfs:label \"turnover number [$tid]\".".PHP_EOL;
	$b .= "$tid rdf:type <http://brenda-enzymes.info/TurnoverNumber> .".PHP_EOL;
	
	//$l = "TN	#4,16# 25 {NADPH}  (#16# pH 7.0, 25°C <14>; #4# pH 7.0, 25°C, wild-type enzyme <13>) <13,14>";
	preg_match("/TN\t#([0-9,]+)#\s([e0-9\.?\-?]+)\s{(.*)}\s+(\(#[0-9]+#\s?(p?H? [0-9\.]+)?,?\s?([0-9\.]+)?°?C?)?/",$l,$m);
	if(count($m) == 0) {
	echo "error in parsing turnover number: $l"."\n";
	}
	$c = explode(",",$m[1]);
	foreach($c as $speciesid) {
	$sid = "<http://brenda-enzymes.info/$id/species$speciesid>";
	$b .= "$tid <http://brenda-enzymes.info/species> $sid .".PHP_EOL; 
	}
	
	$b .= "$tid <http://brenda-enzymes.info/kcat> $m[2] .".PHP_EOL;
	$b .= "$tid <http://brenda-enzymes.info/substrate> \"$m[3]\".".PHP_EOL;
	$t = explode(" ",$m[4]);
	$b .= "$tid <http://brenda-enzymes.info/pH>  $t[1] .".PHP_EOL;
	$b .= "$tid <http://brenda-enzymes.info/temperature>  $m[5] .".PHP_EOL;
	
	return $b; 
}

//**************************************************************** Km Value ***************************************************************************//
function KM_VALUE($l,$id)
{
	// BOOOO 
	return false;
	//
	$kid = "<http://brenda-enzymes.info/$id/km".md5($l).">";
	$b .= "$kid rdfs:label \"km [$kid]\".".PHP_EOL;
	$b .= "$kid rdf:type <http://brenda-enzymes.info/Km> .".PHP_EOL;
	
	preg_match("/KM\t#([0-9,]+)#\s([e0-9\.?\-?]+)\s{(.*)}\s+(\(#[0-9]+#\s?(p?H? [0-9\.]+)?,?\s?([0-9\.]+)?°?C?)?/",$l,$m);
	
	$c = explode(",",$m[1]);
	
	foreach($c as $speciesid) {
	$sid = "<http://brenda-enzymes.info/$id/species$speciesid>";
	$b .= "$kid <http://brenda-enzymes.info/species> $sid .".PHP_EOL; 
	}
	
	$b .= "$kid <http://brenda-enzymes.info/km> $m[2] .".PHP_EOL;
	$b .= "$kid <http://brenda-enzymes.info/substrate> \"$m[3]\".".PHP_EOL;
	$t = explode(" ",$m[4]);
	$b .= "$kid <http://brenda-enzymes.info/pH>  $t[1] .".PHP_EOL;
	$b .= "$kid <http://brenda-enzymes.info/temperature>  $m[5] .".PHP_EOL;
	
	//echo $b;
	//print_r($m);
	//exit;
 
}


function clean_string($mess) {
	if (trim($mess) == "") {
		$mess = BRENDA_EMPTY_STRING;
	}
	$mess = urlencode($mess);
	
	// TODO check for a valid triple
	 
	return $mess;
}
?>
