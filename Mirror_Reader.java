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


public class Mirror_Reader {


	public static void main(String[] args) 
	{
		RICHGeant4Factory richfactory = new RICHGeant4Factory();
		// Graphs creation

		H2F h1S        =  new H2F("h1S","Spherical Mirror ",150,-3000,-1000,150,1500,-1600);
		H2F h1Bottom   =  new H2F("h1B","Back Mirror ",150,-200,200,150,5000,7500);
		H2F h1Back1     =  new H2F("h1Back1","Back Mirror 1 ",150,-2000,0,150,1000,-1000);
		H2F h1Back2     =  new H2F("h1Back2","Back Mirror 2",150,-2000,0,150,1000,-1000);
		H2F h1LL       =  new H2F("h1LL","Left Mirror ",150,4500,7000,150,-1400,0);
		H2F h1LR       =  new H2F("h1LR","Right Mirror ",150,4500,7000,150,0,1400);
		H1F h2         =  new H1F("h2","Mirror ",7,0,7);

		// Counters for photons
		int TotPhotons       = 0;
		int contamirrorleft  = 0;                 
		int contamirrorright = 0;


		// Load two EVIO files
		EvioSource evioreader = new EvioSource();
		EvioSource evioreader2 = new EvioSource();
		evioreader.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/Lund294.evio");
		evioreader2.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/Lund320.evio");

		//evioreader.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/NewMirror10.evio");
		//evioreader.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/MirrorTest.evio");
		//evioreader.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/MirrorTest10k.evio");


		//Reading the file until there are recorded events
		while(evioreader.hasEvent())
		{
			DataEvent evioevent = evioreader.getNextEvent();
			// Check that the digitalized bank is present for the RICH (= detected photons)
			if(evioevent.hasBank("RICH::dgtz"))
			{	
				//Check if the mirrors have hits 
				if(evioevent.hasBank("FLUX::dgtz"))
				{
					//	evioevent.show();
					// Mirror Banks :
					DataBank  bankD     = evioevent.getBank("FLUX::dgtz");	//Mirror Dgtz info (i.e. the number of the mirror)
					DataBank  bankT     = evioevent.getBank("FLUX::true"); // Mirror true info (i.e. the vector of the photon)
					// RICH Banks: 
					DataBank  bankRich  = evioevent.getBank("RICH::dgtz"); // RICH dgtz
					DataBank  bankRichT = evioevent.getBank("RICH::true"); // RICH true

					int rows            = bankRich.rows();  // Obtaining the number of rows in the event RICH
					if (rows == 0) System.out.println("******* -----> ERROR: No Rows!");
					int rowsFlux        = bankD.rows(); // Obtaining the rows in the event mirror
					//	System.out.println("THe rows are (F-R)" + rows + " = " +rowsFlux); 
					int colR            = bankRich.columns(); //Not used in this version of the code 
					int colF            = bankT.columns();    //Not used in this version of the code 

					System.out.println("RowsFlux " + rowsFlux);

					// Mirrros Information and Photon Hits positions
					int[] HitMirror      = new    int[rowsFlux];
					int[] FluxID         = new    int[rowsFlux]; 
					double[] XMirrorHit  = new double[rowsFlux];
					double[] YMirrorHit  = new double[rowsFlux];
					double[] ZMirrorHit  = new double[rowsFlux];

					int contatoremirror=0; // set the hit to mirrors to 0

					//Loop over the entries of the evio (rows) to read the variable of each event 
					for (int i = 0; i < rows; i ++)
					{
						int pixel    = bankRich.getInt("pixel", i); // From RICH dgtz
						// tid is the identifier of the photon that went also in the mirror
						int ReadID   = bankRichT.getInt("tid", i); // From RICH true
						double posx  = bankRichT.getDouble("avgX",i); // From RICH true
						double posy  = bankRichT.getDouble("avgY",i); // From RICH true
						//	 System.out.println("==> Positions: " + posx + " " + posy);
						//	 System.out.println("==> Pixel: " + pixel + " ID " + ReadID);

						// If the pixel gets measured in the RICH (pixel id betwenn 1 and 64) 
						if ( pixel > 0 && pixel < 65)
						{ 
							TotPhotons ++; // increase the number of detected photon
							System.out.println( " -- A photon has been Recorder in the RICH --- ");

							// If a photon is detected, loop over all the photons that hit mirros for that events until you find the photon with the same id            
							for (int _i=0; _i < rowsFlux; _i++)
							{
								// get the information from the Mirror hits
								HitMirror[_i]    =  bankD.getInt("id", _i);
								FluxID [_i]      =  bankT.getInt("tid",_i);
								XMirrorHit[_i]   =  bankT.getDouble("avgX", _i);
								YMirrorHit[_i]   =  bankT.getDouble("avgY", _i);
								ZMirrorHit[_i]   =  bankT.getDouble("avgZ", _i);

								// if a photons as the same ID start to fill the histograms 
								if (ReadID == FluxID[_i])
								{ //  System.out.println(" THe Read ID is : " +ReadID + " The FLUX ID is "+ FluxID [_i] + " Mirror is " + HitMirror[_i]);
									// Spherical mirrors have ID from 1 to 10 
									if (HitMirror[_i]<11)
									{
										// System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1S.fill( XMirrorHit[_i], YMirrorHit[_i]);
									}
									// Bottom mirror has ID 20 
									if (HitMirror[_i]>11 && HitMirror[_i]<24)
									{
										//  System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1Bottom.fill( YMirrorHit[_i], ZMirrorHit[_i]); 
									}
									// Mirror supporting Aerogel (top) has id 31
									if (HitMirror[_i]==31)
									{
										//System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1Back1.fill( XMirrorHit[_i], YMirrorHit[_i]);
									}
									//Mirror supporting Aerogel (bottom) had id 32
									if (HitMirror[_i]==32)
									{
										//System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1Back2.fill( XMirrorHit[_i], YMirrorHit[_i]);
									}
									// Mirror lateral left have id of 81 and 82
									if (HitMirror[_i]>80 && HitMirror[_i]<84)
									{
										//System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1LL.fill( ZMirrorHit[_i], YMirrorHit[_i]);
										contamirrorleft ++;
									}
									// Lateral Right mirrors have ID 91 and 92
									if (HitMirror[_i]>90 && HitMirror[_i]<94)
									{
										//  System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1LR.fill( ZMirrorHit[_i], YMirrorHit[_i]);
										contamirrorright++;
									}

									contatoremirror ++; //increse the counter that takes into account the hits on mirrors
								}

							}

							h2.fill(contatoremirror);
							contatoremirror=0;
						}
					}

				}
			}
			// Check that a new event it is read 
			System.out.println("++++  This is a new event +++++++");

		}
		// -----------------------------------------------------------	
		// Repeat the reading of the second evio event
		// I was lazy and didnt put it into a function to call 
		// -----------------------------------------------------------	
		// comment of the code are the same as before

		while(evioreader2.hasEvent())
		{
			DataEvent evioevent2 = evioreader2.getNextEvent();
			if(evioevent2.hasBank("RICH::dgtz"))
			{	
				if(evioevent2.hasBank("FLUX::dgtz"))
				{

					// Mirror Banks
					DataBank  bankD     = evioevent2.getBank("FLUX::dgtz");	
					DataBank  bankT     = evioevent2.getBank("FLUX::true");
					// RICH Banks 
					DataBank  bankRich  = evioevent2.getBank("RICH::dgtz");
					DataBank  bankRichT = evioevent2.getBank("RICH::true");

					int rows            = bankRich.rows();
					int rowsFlux        = bankD.rows();
					//	System.out.println("THe rows are (F-R)" + rows + " = " +rowsFlux);
					int colR            = bankRich.columns();
					int colF            = bankT.columns();

					// System.out.println("RowsFlux " + rowsFlux);

					// Mirrros Information
					int[] HitMirror      = new    int[rowsFlux];
					int[] FluxID         = new    int[rowsFlux]; 
					double[] XMirrorHit  = new double[rowsFlux];
					double[] YMirrorHit  = new double[rowsFlux];
					double[] ZMirrorHit  = new double[rowsFlux];

					int contatoremirror=0;

					for (int i = 0; i < rows; i ++)
					{
						int pixel    = bankRich.getInt("pixel", i);
						int ReadID   = bankRichT.getInt("tid", i);
						double posx  = bankRichT.getDouble("avgX",i);
						double posy  = bankRichT.getDouble("avgY",i);

						if ( pixel > 0 && pixel < 65)
						{ 
							TotPhotons ++;
							//   System.out.println( " -- A photon has been Recorder in the RICH --- ");

							for (int _i=0; _i < rowsFlux; _i++)
							{

								HitMirror[_i]    =  bankD.getInt("id", _i);
								FluxID [_i]      =  bankT.getInt("tid",_i);
								XMirrorHit[_i]   =  bankT.getDouble("avgX", _i);
								YMirrorHit[_i]   =  bankT.getDouble("avgY", _i);
								ZMirrorHit[_i]   =  bankT.getDouble("avgZ", _i);


								if (ReadID == FluxID[_i])
								{ //  System.out.println(" THe Read ID is : " +ReadID + " The FLUX ID is "+ FluxID [_i] + " Mirror is " + HitMirror[_i]);
									if (HitMirror[_i]<11)
									{
										// System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1S.fill( XMirrorHit[_i], YMirrorHit[_i]);
									}

									if (HitMirror[_i]>11 && HitMirror[_i]<24)
									{
										//  System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1Bottom.fill( YMirrorHit[_i], ZMirrorHit[_i]);

									}
									if (HitMirror[_i]==31)
									{
										//System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1Back1.fill( XMirrorHit[_i], YMirrorHit[_i]);
									}

									if (HitMirror[_i]==32)
									{
										//System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1Back2.fill( XMirrorHit[_i], YMirrorHit[_i]);
									}

									if (HitMirror[_i]>80 && HitMirror[_i]<84)
									{
										//System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1LL.fill( ZMirrorHit[_i], YMirrorHit[_i]);
										contamirrorleft ++;
									}
									if (HitMirror[_i]>90 && HitMirror[_i]<94)
									{
										//  System.out.println(" THe Photon Position is : " +XMirrorHit[_i] + " - "+ YMirrorHit[_i]  + " - " +  ZMirrorHit[_i]);
										h1LR.fill( ZMirrorHit[_i], YMirrorHit[_i]);
										contamirrorright++;
									}

									contatoremirror ++; 
									// System.out.println( "+++++++ >> The Photon ID: "+ FluxID[_i] + " has hitten the Mirror nr. "+ HitMirror[_i]);
								}

							}

							//  System.out.println(contatoremirror);
							h2.fill(contatoremirror);
							contatoremirror=0;
						}
					}

				}
			}
			System.out.println("++++  This is a new event +++++++");

		}


		// -----------------------------------------------------------

		// Set the Histograms properties


		h1S.setTitleX("X Position [mm]");
		h1S.setTitleY("Y Position [mm]");	
		h1Bottom.setTitleX("X Position [mm]");
		h1Bottom.setTitleY("Z Position [mm]");
		h1Back1.setTitleX("Y Position [mm]");
		h1Back1.setTitleY("Z Position [mm]");
		h1Back2.setTitleX("Y Position [mm]");
		h1Back2.setTitleY("Z Position [mm]");
		h1LL.setTitleX("Z Position [mm]");
		h1LL.setTitleY("Y Position [mm]");
		h1LR.setTitleX("Z Position [mm]");
		h1LR.setTitleY("Y Position [mm]");
		//Canvas drawing
		System.out.println("Total Photons = " + TotPhotons);
		System.out.println("Photons on mirror left = "+contamirrorleft);
		System.out.println("Photons on mirror left = "+contamirrorright);
		TCanvas canvas = new TCanvas("canvas",800,800);

		canvas.divide(2,3);
		canvas.cd(0);
		canvas.draw(h1S);
		canvas.cd(1);
		canvas.draw(h1Bottom);
		canvas.cd(2);
		canvas.draw(h1LL);
		canvas.cd(3);
		canvas.draw(h1LR);
		canvas.cd(4);
		canvas.draw(h1Back1);
		canvas.cd(5);
		canvas.draw(h1Back2);



	}

}