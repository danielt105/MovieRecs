package dltoy.calpoly.edu.movierecs.Api.Models;

import android.media.session.MediaSession;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class TokenData implements Parcelable {
    private @SerializedName("id") int id;
    private @SerializedName("name") String name;

    public TokenData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public static final Creator<TokenData> CREATOR = new Creator<TokenData>() {
        public TokenData createFromParcel(Parcel in) {
            int newId = in.readInt();
            String newName = in.readString();
            TokenData a = new TokenData(newId, newName);
            return a;
        }

        public TokenData[] newArray(int size) {
            return new TokenData[size];
        }
    };
}
