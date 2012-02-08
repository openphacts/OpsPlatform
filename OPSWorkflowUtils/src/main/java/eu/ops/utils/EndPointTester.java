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
    
    private static final int NUMBER_OF_THREADS = 10;
    
    public static void main(String[] args) throws ParseException, IOException {
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        for (int i = 0; i < NUMBER_OF_THREADS ; i++){
            threads[i] = new Thread(new ClientThread(i));
            System.out.println(i);
        }
        for (int i = 0; i < NUMBER_OF_THREADS ; i++){
            threads[i].start();
        }        
    }
}
    