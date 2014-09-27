package id.net.iconpln.meetings;

import android.os.Parcel;
import android.os.Parcelable;


public class AtomPayment implements Parcelable {
    private static final long serialVersionUID = -5435670920302756945L;

    private String name = "";
    private double value = 0;
    private String id="";

    public AtomPayment(Parcel s)
    {
        name=s.readString();
        id=s.readString();

    }


    public AtomPayment(String name) {
        this.setName(name);
        //	this.setValue(value);
    }



    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id)
    {
        this.id=id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
    }
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public AtomPayment createFromParcel(Parcel in) {
            return new AtomPayment(in);
        }

        public AtomPayment[] newArray(int size) {
            return new AtomPayment[size];
        }
    };

}
