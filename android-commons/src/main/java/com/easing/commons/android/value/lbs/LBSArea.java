package com.easing.commons.android.value.lbs;

import lombok.Data;

import java.util.ArrayList;

@Data
public class LBSArea {

    private ArrayList<Province> provinces;

    public ArrayList<Province> getAllProvinces() {
        return provinces;
    }

    public ArrayList<ArrayList<City>> getAllCities() {
        ArrayList<ArrayList<City>> l1 = new ArrayList();
        for (Province province : provinces)
            l1.add(province.cities);
        return l1;
    }

    public ArrayList<ArrayList<ArrayList<County>>> getAllCounties() {
        ArrayList<ArrayList<ArrayList<County>>> l1 = new ArrayList();
        for (Province province : provinces) {
            ArrayList<ArrayList<County>> l2 = new ArrayList();
            for (City city : province.cities)
                l2.add(city.counties);
            l1.add(l2);
        }
        return l1;
    }

    @Data
    public static class Province {
        private int provinceId;
        private String provinceName;
        private ArrayList<City> cities;

        @Override
        public String toString() {
            return provinceName;
        }
    }

    @Data
    public static class City {
        private int cityId;
        private String cityName;
        private ArrayList<County> counties;

        @Override
        public String toString() {
            return cityName;
        }
    }

    @Data
    public static class County {
        private int countyId;
        private String countyName;

        @Override
        public String toString() {
            return countyName;
        }
    }
}
