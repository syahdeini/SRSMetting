<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}
$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$keyword = "%".$data->keyword."%";

$sql = "SELECT id_dokumen, nama, waktu_upload, status_dokumen, nama_dokumen, tipe_file ".
		"FROM WILIK.ddokumen, WILIK.ddaftar_user WHERE (".
		"ddokumen.uploader_id = ddaftar_user.id_user AND (".
		"nama LIKE :keyword OR nama_dokumen LIKE :keyword OR tipe_file LIKE :keyword))";
		
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
	array_push($response["dokumen"], $h);
}

echo json_encode($response);
?>

