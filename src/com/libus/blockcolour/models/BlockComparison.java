package com.libus.blockcolour.models;

public class BlockComparison {

    private String block;
    private double difference;

    public BlockComparison(String block, double difference){
        this.block = block;
        this.difference = difference;
    }

    public String getBlock(){
        return this.block;
    }

    public double getDifference(){
        return this.difference;
    }
}
