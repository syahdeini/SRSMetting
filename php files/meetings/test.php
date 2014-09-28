<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}


$sql = "SELECT id_dokumen FROM WILIK.ddokumen";
		
$compiled = oci_parse($conn, $sql);
oci_execute($compiled) or die(oci_error($compiled));
oci_fetch_all($compiled, $array);
unset($array);

$numberofrows = oci_num_rows($compiled) + 1;
oci_free_statement($compiled);

$id_dokumen = "DOC-". sprintf("%03d", $numberofrows);
echo $id_dokumen. "<br/>";
$now = gmdate("d-M-y h.i.s.u A", time()+60*60*7);
echo $now;
?>

