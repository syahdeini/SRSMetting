<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$iduser = $data->iduser;
$fullName = $data->fullName;
$divisi = (int)$data->divisi;
$email = $data->email;
$username= $data->username;
$password = $data->password;

/*$fullName = 'c';
$divisi = 5;
$email = 'c';
$username= 'c';
$password = 'c';
*/

$sql = "UPDATE ".$db_owner."DAFTAR_USER ".
       "SET id_divisi=:divisi,nama=:fullName,username=:username,pass=:password,email=:email 
	   WHERE id_user=:iduser";
	   	   
$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':iduser', $iduser);
oci_bind_by_name($compiled, ':fullName', $fullName);
oci_bind_by_name($compiled, ':divisi', $divisi);
oci_bind_by_name($compiled, ':email',$username);
oci_bind_by_name($compiled, ':username',$username);
oci_bind_by_name($compiled, ':password',$password);

$resultExec=oci_execute($compiled);

$response = array();

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