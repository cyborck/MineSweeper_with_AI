package com.cyborck.minesweeper;

public class MineSweeper {
    public final int FIELDS;
    public final int BOMB_COUNT;

    private final Field[][] fields;
    private final GUI       gui;

    private boolean started = false;
    private boolean stopped = false;

    private int    gamesPlayed = 0;
    private int    gamesWon    = 0;
    private double winRate     = 0;

    private char lastGame = 'e';

    public MineSweeper ( int fields ) {
        FIELDS = fields;
        BOMB_COUNT = ( int ) ( 0.16 * FIELDS * FIELDS );

        this.fields = new Field[ FIELDS ][ FIELDS ];
        gui = new GUI( this );
    }

    public static void main ( String[] args ) {
        new MineSweeper( 15 );
    }

    public void start ( int startX, int startY ) {
        started = true;
        generateMatchField( startX, startY );
        uncoverField( startX, startY );
    }

    private void generateMatchField ( int startX, int startY ) {
        int[][] fieldValues = new int[ FIELDS ][ FIELDS ];
        for ( int y = 0; y < FIELDS; y++ )
            for ( int x = 0; x < FIELDS; x++ )
                fieldValues[ x ][ y ] = 0;

        for ( int i = 0; i < BOMB_COUNT; i++ ) {
            //set random bomb positions
            int x = ( int ) ( Math.random() * FIELDS );
            int y = ( int ) ( Math.random() * FIELDS );
            if ( fieldValues[ x ][ y ] != -1 && !( x <= startX + 1 && x >= startX - 1 && y <= startY + 1 && y >= startY - 1 ) ) {
                fieldValues[ x ][ y ] = -1;

                //+1 to every field around the bomb
                for ( int _y = -1; _y <= 1; _y++ )
                    for ( int _x = -1; _x <= 1; _x++ )
                        try {
                            if ( !( _x == 0 && _y == 0 ) && fieldValues[ _x + x ][ _y + y ] != -1 )
                                fieldValues[ _x + x ][ _y + y ]++;
                        } catch ( IndexOutOfBoundsException ignored ) {
                        }
            } else i--;
        }

        for ( int y = 0; y < FIELDS; y++ )
            for ( int x = 0; x < FIELDS; x++ )
                fields[ x ][ y ] = new Field( fieldValues[ x ][ y ] );
    }

    public void uncoverField ( int x, int y ) {
        if ( started && !stopped ) {
            if ( !fields[ x ][ y ].isUncovered() && !fields[ x ][ y ].isMarked() ) {
                fields[ x ][ y ].uncover();

                if ( fields[ x ][ y ].getValue() == -1 ) {
                    loose();
                } else if ( fields[ x ][ y ].getValue() == 0 ) {
                    //uncover surrounding fields
                    for ( int _y = -1; _y <= 1; _y++ )
                        for ( int _x = -1; _x <= 1; _x++ )
                            if ( _x + x < FIELDS && _x + x >= 0 && _y + y < FIELDS && _y + y >= 0 )
                                uncoverField( _x + x, _y + y );
                }
            }

            gui.update();
            checkWin();
        }
    }

    public void markField ( int x, int y ) {
        if ( fields[ x ][ y ].isMarked() )
            fields[ x ][ y ].deleteMarking();
        else
            fields[ x ][ y ].mark();
        gui.update();
    }

    public void checkWin () {
        if ( started && !stopped ) {
            boolean isWin = true;

            outerLoop:
            for ( Field[] column: fields ) {
                for ( Field field: column ) {
                    if ( !field.isUncovered() && field.getValue() != -1 ) {
                        isWin = false;
                        break outerLoop;
                    }
                }
            }

            if ( isWin ) win();
        }
    }

    private void win () {
        stopped = true;
        lastGame = 'w';
        gui.update();

        gamesPlayed++;
        gamesWon++;
        printStats();
    }

    private void loose () {
        stopped = true;
        lastGame = 'l';
        gui.update();

        gamesPlayed++;
        printStats();
    }

    public void restart () {
        stopped = false;
        started = false;
        gui.update();
    }

    public void quit(){
        gui.setVisible( false );
    }

    private void updateWinRate () {
        winRate = gamesWon / ( double ) gamesPlayed * 100;
    }

    public void printStats () {
        updateWinRate();

        System.out.println( gamesPlayed + " games played" );
        System.out.println( gamesWon + " games won" );
        System.out.println( "win rate: " + winRate );
    }

    public Field[][] getFields () {
        return fields;
    }

    public boolean isStarted () {
        return started;
    }

    public boolean isStopped () {
        return stopped;
    }

    public double getWinRate () {
        updateWinRate();
        return winRate;
    }

    public char getLastGame () {
        return lastGame;
    }

    public static class Field {
        private final int value;

        private boolean uncovered = false;
        private boolean marked    = false;

        public Field ( int value ) {
            this.value = value;
        }

        public void uncover () {
            uncovered = true;
        }

        public void mark () {
            marked = true;
        }

        public void deleteMarking () {
            marked = false;
        }

        public int getValue () {
            return value;
        }

        public boolean isUncovered () {
            return uncovered;
        }

        public boolean isMarked () {
            return marked;
        }
    }
}
