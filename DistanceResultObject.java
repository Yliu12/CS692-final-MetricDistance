package modulemetricdistance;

import java.security.PublicKey;

/**
 * Created by yliu12 on 2017/4/27.
 */
public class DistanceResultObject {


    private int[] distanceArray;
    private int distanceTotal;
    private int numOfDifferentFields;
    private String modName;

    public String getModName() {
        return modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }


    public int[] getDistanceArray() {
        return distanceArray;
    }

    public void setDistanceArray(int[] distanceArray) {
        this.distanceArray = distanceArray;
    }

    //private boolean isSame;

    public int getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(int distanceTotal) {
        this.distanceTotal = distanceTotal;
    }

    public int getNumOfDifferentFields() {
        return numOfDifferentFields;
    }

    public void setNumOfDifferentFields(int numOfDifferentFields) {
        this.numOfDifferentFields = numOfDifferentFields;
    }


}
