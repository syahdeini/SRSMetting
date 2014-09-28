<?php
$conn = oci_connect('system', 'admin123', 'localhost/XE');
if (!$conn) {
    $e = oci_error();
    trigger_error(htmlentities($e['message'], ENT_QUOTES), E_USER_ERROR);
}

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

 //getting data from android, 
$ruangan = $data->ruangan;
$aplikasi = $aplikasi->aplikasi;
$dateMulai = $data->dateMulai;
$dateSelesai= $data->dateSelesai;
$timeStampMulai= $data->timeStampMulai;
$timeStampSelesai = $data->timeStampSelesai;
$perihal = $data->perihal;
$penanggungJawab = $data->penanggungJawab;
$resumeHasil = $data->resumeHasil;
$tanggalBuatRapat = $data->tanggalBuatRapat;
$pembuatJadwal = $data->pembuatJadwal;
$statusRapat = $data->statusRapat; // convert to integer
$listPeserta = explode(",",$data->listPeserta);
/*
$ruangan = "R 102";
//$aplikasi = $aplikasi->aplikasi;
$dateMulai = "2012/02/12";
$dateSelesai= "2011/01/04";
$timeStampMulai= "2011/11/11 14:00";
$timeStampSelesai = "2013/05/15 13:00";
$perihal = "perihal banyak";
$penanggungJawab = "anang,boning,canang";
$resumeHasil = "hasil";
$tanggalBuatRapat = "2014/05/03 21:13";
$pembuatJadwal ="joko";
$statusRapat = 1;
$listAray=explode(",",$penanggungJawab);
echo $listAray[2];
//$listPeserta = s$data->listPeserta;
*/


// getting ID rapat
$sql1="SELECT COUNT(*) FROM WILIK.DRAPAT";
$compiled1 = oci_parse($conn, $sql1);
$resultExec1=oci_execute($compiled1);
$row1= oci_fetch_row($compiled1);
$val= (int)$row1[0]+1;
$id_rapat="RPT-".$val;
//echo $id_rapat



$sql = "INSERT INTO WILIK.DRAPAT".
		"(ID_RAPAT,
ID_RUANGAN,
TANGGAL_MULAI,
TANGGAL_SELESAI,
JAM_MULAI,
JAM_SELESAI,
PERIHAL,
PENANGGUNGJAWAB,
RESUME_HASIL,
TANGGAL_BUAT_RAPAT,
PEMBUAT_JADWAL_ID_USER,
STATUS_RAPAT)".
		"VALUES (:id_rapat,
		:ruangan,
		TO_DATE(:dateMulai,'yyyy/mm/dd'),
		TO_DATE(:dateSelesai,'yyyy/mm/dd'),
		TO_TIMESTAMP(:timeStampMulai,'yyyy/mm/dd hh24:mi')
		,TO_TIMESTAMP(:timeStampSelesai,'yyyy/mm/dd hh24:mi'),
		:perihal,
		:penanggungJawab,
		:resumeHasil,
		TO_TIMESTAMP(:tanggalBuatRapat,'yyyy/mm/dd hh24:mi'),
		:pembuatJadwal,
		:statusRapat)";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':id_rapat', $id_rapat);
oci_bind_by_name($compiled, ':ruangan', $ruangan);
oci_bind_by_name($compiled, ':dateMulai', $dateMulai);
oci_bind_by_name($compiled, ':dateSelesai',$dateSelesai);
oci_bind_by_name($compiled, ':timeStampMulai',$timeStampMulai);
oci_bind_by_name($compiled, ':timeStampSelesai',$timeStampSelesai);
oci_bind_by_name($compiled, ':perihal', $perihal);
oci_bind_by_name($compiled, ':penanggungJawab', $penanggungJawab);
oci_bind_by_name($compiled, ':resumeHasil', $resumeHasil);
oci_bind_by_name($compiled, ':tanggalBuatRapat', $tanggalBuatRapat);
oci_bind_by_name($compiled, ':pembuatJadwal', $pembuatJadwal);
oci_bind_by_name($compiled, ':statusRapat', $statusRapat);


$resultExec=oci_execute($compiled);

// Submit anggota rapat di kolom rapat_anggota
$sizeList=count($listPeserta);
if($listPeserta!=null)
{
		for($j=0;$j<$sizeList;$j++)
		{
			$sqlOnes="INSERT INTO WILIK.DRAPAT_PESERTA (ID_PESERTA,ID_RAPAT,STATE_RP) VALUES(:id_peserta,:id_rapat,0)";
			$compiled2 = oci_parse($conn, $sqlOnes);
			oci_bind_by_name($compiled2, ':id_peserta', $listPeserta[$j]);
			oci_bind_by_name($compiled2, ':id_rapat', $id_rapat);
			oci_execute($compiled2);
			//			$listPeserta[j];
		}
}


$response = array();

if ($resultExec==true) {
	$response["success"] = "1";
	echo json_encode($response);			
}
else {
    $response["success"] = "0";
    $response["message"] = "Tidak ada data";
	echo json_encode($response);
}



?>