<?php
include "koneksi.php";


$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$nama = $data->nama;


$sql1="SELECT COUNT(*) FROM ".$db_owner."PESERTA";
$compiled1 = oci_parse($conn, $sql1);
$resultExec1=oci_execute($compiled1);
$row1= oci_fetch_row($compiled1);
$val= (int)$row1[0]+1;
$id_user="USR".$val;

/*
$fullName = 'c';
$divisi = 5;
$email = 'c';
$username= 'c';
$password = 'c';
*/

$sql = "INSERT INTO ".$db_owner."PESERTA ".
       "  VALUES (:iduser,:nama)";
	   	   
$compiled = oci_parse($conn, $sql);

oci_bind_by_name($compiled, ':iduser', $id_user);
oci_bind_by_name($compiled, ':nama', $nama);

$resultExec=oci_execute($compiled);


if ($resultExec==true) {
	$response["success"] = "1";
	echo json_encode($response);			
}
else {
    $response["success"] = "0";
    $response["message"] = "Tidak ada data";
	echo json_encode($response);
}

?>