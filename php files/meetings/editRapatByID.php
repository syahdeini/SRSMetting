<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

 //getting data from android, 
$idrapat=$data->idrapat;
$ruangan = $data->ruangan;
$aplikasi = $data->aplikasi;
$dateMulai = $data->dateMulai;
$dateSelesai= $data->dateSelesai;
$timeStampMulai= $data->timeStampMulai;
$timeStampSelesai = $data->timeStampSelesai;
$perihal = $data->perihal;
if($perihal==NULL || $data->perihal=="" || is_null($data->perihal))
	$perihal="none";
$penanggungJawab = $data->penanggungJawab;
if($penanggungJawab==NULL  || $data->penanggungJawab=="" || is_null($data->perihal))
	$penanggungJawab="none";
$resumeHasil = $data->resumeHasil;
$tanggalBuatRapat = $data->tanggalBuatRapat;
$pembuatJadwal = $data->pembuatJadwal;
$statusRapat = $data->statusRapat; // convert to integer

// UNTUK MENGECEK APAKAH RAPAT BERTABRAKAN 
$sql0="SELECT * FROM ".$db_owner."RAPAT WHERE ((
jam_mulai <= TO_TIMESTAMP(:timeStampMulai,'yyyy/mm/dd hh24:mi') 
and 
jam_selesai >= TO_TIMESTAMP(:timeStampSelesai,'yyyy/mm/dd hh24:mi'))
or 
(jam_mulai >= TO_TIMESTAMP(:timeStampMulai,'yyyy/mm/dd hh24:mi') 
and 
jam_selesai > TO_TIMESTAMP(:timeStampSelesai,'yyyy/mm/dd hh24:mi') 
and 
jam_mulai < TO_TIMESTAMP(:timeStampSelesai,'yyyy/mm/dd hh24:mi'))
or 
(jam_mulai <= TO_TIMESTAMP(:timeStampMulai,'yyyy/mm/dd hh24:mi') 
and 
jam_selesai >= TO_TIMESTAMP(:timeStampMulai,'yyyy/mm/dd hh24:mi')))
and
ID_RUANGAN!=:idRuangan and ID_RAPAT!=:idrapat";

$compiled=oci_parse($conn,$sql0);
oci_bind_by_name($compiled, ':timeStampMulai', $timeStampMulai);
oci_bind_by_name($compiled, ':timeStampSelesai', $timeStampSelesai);
oci_bind_by_name($compiled, ':idRuangan', $ruangan);
oci_bind_by_name($compiled,':idrapat',$idrapat);


$result=oci_execute($compiled);
$row=oci_fetch_row($compiled);

$response=array();
if($row!=NULL)	
{
	$response['adaRapat']='1'; // berarti rapat ada di database (TABRAKAN)
}
else
{


	$response['adaRapat']='0'; 
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


	
	$sql="DELETE FROM ".$db_owner."RAPAT_APLIKASI WHERE ID_RAPAT=:idrapat";
	$compiled = oci_parse($conn,$sql);
	oci_bind_by_name($compiled,':idrapat',$idrapat);
	oci_execute($compiled);
				
	
	
	// submit aplikasi
	if($aplikasi!=null && $resultExec==True)
	{
		$listAplikasi=explode(',',$aplikasi);
		for($i=0;$i<count($listAplikasi);$i++)
		{
			
			$sql="SELECT * FROM ".$db_owner."APLIKASI WHERE NAMA_APLIKASI LIKE :namaAplikasi";
			$compiled = oci_parse($conn,$sql);
			oci_bind_by_name($compiled,':namaAplikasi',$listAplikasi[$i]);
			oci_execute($compiled);
			$row=oci_fetch_row($compiled);
			if($row[0]!=null)
			{
				
				//print $row[0];
				$sqlInner="INSERT INTO ".$db_owner."RAPAT_APLIKASI VALUES(:idRapat,:idAplikasi,0)";
				$compiledInner=oci_parse($conn,$sqlInner);
				oci_bind_by_name($compiledInner,':idRapat',$idrapat);
				oci_bind_by_name($compiledInner,':idAplikasi',$row[0]);
				oci_execute($compiledInner);
				
			}
			else
			{
				//print $listAplikasi[$i]."<br>";
				$sql="SELECT COUNT(*) FROM ".$db_owner."APLIKASI";
				$compiledCount=oci_parse($conn,$sql);
				oci_execute($compiledCount);
				$row1= oci_fetch_row($compiledCount);
				$val= (int)$row1[0]+1;
				$id_applikasi="APP".$val;
				oci_commit($conn);
				
				
				$sql="INSERT INTO ".$db_owner."APLIKASI VALUES(:idAplikasi,:namaAplikasi)";
				$compiled2=oci_parse($conn,$sql);
				oci_bind_by_name($compiled2,':idAplikasi',$id_applikasi);
				oci_bind_by_name($compiled2,':namaAplikasi',$listAplikasi[$i]);
				oci_execute($compiled2);
				
				
				$sqlInner="INSERT INTO ".$db_owner."RAPAT_APLIKASI VALUES(:idRapat,:idAplikasi,0)";
				$compiledInner=oci_parse($conn,$sqlInner);
				oci_bind_by_name($compiledInner,':idRapat',$idrapat);
				oci_bind_by_name($compiledInner,':idAplikasi',$id_applikasi);
				oci_execute($compiledInner);
				
			}
		}
	}


	if ($resultExec==true) {
		$response["success"] = "1";
		
	}
	else {
		$response["success"] = "0";
		$response["message"] = "Tidak ada data";
		
	}
}

echo json_encode($response);			

?>