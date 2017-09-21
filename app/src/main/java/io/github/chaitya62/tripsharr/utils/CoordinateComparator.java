package io.github.chaitya62.tripsharr.utils;

import java.util.Comparator;

import io.github.chaitya62.tripsharr.primeobjects.Coordinates;

/**
 * Created by mikasa on 20/9/17.
 */

public class CoordinateComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Coordinates c1 = (Coordinates)o1;
        Coordinates c2 = (Coordinates)o2;
        if(c1.getTimestamp().after(c2.getTimestamp())){
            return 1;
        } else {
            return -1;
        }
    }
}
