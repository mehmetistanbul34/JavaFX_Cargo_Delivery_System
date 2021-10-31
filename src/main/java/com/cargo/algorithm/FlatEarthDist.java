package com.cargo.algorithm;

public class FlatEarthDist {
    //returns distance in meters
    public static double distance(double plat1, double plng1,
                                  double plat2, double plng2){
        final int R = 6371; // Radious of the earth
        Double lat1 = plat1;
        Double lon1 = plng1;
        Double lat2 = plat2;
        Double lon2 = plng2;
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        return distance.doubleValue();
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }
}