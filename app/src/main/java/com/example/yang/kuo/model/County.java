package com.example.yang.kuo.model;

/**
 * Created by yang on 2017/2/27.
 */

public class County {
    private int id ;
    private String countyName;
    private String countyCode;
    private int cityId;

    public void setId(int id) {
        this.id = id;
    }

    public void setCountryName(String countryName) {
        this.countyName = countryName;
    }

    public void setCountryCode(String countryCode) {
        this.countyCode = countryCode;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getId() {

        return id;
    }

    public String getCountryName() {
        return countyName;
    }

    public String getCountryCode() {
        return countyCode;
    }

    public int getCityId() {
        return cityId;
    }
}
