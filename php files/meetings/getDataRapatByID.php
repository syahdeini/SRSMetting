<?php

// mengambil 2 hal, ruangan dan aplikasi
include "koneksi.php";
$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$rapat_id = $data->rapat_id;
$sql = "SELECT * FROM ".$db_owner."RUANGAN";
//$sqlGetRuangan="SELECT * FROM".$D
$sql2= "SELECT * FROM ".$db_owner."APLIKASI";		
//SELECT ID_PESERTA,NAMA FROM DRAPAT_PESERTA, ddaftar_user WHERE id_rapat='RPT-10' and ddaftar_user.id_user=drapat_peserta.id_peserta;
$sql3= "SELECT * FROM ".$db_owner."RAPAT where ID_RAPAT=:idrapat";
$sql4="SELECT ID_PESERTA,NAMA  FROM ".$db_owner."RAPAT_PESERTA,".$db_owner."DAFTAR_USER WHERE ID_RAPAT=:idrapat and ddaftar_user.id_user=drapat_peserta.id_peserta";
$sql5="SELECT * FROM ".$db_owner."RAPAT_APLIKASI WHERE ID_RAPAT=:idrapat";



$response = array();
$response["ruangan"] = array();
$response["aplikasi"]=array();
$response['rapat']=array();
$response['pesertaRapat']=array();

///////////////////////// info dari ruangan
$compiled = oci_parse($conn, $sql);
//oci_bind_by_name($compiled, ':keyword', $keyword);
oci_execute($compiled);

$dictRuangan=array();
while (($row = oci_fetch_row($compiled)) != false) {
	$h['id_ruangan']  		= $row[0];
	$h['nama_ruangan'] 		= $row[1];
	$dictRuangan[$row[0]]	= $row[1];
	array_push($response["ruangan"], $h);
}


//////////////////// info dari aplikasi
$compiled = oci_parse($conn, $sql2);
//oci_bind_by_name($compiled, ':keyword', $keyword);
oci_execute($compiled);

$dictAplikasi=array();
while (($row = oci_fetch_row($compiled)) != false) {
	$h2['id_aplikasi']  		= $row[0];
	$h2['nama_aplikasi'] 		= $row[1];
	$dictAplikasi[$row[0]]		= $row[1];
	array_push($response["aplikasi"], $h2);
}


//////////////// DAPATKAN INFO RAPAT BASED ON ID_RAPAT
$compiled =oci_parse($conn, $sql3);
oci_bind_by_name($compiled,':idrapat',$rapat_id);
oci_execute($compiled);
$row=oci_fetch_row($compiled);
if(array_key_exists($row[1],$dictRuangan))
	$h3['ID_RUANGAN']=$dictRuangan[$row[1]];
else
	$h3['ID_RUANGAN']=NULL;
$h3['TANGGAL_MULAI']=$row[2];
$h3['TANGGAL_SELESAI']=$row[3];
$h3['JAM_MULAI']=$row[4];
$h3['JAM_SELESAI']=$row[5];
$h3['PERIHAL']=$row[6];
$h3['PENANGGUNG_JAWAB']=$row[7];
$h3['RESUME_HASIL']=$row[8];
$h3['TANGGAL_BUAT_RAPAT']=$row[9];
$h3['PEMBUAT_JADWAL_ID_USER']=$row[10];
$h3['STATUS_RAPAT']=$row[11];
array_push($response["rapat"],$h3);


//echo $dictRuangan[$h3['ID_RUANGAN']];

// Getting rapat peserta dari rapat_peserta table
$compiled=oci_parse($conn,$sql4);
oci_bind_by_name($compiled,':idrapat',$rapat_id);
oci_execute($compiled);
while (($row = oci_fetch_row($compiled)) != false) {
	$h4['ID_PESERTA']=$row[0];
	$h4['NAMA_PESERTA']=$row[1];
	array_push($response['pesertaRapat'],$h4);
}


// Getting aplikasi from rapat_aplikasi table
$response['aplikasi_rapat']=array();
$compiled =oci_parse($conn, $sql5);
oci_bind_by_name($compiled,':idrapat',$rapat_id);
oci_execute($compiled);
$row=oci_fetch_row($compiled);
if(array_key_exists($row[1],$dictAplikasi))
	$h5['ID_APLIKASI']=$dictAplikasi[$row[1]];
else
	$h5['ID_APLIKASI']=NULL;
array_push($response["aplikasi_rapat"],$h5);

echo json_encode($response);



?>

