<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

 //getting data from android, 
$idrapat=$data->idrapat;
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



$sql = "UPDATE ".$db_owner."RAPAT".
		" SET 
ID_RUANGAN=:ruangan,
TANGGAL_MULAI=TO_DATE(:dateMulai,'yyyy/mm/dd'),
TANGGAL_SELESAI=TO_DATE(:dateSelesai,'yyyy/mm/dd'),
JAM_MULAI=TO_TIMESTAMP(:timeStampMulai,'yyyy/mm/dd hh24:mi'),
JAM_SELESAI=TO_TIMESTAMP(:timeStampSelesai,'yyyy/mm/dd hh24:mi'),
PERIHAL=:perihal,
PENANGGUNGJAWAB=:penanggungJawab,
RESUME_HASIL=:resumeHasil,
TANGGAL_BUAT_RAPAT=TO_TIMESTAMP(:tanggalBuatRapat,'yyyy/mm/dd hh24:mi'),
PEMBUAT_JADWAL_ID_USER=:pembuatJadwal,
STATUS_RAPAT=:statusRapat WHERE ID_RAPAT=:idrapat";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled, ':idrapat', $idrapat);
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


//delete semua user pada rapat
$sql = "DELETE FROM ".$db_owner.".RAPAT_PESERTA WHERE ID_RAPAT=:idrapat";

$compiled = oci_parse($conn, $sql);
oci_bind_by_name($compiled,':idrapat',$idrapat);
oci_execute($compiled);
//////////////////////
// Submit anggota rapat di kolom rapat_anggota

$sizeList=count($listPeserta);
if($listPeserta!=null)
{
		for($j=0;$j<$sizeList;$j++)
		{
			$sqlOnes="INSERT INTO ".$db_owner."RAPAT_PESERTA (ID_PESERTA,ID_RAPAT,STATE_RP) VALUES(:id_peserta,:id_rapat,0)";
			$compiled2 = oci_parse($conn, $sqlOnes);
			oci_bind_by_name($compiled2, ':id_peserta', $listPeserta[$j]);
			oci_bind_by_name($compiled2, ':id_rapat', $idrapat);
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