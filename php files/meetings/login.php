<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$user = $data->user;
$pass = $data->pass;

$sql = "SELECT * FROM ".$db_owner."DAFTAR_USER, ".$db_owner."PERAN ".
       "WHERE username = :u_user AND pass = :u_pass AND DDAFTAR_USER.id_user = DPERAN.id_user";
	   	   
$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':u_user', $user);
oci_bind_by_name($compiled, ':u_pass', $pass);
oci_execute($compiled);

$response = array();
$response["login"] = array();

$nrows = 0;

while (($row = oci_fetch_row($compiled)) != false) {
	$h['id_user']  	= $row[0];
	$h['id_divisi'] = $row[1];
	$h['nama']		= $row[2];
	$h['email']		= $row[5];
	$h['peran']		= $row[7];
	array_push($response["login"], $h);	
	$nrows = $nrows+1;
}

if ($nrows > 0) {
	$response["success"] = "1";
	echo json_encode($response);			
}
else {
    $response["success"] = "0";
    $response["message"] = "Tidak ada data";
	echo json_encode($response);
}
?>