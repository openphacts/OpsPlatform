/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ops.utils;

import java.io.IOException;

import org.apache.http.ParseException;


/**
 *
 * @author Christian
 */
public class EndPointTester {
    
    private static final int NUMBER_OF_THREADS = 20;
    
    public static void main(String[] args) throws ParseException, IOException {
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        for (int i = 0; i < NUMBER_OF_THREADS ; i++){
        	int method=new Double(Math.round(Math.random()*6)).intValue();
            threads[i] = new Thread(new ClientThread(i,method));
            System.out.println(i);
        }
        for (int i = 0; i < NUMBER_OF_THREADS ; i++){
        	System.out.println("Starting thread: "+ i);
            threads[i].start();
        }        
    }
}
    