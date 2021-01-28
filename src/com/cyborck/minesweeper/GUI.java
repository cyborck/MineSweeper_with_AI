package com.cyborck.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends JFrame implements MouseListener, KeyListener {
    private final int   FIELD_SIZE;
    private final Color gridColor;
    private final Color fieldColor;
    private final Color mineColor;
    private final Color markerColor;
    private final Font  valueFont;
    private final Font  endFont;

    private final MineSweeper mineSweeper;
    private final JPanel      content;

    public GUI ( MineSweeper mineSweeper ) {
        super();
        this.mineSweeper = mineSweeper;

        FIELD_SIZE = 1000 / mineSweeper.FIELDS;
        Color backgroundColor = new Color( 100, 100, 100 );
        gridColor = Color.white;
        fieldColor = new Color( 158, 158, 158 );
        mineColor = Color.black;
        markerColor = Color.red;
        valueFont = new Font( "Aller Display", Font.BOLD, ( int ) ( FIELD_SIZE * .8 ) );
        endFont = new Font( "Aller Display", Font.BOLD, 200 );

        setSize( mineSweeper.FIELDS * FIELD_SIZE + 1, mineSweeper.FIELDS * FIELD_SIZE + 1 );
        setUndecorated( true );
        setTitle( "Minesweeper" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setLocationRelativeTo( null );
        setResizable( false );
        addMouseListener( this );
        addKeyListener( this );
        setVisible( true );

        content = new JPanel() {
            @Override
            protected void paintComponent ( Graphics g ) {
                super.paintComponent( g );

                Graphics2D g2d = ( Graphics2D ) g;
                g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

                if ( mineSweeper.isStarted() ) {
                    //draw fields
                    MineSweeper.Field[][] fields = mineSweeper.getFields();

                    for ( int y = 0; y < mineSweeper.FIELDS; y++ ) {
                        for ( int x = 0; x < mineSweeper.FIELDS; x++ ) {

                            if ( fields[ x ][ y ].isUncovered() ) {
                                switch ( fields[ x ][ y ].getValue() ) {
                                    case -1 -> {
                                        g.setColor( mineColor );
                                        g.fillRect( x * FIELD_SIZE, y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE );
                                    }
                                    case 0 -> {
                                    }
                                    default -> {
                                        g.setColor( getValueColor( fields[ x ][ y ].getValue() ) );
                                        g.setFont( valueFont );
                                        g.drawString( Integer.toString( fields[ x ][ y ].getValue() ), ( int ) ( x * FIELD_SIZE + FIELD_SIZE * .26 ), ( int ) ( ( y + 1 ) * FIELD_SIZE - FIELD_SIZE * .2 ) );
                                    }
                                }
                            } else {
                                if ( fields[ x ][ y ].isMarked() ) g.setColor( markerColor );
                                else g.setColor( fieldColor );
                                g.fillRect( x * FIELD_SIZE, y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE );
                            }
                        }
                    }
                } else {
                    g.setColor( fieldColor );
                    for ( int y = 0; y < mineSweeper.FIELDS; y++ )
                        for ( int x = 0; x < mineSweeper.FIELDS; x++ )
                            g.fillRect( x * FIELD_SIZE, y * FIELD_SIZE, FIELD_SIZE, FIELD_SIZE );
                }

                //draw grid
                g.setColor( gridColor );
                for ( int x = 0; x <= mineSweeper.FIELDS; x++ )
                    g.drawLine( x * FIELD_SIZE, 0, x * FIELD_SIZE, getHeight() );
                for ( int y = 0; y <= mineSweeper.FIELDS; y++ )
                    g.drawLine( 0, y * FIELD_SIZE, getWidth(), y * FIELD_SIZE );

                //draw end screen
                if ( mineSweeper.isStopped() ) {
                    g.setColor( Color.white );
                    g.setFont( endFont );
                    FontMetrics fm = getFontMetrics( endFont );

                    switch ( mineSweeper.getLastGame() ) {
                        case 'w' -> g.drawString( "You won!", getWidth() / 2 - fm.stringWidth( "You won!" ) / 2, getHeight() / 2 + fm.getHeight() / 4 );
                        case 'l' -> g.drawString( "You lost!", getWidth() / 2 - fm.stringWidth( "You lost!" ) / 2, getHeight() / 2 + fm.getHeight() / 4 );
                    }
                }
            }
        };
        content.setLayout( null );
        content.setBackground( backgroundColor );
        content.setSize( getSize() );
        add( content );
    }

    public void update () {
        content.repaint();
    }

    private Color getValueColor ( int value ) {
        return switch ( value ) {
            case 1 -> Color.blue;
            case 2 -> Color.green;
            case 3 -> Color.red;
            case 4 -> Color.blue.darker().darker();
            case 5 -> Color.red.darker().darker();
            case 6 -> Color.pink;
            case 7 -> Color.black;
            case 8 -> Color.cyan;
            default -> throw new IllegalStateException( "Unexpected value: " + value );
        };
    }

    @Override
    public void mousePressed ( MouseEvent e ) {
        if ( !mineSweeper.isStopped() ) {
            int x = e.getX() / FIELD_SIZE;
            int y = e.getY() / FIELD_SIZE;

            if ( !mineSweeper.isStarted() )
                mineSweeper.start( x, y );

            if ( e.getButton() == MouseEvent.BUTTON1 )
                mineSweeper.uncoverField( x, y );
            else if ( e.getButton() == MouseEvent.BUTTON3 )
                mineSweeper.markField( x, y );
        }
    }

    @Override
    public void keyPressed ( KeyEvent e ) {
        switch ( e.getKeyCode() ) {
            case KeyEvent.VK_R -> mineSweeper.restart();
            case KeyEvent.VK_Q -> System.exit( 0 );
        }
    }

    //not used
    @Override
    public void mouseClicked ( MouseEvent e ) {}

    @Override
    public void mouseReleased ( MouseEvent e ) {}

    @Override
    public void mouseEntered ( MouseEvent e ) {}

    @Override
    public void mouseExited ( MouseEvent e ) {}

    @Override
    public void keyTyped ( KeyEvent e ) {}

    @Override
    public void keyReleased ( KeyEvent e ) {}
}
