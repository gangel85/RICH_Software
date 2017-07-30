package Reading;


import java.util.*;


import org.jlab.groot.*;
import org.jlab.io.evio.*;
import org.jlab.groot.ui.TCanvas;
import org.jlab.io.base.*;
import org.jlab.io.hipo.*;

import Position_Finder.Pixel_Position;
import eu.mihosoft.vrl.v3d.Vector3d;

import org.jlab.groot.data.H1F;
import org.jlab.groot.ui.*;

import org.jlab.detector.geant4.v2.*;
import org.jlab.detector.units.SystemOfUnits.Length;
import org.jlab.geom.prim.Point3D;



public class Reading_EVIO
{
    public static void main(String[] args) 
    {
    // Write down an histragram	
      //  TCanvas canvas = new TCanvas("canvas",800,800);


    

     List<Integer> myList = new ArrayList<Integer>();
     
	System.out.println("Hi, welcome in EVIO reader!");
	EvioSource evioreader = new EvioSource();
	/* From here to..
	evioreader.open("/Users/quasar/Desktop/RICH_Bank/testRICH.evio");
	//evioreader.open("/Users/quasar/work/rich/software/simu/detectors/clas12/rich/testJustin.evio");
		 while(evioreader.hasEvent())
		 {
			 System.out.println("Evio has events");
			 DataEvent evioevent = evioreader.getNextEvent();
			 evioevent.show();
			 	if(evioevent.hasBank("RICH::dgtz"))
		        {
			 		
			 		DataBank  bank = evioevent.getBank("RICH::dgtz");
			         System.out.println("there is a bank into RICH!");
			         int rows = bank.rows();
			         for(int i = 0; i < rows; i++){
			        	int pmt= bank.getInt("pmt", i);
			        	myList.add(pmt);		    
		        	 System.out.println("PMT = "+ pmt);
			         }
			         int[] pmts = myList.stream().mapToInt(i->i).toArray();   
			        
			        int hash = Arrays.hashCode(pmts);
			        h1.fill(hash);
			    	System.out.println("**** The Number is = "+ hash);
				 System.out.println("I found an Evio bank");
		        }
		        
		 }	
		 // To here
	*/
	
		// canvas.divide(1, 2);
		//  canvas.draw(h1);
		
	System.out.println("************** END !!! **************");
    
	int[] inputNumbers = { 543, 134, 998 };
	RICHGeant4Factory richfactory = new RICHGeant4Factory();
	
	// Definition of variables
     final int Pmts_total = 22;
     final double MAPMTWall_thickness = 0.1;
     final double MAPMTPhotocathode_side = 4.9;
	// Bring it back to 1 !
	for (int i=20 ; i <= Pmts_total; i++ )
	{
		//get the 4 vertex of the frontal face (CCW)
	Vector3d Vertex0 = richfactory.GetPhotocatode(i).getVertex(0);
	Vector3d Vertex1 = richfactory.GetPhotocatode(i).getVertex(1);
	Vector3d Vertex2 = richfactory.GetPhotocatode(i).getVertex(2);
	Vector3d Vertex3 = richfactory.GetPhotocatode(i).getVertex(3);
    // define the direction pointing up to the Photocatode (x should be more negative)
	Vector3d upversor = (Vertex2.minus(Vertex0)).normalized();	
    // define the direction pointing right to the Photocatode (y should increase)
	Vector3d rightversor =  (Vertex1.minus(Vertex0)).normalized();
	// define the direction pointing down to the Photocatode (x should be less negative)
	Vector3d downversor = (Vertex0.minus(Vertex2)).normalized();
	// define the direction pointing left (y should decrease)
	Vector3d leftversor = (Vertex0.minus(Vertex1).normalized());
	
	

   //rightversor = rightversor.normalized();
	
	//System.out.println("The versor up is " + upversor);
	//System.out.println("The versor right is " + rightversor);
	//System.out.println("The versor down is " + downversor);
	//System.out.println("The versor left is " + leftversor);
	
	
	// Check the Vertexes position.
	//System.out.println(rightversor);
    Vector3d vettoreRight = new Vector3d(rightversor.times(MAPMTPhotocathode_side));
    Vector3d vettoreUp    = new Vector3d(upversor.times(MAPMTPhotocathode_side));
 //   System.out.println(rightversor);

    Vector3d CheckV3      =  Vertex0.plus(vettoreUp).plus(vettoreRight);
    Vector3d CheckV2      =  Vertex0.plus(vettoreUp);
    Vector3d CheckV1      =  Vertex0.plus(vettoreRight);
    
    // Create the object containing the pixels for each pmt
	// Check the units of measurements! I defined it in cm into Pixel because of GEMC units
    Pixel pixels = new Pixel(); //add the constructor with the vertex of the PMT
    
    // Defining vectors conteining the position of the pixel centers.
    double[] PIXEL_X= new double[65];
    double[] PIXEL_Y= new double[65];
    double[] PIXEL_Z= new double[65];
    
    // Define the position of the vertex of the PMT (this function could go into the PMT position).
   double Vertex3_Xpos=  Vertex3.x;
   double Vertex3_Ypos=  Vertex3.y; 
   // We should use nr 8 here for defying the nr of pixels
   int nrpixel = 8;
 //  System.out.println("\n Before pixel :"+ pixels.GetYPixel(8));
 //  System.out.println("\n Before pixel :"+ Vertex3.plus(downversor.times(pixels.GetYPixel(8))));
  // System.out.println("Before vertex"+ Vertex3.toString());
   int pixelcounter = 1; // Check the nr of the pixel
   Vector3d P3 = new Vector3d(Vertex3); //Copy the position of the vector into another allocation of memory
    P3.add(downversor.times(pixels.GetYPixel(1)/2)); //Define the position of the first pixel
	P3.add(leftversor.times(pixels.GetXPixel(1)/2)); //Define the position of the first pixel 
	// Pixel array start from 0 (so remember it is pixel 1 in array position 0)
	PIXEL_X[0]=P3.x; 
	PIXEL_Y[0]=P3.y;
	PIXEL_Z[0]=P3.z;
	// ** We need to check this loop **
	//I need to loop over the other pixel
	//start from 1 so to the right is always second row

    for (int ii=1; ii <= nrpixel; ii++)
    {   	
    double OriginalX = PIXEL_X[(pixelcounter-1)] ;
	double OriginalY = PIXEL_Y[(pixelcounter-1)] ;
	double OirinalZ  = PIXEL_Z[(pixelcounter-1)]  ;
    	
    	for (int kk=1; kk < nrpixel; kk++)
    	{       			
    		//System.out.println("------ Gonna move down -----");

    		
        	
    		// Moving to the left, check the kk+1 pay attention on that
    		//I need to move half of the pixel where I start and half of the next
    		P3.add(leftversor.times((pixels.GetXPixel(kk)+pixels.GetXPixel(kk+1))/2));
     		// ********
    		PIXEL_X[pixelcounter]=P3.x;
    		PIXEL_Y[pixelcounter]=P3.y;
    		PIXEL_Z[pixelcounter]=P3.z;
    		
    		//System.out.println("\n X:"+ Vertex3.x+ " Y: " + Vertex3.y + " Z: "+ Vertex3.z);
    		pixelcounter ++;
    		//set P3 back to the vertex 3 position
        	//P3.set(Vertex3);
    		//System.out.println(pixelcounter);
    	    
    		}
    	
    	if(ii!= nrpixel)
    	{
    	// Moving down at every ii index
    	P3.set(OriginalX, OriginalY, OirinalZ);
    	P3.add(downversor.times((pixels.GetYPixel(ii)+pixels.GetYPixel(ii+1))/2));
    	PIXEL_X[pixelcounter]=P3.x;
		PIXEL_Y[pixelcounter]=P3.y;
		PIXEL_Z[pixelcounter]=P3.z;
		pixelcounter ++;
    	}
    	//else System.out.println("&&&&&& Attention PMT end");
    }
    
    //Some issues with the calculations, please check again the loop. I would move that into the constructor of the pixels
    for (int q= 0; q< pixelcounter;q++ )
    {
    System.out.println("PMT "+i+" pixel "+(q+1) + " " +PIXEL_X[q] + " "+ PIXEL_Y[q] + " " + PIXEL_Z[q]);
    }
    
	/*
	//Vector3d CheckV1=Vertex0.add(rightversor.times(MAPMTPhotocathode_side));
     System.out.println("*** v1 - calculated is: " + Vertex1.minus(CheckV1));
 	 System.out.println("*** v2 - calculated is: " + Vertex2.minus(CheckV2));
	 System.out.println("*** v3 - calculated is: " + Vertex3.minus(CheckV3));
	
	*/
	//Check by splitting the operation on the vectors.
	
	// Build PMTs Position 
	   
	 
	}
	
	/*
	Vector3d Vertice =richfactory.GetPMT(1).getVertex(0);
	Vector3d Vertice1 =richfactory.GetPMT(1).getVertex(1);
	Vector3d Vertice2 =richfactory.GetPMT(1).getVertex(2);
	Vector3d Vertice3 =richfactory.GetPMT(1).getVertex(3);
	Vector3d Vertice4 =richfactory.GetPMT(1).getVertex(4);
	Vector3d Vertice5 =richfactory.GetPMT(1).getVertex(5);
	Vector3d Vertice6 =richfactory.GetPMT(1).getVertex(6);
	Vector3d Vertice7 =richfactory.GetPMT(1).getVertex(7);
	
	
	
 	System.out.println(Vertice.toString());
 	System.out.println(Vertice1.toString());
 	System.out.println(Vertice2.toString());
 	System.out.println(Vertice3.toString());
 	System.out.println(Vertice4.toString());
 	System.out.println(Vertice5.toString());
 	System.out.println(Vertice6.toString());
 	System.out.println(Vertice7.toString());
 	*/
 	
	
    }		

}

		





