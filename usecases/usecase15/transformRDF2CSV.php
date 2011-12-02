<?php
// The file test.xml contains an XML document with a root element
// and at least an element /[root]/title.

if (file_exists($argv[1])) {
    $xml = simplexml_load_file($argv[1]);
 
} else {
    exit('Failed to open test.xml.');
}

//var_dump($xml->head);
$temp = array();
foreach ($xml->head->variable as $head){
   array_push($temp, $head["name"]);
}
print implode("; ", $temp);
print "\n";
$temp2 = array();
foreach($xml->results->result as $result){
$temp2 = array();
  foreach ($result->binding as $binding){
     array_push($temp2, $binding->literal);
  }
  print implode("; ", $temp2);
  print "\n";
}
?>
