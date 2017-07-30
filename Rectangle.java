package Reading;
import java.awt.Point;
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

public  class Rectangle {
    java.awt.Rectangle _r;

    public Rectangle(int x, int y, int w, int h) {
        this._r = new java.awt.Rectangle(x,y,w,h);
    }
    public boolean contains(Point p) {
        return this._r.contains(p);
    }
}
