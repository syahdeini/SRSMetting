<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

$id_rapat = $data->id_rapat;

$sql = "UPDATE ".$db_owner."dokumen SET status_dokumen = 0 WHERE id_rapat = :id_rapat";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':id_rapat', $id_rapat);
oci_execute($compiled);
oci_commit($conn);

$sql = "UPDATE ".$db_owner."rapat_peserta SET state_rp = '0' WHERE id_rapat = :id_rapat";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':id_rapat', $id_rapat);
oci_execute($compiled);
oci_commit($conn);

$sql = "UPDATE ".$db_owner."rapat SET status_rapat = 0 WHERE id_rapat = :id_rapat";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':id_rapat', $id_rapat);
oci_execute($compiled);
oci_commit($conn);
?>