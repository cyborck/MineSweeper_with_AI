package com.cyborck.minesweeper;

public class AITest {
    public static void main ( String[] args ) throws InterruptedException {
        AI ai = new AI();
        MineSweeper ms = new MineSweeper( 100 );

        Thread.sleep( 1000 );

        ai.play( ms, 50, 30 );
    }
}
