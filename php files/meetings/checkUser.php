<?php

// mengambil 2 hal, ruangan dan aplikasi
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}
$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

//$keyword = "%".$data->keyword."%";
$name=$data->nama;
$sql = "SELECT * FROM WILIK.DDAFTAR_USER WHERE NAMA=:username";


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

