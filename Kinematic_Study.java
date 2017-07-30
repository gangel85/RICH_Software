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


public class Kinematic_Study {


	public static void main(String[] args) 
	{

		H2F h2P        =  new H2F("h2P","sPi Minu ",10,0,10,10,0,40);
		    
	    
		// Load the EVIO file
				EvioSource evioreader = new EvioSource();
				EvioSource evioreader2 = new EvioSource();

				evioreader.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/Lund294.evio");
				evioreader2.open("/Users/quasar/work/rich/software/opticalTestRICH/rich/Lund320.evio");
				
				while(evioreader.hasEvent())
				{
				
					
						DataEvent evioevent = evioreader.getNextEvent();
						// Check the one that gets digitalized
						
					//	evioevent.show();
						
						if(evioevent.hasBank("GenPart::true"))
						{
						//	System.out.println("Find the generated particles");
							DataBank  bankMC    = evioevent.getBank("GenPart::true");
							
						  //bankMC.show();
							int rows            = bankMC.rows();
							
							for (int i = 0; i < rows; i ++)
	                        {
								// Reading the bank information from the simulation
	                        	 int pid    = bankMC.getInt("pid", i);
	                        	 double px  = bankMC.getDouble("px", i);
	                        	 double py  = bankMC.getDouble("py", i);
	                        	 double pz  = bankMC.getDouble("pz", i);
	                        	 //System.out.println("==> " + px+" "+ py+" "+pz);
	                        	 if(pid == -211)
	                        	 {
	                        		 double TMomentum = Math.sqrt(px*px+py*py+pz*pz)/1000;
	                        		 Vector3d momentum = new Vector3d(px,py,pz);
	                        		 
	                        		 double Theta = momentum.theta();
	                        		 double Phi = momentum.phi();
	                        		 double PhiDgr = (Phi*180)/3.1416;
	                        		 double ThetaDgr = (Theta*180)/3.1416;                
	                        		//System.out.println(TMomentum+" Angles "+ThetaDgr + " " + PhiDgr);
	                        		 
	                        	//	 System.out.println("PI - find with momentum: "+px+" "+ py+" "+pz);
	                        		 h2P.fill(TMomentum, ThetaDgr);
	                        	 } 
	                        	 
	                        	 }
	                        	 
	                        	 
	                        }
						}
				while(evioreader2.hasEvent())
				{
				
					
						DataEvent evioevent2 = evioreader2.getNextEvent();
						// Check the one that gets digitalized
						
					//	evioevent.show();
						
						if(evioevent2.hasBank("GenPart::true"))
						{
						//	System.out.println("Find the generated particles");
							DataBank  bankMC    = evioevent2.getBank("GenPart::true");
							
						  //bankMC.show();
							int rows            = bankMC.rows();
							
							for (int i = 0; i < rows; i ++)
	                        {
								// Reading the bank information from the simulation
	                        	 int pid    = bankMC.getInt("pid", i);
	                        	 double px  = bankMC.getDouble("px", i);
	                        	 double py  = bankMC.getDouble("py", i);
	                        	 double pz  = bankMC.getDouble("pz", i);
	                        	 //System.out.println("==> " + px+" "+ py+" "+pz);
	                        	 if(pid == -211)
	                        	 {
	                        		 double TMomentum = Math.sqrt(px*px+py*py+pz*pz)/1000;
	                        		 Vector3d momentum = new Vector3d(px,py,pz);
	                        		 
	                        		 double Theta = momentum.theta();
	                        		 double Phi = momentum.phi();
	                        		 double PhiDgr = (Phi*180)/3.1416;
	                        		 double ThetaDgr = (Theta*180)/3.1416;                
	                        		//System.out.println(TMomentum+" Angles "+ThetaDgr + " " + PhiDgr);
	                        		 
	                        	//	 System.out.println("PI - find with momentum: "+px+" "+ py+" "+pz);
	                        		 h2P.fill(TMomentum, ThetaDgr);
	                        	 } 
	                        	 
	                        	 }
	                        	 
	                        	 
	                        }
						}
				
				TCanvas canvas = new TCanvas("canvas",800,800);
				canvas.divide(1,2);
				canvas.cd(0);
				canvas.draw(h2P);
				
				}
	}
	
