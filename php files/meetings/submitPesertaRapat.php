
<?php
include "koneksi.php";

$json = $_SERVER['HTTP_JSON'];
$data = json_decode($json);



$id_rapat=$data->idrapat;


// Deleting data before updating
	$sqlDel="DELETE FROM ".$db_owner."RAPAT_USER WHERE  ID_RAPAT=:id_rapat";
	$compiled = oci_parse($conn, $sqlDel);
	oci_bind_by_name($compiled, ':id_rapat', $id_rapat);
	oci_execute($compiled);
	oci_commit($conn);
	
	$sqlDel="DELETE FROM ".$db_owner."RAPAT_PESERTA WHERE  ID_RAPAT=:id_rapat";
	$compiled = oci_parse($conn, $sqlDel);
	oci_bind_by_name($compiled, ':id_rapat', $id_rapat);
	oci_execute($compiled);
	oci_commit($conn);





if($data->listUser!="")
	$listUser = explode(",",$data->listUser);
if($data->listPeserta!="")
	$listPeserta = explode(",",$data->listPeserta);
if($data->tambahanPeserta!="")
	$listTambahanPeserta = explode(",",$data->tambahanPeserta);

	
	
	// update list User
	if($data->listUser!="")
	{
		// Submit anggota rapat di kolom rapat_anggota
		$sizeList=count($listUser);
		if($listUser!=null)
		{
				for($j=0;$j<$sizeList;$j++)
				{
				// awalnya rapat peserta
					$sqlOnes="INSERT INTO ".$db_owner."RAPAT_USER (ID_USER,ID_RAPAT,STATE_RU) VALUES(:id_peserta,:id_rapat,0)";
					$compiled2 = oci_parse($conn, $sqlOnes);
					oci_bind_by_name($compiled2, ':id_peserta', $listUser[$j]);
					oci_bind_by_name($compiled2, ':id_rapat', $id_rapat);
					oci_execute($compiled2);
					oci_commit($conn);
				}
		}
	}
	// rapat peserta
	if($data->listPeserta!="")
	{
		$sizeList=count($listPeserta);
		if($listPeserta!=null)
		{
			for($j=0;$j<$sizeList;$j++)
			{
				// awalnya rapat peserta
					$sqlOnes="INSERT INTO ".$db_owner."RAPAT_PESERTA (ID_PESERTA,ID_RAPAT,STATE_RP) VALUES(:id_peserta,:id_rapat,0)";
					$compiled2 = oci_parse($conn, $sqlOnes);
					oci_bind_by_name($compiled2, ':id_peserta', $listPeserta[$j]);
					oci_bind_by_name($compiled2, ':id_rapat', $id_rapat);
					oci_execute($compiled2);
					oci_commit($conn);

			}
		}
	}
	// insert ke peserta
	// untuk tambah peserta yang di parsing ialah nama
	if($data->tambahanPeserta!="")
	{
	
		$sizeList=count($listTambahanPeserta);
		if($listTambahanPeserta!=null)
		{
			for($j=0;$j<$sizeList;$j++)
			{
			
			
					// CARI ID PESERTA
		
					// GET PESERTA ID
					$sql1="SELECT COUNT(*) FROM ".$db_owner."PESERTA";
					$compiled1 = oci_parse($conn, $sql1);
					$resultExec1=oci_execute($compiled1);
					$row1= oci_fetch_row($compiled1);
					$val= (int)$row1[0]+1;
					$id_user="PST-".$val;
					oci_commit($conn);
					
					$sql1="INSERT INTO ".$db_owner."PESERTA VALUES (:iduser,:namauser)";
					$compiled1 = oci_parse($conn, $sql1);					
					oci_bind_by_name($compiled1,':iduser',$id_user);
					oci_bind_by_name($compiled1,':namauser',$listTambahanPeserta[$j]);
					$resultExec1=oci_execute($compiled1);
					oci_commit($conn);
					
				// awalnya rapat peserta
					$sqlOnes="INSERT INTO ".$db_owner."RAPAT_PESERTA (ID_PESERTA,ID_RAPAT,STATE_RP) VALUES(:id_peserta,:id_rapat,0)";
					$compiled2 = oci_parse($conn, $sqlOnes);
					oci_bind_by_name($compiled2, ':id_peserta', $id_user);
					oci_bind_by_name($compiled2, ':id_rapat', $id_rapat);
					oci_execute($compiled2);
					oci_commit($conn);

			}
		}
	}
	
	
	
?>