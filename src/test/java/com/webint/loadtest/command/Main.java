package com.webint.loadtest.command;

import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class Main {

    /*
        int[] array = new int[] {5,5,5, 33, 1, 1, 1, 3};
        response = 33,3,5,5,5,1,1,1
     */
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        int[] array = new int[] {5, 5, 5, 33, 1, 1, 1, 3};
        Map<Integer, Integer> map = new LinkedHashMap<>();
        Map<Integer, List<Integer>> values = new TreeMap<>();
        int[] sortedArray = new int[array.length];


        for (int i = 0; i < array.length; i++) {
            map.compute(array[i], (k,v) -> v == null ? 1 : v + 1);
        }
        for (Integer key :  map.keySet()) {
            values.compute(map.get(key), (k,vel) -> vel == null ? new ArrayList<Integer>() : vel).add(key);
        }

        int sortedArrayI = 0;
        for (Map.Entry<Integer, List<Integer>> entry : values.entrySet()) {

            for (Integer val1 : entry.getValue()) {
                for (int i = sortedArrayI; i < entry.getKey() + sortedArrayI; i++) {
                    sortedArray[i] = val1;
                }
                sortedArrayI += entry.getKey();
            }
        }
        System.out.println(Arrays.toString(sortedArray));
    }
}
