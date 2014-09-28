<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}

$id_dokumen = $_GET["id_dokumen"];
$sql = "SELECT * FROM WILIK.ddokumen WHERE id_dokumen = :id_dokumen";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':id_dokumen', $id_dokumen);
$result = oci_execute($compiled);

if($result !== false){
	while($row = oci_fetch_assoc($compiled)){
		header("Content-type: ".$row['TIPE_FILE']);
		header("Content-disposition: download; filename=".$row['NAMA_DOKUMEN']);
		echo $row['DOKUMEN']->load();
		//or
		//echo $row['DOKUMEN']->read(2000);
	}
}
?>