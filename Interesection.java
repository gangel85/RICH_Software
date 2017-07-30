package Reading;
import java.util.*;

import org.jlab.detector.geant4.v2.RICHGeant4Factory;
import org.jlab.detector.units.SystemOfUnits.Length;
import org.jlab.detector.volume.G4Stl;
import org.jlab.geom.geant.Geant4Basic;
import org.jlab.geometry.prim.Line3d;

import eu.mihosoft.vrl.v3d.Polygon;
import eu.mihosoft.vrl.v3d.Vector3d;
import eu.mihosoft.vrl.v3d.Vertex;

import java.util.ArrayList;
import java.util.List;


public class Interesection {



	public static void main(String[] args) 
	{
		RICHGeant4Factory richfactory = new RICHGeant4Factory();
		//OpticalGasVolume - 1 AerogelTiles,2 Aluminum,3 CFRP,4 Glass,5 TedlarWrapping
		/*
		for( org.jlab.detector.volume.Geant4Basic comp: richfactory.getComponents()){
		    if (comp.getName().contains("Stl")) {System.out.println(comp.getName()); }
			//System.out.println(comp.getClass());
		   // System.out.println(comp.getName());
		}
		*/
		
		
		System.out.println("Belin");
		System.out.println(richfactory.GetStl(2).toCSG().getBounds().getCenter().toString());
	    Line3d line = new Line3d(new Vector3d(0,0,0), new Vector3d(-3.716517639160145, -1.1847402954101511, 58.78809570312456));
	    System.out.println("Calculating intersection");
		System.out.println(richfactory.GetStl(3).toCSG().getIntersections(line).size());
		int counterpol = 0;
		
		for ( Polygon comp:	richfactory.GetStl(3).toCSG().getPolygons() )
		{
			if (counterpol ==1 )
			{
			for (Vertex vert: comp.vertices )
			{
			System.out.println(vert.toString());
			}
			}
			counterpol ++;
		
		}
			
		
  //      System.out.println(	richfactory.GetStl(2).toCSG().getPolygons().);
		
        
      // richfactory.getComponents().forEach(if(it.getClass().getName().contains("Stl"));
       
	//	 ClassLoader cloader = getClass().getClassLoader();
	       // G4Stl vol = new G4Stl("OpticalGasVolume", cloader.getResourceAsStream("/Users/quasar/work/rich/software/opticalTestRICH?rich/javacad/Glass.stl"), Length.mm / Length.cm);
       
	     //   G4Stl vol = new G4Stl("mirror","/Users/quasar/work/rich/software/opticalTestRICH?rich/javacad/Glass.stl",0.1);
        
      
        //System.out.println(vol.toCSG().getIntersections(line));
        
        

}
	
	
}