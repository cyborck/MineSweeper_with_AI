package com.cyborck.minesweeper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AI {
    private Probability[][] probabilities;
    private MineSweeper     mineSweeper;

    private int waitTime;

    public void play ( MineSweeper mineSweeper, int times, int waitTime ) {
        probabilities = new Probability[ mineSweeper.FIELDS ][ mineSweeper.FIELDS ];

        this.mineSweeper = mineSweeper;
        this.waitTime = waitTime;

        for ( int i = 0; i < times; i++ ) {
            if ( i != 0 )
                mineSweeper.restart();
            mineSweeper.start( ( int ) ( ( mineSweeper.FIELDS - 1 ) * .5 ), ( int ) ( ( mineSweeper.FIELDS - 1 ) * .5 ) );
            resetProbabilities();

            while ( mineSweeper.isStarted() && !mineSweeper.isStopped() ) {
                resetProbabilities();
                calculateProbabilities();
                execute();
            }

            if ( waitTime > 0 )
                try {
                    Thread.sleep( waitTime * 10L );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
        }
    }

    private void resetProbabilities () {
        for ( int y = 0; y < mineSweeper.FIELDS; y++ )
            for ( int x = 0; x < mineSweeper.FIELDS; x++ )
                probabilities[ x ][ y ] = new Probability();
    }

    private void calculateProbabilities () {
        for ( int y = 0; y < mineSweeper.FIELDS; y++ )
            for ( int x = 0; x < mineSweeper.FIELDS; x++ )
                if ( mineSweeper.getFields()[ x ][ y ].isUncovered() )
                    if ( mineSweeper.getFields()[ x ][ y ].getValue() > 0 )
                        calculateProbabilityAroundField( x, y );
    }

    private void calculateProbabilityAroundField ( int x, int y ) {
        List<Point> fields = new ArrayList<>();
        double      value  = mineSweeper.getFields()[ x ][ y ].getValue();

        for ( int _y = -1; _y <= 1; _y++ )
            for ( int _x = -1; _x <= 1; _x++ )
                if ( !( _x == 0 && _y == 0 ) )
                    try {
                        if ( mineSweeper.getFields()[ x + _x ][ y + _y ].isMarked() )
                            value--;
                        else if ( !mineSweeper.getFields()[ x + _x ][ y + _y ].isUncovered() )
                            fields.add( new Point( x + _x, y + _y ) );
                    } catch ( ArrayIndexOutOfBoundsException ignored ) {
                    }

        if ( value == 0 )
            for ( Point field: fields ) probabilities[ field.x ][ field.y ].setTo0();
        else if ( value == fields.size() )
            for ( Point field: fields ) probabilities[ field.x ][ field.y ].setTo100();
        else {
            if ( fields.size() > 1 ) {
                double fieldProbability = value / ( double ) fields.size();

                for ( Point field: fields )
                    probabilities[ field.x ][ field.y ].addProbability( fieldProbability );
            }
        }
    }

    private void execute () {
        int actionCounter = 0;
        for ( int x = 0; x < mineSweeper.FIELDS; x++ ) {
            for ( int y = 0; y < mineSweeper.FIELDS; y++ ) {
                if ( probabilities[ x ][ y ].is100 && !mineSweeper.getFields()[ x ][ y ].isMarked() ) {
                    _wait();
                    mineSweeper.markField( x, y );
                    actionCounter++;
                } else if ( probabilities[ x ][ y ].is0 ) {
                    _wait();
                    mineSweeper.uncoverField( x, y );
                    actionCounter++;
                }
            }
        }

        if ( actionCounter == 0 ) {
            double lowestProb  = 1000;
            Point  lowestPoint = new Point( -1, -1 );

            for ( int x = 0; x < mineSweeper.FIELDS; x++ ) {
                for ( int y = 0; y < mineSweeper.FIELDS; y++ ) {
                    if ( probabilities[ x ][ y ].hasProbability() && probabilities[ x ][ y ].calculateProbability() < lowestProb ) {
                        lowestProb = probabilities[ x ][ y ].calculateProbability();
                        lowestPoint = new Point( x, y );
                    }
                }
            }

            if ( lowestPoint.equals( new Point( -1, -1 ) ) ) {
                //uncover remaining field
                for ( int y = 0; y < mineSweeper.FIELDS; y++ )
                    for ( int x = 0; x < mineSweeper.FIELDS; x++ )
                        if ( !mineSweeper.getFields()[ x ][ y ].isMarked() && !mineSweeper.getFields()[ x ][ y ].isUncovered() ) {
                            _wait();
                            mineSweeper.uncoverField( x, y );
                        }
            } else {
                _wait();
                mineSweeper.uncoverField( lowestPoint.x, lowestPoint.y );
            }
        }
    }

    private void _wait () {
        if ( waitTime > 0 )
            try {
                Thread.sleep( waitTime );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
    }

    private static class Probability {
        private double totalProbability = 0;
        private int    potentialMines   = 0;

        private boolean is0   = false;
        private boolean is100 = false;

        private double calculateProbability () {
            return totalProbability / potentialMines;
        }

        private void addProbability ( double probability ) {
            if ( !is0 && !is100 ) {
                totalProbability += probability;
                potentialMines++;
            }
        }

        private void setTo100 () {
            totalProbability = 100;
            is100 = true;
        }

        private void setTo0 () {
            totalProbability = 0;
            is0 = true;
        }

        private boolean hasProbability () {
            return totalProbability >= 0;
        }
    }
}
