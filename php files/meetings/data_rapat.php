<?php
include "koneksi.php";

$sql = "SELECT * FROM ".$db_owner."RAPAT, ".$db_owner."RUANGAN, ".$db_owner."DAFTAR_USER WHERE DRAPAT.id_ruangan = DRUANGAN.id_ruangan AND DRAPAT.pembuat_jadwal_id_user = DDAFTAR_USER.id_user AND DRAPAT.status_rapat = 1";

$compiled = oci_parse($conn, $sql);
oci_execute($compiled);

$response = array();
$response["rapat"] = array();

while (($row = oci_fetch_row($compiled)) != false) {
	$h['id_rapat']  				= $row[0];
	$h['id_ruangan']  				= $row[1];
	$h['tanggal_mulai']  			= $row[2];
	$h['tanggal_selesai']  			= $row[3];
	$h['jam_mulai']  				= $row[4];
	$h['jam_selesai']  				= $row[5];
	$h['perihal']  					= $row[6];
	$h['penanggungjawab']  			= $row[7];
	$h['resume_hasil']  			= $row[8];
	$h['tanggal_buat_rapat']  		= $row[9];
	$h['pembuat_jadwal_id_user'] 	= $row[10];
	$h['status_rapat']				= $row[11];
	$h['nama_ruangan']				= $row[13];
	$h['nama_pembuat_jadwal']		= $row[16];
	array_push($response["rapat"], $h);
}

echo json_encode($response);
?>

