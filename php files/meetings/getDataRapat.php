<?php

// mengambil 2 hal, ruangan dan aplikasi
$conn = oci_connect('system', '123456', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}
//$json = $_SERVER['HTTP_JSON'];
//$data = json_decode($json);

//$keyword = "%".$data->keyword."%";

$sql = "SELECT * FROM SYAHDEINI.RUANGAN";
$sql2= "SELECT * FROM SYAHDEINI.APLIKASI";		

$response = array();
$response["ruangan"] = array();
$response["aplikasi"]=array();
// data pertama

$compiled = oci_parse($conn, $sql);
//oci_bind_by_name($compiled, ':keyword', $keyword);
oci_execute($compiled);


while (($row = oci_fetch_row($compiled)) != false) {
	$h['id_ruangan']  		= $row[0];
	$h['nama_ruangan'] 		= $row[1];
	array_push($response["ruangan"], $h);
}

// data kedua
$compiled = oci_parse($conn, $sql2);
//oci_bind_by_name($compiled, ':keyword', $keyword);
oci_execute($compiled);


while (($row = oci_fetch_row($compiled)) != false) {
	$h2['id_aplikasi']  		= $row[0];
	$h2['nama_aplikasi'] 		= $row[1];
	array_push($response["aplikasi"], $h2);
}


echo json_encode($response);
?>

