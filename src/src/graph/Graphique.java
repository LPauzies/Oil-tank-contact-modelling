package src.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

public class Graphique extends JPanel {

    /* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	public static final int LABEL_NORMAL = 0;
    public static final int LABEL_AUTRE = 1;

    private Color couleurFond = this.getBackground();
    private final Font fontLegende = new Font("SansSerif",Font.PLAIN,10);
    private final Font fontLabel = new Font("SansSerif",Font.BOLD,12);
    private final Font fontLabelVert = fontLabel.deriveFont(AffineTransform.getRotateInstance(-Math.PI/2d));
    private final Stroke traitEpais = new BasicStroke(1.5f);
    private final Stroke traitFin = new BasicStroke(0.3f);

    private boolean firstTime = true;
    private boolean echelleModif = true;

    private Graphics2D gFond;
    private BufferedImage fond;

    /** marge = {left,up,right,down} **/
    private int [] marge = {50,20,70,20};
    private int positionLabel = LABEL_NORMAL;
    private String Xlabel = "X";
    private String Ylabel = "Y";
  
    private double Xmin = -1E0;
    private double Xmax = 1E0;
    private double Ymin = -1E0;   
    private double Ymax = 1E0;
    private double pasEchelleX = 0.1d;
    private double pasEchelleY = 0.1d;
    private int precisionX = 3;
    private int precisionY = 3;
    //private BoutonCouleur bout= new BoutonCouleur();

    private AffineTransform transf = new AffineTransform();
    private Vector<Shape> courbes = new Vector<Shape>(3);
    private Vector<Color> couleurs = new Vector<Color>(3);
    GraphContact gcTest= new GraphContact();

    public Graphique() {
    	super();
    }

    public void setMarge(int left,int up,int right,int down) {
		echelleModif = true;
		marge[0] = left;
		marge[1] = up;
		marge[2] = right;
		marge[3] = down;
    }

    public void setPasEchelle(double pasx , double pasy) {
		echelleModif = true;
		pasEchelleX = pasx;
		pasEchelleY = pasy;
    }
   
    public void setPrecision(int px, int py) {
		echelleModif = true;
		precisionX = px;
		precisionY = py;
    }

    public double getPasEchelleX() {
    	return pasEchelleX;
    }

    public double getPasEchelleY() {
    	return pasEchelleY;
    }

    public void setX(double _Xmin,double _Xmax) {
		echelleModif = true;
		Xmin = _Xmin;
		Xmax = _Xmax;
	}
    
    public void setY(double _Ymin,double _Ymax) {
		echelleModif = true;
		Ymin = _Ymin;
		Ymax = _Ymax;
    }
    
    
    public double getXmax() {
    	return Xmax;
    }

    public double getYmax() {
    	return Ymax;
    }

    public void setXlabel(String label){
		echelleModif = true;
		Xlabel = label;
    }
    
    public void setYlabel(String label){
		echelleModif = true;
		Ylabel = label;
    }

    public void setPositionLabel(int pos) {
		echelleModif = true;
		positionLabel = pos;
    }
    
    public void addCourbe(Shape sh , Color col) {
    	courbes.add(sh);
    	couleurs.add(col);
    }

    public void removeCourbe(int i) {
		courbes.remove(i);
		couleurs.remove(i);
    }

    public void clear() {
		courbes.clear();
		couleurs.clear();
    }

    public void setPreferredSize(Dimension dim) {
		super.setPreferredSize(dim);
		firstTime = true;
    }
    
    public void setMaximumSize(Dimension dim) {
    	super.setMaximumSize(dim);
    	firstTime = true;
    }
    
    public void setMinimumSize(Dimension dim) {
		super.setMinimumSize(dim);
		firstTime = true;
    }
    public void setSize(Dimension dim) {
    	super.setSize(dim);
		firstTime = true;
    }

    private double arrondi(double val,int n) {
		double eps = Math.pow(10,n);
		return (Math.rint (val*eps))/eps;
    }
    

     
    private Point pointTransf(double x , double y) {
	Point2D.Double p = (Point2D.Double)transf.transform( new Point2D.Double(x,y),null);
	return new Point((int)p.getX(),(int) p.getY());
    }

    public void paintComponent(Graphics g) {
	
		Dimension dim = getSize();
		double wd = dim.getWidth();
	    double hd = dim.getHeight();
	    
		if (firstTime) {
	 	    fond = (BufferedImage)createImage(dim.width,dim.height);
	  	    gFond = fond.createGraphics();
	 	    gFond.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    firstTime = false;
		}
		 
		double factx = (wd-(double)(marge[0]+marge[2]))/(Xmax-Xmin) ;
	    double facty = (hd-(double)(marge[1]+marge[3]))/(Ymax-Ymin);
		
	    if (echelleModif) {
			transf.setTransform(0d,facty,factx,0d,(double)(marge[0])-factx*Xmin ,(double)(marge[1])-facty*Ymin);
			
		    /** Rectangle **/
			int w=dim.width-marge[0]-marge[2];
			int h=dim.height-marge[1]-marge[3];
		    gFond.setPaint(couleurFond);
		    gFond.fillRect(0,0,dim.width,dim.height);
		    gFond.setPaint(Color.white);
		    gFond.setStroke(traitEpais);
		    gFond.fillRect(marge[0],marge[1],w,h);
		    gFond.setPaint(Color.black);
		    gFond.drawRect(marge[0],marge[1],w,h);
		    
		    /** Axes **/
		    Point p = pointTransf(0d,0d);
		    
		    /** Labels **/
		    if (positionLabel == LABEL_AUTRE) {
				gFond.setFont(fontLabelVert);
				gFond.drawString(Ylabel,marge[0]-30,dim.height/2+3*Ylabel.length());
				gFond.setFont(fontLabel);
				gFond.drawString(Xlabel,dim.width/2-3*Xlabel.length(),marge[1]+dim.height+20);
		    } else {
				gFond.setFont(fontLabel);
				gFond.drawString(Xlabel,marge[0]+dim.width+5,marge[1]+dim.height);
				gFond.drawString(Ylabel,marge[0],marge[1]-5);
		    }
	
		    /** Legende **/
		    gFond.setStroke(traitFin);
		    gFond.setFont(fontLegende);
		    
		    /** X **/
		    String str = "";
		    double depart = Math.ceil(Xmin/pasEchelleX)*pasEchelleX;
		    
		    for (double j = depart ; j <= Xmax ; j+= pasEchelleX) {
		 		p =  pointTransf(0d,j);
				gFond.drawLine(p.x,marge[1],p.x,dim.height-marge[3]);
				str = (precisionX > 0) ? ""+arrondi(j,precisionX) : ""+(int)j;
				gFond.drawString(str,p.x-3*str.length(),marge[1]+dim.height-marge[3]-10);
	 	    }
		    
		    /** Y **/
		    depart = Math.ceil(Math.min(Ymin, Ymax)/pasEchelleY)*pasEchelleY;
		    for (double j = depart ; j <= Math.max(Ymin, Ymax) ; j+= pasEchelleY) {
		 		p =  pointTransf(j,0d);
				gFond.drawLine(marge[0],p.y,marge[0]+w,p.y);
				str = (precisionY > 0) ? ""+arrondi(j,precisionY) : ""+(int)j;
				gFond.drawString(str,marge[0]-6*str.length(),p.y+3);
	 	    }
		    
		    echelleModif = false;
		}
	    
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(traitEpais);
		g2.drawImage(fond,0,0,this);
		paintCourbes(g2);
    }

    
    private void paintCourbes(Graphics2D g2) {
		int lg = courbes.size();
		for (int i = 0 ; i < lg ; i++) {
		    g2.setPaint((Color) couleurs.elementAt(i));
		    g2.draw(transf.createTransformedShape((Shape) courbes.elementAt(i) ));
		}
    }
    

}