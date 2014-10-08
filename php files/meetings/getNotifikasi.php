<?php
include "koneksi.php";

$waktu_sekarang = gmdate("d-M-y", time()+60*60*7);
#$waktu_sekarang = '07-aug-14';

#echo $waktu_sekarang;
$sql = "SELECT jam_mulai, jam_selesai, perihal, nama_ruangan ".
		"FROM ".$db_owner."rapat, ".$db_owner."ruangan ".
		"WHERE drapat.status_rapat = 1 AND drapat.id_ruangan = druangan.id_ruangan AND (tanggal_mulai = :waktu_sekarang ".
		"OR :waktu_sekarang BETWEEN tanggal_mulai AND tanggal_selesai)";
		
$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':waktu_sekarang', $waktu_sekarang);
oci_execute($compiled);

$response = array();
$response["notifikasi"] = array();

while (($row = oci_fetch_row($compiled)) != false) {
	$h['jam_mulai']  	= $row[0];
	$h['jam_selesai'] 	= $row[1];
	$h['perihal']		= $row[2];
	$h['nama_ruangan']	= $row[3];
	array_push($response["notifikasi"], $h);
}

echo json_encode($response);
?>