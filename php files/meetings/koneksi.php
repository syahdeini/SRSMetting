<?php
$db_user = 'system';
$db_pass = 'admin123';
$db_location = 'localhost/XE';

$conn = oci_connect($db_user, $db_pass, $db_location);
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}

$db_owner = 'WILIK.D';
?>