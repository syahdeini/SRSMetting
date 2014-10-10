SELECT id_dokumen, nama, waktu_upload, status_dokumen, nama_dokumen, tipe_file, perihal, tanggal_mulai, jam_mulai, nama_ruangan
FROM ddokumen
INNER JOIN ddaftar_user ON ddokumen.uploader_id = ddaftar_user.id_user
INNER JOIN drapat ON ddokumen.id_rapat = drapat.id_rapat
LEFT OUTER JOIN druangan ON drapat.id_ruangan = druangan.id_ruangan