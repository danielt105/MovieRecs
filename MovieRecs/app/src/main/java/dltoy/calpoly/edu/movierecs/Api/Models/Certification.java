package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by connor on 11/21/16.
 *
 * This class deals with the nested JSON that is returned by the release dates object
 * returned from the api. Since the only thing of interest is the movie rating (R, PG13, etc)
 * the class deals with extracting that from the nested JSON format
 */

public class Certification {
    public static final String US_CERT = "US";
    @SerializedName("results") private List<CertInfo> certs;

    public static class CertInfo {
        @SerializedName("iso_3166_1") String region;
        @SerializedName("release_dates") List<Cert> info;

        public CertInfo() {}
        public CertInfo(String region) {
            this.region = region;
        }

        public boolean equals(Object other) {
            boolean eq = true;

            if (other == null) {
                eq = false;
            } else if (other.getClass() != this.getClass()) {
                eq = false;
            } else if (!region.equals(((CertInfo)other).region)) {
                eq = false;
            }

            return eq;
        }
    }

    public static class Cert {
        @SerializedName("certification") String rating;
    }

    public String getCertification(String region) {
        int index = certs.indexOf(new CertInfo(region));
        return index >= 0 ? certs.get(index).info.get(0).rating : null;
    }

    /*private static class CertInfo {
        @SerializedName("iso_3166_1") String region;
        @SerializedName("release_dates") List<Cert> info;

        public CertInfo(){}
        public CertInfo(String region) {
            this.region = region;
        }

        public boolean equals(CertInfo other) {
            boolean eq = true;

            if (other == null) {
                eq = false;
            } else if (other.getClass() != this.getClass()) {
                eq = false;
            } else if (!region.equals(other.region)) {
                eq = false;
            }

            return eq;
        }
    }

    private static class Cert {
        @SerializedName("certification") String rating;
    }*/
}
