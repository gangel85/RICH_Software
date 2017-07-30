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
import org.jlab.detector.volume.G4Box;


public class Digitalization {


	public static void main(String[] args) 
	{
		RICHGeant4Factory richfactory = new RICHGeant4Factory();

		// Load the EVIO file
		EvioSource evioreader = new EvioSource();
		//evioreader.open("/Users/quasar/work/rich/software/simu/detectors/clas12/rich/PixelTest.evio");
		evioreader.open("/Users/quasar/work/rich/software/simu/detectors/clas12/rich/Output/TDR_pion_down.evio");

		while(evioreader.hasEvent())
		{
			DataEvent evioevent = evioreader.getNextEvent();
			// Check the one that gets digitalized in GEMC
			if(evioevent.hasBank("RICH::dgtz"))
			{

				DataBank  bank = evioevent.getBank("RICH::dgtz");
				DataBank  bankT = evioevent.getBank("RICH::true");	
              // Reading the rows of the file, each of them contains an event
				int rows = bank.rows();

				for(int i = 0; i < rows; i++)
				{
					// It gets PMT and Pixel of the hit in dgtz info
					int pmt= bank.getInt("pmt", i);
					int pixel = bank.getInt("pixel", i);
					if ( pixel > 0 && pixel< 65)
					{
						// remember to divide them by 10 at the end of the day since it is mm not cm
						double posx  = bankT.getDouble("avgX",i);
						double posy  = bankT.getDouble("avgY",i);
						double posz  = bankT.getDouble("avgZ",i);
						
						Vector3d Hitp = new Vector3d(posx/10, posy/10, posz/10); // corrected units
						//	System.out.println(Hitp);
						// Loop all over the PMTS
						for ( int pmt_nr = 1 ; pmt_nr <= 391 ; pmt_nr ++)
						{
							Vector3d Vertex0 = richfactory.GetPhotocatode(pmt_nr).getVertex(0);
							Vector3d Vertex1 = richfactory.GetPhotocatode(pmt_nr).getVertex(1);
							Vector3d Vertex2 = richfactory.GetPhotocatode(pmt_nr).getVertex(2);
							Vector3d Vertex3 = richfactory.GetPhotocatode(pmt_nr).getVertex(3);
							// object defined in ccw way
							Vector3d side1 = Vertex1.minus(Vertex0);
							Vector3d side2 = Vertex3.minus(Vertex1);
							Vector3d side3 = Vertex2.minus(Vertex3);
							Vector3d side4 = Vertex0.minus(Vertex2);
							// Distance from the point and the first vertex of the side (CCW)
							Vector3d dis1 = Vertex0.minus(Hitp);
							Vector3d dis2 = Vertex1.minus(Hitp);
							Vector3d dis3 = Vertex3.minus(Hitp);
							Vector3d dis4 = Vertex2.minus(Hitp);

							double PMTLenght = side1.magnitude();

							double distance1 = ((side1.cross(dis1)).magnitude())/PMTLenght;
							double distance2 = ((side2.cross(dis2)).magnitude())/PMTLenght;
							double distance3 = ((side3.cross(dis3)).magnitude())/PMTLenght;
							double distance4 = ((side4.cross(dis4)).magnitude())/PMTLenght;
							//distance1 = distance1/PMTLenght;

							//System.out.println(pmt_nr + " " + distance1);
							if(distance1 < PMTLenght && distance2< PMTLenght && distance3< PMTLenght && distance4 < PMTLenght) 
							{

								//	System.out.println("=> PMT nr "+ pmt_nr);
								// define the direction pointing down to the Photocatode (x should be less negative)
								Vector3d downversor = (Vertex0.minus(Vertex2)).normalized();
								// define the direction pointing left (y should decrease)
								Vector3d leftversor = (Vertex0.minus(Vertex1).normalized());
								Vector3d pixeldistance = Hitp.minus(Vertex2);
								Pixel pixels = new Pixel(Vertex3,downversor,leftversor);
								for (int q=1; q<= 64; q ++)
								{
									G4Box pixelbox =  pixels.GetPixelBox(q);
									Vector3d VertexPX0 = pixelbox.getVertex(0);
									Vector3d VertexPX1 = pixelbox.getVertex(1);
									Vector3d VertexPX2 = pixelbox.getVertex(2);
									Vector3d VertexPX3 = pixelbox.getVertex(3);
									// object defined in ccw way
									Vector3d sidePX1 = VertexPX1.minus(VertexPX0);
									Vector3d sidePX2 = VertexPX3.minus(VertexPX1);
									Vector3d sidePX3 = VertexPX2.minus(VertexPX3);
									Vector3d sidePX4 = VertexPX0.minus(VertexPX2);
									// Distance from the point and the first vertex of the side (CCW)
									Vector3d disPX1 = VertexPX0.minus(Hitp);
									Vector3d disPX2 = VertexPX1.minus(Hitp);
									Vector3d disPX3 = VertexPX3.minus(Hitp);
									Vector3d disPX4 = VertexPX2.minus(Hitp);

									double PixelLenght = sidePX1.magnitude();
									double PixelXLenght = sidePX2.magnitude();


									//  System.out.println(" ---> The Lenght of the Pixel nr " + q + " is "+  PixelLenght);
									//  System.out.println(" ---> The XLenght of the Pixel nr " + q + " is "+  PixelXLenght);



									double distancePX1 = ((sidePX1.cross(disPX1)).magnitude())/PixelLenght;
									double distancePX2 = ((sidePX2.cross(disPX2)).magnitude())/PixelLenght;
									double distancePX3 = ((sidePX3.cross(disPX3)).magnitude())/PixelLenght;
									double distancePX4 = ((sidePX4.cross(disPX4)).magnitude())/PixelLenght;
									// Check this cycle
									if(distancePX1 < PixelLenght && distancePX2< PixelLenght && distancePX3< PixelLenght && distancePX4< PixelLenght) 
									{  
										if (pmt_nr != pmt)
										{
											System.out.println(" ==============================" );
											System.out.println(" ====     HIT FOUND     ======" );
											System.out.println("      PMT "+ pmt_nr +" Pixel is " + q);
											System.out.println(" ++ EXPECTED+PMT "+ pmt +" Pixel is " + pixel);
											System.out.println(" ==============================" );
										}
									}


								}

							}
						}
					}
				}
			}
		}
		System.out.println("Done");





	}




}
