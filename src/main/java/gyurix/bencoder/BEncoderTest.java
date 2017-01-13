package gyurix.bencoder;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * A simple tester console app for testing how the BEncoder works
 * Created by GyuriX on 2017. 01. 12..
 */
public class BEncoderTest {
    public static void main(String[] args) throws Throwable {
        //Get the console input
        Scanner scan = new Scanner(System.in);
        //Make a new BEncoder instance
        BEncoder be = new BEncoder();
        while (true) {
            //Read the decodeable BEncoded data
            System.out.print("Enter the testable BEncoded data: ");
            String in = scan.nextLine();
            //Set it to the BEncoders input
            be.setInput(in);
            //Decode it
            ArrayList<Object> list = be.readAll();
            System.out.println("Decoded:\n" + list);
            //Write it back, for testing the encoder part
            be.writeAll(list);
            System.out.println("Encoded:\n" + be);
            //And finally reset the BEncoders output to be usable next time
            be.resetOutput();
        }
    }
}
