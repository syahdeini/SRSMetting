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
$iduser=$data->iduser;
//$iduser='o';
$sql = "SELECT * FROM WILIK.DDAFTAR_USER WHERE ID_USER=:id_user";


$response = array();
$response['userinfo']=array();
// data pertama

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':id_user', $iduser);
$result=oci_execute($compiled);
$row = oci_fetch_row($compiled);


if($result==true && $row!=null)
{
	
	$h['ID_DIVISI']=$row[1];
	$h['NAMA']=$row[2];
	$h['USERNAME']=$row[3];
	$h['PASS']=$row[4];
	$h['email']=$row[5];
	array_push($response["userinfo"], $h);
}
else
{
	$response['flag']="-1";
}

echo json_encode($response);
?>

