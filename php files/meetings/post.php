<?php
    $json = $_SERVER['HTTP_JSON'];
    echo "JSON: \n";
    echo "--------------\n";
    var_dump($json);
    echo "\n\n";
 
    $data = json_decode($json);
    echo "Array: \n";
    echo "--------------\n";
    var_dump($data);
    echo "\n\n";
 
    $name = $data->name;
    $pos = $data->position;
    echo "Result: \n";
    echo "--------------\n";
    echo "Name     : ".$name."\n Position : ".$pos; 
?>


<?php
$db = oci_new_connect("system","admin123","localhost/XE");

#$url_name = $_POST['textfield'];
#$anchor_text = $_POST['textfield2'];
#$description = $_POST['textfield3'];

$satu = 'hello';
$dua = '123';

$sql = 'INSERT INTO WILIK.TEST(SATU,DUA) '.
       'VALUES(:satu, :dua)';

$compiled = oci_parse($db, $sql);

oci_bind_by_name($compiled, ':satu', $satu);
oci_bind_by_name($compiled, ':dua', $dua);

oci_execute($compiled);
?>