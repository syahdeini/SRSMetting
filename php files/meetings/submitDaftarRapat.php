<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);

 //getting data from android, 

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
//if($resumeHasil==NULL || $data->resumeHasil || is_null($data->perihal))// . 
	//$resumeHasil="none";
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
ID_RUANGAN=:idRuangan";

$compiled=oci_parse($conn,$sql0);
oci_bind_by_name($compiled, ':timeStampMulai', $timeStampMulai);
oci_bind_by_name($compiled, ':timeStampSelesai', $timeStampSelesai);
oci_bind_by_name($compiled, ':idRuangan', $ruangan);


$result=oci_execute($compiled);
$row=oci_fetch_row($compiled);

$response=array();
if($row!=NULL)	
{
	$response['adaRapat']='1'; // berarti rapat ada di database (TABRAKAN)
}
else
{
	// RAPAT TERSEDIA
	$response['adaRapat']='0';
	// getting ID rapat
	$sql1="SELECT COUNT(*) FROM ".$db_owner."RAPAT";
	$compiled1 = oci_parse($conn, $sql1);
	$resultExec1=oci_execute($compiled1);
	$row1= oci_fetch_row($compiled1);
	$val= (int)$row1[0]+1;
	$id_rapat="RPT-".$val;
	//echo $id_rapat



	$sql = "INSERT INTO ".$db_owner."RAPAT".
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

	// submit aplikasi
	if($aplikasi!=null && $resultExec==True)
	{
		$listAplikasi=explode(',',$aplikasi);
		for($i=0;$i<count($listAplikasi);$i++)
		{
			//print $listAplikasi[$i]."<br>";
		
			$sql="SELECT * FROM ".$db_owner."APLIKASI WHERE NAMA_APLIKASI LIKE :namaAplikasi";
			$compiled = oci_parse($conn,$sql);
			oci_bind_by_name($compiled,':namaAplikasi',$listAplikasi[$i]);
			oci_execute($compiled);
			$row=oci_fetch_row($compiled);
			if($row[0]!=null)
			{
				$row=oci_fetch_row($compiled);
				//print $row[0];
				$sqlInner="INSERT INTO ".$db_owner."RAPAT_APLIKASI VALUES(:idRapat,:idAplikasi,0)";
				$compiledInner=oci_parse($conn,$sqlInner);
				oci_bind_by_name($compiledInner,':idRapat',$id_rapat);
				oci_bind_by_name($compiledInner,':idAplikasi',$row[0]);
				oci_execute($compiledInner);
				
			}
			else
			{	//print $listAplikasi[$i]."<br>";
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
				oci_bind_by_name($compiledInner,':idRapat',$id_rapat);
				oci_bind_by_name($compiledInner,':idAplikasi',$id_applikasi);
				oci_execute($compiledInner);
			}
		}
	}

	if ($resultExec==true) {
		$response["success"] = "1";
		$response["idrapat"]=$id_rapat;
	}
	else {
		$response["success"] = "0";
		$response["message"] = "Tidak ada data";
	}
}

echo json_encode($response);

?>