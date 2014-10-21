package id.net.iconpln.meetings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Wilik on 8/23/2014.
 */
public class globalVar {
    final static public String serverIPaddress = "192.168.212.1";
    final static public String emailAddressPengirim = "testing@gmail.com";
    final static public String passwordEmail ="mantap jaya";
    static public String idRapatEdited="";
    // key=nama lengkap, value=ID
    final static public HashMap<String,String> id_user_saver=new HashMap<String,String>();
    final static public HashMap<String,String> id_peserta_saver=new HashMap<String, String>();
    final static public List<String> tambahan_peserta=new ArrayList<String>();

    public static void deleteAll()
    {
        globalVar.id_peserta_saver.clear();
        globalVar.id_user_saver.clear();
        globalVar.tambahan_peserta.clear();
    }
}
