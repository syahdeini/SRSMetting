<?php
include "koneksi.php";
// mengambil 2 hal, ruangan dan aplikasi
$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

//$keyword = "%".$data->keyword."%";
$name=$data->nama;
$sql = "SELECT * FROM ".$db_owner."DAFTAR_USER WHERE nama=:username";


$response = array();
// data pertama

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':username', $name);
$result=oci_execute($compiled);
$row = oci_fetch_row($compiled);
if($result==true && $row!=null)
{
	
	$response['ID_USER']  = $row[0];
	$response['success']="1";
//	array_push($response["ruangan"], $h);
}
else
{
	$response['flag']="-1";
}

echo json_encode($response);
?>

