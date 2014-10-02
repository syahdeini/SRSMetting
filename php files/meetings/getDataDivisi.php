<?php

// mengambil 2 hal, ruangan dan aplikasi
include "koneksi.php";
//$json = $_SERVER['HTTP_JSON'];
//$data = json_decode($json);

//$keyword = "%".$data->keyword."%";

$sql = "SELECT * FROM ".$db_owner."DIVISI";

$response = array();
$response['divisi']=array();
// data pertama

$compiled = oci_parse($conn, $sql);
//oci_bind_by_name($compiled, ':keyword', $keyword);
oci_execute($compiled);


while (($row = oci_fetch_row($compiled)) != false) {
	$h['id_divisi']  		= $row[0];
	$h['divisi'] 		= $row[1];
	array_push($response['divisi'], $h);
}


echo json_encode($response);
?>

