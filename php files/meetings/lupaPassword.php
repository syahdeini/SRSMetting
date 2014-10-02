<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

//$email = $data->email;
$id=$data->id;

$sql = "SELECT * FROM ".$db_owner."DAFTAR_USER ".
       "WHERE ID_USER = :u_id";
	   	   
$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':u_id', $id);
$resultExec=oci_execute($compiled);

$response = array();
//$response["login"] = array();


$row = oci_fetch_row($compiled);
$response['username']  = $row[3];
$response['password'] = $row[4];
//array_push($response["login"], $h);	

if($resultExec==true)
{
if ($response['username']=="") {
	$response["success"] = "0";
	$response["message"]="tidak dapat menemukan data user";
	echo json_encode($response);			
}
else {
    $response["success"] = "1";
    $response["message"] = "Data berhasil di dapatkan";
	echo json_encode($response);
}
}
else
{
    $response["success"] = "-1";
    $response["message"] = "Tidak ada melakukan query";
	echo json_encode($response);
	
}
?>