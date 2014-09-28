<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}
#$json = $_SERVER['HTTP_JSON'];
#$data = json_decode($json);

#$keyword = "%".$data->keyword."%";
$keyword = "%%";

$sql = "SELECT id_dokumen, nama, waktu_upload, status_dokumen, nama_dokumen, tipe_file, perihal, tanggal_mulai, jam_mulai, nama_ruangan ".
		"FROM WILIK.ddokumen ".
		"INNER JOIN WILIK.ddaftar_user ON ddokumen.uploader_id = ddaftar_user.id_user ".
		"INNER JOIN WILIK.drapat ON ddokumen.id_rapat = drapat.id_rapat ".
		"LEFT OUTER JOIN WILIK.druangan ON drapat.id_ruangan = druangan.id_ruangan AND (".
		"nama LIKE :keyword OR nama_dokumen LIKE :keyword OR tipe_file LIKE :keyword)";
		
$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':keyword', $keyword);
oci_execute($compiled);

$response = array();
$response["dokumen"] = array();

while (($row = oci_fetch_row($compiled)) != false) {
	$h['id_dokumen']  		= $row[0];
	$h['nama'] 				= $row[1];
	$h['waktu_upload']		= $row[2];
	$h['status_dokumen']	= $row[3];
	$h['nama_dokumen']		= $row[4];
	$h['tipe_file']			= $row[5];
	$h['perihal']			= $row[6];
	$h['tanggal_mulai']		= $row[7];
	$h['jam_mulai']			= $row[8];
	$h['nama_ruangan']		= $row[9];
	array_push($response["dokumen"], $h);
}

echo json_encode($response);
?>

