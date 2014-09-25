<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}

$sql = "SELECT * FROM WILIK.drapat";

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
	array_push($response["rapat"], $h);
}

echo json_encode($response);
?>

