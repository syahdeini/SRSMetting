<?php
include "koneksi.php";


$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$fullName = $data->fullName;
$divisi = $data->divisi;
$email = $data->email;
$username= $data->username;
$password = $data->password;


$sql1="SELECT COUNT(*) FROM ".$db_owner."DAFTAR_USER";
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

$sql = "INSERT INTO ".$db_owner."DAFTAR_USER ".
       "(id_user,id_divisi,nama,username,pass,email) VALUES (:iduser,:divisi,LOWER(:fullName),LOWER(:username),:password,LOWER(:email))";
	   	   
$compiled = oci_parse($conn, $sql);

oci_bind_by_name($compiled, ':iduser', $id_user);
oci_bind_by_name($compiled, ':fullName', $fullName);
oci_bind_by_name($compiled, ':divisi', $divisi);
oci_bind_by_name($compiled, ':email',$email);
oci_bind_by_name($compiled, ':username',$username);
oci_bind_by_name($compiled, ':password',$password);

$resultExec=oci_execute($compiled);

// insert peran

$sqlT="INSERT INTO ".$db_owner."PERAN VALUES(:iduser,'USR')";
$compiled = oci_parse($conn, $sqlT);
oci_bind_by_name($compiled,':iduser',$id_user);
oci_execute($compiled);

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