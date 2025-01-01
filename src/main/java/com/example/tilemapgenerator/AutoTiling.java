package com.example.tilemapgenerator;

import javafx.scene.paint.Color;

import java.util.HashMap;

public class AutoTiling {

    public int NORTH;
    public int NORTH_EAST;
    public int NORTH_WEST;

    public int SOUTH;
    public int SOUTH_EAST;
    public int SOUTH_WEST;

    public int WEST;
    public int EAST;

    private HashMap<Integer, Integer> correspondingValues = new HashMap<>();


    public AutoTiling(){

        int[] keys = { 2, 8, 10, 11, 16, 18, 22, 24, 26, 27, 30, 31, 64, 66, 72, 74, 75, 80, 82, 86, 88, 90, 91, 94, 95, 104, 106, 107, 120, 122, 123, 126, 127, 208, 210, 214, 216, 218, 219, 222, 223, 248, 250, 251, 254, 255, 0};
        int value = 1;

        for (int key : keys){
            if (key != 0) {
                correspondingValues.put(key, value);
                value++;
            }else{
                correspondingValues.put(key, 47);
            }
        }

        System.out.println(correspondingValues);

    }

    public int getBinaryValue(byte hasNorth, byte hasSouth, byte hasEast, byte hasWest){

        NORTH = (int) Math.pow(2, 0);
        WEST = (int) Math.pow(2, 1);
        EAST = (int) Math.pow(2, 2);
        SOUTH = (int) Math.pow(2, 3);

        return NORTH*hasNorth + EAST*hasEast + SOUTH*hasSouth + WEST*hasWest;

    }

    public int getEightBitDirectional(byte hasNorth,
                                      byte hasNorthEast,
                                      byte hasNorthWest,
                                      byte hasSouth,
                                      byte hasSouthEast,
                                      byte hasSouthWest,
                                      byte hasEast,
                                      byte hasWest){

        NORTH_WEST = (int) Math.pow(2, 0);
        NORTH = (int) Math.pow(2, 1);
        NORTH_EAST = (int) Math.pow(2, 2);

        WEST = (int) Math.pow(2, 3);
        EAST = (int) Math.pow(2, 4);

        SOUTH_WEST = (int) Math.pow(2, 5);
        SOUTH = (int) Math.pow(2, 6);
        SOUTH_EAST = (int) Math.pow(2, 7);


        int bitmask = NORTH_WEST*hasNorthWest + NORTH*hasNorth + NORTH_EAST*hasNorthEast
                + WEST*hasWest + EAST*hasEast + SOUTH_EAST*hasSouthEast
                + SOUTH*hasSouth + SOUTH_WEST*hasSouthWest;


        return correspondingValues.get(bitmask);

    }


}
