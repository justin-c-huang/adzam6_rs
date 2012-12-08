package com.vpon.landingpage.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public class TestCSV {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 try {
	
			 CSVReader reader = new CSVReader(new FileReader("/Users/apple/mazda6_200_ok.txt"),'\t');
				 String [] nextLine;
				    while ((nextLine = reader.readNext()) != null) {
				        // nextLine[] is an array of values from the line
				        System.out.println(nextLine[1]+"|"+nextLine[2]+"|"+nextLine[3]+"|"+nextLine[4]);
				    }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
