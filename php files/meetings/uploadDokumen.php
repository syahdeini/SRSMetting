<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
	$e = oci_error();
	trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}

$sql = "SELECT id_dokumen FROM WILIK.ddokumen";
		
$compiled = oci_parse($conn, $sql);
oci_execute($compiled) or die(oci_error($compiled));
oci_fetch_all($compiled, $array);
unset($array);

$numberofrows = oci_num_rows($compiled) + 1;
oci_free_statement($compiled);

$id_dokumen = "DOC-". sprintf("%03d", $numberofrows);

$id_rapat    = $_GET["id_rapat"];
$uploader_id    = $_GET["uploader_id"];
$waktu_upload    = gmdate("d-M-y h.i.s.u A", time()+60*60*7);
$status_dokumen    = $_GET["status_dokumen"];
$nama_dokumen    = $_GET["nama_dokumen"];

$image = file_get_contents($_FILES['uploaded_file']['tmp_name']);

$finfo = finfo_open(FILEINFO_MIME_TYPE);
$tipe_file = finfo_file($finfo, $_FILES['uploaded_file']['tmp_name']);
finfo_close($finfo);

$sql = "INSERT INTO WILIK.ddokumen (id_dokumen, id_rapat, dokumen, uploader_id, waktu_upload, status_dokumen, ".
		"nama_dokumen, tipe_file) VALUES(:id_dokumen, :id_rapat, empty_blob(), :uploader_id, :waktu_upload, ".
		":status_dokumen, :nama_dokumen, :tipe_file) RETURNING dokumen INTO :image";
$result = oci_parse($conn, $sql);
$blob = oci_new_descriptor($conn, OCI_D_LOB);
oci_bind_by_name($result, ':id_dokumen', $id_dokumen);
oci_bind_by_name($result, ':id_rapat', $id_rapat);
oci_bind_by_name($result, ':uploader_id', $uploader_id);
oci_bind_by_name($result, ':waktu_upload', $waktu_upload);
oci_bind_by_name($result, ':status_dokumen', $status_dokumen);
oci_bind_by_name($result, ':nama_dokumen', $nama_dokumen);
oci_bind_by_name($result, ':tipe_file', $tipe_file);
oci_bind_by_name($result, ":image", $blob, -1, OCI_B_BLOB);
oci_execute($result, OCI_DEFAULT) or die ("Unable to execute query");

if(!$blob->save($image)) {
    oci_rollback($conn);
}
else {
    oci_commit($conn);
}

oci_free_statement($result);
$blob->free();
?>