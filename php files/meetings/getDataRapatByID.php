<?php

// mengambil 2 hal, ruangan dan aplikasi
include "koneksi.php";
$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$rapat_id =$data->rapat_id;
$sql = "SELECT * FROM ".$db_owner."RUANGAN";
$sql2= "SELECT * FROM ".$db_owner."APLIKASI";		
//$sql3= "SELECT * FROM ".$db_owner."RAPAT where ID_RAPAT=:idrapat";
$sql3= "SELECT ID_RAPAT,ID_RUANGAN,TO_CHAR(TANGGAL_MULAI,'DD-Mon-YYYY HH:MI:SS'),TO_CHAR(TANGGAL_SELESAI,'DD-Mon-YYYY HH:MI:SS'),TO_CHAR(JAM_MULAI,'DD-Mon-YYYY HH:MI:SS'),TO_CHAR(JAM_SELESAI,'DD-Mon-YYYY HH:MI:SS'),PERIHAL,PENANGGUNGJAWAB,RESUME_HASIL,TANGGAL_BUAT_RAPAT,PEMBUAT_JADWAL_ID_USER,STATUS_RAPAT FROM ".$db_owner."RAPAT where ID_RAPAT=:idrapat";
//$sql3= "SELECT ID_RAPAT,ID_RUANGAN,TANGGAL_MULAI,TANGGAL_SELESAI,JAM_MULAI,JAM_SELESAI,PERIHAL,PENANGGUNGJAWAB,RESUME_HASIL,TANGGAL_BUAT_RAPAT,PEMBUAT_JADWAL_ID_USER,STATUS_RAPAT FROM ".$db_owner."RAPAT where ID_RAPAT=:idrapat";


$sql4="SELECT DPESERTA.ID_PESERTA,NAMA_PESERTA FROM ".$db_owner."RAPAT_PESERTA,".$db_owner."PESERTA WHERE DRAPAT_PESERTA.ID_RAPAT=:idrapat AND dpeserta.id_peserta=drapat_peserta.id_peserta";
$sql5="SELECT * FROM ".$db_owner."RAPAT_APLIKASI WHERE ID_RAPAT=:idrapat";
$sql6="SELECT DDAFTAR_USER.ID_USER,NAMA FROM ".$db_owner."RAPAT_USER,".$db_owner."DAFTAR_USER WHERE DRAPAT_USER.ID_RAPAT=:idrapat 
and ddaftar_user.id_user=drapat_user.id_user";


$response = array();
$response["ruangan"] = array();
$response["aplikasi"]=array();
$response['rapat']=array();
$response['rapatUser']=array();
$response['rapatPeserta']=array();


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


// Getting rapat peserta dari rapat_peserta ///////////////
$compiled=oci_parse($conn,$sql4);
oci_bind_by_name($compiled,':idrapat',$rapat_id);
oci_execute($compiled);
while (($row = oci_fetch_row($compiled)) != false) {
	$h4['ID_PESERTA']=$row[0];
	$h4['NAMA_PESERTA']=$row[1];
	array_push($response['rapatPeserta'],$h4);
}


// Getting rapat user dari rapat_user /////////////
$compiled=oci_parse($conn,$sql6);
oci_bind_by_name($compiled,':idrapat',$rapat_id);
oci_execute($compiled);
while (($row = oci_fetch_row($compiled)) != false) {
	$h6['ID_USER']=$row[0];
	$h6['NAMA']=$row[1];
	array_push($response['rapatUser'],$h6);
}



// Getting aplikasi from rapat_aplikasi table
$response['aplikasi_rapat']=array();
$compiled =oci_parse($conn, $sql5);
oci_bind_by_name($compiled,':idrapat',$rapat_id);
oci_execute($compiled);
$rapat_list="";
while(($row=oci_fetch_row($compiled))!=false)
{
	if(array_key_exists($row[1],$dictAplikasi))
	{
		if($rapat_list=="")
			$rapat_list=$dictAplikasi[$row[1]];
		else
			$rapat_list=$rapat_list.",".$dictAplikasi[$row[1]];
	}
}

array_push($response["aplikasi_rapat"],$rapat_list);
echo json_encode($response);



?>

