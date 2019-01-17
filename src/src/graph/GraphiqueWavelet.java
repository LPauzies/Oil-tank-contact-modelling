package src.graph;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import src.graph.tracesismique.ObjetGraphique;

/** @author : Hugues Narjoux
 *  @version : 3.0
 * **/

public class GraphiqueWavelet extends JPanel implements Printable {
	
    /* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	public static final int TOP = 2;
    public static final int BOTTOM = 1;
    public static final int LEFT = -1;
    public static final int RIGHT = -2;
    
    private int positionLegendeX = BOTTOM;
    private int positionLegendeY = LEFT;
    
    private final Font fontLegende = new Font("SansSerif",Font.PLAIN,10);
    private Font fontLabel = new Font("SansSerif",Font.BOLD,12);
    private Font fontLabelVert1 = fontLabel.deriveFont(AffineTransform.getRotateInstance(-Math.PI/2d));
    private Font fontLabelVert2 = fontLabel.deriveFont(AffineTransform.getRotateInstance(Math.PI/2d));
    
    private Color couleurFond;
    private final Color transparent = new Color(0, 0, 0, 0);
    private Color couleurGraph = new Color(255,255,255);
    private final Stroke traitEpais = new BasicStroke(1.5f);
    private final Stroke traitFin = new BasicStroke(0.3f);
    //private final static float dash1[] = {10.0f};
    
    private AffineTransform transf = new AffineTransform();
    private AffineTransform transf2 = new AffineTransform();
    private Vector<ObjetGraphique> objets = new Vector<ObjetGraphique>();
    private double xmin,xmax,ymin,ymax,dx,dy;
    private double xminInit,xmaxInit,yminInit,ymaxInit;
    private String [] legendesX,legendesY;
    private double [] valLegendesX,valLegendesY;
    
    private boolean echelleDB = false;
    private boolean visibleOfAxeX = true,visibleOfAxeY = true;
    
    private int epsx,epsy;
    private int gauche,droite,haut,bas;
    private boolean echelleModif = true;
    private boolean reverseX = false; 
    private boolean reverseY = false;
    private boolean inverseAxes = false;
    private boolean echelleXAuto = true;
    private boolean echelleYAuto = true;
    
    private boolean firstTime = true;
    private Graphics2D gfond;
    private BufferedImage fond;
    private Graphics2D gimg,g2;
    private BufferedImage img;
    private String Xlabel,Ylabel;
    private Rectangle rectZoom = null;
    private boolean zoomEnabled = false;
    
    private int width,w;
    private int height,h;
    private double wd,hd;
    
    
    private JPopupMenu popup = new JPopupMenu();
    private JMenuItem itemResetZoom,itemPrint;
        
    public GraphiqueWavelet () {
        this(-1d,1d,-1d,1d);
    }
    
    public GraphiqueWavelet (double _xmin,double _xmax,double _ymin, double _ymax) {
        super();
        couleurFond = this.getBackground();
        setMaximumBounds(_xmin,_xmax,_ymin,_ymax);
        resetZoom();
        Xlabel = "X";
        Ylabel = "Y";
        
        /** Reset zoom on double click **/
        this.addMouseListener(new MouseAdapter() {	
        	public void mouseClicked(MouseEvent e) {
        		if (e.getClickCount() == 2) resetZoom();
        	}
        });
        /** menu popup **/
        itemResetZoom = new JMenuItem(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {resetZoom();}
        });
        itemResetZoom.setText("Reset Zoom");
        itemResetZoom.setMnemonic(KeyEvent.VK_R);
        popup.add(itemResetZoom);
        
        itemPrint = new JMenuItem(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {imprimer();}
        });
        itemPrint.setText("Print...");
        itemPrint.setMnemonic(KeyEvent.VK_P);
        popup.add(itemPrint);
        
        /** listeners **/
        final MouseInputAdapter mia = new MouseInputAdapter () {
            Cursor curseur = new Cursor(Cursor.CROSSHAIR_CURSOR);
            int x0,y0;
            
            public void mouseWheelMoved(MouseWheelEvent e) {
            	int nscroll = e.getWheelRotation();
            	double xcenter = (getXmin()+getXmax())/2d ;
            	double ycenter = (getYmin()+getYmax())/2d ;
            	double xrange = (getXmax()-getXmin())/2d ;
            	double yrange = (getYmax()-getYmin())/2d ;
            	double xrangeNew = xrange *(1+ ((double) nscroll) * 0.05);
            	double yrangeNew = yrange *(1+ ((double) nscroll) * 0.05);
            	if(e.isControlDown()) xrangeNew = xrange;
            	if(e.isAltDown())     yrangeNew = yrange;
            	double x1 = xcenter - xrangeNew;
            	double x2 = xcenter + xrangeNew;
            	double y1 = ycenter - yrangeNew;
            	double y2 = ycenter + yrangeNew;
            	setBounds(Math.min(x1,x2),Math.max(x1,x2),Math.min(y1,y2),Math.max(y1,y2));	
            	}
           
            public void mouseEntered(MouseEvent e) {
                requestFocusInWindow();
            }
            
            public void mouseMoved(MouseEvent e) {
                if (isInside(e.getX(),e.getY())) setCursor(curseur);
                else setCursor(null);
            }
            
            public void mousePressed(MouseEvent e) {
                x0 = e.getX();
                y0 = e.getY();
                if (isInside(x0,y0)&& zoomEnabled) rectZoom = new Rectangle();
            }
            
            public void mouseDragged(MouseEvent e) {
                if (rectZoom != null) {
                    int x = Math.min(Math.max(e.getX(),gauche),width-droite);
                    int y = Math.min(Math.max(e.getY(),haut),height-bas);
                    
                    rectZoom.setBounds(Math.min(x0,x),Math.min(y0,y),Math.abs(x0-x),Math.abs(y0-y));
                    repaint();
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                int x = Math.min(Math.max(e.getX(),gauche),width-droite);
                int y = Math.min(Math.max(e.getY(),haut),height-bas);
                
                if ((rectZoom !=null)&&(Math.abs(x0-x) > 5)&&(Math.abs(y0-y) > 5)) {
                    rectZoom.setBounds(Math.min(x0,x),Math.min(y0,y),Math.abs(x0-x),Math.abs(y0-y));
                    double x1,x2,y1,y2;
                    
                    /** point 1 **/
                    double [] pt = new double [2];
                    try {
                        pt[0] =  x0; pt[1] = y0;
                        transf.inverseTransform(pt,0,pt,0,1);
                        x1 = pt[0];
                        y1 = pt[1];
                        
                        /** point 2 **/
                        pt[0] =  x ; pt[1] = y;
                        transf.inverseTransform(pt,0,pt,0,1);
                        x2 = pt[0];
                        y2 = pt[1];
                        
                        setBounds(Math.min(x1,x2),Math.max(x1,x2),Math.min(y1,y2),Math.max(y1,y2));
                    } catch (Exception exc) {
                    	System.out.println(exc.getMessage());
                    }
                }
                rectZoom = null;
                repaint();
            }
            
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) popup.show(e.getComponent(),e.getX(), e.getY());
            }
        };
        
        addMouseMotionListener(mia);
        addMouseListener(mia);
        addMouseWheelListener(mia);
        
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (((!reverseY) && ((code == KeyEvent.VK_DOWN) || (code == KeyEvent.VK_KP_DOWN)))
                    || ((reverseY) && ((code == KeyEvent.VK_UP) || (code == KeyEvent.VK_KP_UP)))) {
                    double ymin2 = Math.max(ymin-dy,yminInit);
                    setBounds(xmin,xmax,ymin2,ymin2+(ymax-ymin));   
                }
                
                else if (((reverseY) && ((code == KeyEvent.VK_DOWN) || (code == KeyEvent.VK_KP_DOWN)))
                    || ((!reverseY) && ((code == KeyEvent.VK_UP) || (code == KeyEvent.VK_KP_UP)))) {
                    double ymax2 = Math.min(ymax+dy,ymaxInit);
                    setBounds(xmin,xmax,ymax2-(ymax-ymin),ymax2);
                }
                else if (((!reverseX) && ((code == KeyEvent.VK_LEFT) || (code == KeyEvent.VK_KP_LEFT)))
                    || ((reverseX) && ((code == KeyEvent.VK_RIGHT) || (code == KeyEvent.VK_KP_RIGHT)))){
                    double xmin2 = Math.max(xmin-dx,xminInit);
                    setBounds(xmin2,xmin2+(xmax-xmin),ymin,ymax);
                }
                else if (((reverseX) && ((code == KeyEvent.VK_LEFT) || (code == KeyEvent.VK_KP_LEFT)))
                    || ((!reverseX) && ((code == KeyEvent.VK_RIGHT) || (code == KeyEvent.VK_KP_RIGHT)))){
                    double xmax2 = Math.min(xmax+dx,xmaxInit);
                    setBounds(xmax2-(xmax-xmin),xmax2,ymin,ymax);
                }
                repaint();
            }
        });
    }
        
    public boolean isInside(int x, int y) {
        return ((x >= gauche) && 
        		(x <= width-droite) &&
        		(y >= haut) &&
        		(y <= height-bas));
    }
    
    public void setVisibleOfAxes(boolean _visibleOfAxeX ,boolean _visibleOfAxeY) {
        visibleOfAxeX = _visibleOfAxeX ;
        visibleOfAxeY = _visibleOfAxeY;
        repaint();
    }
    
    public void setMarges(int _gauche,int _droite, int _haut, int _bas) {
        gauche = _gauche;
        droite = _droite;
        haut = _haut;
        bas = _bas;
        setMinimumSize(new Dimension(gauche+droite+10,haut+bas+10));
        echelleModif = true;
    }
    
    public void setMarges(Insets _marges) {
        gauche = _marges.left;
        droite = _marges.right;
        haut = _marges.top;
        bas = _marges.bottom;
        setMinimumSize(new Dimension(gauche+droite+10,haut+bas+10));
        echelleModif = true;
    }
    
    public void setEchelleDB(boolean b) {
        echelleDB = b;
        echelleModif = true;
    }
    
    public void resetZoom() {
        setBounds(xminInit,xmaxInit,yminInit,ymaxInit);
    }
    
    public void setZoomEnabled(boolean _zoomEnabled) {
        zoomEnabled = _zoomEnabled;
        itemResetZoom.setVisible(zoomEnabled);
    }
    
    public void setMaximumBounds(double _xmin,double _xmax,double _ymin,double _ymax) {
        /** reglages des bornes maximales **/
        xminInit = _xmin;
        xmaxInit = _xmax;
        yminInit = _ymin;
        ymaxInit = _ymax;
        if (xminInit >= xmaxInit) xmaxInit = xminInit +1d;
        if (yminInit >= ymaxInit) ymaxInit = yminInit +1d;
        
        setBounds(xmin,xmax,ymin,ymax);
    }
    
    
    public void setBounds(double _xmin,double _xmax,double _ymin,double _ymax) {
        xmin = Math.max(_xmin,xminInit);
        xmax = Math.min(_xmax,xmaxInit);
        ymin = Math.max(_ymin,yminInit);
        ymax = Math.min(_ymax,ymaxInit);
        
        if (xmin >= xmax) xmax = xmin +1d;
        if (ymin >= ymax) ymax = ymin +1d;
        
        // calcul de dx
        double delta = (xmax-xmin)/10d;
        double n = Math.floor(Math.log(delta)/Math.log(10d));
        double a = delta/Math.pow(10d,n);
        double a2 = 0d;
        
        
        if (a > 5d) {
        	a2 = 1d;
        	n++;
        } else if (a > 2d) {
        	a2 = 5d;
        } else if (a > 1d) {
        	a2 = 2d;
        } else {
        	a2 = 1d;
        }
        dx = a2*Math.pow(10d,n);
        epsx = Math.max(0,(int)-n);
        
        // calcul de dy
        delta = (ymax-ymin)/10d;
        n = Math.floor(Math.log(delta)/Math.log(10d));
        a = delta/Math.pow(10d,n);
        a2 = 0d;
        
        
        if (echelleDB) {
            if (a > 6d) {
            	a2 = 1.2d;
            	n++;
            } else if (a > 3d) {
            	a2 = 6d;
            } else {
            	a2 = 3d;
            }
            dy = a2*Math.pow(10d,n);
            epsy = Math.max(0,(int)-n);
        } else {
            if (a > 5d) {
            	a2 = 1d;
            	n++;
            } else if (a > 2d) {
            	a2 = 5d;
            } else if (a > 1d) {
            	a2 = 2d;
            } else {
            	a2 = 1d;
            }
            dy = a2*Math.pow(10d,n);
            epsy = Math.max(0,(int)-n);
        }
        
        createLegendesX();
        createLegendesY();
        
        echelleModif = true;
        repaint();
    }
    
    
    private void createLegendesX() {
        // calcul des legendes à afficher en abscisses
        if (echelleXAuto) {
            double x = Math.ceil(xmin/dx)*dx;
            int nx = (int) Math.ceil((xmax-xmin)/dx);
            valLegendesX = new double[nx];
            legendesX = new String[nx];
            double precision = Math.pow(10d,(double)epsx);
            for (int i = 0 ; i < nx ; i++) {
                valLegendesX[i] = x;
                legendesX[i] = (epsx == 0) ? (""+(int)Math.rint(x)) : (""+Math.rint(x*precision)/precision) ;
                x += dx;
            }
        }
    }
    
    private void createLegendesY() {
        // calcul des legendes � afficher en ordonn�es
        if (echelleYAuto) {
            double y = Math.ceil(ymin/dy)*dy;
            int ny = (int) Math.ceil((ymax-ymin)/dy);
            valLegendesY = new double[ny];
            legendesY = new String[ny];
            double precision = Math.pow(10d,(double)epsy);
            for (int i = 0 ; i < ny ; i++) {
                valLegendesY[i] = y;
                legendesY[i] = (epsy == 0) ? (""+(int)Math.rint(y)) : (""+Math.rint(y*precision)/precision) ;
                y += dy;
            }
        } 
    }
    
    public void addObjet(ObjetGraphique c) {
        objets.add(c);
    }
    public void removeObjet(ObjetGraphique c) {
        objets.remove(c);
    }
    
    private void imprimer() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog())
          try { 
            printJob.print();
          } catch(PrinterException pe) {
            System.out.println("Error printing: " + pe);
          }
    }
    
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
          return(NO_SUCH_PAGE);
        } else {
          Graphics2D g2d = (Graphics2D)g;
          g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
          paintComponent(g2d);
          return(PAGE_EXISTS);
        }
    }

    private void compileTransform() {
        double xw,xh,yw,yh,w0,h0;
        
        if (!inverseAxes) {
            xw = wd/(xmax-xmin);
            xh = 0d;
            yw = 0d;
            yh = hd/(ymax-ymin);
            w0 = xmin;
            h0 = ymin;
            
            if (reverseX) {
                xw = -xw;
                w0 = xmax;
            }
            if (!reverseY) {
                yh = -yh;
                h0 = ymax;
            }
        }
        else {
            xw = 0d;
            xh = hd/(xmax-xmin);
            yw = wd/(ymax-ymin);
            yh = 0d;
            w0 = ymin;
            h0 = xmin;
            if (!reverseX) {
                xh = -xh;
                h0 = xmax;
            }
            if (reverseY) {
                yw = -yw;
                w0 = ymax;
            }
        }
        transf2.setTransform(xw,xh,yw,yh,-w0*(yw+xw),-h0*(xh+yh));
        transf.setTransform(xw,xh,yw,yh,-w0*(yw+xw)+(double)gauche,-h0*(xh+yh)+(double)haut);
    }
    
    private void drawLabel(String label, Graphics2D g,int position) {
        int lg = SwingUtilities.computeStringWidth(g.getFontMetrics(),label);
        Font ft = g.getFont();
       
        switch (position) {
            case TOP : 
                g.drawString(label,gauche+w/2-lg/2,14);
            break;
            case BOTTOM : 
                g.drawString(label,gauche+w/2-lg/2,height-2);
            break;
            case LEFT: 
                g.setFont(fontLabelVert1);
                g.drawString(label,12,haut+h/2+lg/2);
            break;
            case RIGHT: 
                g.setFont(fontLabelVert2);
                g.drawString(label,width-14,haut+h/2-lg/2);
            break;
            default : break;
        }
        
        g.setFont(ft);
    }
    
    
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Dimension dim = getSize();
        width = dim.width;
        height = dim.height;
        w = width-gauche-droite;
        h = height-haut-bas;
        wd = (double)w;
        hd = (double)h;
        double [] pt = new double [2];
        int w0,h0;
        g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (firstTime) {
            fond = (BufferedImage)createImage(width,height);
            gfond = fond.createGraphics();
            gfond.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            gimg = img.createGraphics();
            gimg.setStroke(traitEpais);
            firstTime = false;
        }
        
        /** dessin de l'image de fond **/
        if (echelleModif) {
            echelleModif = false;
            compileTransform();
            
            gfond.setPaint(couleurFond);
            gfond.fillRect(0,0,width,height);
            
            gfond.setPaint(couleurGraph);
            gfond.fillRect(gauche,haut,w,h);
            gfond.setPaint(Color.black);
            gfond.setStroke(traitEpais);
            gfond.drawRect(gauche,haut,w,h);            
            
            /** Axes **/
            pt[0] = 0d ; pt[1] = 0d;
            transf.transform(pt,0,pt,0,1);
            w0 = (int)pt[0];
            h0 = (int)pt[1];
            if (inverseAxes) {
                if ((visibleOfAxeY) &&(h0 > haut) && (h0 < haut+h)) gfond.drawLine(gauche,h0,gauche+w,h0);
                if ((visibleOfAxeX) && (w0 > gauche) && (w0 < gauche+w))    gfond.drawLine(w0,haut,w0,haut+h);
            }
            else {
                if ((visibleOfAxeX) &&(h0 > haut) && (h0 < haut+h)) gfond.drawLine(gauche,h0,gauche+w,h0);
                if ((visibleOfAxeY) && (w0 > gauche) && (w0 < gauche+w))    gfond.drawLine(w0,haut,w0,haut+h);
            }
            
            /** lib�l�s des axes **/
            gfond.setPaint(Color.black);
            gfond.setFont(fontLabel);
            drawLabel(Xlabel,gfond,positionLegendeX);
            drawLabel(Ylabel,gfond,positionLegendeY);
            
            /** Grilles et l�gendes **/
            gfond.setStroke(traitFin);
            gfond.setFont(fontLegende);
            String str;
            int nx = (valLegendesX == null) ? 0 : valLegendesX.length;
            int ny = (valLegendesY == null) ? 0 : valLegendesY.length;
            int lgx,lgy;
            /** abscisse **/
            for (int i = 0 ; i < nx ; i++) {
                pt[0] = valLegendesX[i];
                if ((pt[0] <= xmax) && (pt[0] >= xmin)) {
                    str = legendesX[i];
                    if (str == null) str = "";
                    lgx = SwingUtilities.computeStringWidth(gfond.getFontMetrics(),str);
                    transf.transform(pt,0,pt,0,1);
                    if (inverseAxes) {
                        h0 = (int)pt[1];
                        gfond.drawLine(gauche,h0,w+gauche,h0);                    
                        if (positionLegendeX == LEFT) gfond.drawString(str,gauche-lgx-2,h0+5);
                        else if (positionLegendeX == RIGHT)gfond.drawString(str,gauche+w+2,h0+5);
                    }
                    else {
                        w0 = (int)pt[0];
                        gfond.drawLine(w0,haut,w0,h+haut);
                        if (positionLegendeX == TOP) gfond.drawString(str,w0-lgx/2,haut-2);
                        else if (positionLegendeX == BOTTOM) gfond.drawString(str,w0-lgx/2,haut+h+12);
                    }
                }
            }
                
            /** ordonn�es **/    
            for (int i = 0 ; i < ny ; i++) {
                pt[1] = valLegendesY[i];
                if ((pt[1] <= ymax) && (pt[1] >= ymin)) {
                    str = legendesY[i];
                    if (str == null) str = "";
                    transf.transform(pt,0,pt,0,1);
                    lgy = SwingUtilities.computeStringWidth(gfond.getFontMetrics(),str);
                    if (inverseAxes) {
                        w0 = (int)pt[0];
                        gfond.drawLine(w0,haut,w0,h+haut);
                        if (positionLegendeY == TOP) gfond.drawString(str,w0-lgy/2,haut-2);
                        else if (positionLegendeY == BOTTOM) gfond.drawString(str,w0-lgy/2,haut+h+12);
                    }
                    else {
                        h0 = (int)pt[1];
                        gfond.drawLine(gauche,h0,w+gauche,h0);
                        if (positionLegendeY == LEFT) gfond.drawString(str,gauche-lgy-2,h0+5);
                        else if (positionLegendeY == RIGHT) gfond.drawString(str,gauche+w+2,h0+5);
                    }
                }
            }
        }
        
        
        /** dessin des objets graphiques **/
        gimg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        gimg.setColor(transparent);
        gimg.setComposite(AlphaComposite.Src);
        gimg.fillRect(0, 0, w, h);
        gimg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int nbObjets = objets.size();
        try {
            for (int i = 0 ; i < nbObjets ; i++) {
                ObjetGraphique c = (ObjetGraphique) objets.elementAt(i);
                c.dessiner(gimg,transf2);
            }
        } catch (Exception e) {}
        
        g2.drawImage(fond,0,0,this);
        g2.drawImage(img,gauche,haut,this);
        /** dessin eventuel du rectangle de zoom **/
        if (rectZoom != null) {
            g2.setPaint(Color.black);
            g2.draw(rectZoom);
        }
    }
    
    /* GETTERS */
    public double getXmin() {
        return xmin;
    }
    
    public double getXmax() {
        return xmax;
    }
    
    public double getYmin() {
        return ymin;
    }
    
    public double getYmax() {
        return ymax;
    }
    
    /* SETTERS */
    public void setXlabel(String label) {
        Xlabel = label;
        echelleModif = true;
    }
    
    public void setYlabel(String label) {
        Ylabel = label;
        echelleModif = true;
    }
    
    public void setEchelleX(double [] _valLegendesX, String [] _legendesX) {
        int nx = _valLegendesX.length;
        if (nx != _legendesX.length) throw new ArrayIndexOutOfBoundsException("Arrays sizes mismatch");
        valLegendesX = new double[nx];
        legendesX = new String[nx];
        System.arraycopy(_valLegendesX,0,valLegendesX,0,nx);
        System.arraycopy(_legendesX,0,legendesX,0,nx);
        echelleXAuto = false;
    }
    
    public void setEchelleY(double [] _valLegendesY, String [] _legendesY) {
        int ny = _valLegendesY.length;
        if (ny != _legendesY.length) throw new ArrayIndexOutOfBoundsException("Arrays sizes mismatch");
        valLegendesY = new double[ny];
        legendesY = new String[ny];
        System.arraycopy(_valLegendesY,0,valLegendesY,0,ny);
        System.arraycopy(_legendesY,0,legendesY,0,ny);
        echelleYAuto = false;
    }
    
    public void setEchelleXAuto() {
        echelleXAuto = true;
        createLegendesX();
        echelleModif = true;
    }
    
    public void setEchelleYAuto() {
        echelleYAuto = true;
        createLegendesY();
        echelleModif = true;
    }
    
    public void setReverseX(boolean _reverseX) {
        reverseX = _reverseX;
        echelleModif = true;
    }
    public void setReverseY(boolean _reverseY) {
        reverseY = _reverseY;
        echelleModif = true;
    }
    
    public void setInverseAxes(boolean _inverseAxes) {
        if (inverseAxes != _inverseAxes) {
            positionLegendeX = - positionLegendeX;
            positionLegendeY = - positionLegendeY;
        }
        inverseAxes = _inverseAxes;
        echelleModif = true;
    }
    
    public void setPositionLegendeX(int position) {
        if (inverseAxes) {
            if ((position == LEFT) || (position == RIGHT)) positionLegendeX = position;
            else positionLegendeX= - position;
        }
        else {
            if ((position == TOP) || (position == BOTTOM)) positionLegendeX= position;
            else positionLegendeX = - position;
        }
        echelleModif = true;      
    }
    
    public void setPositionLegendeY(int position) {
        if (inverseAxes) {
            if ((position == TOP) || (position == BOTTOM)) positionLegendeY= position;
            else positionLegendeY = - position;
        }
        else {
            if ((position == LEFT) || (position == RIGHT)) positionLegendeY = position;
            else positionLegendeY = - position;
        }
        echelleModif = true;      
    }
    
    public void setFontLabel(Font _font) {
    	fontLabel = _font;
    	fontLabelVert1 = fontLabel.deriveFont(AffineTransform.getRotateInstance(-Math.PI/2d));
        fontLabelVert2 = fontLabel.deriveFont(AffineTransform.getRotateInstance(Math.PI/2d));
    }
    
}



