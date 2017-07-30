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
import org.jlab.groot.data.H2F;
import org.jlab.groot.ui.*;

import org.jlab.detector.geant4.v2.*;
import org.jlab.detector.units.SystemOfUnits.Length;
import org.jlab.geom.prim.Point3D;

public class TestPixels {


	public static void main(String[] args) 
	{
		TCanvas canvas = new TCanvas("canvas",800,800);
		H1F h1 = new H1F("h1","Delta X",100,-10,10);
		H1F h2 = new H1F("h2","Delta Y",100,-10,10);
		H1F h3 = new H1F("h1","Delta X",100,-10,10);
		H1F h4 = new H1F("h2","Delta Y",100,-10,10);
		H2F h5   = new H2F("h3","Spot ",100,-4,4,100,-4,4);		
		H2F h6   = new H2F("h6","Spot ",100,-10,10,100,-10,10);	
		
		
		List<Integer> pmtList = new ArrayList<Integer>();
		// Load the RICH geometry
		RICHGeant4Factory richfactory = new RICHGeant4Factory();
		// Load the EVIO file
		EvioSource evioreader = new EvioSource();
		//evioreader.open("/Users/quasar/work/rich/software/simu/detectors/clas12/rich/PixelTest.evio");
	//	evioreader.open("/Users/quasar/work/rich/software/simu/detectors/clas12/rich/Output/TDR_pion_down.evio");
		evioreader.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/MirrorTest.evio");
	
		// Just for the matter of a checker
		int contatore = 0 ;
		while(evioreader.hasEvent())
		{
			DataEvent evioevent = evioreader.getNextEvent();
			 //evioevent.show();
			if(evioevent.hasBank("RICH::dgtz"))
			{
				DataBank  bank = evioevent.getBank("RICH::dgtz");
				
				DataBank  bankT = evioevent.getBank("RICH::true");	
			
			//	bank.show();
				
				int rows = bank.rows();
				
				// added for studying the columns
				for(int i = 0; i < rows; i++)
				{
					System.out.println("------------- ROW -------------");
					
					// It gets PMT and Pixel of the hit in dgtz info
					int pmt= bank.getInt("pmt", i);
					int pixel = bank.getInt("pixel", i);
					System.out.println(pmt+" "+pixel);
					// System.out.println(posx + " " +posy + " " +posz + " ");
					if ( pixel > 0 && pixel< 65)
					{
						contatore ++;
						double posx  = bankT.getDouble("avgX",i);
						double posy  = bankT.getDouble("avgY",i);
						double posz  = bankT.getDouble("avgZ",i);
						
						double avgLX  = bankT.getDouble("avgLx",i);
						double avgLY  = bankT.getDouble("avgLy",i);
						double avgLZ  = bankT.getDouble("avglz",i);
						 System.out.println(" AVG " + avgLX + " " + avgLY+ " "+ avgLZ);
				         System.out.println(" PMT " + pmt + " PIXEL " + pixel);
					//get the 4 vertex of the frontal face of the photocathode (CCW)
					Vector3d Vertex0 = richfactory.GetPhotocatode(pmt).getVertex(0);
					Vector3d Vertex1 = richfactory.GetPhotocatode(pmt).getVertex(1);
					Vector3d Vertex2 = richfactory.GetPhotocatode(pmt).getVertex(2);
					Vector3d Vertex3 = richfactory.GetPhotocatode(pmt).getVertex(3);
				    // define the direction pointing up to the Photocatode (x should be more negative)
					Vector3d upversor = (Vertex2.minus(Vertex0)).normalized();	
				    // define the direction pointing right to the Photocatode (y should increase)
					Vector3d rightversor =  (Vertex1.minus(Vertex0)).normalized();
					// define the direction pointing down to the Photocatode (x should be less negative)
					Vector3d downversor = (Vertex0.minus(Vertex2)).normalized();
					// define the direction pointing left (y should decrease)
					Vector3d leftversor = (Vertex0.minus(Vertex1).normalized());
					//pmtList.add(pmt);		 
					
				    Pixel pixels = new Pixel(Vertex3,downversor,leftversor);
				    // Return the calculated center of the pixel nr pixel
				    
				    Vector3d CenPos = pixels.GetPixelCenter(pixel);
				 //   System.out.println(pixels.GetPixelCenter(pixel).toString());
				 //   System.out.println("{"+(posx)/10 + " " +(posy)/10 + " " +(posz)/10 + "}");
				 //   System.out.println("||"+(posx-avgLX)/10 + " " +(posy-avgLY)/10 + " " +(posz-avgLZ)/10 + "||");
				    
				    double diffX = (CenPos.x*10)-(posx);
				    double diffY = (CenPos.y*10)-(posy);
				    double diffZ = (CenPos.z*10)-(posz); 
				    //double CDiffX = (CenPos.x*10 + avgLX)-(posx);
				    //double CDiffY = (CenPos.y*10 + avgLY)-(posy);
				    
					h1.fill(diffX);
					h2.fill(diffY);
					h3.fill(diffZ);
					//h4.fill(CDiffY);
					h5.fill(diffX, diffY);
					//h6.fill(, y);
				    
					}
				}
				System.out.println("+++ This is a new event ++ ");
				//int[] pmts = pmtList.stream().mapToInt(i->i).toArray();   
			}

		}	
		
		
		h1.setTitleX("DX [mm]");
		h1.setTitleY("Nr of photons measured");
		h1.setLineWidth(2);
		h1.setFillColor(38);
		
		
		h2.setTitleX("DY[mm]");
		h2.setTitleY("Nr of photons measured");
		h2.setLineWidth(2);
		h2.setFillColor(34);
		
		
		h3.setTitleX("Corrected DZ[mm]");
		h3.setTitleY("Nr of photons measured");
		h3.setLineWidth(2);
		h3.setFillColor(11);
		
		h4.setTitleX("Corrected DY[mm]");
		h4.setTitleY("Nr of photons measured");
		h4.setLineWidth(2);
		h4.setFillColor(11);
		
		canvas.divide(2, 2);
		canvas.cd(0);
		canvas.draw(h1);
		canvas.cd(1);
		canvas.draw(h2);
		canvas.cd(2);
		canvas.draw(h3);
		canvas.cd(3);
		canvas.draw(h5);
		System.out.println(contatore);
		
	}

}
