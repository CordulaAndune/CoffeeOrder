package com.example.andriod.justjava;

import java.util.concurrent.atomic.AtomicInteger;



public class ViewIdGenerator {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setId.
     * This value will not collide with ID values generated at build time by aadt for R.id.
     *
     * @return a generated ID value
     *
     * Created by Cordula Gloge on 17/01/2018.
     */

    public static int generateViewId(){
        for(;;){
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if(newValue > 0x00FFFFFF) newValue = 1;
            if(sNextGeneratedId.compareAndSet(result, newValue)){
                return result;
            }
        }
    }
}
