package src.graph.tracesismique;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;


public class TraceSismique implements ObjetGraphique,Courbe
{
    private GeneralPath path;
    private GeneralPath pathPlus,pathMoins;
    private Color couleurPlus, couleurMoins, couleurWiggle;
    private String legende = "";
    private boolean haspoint = false;
    private boolean closed = false;
    private float centreY = 0f;
    private float xmem = 0f,ymem=0f;
    private boolean positif = true;
    public boolean selected;
    
    private int colorSet = 1;
    private Color [] colorPos = { Color.black,Color.black, Color.gray,Color.darkGray,Color.black, Color.black, Color.blue,Color.red,
    							new Color(64,128,128), Color.green, new Color(0,128,0)};
    private Color [] colorNeg = {new Color(255,128,64,0), Color.gray, Color.black,Color.lightGray,Color.gray,   Color.white, Color.red,Color.blue,
    							new Color(128,128,255), Color.yellow, new Color(255,128,64) };
    private Color [] colorWig = { Color.blue,Color.blue, Color.black,  Color.black, Color.black,  Color.black, Color.black, Color.black,
    							Color.blue, Color.black , Color.black};

    public TraceSismique(int _colorSet) {
        path = new GeneralPath();
        pathPlus = new GeneralPath();
        pathMoins = new GeneralPath();
        this.setColorSet(_colorSet);        
        selected = false;
        reset();
    }
    public TraceSismique() {
        this.setColorSet(0);
        path = new GeneralPath();
        pathPlus = new GeneralPath();
        pathMoins = new GeneralPath();       
        selected = false;
        reset();
    }
    public void setColorSet(int _colorSet) {
    	colorSet = _colorSet;
    	while (_colorSet > colorPos.length-1) {
    		_colorSet -= colorPos.length;}
    	while (_colorSet < 0) {
    		_colorSet += colorPos.length;}
    	colorSet = _colorSet;
    	couleurWiggle = colorWig[colorSet];
    	couleurPlus = colorPos[colorSet];
    	couleurMoins = colorNeg[colorSet];
    }
    public int getColorSet() { return colorSet ; }
    
    public void setCouleur(Color _couleurPlus) {
        couleurPlus = _couleurPlus;
    }
    public void setOrigine(double _centreY) {
        centreY = (float) _centreY;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean _selected) {
        selected = _selected;
    }
    public void setLegende(String str) {
        legende = str;
    }
    public String getLegende(){
        return legende;
    }
    
    public Color getCouleur() {
        return couleurPlus;
    }
    
    public void reset() {
        path.reset();
        pathPlus.reset();
        pathMoins.reset();
        xmem = 0f;
        ymem = 0f;
        closed = false;
        haspoint = false;
    }
    
    public void close() {
        closed = true;
    }
    
    public boolean isClosed() {return closed;}
    
    public void setPointille(boolean _pointille) {};
    
    public void addPoint(double x,double y) {
        float xf = (float) x;
        float yf = (float) y;
        
        float x0 = 0f,dx,dy;
        boolean change;
        
        if (!closed) {
            dx = xf-xmem;
            dy = yf-ymem;
            change = (((positif) && (yf <= centreY)) || ((!positif) && (yf > centreY)));
            if  (change) {
                if (dy != 0f) 
                    x0 = xmem + dx*(centreY-ymem)/dy;
                else change = false;
            }
            
            if (!haspoint) {
                path.moveTo(xf,yf);
                if (change) {
                    pathPlus.moveTo(x0,centreY);
                    pathMoins.moveTo(x0,centreY);
                }
                
                pathPlus.moveTo(xf,Math.max(yf,centreY));
                pathMoins.moveTo(xf,Math.min(yf,centreY));
                haspoint = true;
            }
            else {
                path.lineTo(xf,yf);
                if (change) {
                    pathPlus.lineTo(x0,centreY);
                    pathMoins.lineTo(x0,centreY);
                }
                pathPlus.lineTo(xf,Math.max(yf,centreY));
                pathMoins.lineTo(xf,Math.min(yf,centreY));
            }
            positif = (yf > centreY);
            xmem = xf;
            ymem = yf;
        }
    }
    
    public boolean isEmpty() {return !haspoint;}
    
    
    public void dessiner(Graphics2D g , AffineTransform transf) {
        Color col = g.getColor();
        try {
        	g.setPaint(couleurMoins);
            g.fill(transf.createTransformedShape(pathMoins));
        	g.setPaint(couleurPlus);
            g.fill(transf.createTransformedShape(pathPlus));
            g.setPaint(couleurWiggle);
            if (this.selected) g.setPaint(Color.red);
            g.draw(transf.createTransformedShape(path));
            
        }
        catch (Exception e) {}
        g.setPaint(col);
    }
    public void dessinerArea(Graphics2D g , AffineTransform transf) {
        Color col = g.getColor();
        try {
        	g.setPaint(couleurMoins);
            g.fill(transf.createTransformedShape(pathMoins));
        	g.setPaint(couleurPlus);
            g.fill(transf.createTransformedShape(pathPlus));
            //g.setPaint(Color.blue);
            //if (this.selected) g.setPaint(Color.red);
            ////g.setPaint(this.couleurWiggle);
            //g.draw(transf.createTransformedShape(path));
            
        }
        catch (Exception e) {}
        g.setPaint(col);
    }
    public void dessinerWiggle(Graphics2D g , AffineTransform transf) {
        Color col = g.getColor();
        try {

            g.setPaint(couleurWiggle);
            if (this.selected) g.setPaint(Color.red);
            g.draw(transf.createTransformedShape(path));
            
        }
        catch (Exception e) {}
        g.setPaint(col);
    }
}
