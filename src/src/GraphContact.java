package src;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;

import src.calculations.FastFourierTransform;
import src.debugger.DebugLog;
import src.graph.tracesismique.Courbe;
import src.graph.tracesismique.TraceSismique;
import src.model.Model;
import src.model.models.DefaultModel;
import src.typecouche.TypeCouche;

public class GraphContact extends JPanel {

	/* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	public static final Font style = new Font("Helvetica",0,12);//0,10
	public static final int NB_SANDSMAX = 31;
    public static final int NB_CRBMAX = 80;
    public static final int NB_CONTACTSMAX = 2;
    
    private boolean firstTime = true;
    private boolean dessinCourbes = true;
    private boolean echelleModif = true;   
    private boolean refractionEffect = true;
    
    private int tailleFleche = 6;

    private String Xlabel, Ylabel;
    private int widthGc, heightGc;
    
    
    /* Model parameters */
    private Model model = new DefaultModel();
    private double thick=getModel().thick;
    private double dip=getModel().dip;
    
    /* Other parameters */
    private double freq=40;
    private double MinRatio=0.3,MaxRatio=0.8;
    private double BufferRatio=1;
    private double LC; //Contact length
    private double LMmin,LMmax,LM;
    private int dx;
    private double [] PosiTrace;
    private Point[] PosiPixel;
    private int test1,test0;
	double Posi;
    private int wgc; //Graphcontact wide
    public double LStartCont,LEndCont;
    private double Tmin,Tmax,Zmax,ZM,TM;
    private int horizScaleX=100,horizScaleY=704,vertscaleX=100,vertscaleY=600;
    GradientPaint grad;
    
    private int ncrb = 80;
    /* margin = {left,up,right,down} */
    private int [] marge = {40,20,50,30};
    
    private int[] absF1=new int [4];
	private int[] ordF1=new int [4];  
    private int[] absF2=new int [5];
    private int[] ordF2=new int [5];

    private double[] atop;
    private double[] abase;
    public double[][] acon = new double [NB_CONTACTSMAX][ncrb];
    private double[] ttop;
    private double[] tbase;
    public double[][] tcon = new double [NB_CONTACTSMAX][ncrb];
    public double [][] xCon = new double [NB_CONTACTSMAX][2*NB_SANDSMAX];
    public double [][] tCon = new double [NB_CONTACTSMAX][2*NB_SANDSMAX];
   
    private BufferedImage biAxes;
    private BufferedImage biTot;
    private Graphics2D gAxes;
    private Graphics2D gTot;
    private AffineTransform transf  = new AffineTransform();
   
    private int indexOfcolorSet = 1;
   
    public int nb_sands; //5
    public double net2gross; //0.6
    public int nb_contacts = 2;
    public int nb_interfaces;// = 2*nb_sands;//+nb_contacts;
    public double [][] twttop;
    public double [] ztop; 
    public int [][] ktypeCoucheAbove; 
    public int [][] ktypeCoucheBelow; 
    public int [] knb_interf;
    public double [] layerThick; //new double [2*nb_sands-1];
    public double [] sandThick, shaleThick;
    public int [] itypeAboveRes;
    public int [] itypeAboveFluid;
    public int [][] ktypeAboveRes;
    public int [][] ktypeAboveFluid;
    public int [][] kContact;
    
    private int nb_interf=5;
    private double [][] R = new double [5][5];//coef de reflexion
    
    private TraceSismique [] courbes;
    public Vector<Courbe> vectCourbes = new Vector<Courbe>();
    
    private TypeCouche[] typeCouches;
    public Vector<TypeCouche> TypeCouches = new Vector<TypeCouche>(); 

    private double[] rho = getModel().rho;
    private double[] vp = getModel().vp;
    private double[] vs = getModel().vs;
    private double[] pr = getModel().pr;
    private double phase = 0d;
    private double gain = 1d;
    private double transF1 = 153d;
    private double transF2 = 153d;
    private double angleIncident = 0d;
    
    private double Vth2,Vth,Vzh;
    
    private Color cF1 = new Color(1f,0f,0f);
    private Color cF2 = new Color(0f,0f,1f);
    
    GeneralPath courbeAmplBase = new GeneralPath();
    Line2D.Double horiz1 = new Line2D.Double();
    
    private final int N = 2048; /** une puissance de 2 **/
    private final double dt = 0.001;
    
    
    private final double domega = 2d*Math.PI/((double)N*dt);
    private double timeshift; 
    private double [] X = new double [N];
    private double [] rick = new double [N];
    private double [][] fourierRick = new double [N][2];
    private double [][] fourierRickDephase = new double [N][2];
    private double [][] convolutionFourier = new double [N][2];  
    private double [] convolution = new double [N];
    private double [][] section = new double [N][150];

    public GraphContact() {
    	super();
    	initArray();
    	setTypeCouches();
    
	    setXlabel("TWT (ms)");
	    setYlabel("X(m)");
	    calculX();
	    calculRicker();
	    setX(0d, (double) ncrb);
	    setY(0d, TM);
    }
    
    public void calculRicker() {
    	timeshift= 1.2d/freq;  
        for (int i = 0 ; i < N ; i++) {
        	double t=(double)i *dt-timeshift;
            rick[i] = ricker2(t);
        }
        FastFourierTransform.fftReal(rick,fourierRick);
        /** dephasage **/
        double c = Math.cos(Math.toRadians(phase));
        double s = Math.sin(Math.toRadians(phase));
        double a,b;
        for (int i = 0 ; i < N ; i++) {
            a = fourierRick[i][0];
            b = fourierRick[i][1];
            fourierRickDephase[i][0] = a*c + b*s;
            fourierRickDephase[i][1] = -a*s + b*c;
        }
    	FastFourierTransform.invfftReal(fourierRickDephase,rick);
        
    }
    
    public void calculX() {
	    for (int i = 0 ; i < N ; i++) {
	        X [i] = (double) i*dt;
	    }
    }
    
  
    public boolean isInside(int x, int y) {
        Dimension dim = getSize();
        int width = dim.width;
        int height = dim.height;
        return ((x >= marge[0]) &&
        		(x <= width-marge[2]) &&
        		(y >= marge[1]) &&
        		(y <= height-marge[3]));
    }

    
    public void setXlabel(String _Xlabel) {
    	Xlabel  = _Xlabel ;
    }
    
    public void setYlabel(String _Ylabel) {
    	Ylabel  = _Ylabel ;
    }
    
    public double getThickness() {
    	return thick;
    }
    
    public double getDip() {
    	return dip;
    }
    
	public double getDt(){
		return dt;
	}
	
	public int getN(){
		return N;
	}
	
	public double getPhase() {
		return phase;
	}
	
	public double getGain() {
		return gain;
	}
	
	public boolean getRefractionEffect() {
		return refractionEffect;
	}
	
	public double getAngleIncident() {
		return angleIncident;
	}
	
    public int[] getAbsF1() {
		return absF1;
	}

	public int[] getOrdF1() {
		return ordF1;
	}

	public int[] getAbsF2() {
		return absF2;
	}

	public int[] getOrdF2() {
		return ordF2;
	}
	public double [] getRick(){
		return rick;
	}
	
	public int getIndexOfcolorSet(){
		return indexOfcolorSet;
	}
	
	public double getTmax(){
		return TM;
	}
	
	public double getLM(){
		return LM;
	}
	
	public int getNcrb(){
		return ncrb;
	}
	public void setIndexOfcolorSet(int val){
    	indexOfcolorSet = val;
    }
	
    public void setX(double _LMmin, double _LMmax) {
	    LMmin    =_LMmin ;
	    LMmax    =_LMmax;
	    dx=(int)(LMmax- LMmin);
	    echelleModif = true;
    }
    
    public void setY(double _Tmin, double _Tmax) {
	    Tmin    = _Tmin ;
	    Tmax    = _Tmax ;
	    echelleModif = true;
    }
    
    public void setValEchelle(int _horizX, int _horizY, int _vertX, int _vertY) {
    	// origine des echelles hor et vert 
    	horizScaleX =_horizX;
    	horizScaleY =_horizY;
    	vertscaleX	=_vertX;
    	vertscaleY  =_vertY;
    	
    }
    
    public void setRaytracing(boolean val) {
    	refractionEffect = val;
    }
    
    public void setFrequence(double val) {
    	freq = val;
    }
    
    public void setNBtrace(double val) {
    	ncrb = (int) val;      
    }
    
    public void setNBsands(int val) {
        nb_sands = val;      
    }
    
    public void setNet2Gross(double val) {
        net2gross = val;      
    }
    
    public void setPhase(double val) {
    	phase = val;
    }
    
    public void setAngleIncident(double val) {
    	angleIncident = val;
    }
    
    public void setRho(int i,double val) {
	    rho[i] = val;
	    typeCouches[i].removeOwnChangeListenerRho();
	    typeCouches[i].setRho(val);
	    typeCouches[i].addOwnChangeListenerRho();
    }
    
    public void setVp(int i , double val) {
	    vp[i] = val;
	    typeCouches[i].removeOwnChangeListenerVp();
	    typeCouches[i].setVp(val);
	    typeCouches[i].addOwnChangeListenerVp();
    }
    
    public void setVs(int i , double val) {
	    vs[i] = val;
	    typeCouches[i].removeOwnChangeListenerVs();
	    typeCouches[i].setVs(val);
	    typeCouches[i].addOwnChangeListenerVs();
    }
    
    public void setPr(int i , double val) {
    	pr[i] = val;
    	typeCouches[i].removeOwnChangeListenerPr();
    	typeCouches[i].setPr(val);
    	typeCouches[i].addOwnChangeListenerPr();
    } 
    
    public void setGain(double val) {
    	gain = val;
    }
    
    public void setMinRatio(double val) {
    	MinRatio = val;
    }
    
    public void setMaxRatio(double val) {
    	MaxRatio = val;
    }
    
    public void setColorF1(Color val) {
    	cF1 = val; 	
    }
    
    public void setColorF2(Color val) {
    	cF2 = val;
    }
    
    public void setTransF1(double val) {
    	transF1 = val;
    }
    
    public void setTransF2(double val) {
    	transF2 = val;
    }
    
    public void setThickness(double val) {
    	thick= val;
    }
    
    public void setDip(double val) {
    	dip = val;
    }
    
    private double ricker2(double x) {
    	return (double) Math.exp(- carre(Math.PI*freq*x))*(1d-2d*carre(Math.PI*freq*x));
    }
    
    public double poissonRatio(double _vp , double _vs) {
        double P = carre(_vp/_vs);
        return (double) Math.round((P-2d)/(2d*P-2d) * 1000) / 1000; // 4.248 --> 4.25(P-2d)/(2d*P-2d);
    }
    
    public double carre(double val) {
    	return Math.pow(val,2d);
    }
    
    public void initArray() {
    	layerThick = new double [2*NB_SANDSMAX];
    	sandThick = new double [NB_SANDSMAX];
    	shaleThick = new double [NB_SANDSMAX];
    	ztop = new double [2*NB_SANDSMAX]; //nb_interfaces
    	itypeAboveRes  = new int [2*NB_SANDSMAX]; // -1= non res 1 = reservoir
    	itypeAboveFluid = new int [2*NB_SANDSMAX]; // 0 = HC1 1 = HC2 2=Brine

    	Arrays.fill(sandThick,0,NB_SANDSMAX,0d);
    	Arrays.fill(shaleThick,0,NB_SANDSMAX,0d);
    	Arrays.fill(layerThick,0,2*NB_SANDSMAX,0d);
    	Arrays.fill(ztop      ,0,2*NB_SANDSMAX,0d);
    	Arrays.fill(itypeAboveRes,0,2*NB_SANDSMAX,0);
    	Arrays.fill(itypeAboveFluid,0,2*NB_SANDSMAX,0);
    	
    	vectCourbes.clear();
    	courbes=new TraceSismique[ncrb];
    	for (int i = 0 ; i < ncrb ; i++) {		
    		courbes[i]= new TraceSismique();
    		courbes[i].setOrigine((double)i);
    		courbes[i].setSelected(false);
    		vectCourbes.add(courbes[i]);
    		for (int j = 0 ; j< N ; j++) {
    			section[j][i] = 0d ;
    		}
    	}   
    }
    
    /* Use current model definition to set TypeCouches */
    public void setTypeCouches(){
    	TypeCouches.clear();
    	TypeCouches = getModel().generateTypeCouche();
    	typeCouches = getModel().generateTypeCoucheArray();
    }
    
    public void calculReflectivities(boolean _refractionEffect) {
        /* CALCUL DES COEFFS DE REFLECTIVITE */
    	double x=angleIncident;
    	if (_refractionEffect) { // Zoeppritz	
	        double p,sint1,sin2t1,sin2t2,sin2f1,sin2f2,cost1_vp1,cost2_vp2,cosf1_vs1,cosf2_vs2,a,b,c,d,E,F,G,H,D;
	        for (int i=0; i<5; i++) {
	        	for(int j=0; j<5; j++) {          	
	            	sint1 = Math.sin(Math.PI*x/180d);
	            	sin2t1 = carre(sint1);
	            	p = sint1/vp[i];
	            	cost1_vp1 = Math.sqrt(1d-sin2t1)/vp[i];
	            	// theta2
	            	if (sint1 < vp[i]/vp[j]) {
	            		 sin2t2 = carre(vp[j]/vp[i])*sin2t1;
	            	} else {
	            		sin2t2 = 1d;
	            	}
	            	cost2_vp2 = Math.sqrt(1d-sin2t2)/vp[j];
	            	// phi1
	            	if (sint1 < vp[i]/vs[i]) {
	            		sin2f1 = carre(vs[i]/vp[i])*sin2t1;
	            	} else {
	            		sin2f1 = 1d;
	            	}
	            	cosf1_vs1 = Math.sqrt(1d-sin2f1)/vs[i]; 
	            	// phi2
	            	if (sint1 < vp[i]/vs[j]) {
	            		sin2f2 = carre(vs[j]/vp[i])*sin2t1;
	            	} else {
	            		sin2f2 = 1d;
	            	}
	            	cosf2_vs2 = Math.sqrt(1d-sin2f2)/vs[j];
	            	// parametres
	            	a = rho[j]*(1d-2d*sin2f2) - rho[i]*(1d-2d*sin2f1);
	            	b = rho[j]*(1d-2d*sin2f2) + 2d*rho[i]*sin2f1;
	            	c = rho[i]*(1d-2d*sin2f1) + 2d*rho[j]*sin2f2;
	            	d = 2d*(rho[j]*carre(vs[j]) - rho[i]*carre(vs[i]));
	            	E = b*cost1_vp1 + c*cost2_vp2;
	            	F = b*cosf1_vs1 + c*cosf2_vs2;
	            	G = a - d*cost1_vp1*cosf2_vs2;
	            	H = a - d*cost2_vp2*cosf1_vs1;
	            	D = E*F + G*H*carre(p);
	            
	            	if (D == 0d) {
	            		R[i][j] = 0d;
	            	} else {
	            		R[i][j] = (F*(b*cost1_vp1-c*cost2_vp2) - H*carre(p)*(a+d*cost1_vp1*cosf2_vs2))/D;
	            	}
            	}
            }
    	} else {	// AKI-RICHARDS		
    		double vp_av, vs_av, rho_av, dvp, dvs, drho, a,b,c, gamma, sint1, sin2t1, reflec ;
    		for (int i=0; i<5; i++) {
            	for(int j=0; j<5; j++) {
            		// theta1          	
                	sint1 = Math.sin(Math.PI*x/180d);
                	sin2t1 = carre(sint1);
                	vp_av = (typeCouches[i].getVp()+typeCouches[j].getVp())/2;
                	vs_av = (typeCouches[i].getVs()+typeCouches[j].getVs())/2;
                	rho_av = (typeCouches[i].getRho()+typeCouches[j].getRho())/2;
                	dvp = (typeCouches[j].getVp()-typeCouches[i].getVp());
                	dvs = (typeCouches[j].getVs()-typeCouches[i].getVs());
                	drho = (typeCouches[j].getRho()-typeCouches[i].getRho());
                	gamma = vs_av/vp_av;
                	a=0.5/(1d-sin2t1);
                	b=0.5-2d*gamma*gamma*sin2t1;
                	c=-4d*gamma*gamma*sin2t1;
                	reflec = a*(dvp/vp_av) + b*(drho/rho_av)+c*(dvs/vs_av);
                	R[i][j] = reflec;       	
            	}
    		}
    	}
    }

    
    public void checkModeldimension() {
    	/**  the contact boundaries conditions to have a good visual 
		MinRatio<ratio>MaxRatio and Bufferf<T-TM >Buffer5Hz, **/
    	double Bufferf=1/freq;
    	double zcLeft,zcRight,zcMax;
    	double Buffer_Ratio=2;
    	double timeRatio,timeRatioMin, timeRatioMax;
    	
    	timeRatioMin=0.3;
    	timeRatioMax=0.8;
    	
    	zcLeft = getModel().ZContact[0]-ZM/2;
    	zcRight = getModel().ZContact[1]-ZM/2;
    	zcMax = Math.max(zcRight, -zcLeft);
    	
	    LC=thick/Math.sin(Math.PI*dip/180d);
	    LC = LC +2*zcMax/Math.tan(Math.PI*dip/180d);
	    Vzh=thick/(Math.cos(Math.PI*dip/180d));
	    Vth=2*Vzh/vp[1];
	    Vth2=2*Vzh/vp[2];
	     
		if((LC/getModel().LM) <MinRatio) {
			
			LM=LC/MinRatio;
			Tmax= 2*((LM*Math.tan(Math.PI*dip/180d))/vp[0])+Vth;
			Zmax=LM*Math.tan(Math.PI*dip/180d)+ Vzh;
			firstTime=true;
			
			if((getModel().TM-Tmax) > Buffer_Ratio*2*Bufferf) {
				TM= Tmax + 2*Bufferf;
				firstTime=true;
				ZM=Zmax + Bufferf*vp[0];
			} else if((getModel().TM-Tmax) <2*Bufferf) {
				TM= Tmax + 2*Bufferf;
				firstTime=true;
				ZM=Zmax + Bufferf*vp[0];
			} else {
				TM=getModel().TM;	
				ZM=getModel().ZM;
			} 
		} else if ((LC/getModel().LM) >MaxRatio) {
			LM=LC/MaxRatio;
			Tmax= 2*((LM*Math.tan(Math.PI*dip/180d))/vp[0])+Vth;
			Zmax=LM*Math.tan(Math.PI*dip/180d) +Vzh;
			firstTime=true;
			
			if((getModel().TM-Tmax) > Buffer_Ratio*2*Bufferf) {
				//Bufferf=Buffer5Hz;
				TM= Tmax + 2*Bufferf;
				firstTime=true;
				ZM=Zmax + Bufferf*vp[0];
				//TM=getModel().TM;
				//ZM=getModel().ZM;
			} else if((getModel().TM-Tmax) <2*Bufferf) {
				TM= Tmax + 2*Bufferf;
				firstTime=true;
				ZM=Zmax + Bufferf*vp[0];
			} else {
				TM=getModel().TM;
				ZM=getModel().ZM;
			} 
			
		} else {
			LM=getModel().LM;
			Tmax= 2*((LM*Math.tan(Math.PI*dip/180d))/vp[0])+Vth;
			Zmax=LM*Math.tan(Math.PI*dip/180d) + Vzh;
			
			if((getModel().TM-Tmax) > Buffer_Ratio*2*Bufferf) {
				TM= Tmax + 2*Bufferf;
				firstTime=true;
				ZM=Zmax + Bufferf*vp[0];
			} else if((getModel().TM-Tmax) <2*Bufferf) {
				TM= Tmax + 2*Bufferf;
				firstTime=true;
				ZM=Zmax + Bufferf*vp[0];
			} else {
				TM=getModel().TM;
				ZM=getModel().ZM;
			}  
		}

	    getModel().LM=LM; 
	    getModel().TM=TM;
	    getModel().ZM=ZM;
	    getModel().ZContact[0]=zcLeft+ZM/2;
	    getModel().ZContact[1]=zcRight+ZM/2;
    }
    
    public void createNet2Gross() {
    	// randomly split the gross thickness in reservoir / non res according to N:G
    	double xodd, xeven;	// even = reservoir - odd = non reservoir
    	Vzh=thick/(Math.cos(Math.PI*dip/180d));
    	double x = Math.random();
    	int i = 0;
    	layerThick[i] = x;
    	xeven = x;
    	xodd =0;
    	for(int k = 1 ; k < nb_sands ; k++) {
    		x = Math.random();
    		i = 2*k-1;
    		layerThick[i] = x;
    		xodd = xodd+x; // shale
    		i = i+1;
    		x = Math.random();
    		layerThick[i] = x;
    		xeven = xeven+x; // sand
    	}   	
    	layerThick[0] = Vzh*net2gross*layerThick[0]/(xeven);
    	for(int k = 1 ; k < nb_sands ; k++) {   		
    		i = 2*k-1;
    		layerThick[i] = Vzh*(1-net2gross)*layerThick[i]/(xodd);
    		i = i+1;  		
    		layerThick[i] = Vzh*net2gross*layerThick[i]/(xeven);
    	}
   	
    }
    
    public void createNet2Gross(int iSeq) {	
    	// randomly split the gross thickness in reservoir / non res according to N:G
    	// iSeq 0 = Thining up 1 = Thickening up 2 = Compensation 3 = Random
    	double xodd, xeven;					// even = reservoir - odd = non reservoir
    	Vzh=thick/(Math.cos(Math.PI*dip/180d));
    	if(nb_sands == 1 ) {
    		layerThick[0] = Vzh ;
    		return;
    	}
    	double x = Math.random();
    	int i = 0;
    	sandThick[i]=x;
    	xeven = x;
    	xodd =0;
    	for(int k = 1 ; k < nb_sands ; k++) {
    		x = Math.random();
    		sandThick[k]=x;
    		xeven = xeven+x; // sand
    		x = Math.random();
    		shaleThick[k-1]=x;
    		xodd = xodd+x; // shale
    	}
    	sandThick[0] = Vzh*net2gross*sandThick[0]/xeven;
    	for(int k = 1 ; k < nb_sands ; k++) {
    		sandThick[k] = Vzh*net2gross*sandThick[k]/xeven;
    		shaleThick[k-1] = Vzh*(1-net2gross)*shaleThick[k-1]/xodd;
    	}
    	switch (iSeq) {
		case 0: // sorting by increasing thicknesses
    		Arrays.sort(sandThick,0,nb_sands);
    		Arrays.sort(shaleThick,0,nb_sands-1);
    		i=0;
    		layerThick[0] = sandThick[0];
    		i=i+1;
    		for(int k = 1 ;k < nb_sands ; k++ ) {
    			layerThick[i] = shaleThick[k-1];
    			i=i+1;
    			layerThick[i] = sandThick[k];
    			i=i+1;
    		}
			break;
		case 1: // sorting by decreasing thicknesses
			Arrays.sort(sandThick,0,nb_sands);
    		Arrays.sort(shaleThick,0,nb_sands-1);
    		i=0;
    		layerThick[0] = sandThick[nb_sands-1];
    		i=i+1;
    		for(int k = nb_sands - 1 ;k > 0 ; k-- ) {
    			layerThick[i] = shaleThick[k-1];
    			i=i+1;
    			layerThick[i] = sandThick[k-1];
    			i=i+1;
    		}
			break;
		case 2: // sorting by increasing sand and decreasing shale thicknesses
			Arrays.sort(sandThick,0,nb_sands);
    		Arrays.sort(shaleThick,0,nb_sands-1);
    		i=0;
    		layerThick[0] = sandThick[0];
    		i=i+1;
    		for(int k = nb_sands-1 ;k > 0 ; k-- ) {
    			layerThick[i] = shaleThick[k-1];
    			i=i+1;
    			layerThick[i] = sandThick[nb_sands-k];
    			i=i+1;
    		}
    		break;
		case 3: // no sorting out
			i=0;
    		layerThick[0] = sandThick[0];
    		i=i+1;
    		for(int k = 1 ;k < nb_sands ; k++ ) {
    			layerThick[i] = shaleThick[k-1];
    			i=i+1;
    			layerThick[i] = sandThick[k];
    			i=i+1;
    		}
			break;
		default:
			break;
		}
    	return;
    }
    
    public void constructModel() {
    	
    	twttop = new double [ncrb][2*nb_sands+nb_contacts];
        ktypeCoucheAbove = new int [ncrb][2*nb_sands+nb_contacts];
        ktypeCoucheBelow = new int [ncrb][2*nb_sands+nb_contacts];
        
        knb_interf = new int [ncrb];
    	nb_interfaces = 2*nb_sands;
    	ktypeAboveRes = new int [ncrb][nb_interfaces+nb_contacts]; // -1= non res 1 = reservoir
    	ktypeAboveFluid = new int [ncrb][nb_interfaces+nb_contacts]; // 0 = HC1 / 1 = HC2 / 2=Brine

    	int nblayers = 2*nb_sands-1;
    	
    	// init 
    	for(int ii = 1 ; ii < nb_interfaces+nb_contacts ; ii++) {
    		for(int n = 1 ; n < ncrb ; n++) {
    			ktypeAboveRes[n][ii]= -1 ; // non reservoir
    			ktypeAboveFluid[n][ii]= 1 ; // lighter fluid 1
    			ktypeCoucheAbove[n][ii]= 0 ; // overburden
    			ktypeCoucheBelow[n][ii]= 4 ; // underburden 3
    		}	
    	}

    	double [] zcontact = new double [NB_CONTACTSMAX];
    	int kContact[][] = new int [NB_CONTACTSMAX][ncrb];
    	double twt, depth, depthprevious, veloc;
    	
    	for(int j = 0 ; j < nb_contacts ; j++) {
    		zcontact[j] = getModel().ZContact[j];
    		for(int n = 0 ; n < nb_interfaces ; n++) {
    			xCon[j][n] =0;
    			tCon[j][n] =0;
    		}
    		for(int n = 0 ; n < ncrb ; n++) {
    			acon[j][n] =0;
    			tcon[j][n] =0;
    			kContact[j][n] =-1;
    		}
        }
    	
    	for(int j = 0 ; j < nb_interfaces+nb_contacts ; j++) {
    		for(int n = 0 ; n < ncrb ; n++) {
    			ktypeAboveRes[n][j] = 0;
    			ktypeCoucheAbove[n][j] = 0;
    			ktypeCoucheBelow[n][j] = 0;
    			ktypeAboveFluid[n][j] = 0;
    			twttop[n][j]=0;
    		}
		}
    	Arrays.fill(ztop, 0, nb_interfaces, 0d);
    	Arrays.fill(itypeAboveRes, 0, nb_interfaces, 0);
    	Arrays.fill(knb_interf, 0, ncrb, 0);
    	
    	// modele centre sur le milieu du modele profondeur
    	double zc;
    	zc = getModel().ZM/2d;
    	
    	int iii = (int) layerThick.length;
    	int kk = 0;
    	ztop[0]= zc-0.5*Vzh-LM*Math.tan(Math.PI*dip/180d)/2;
    	itypeAboveRes[0]=-1; // overburden non reservoir
    	ktypeAboveRes[0][0]=-1;
    	ktypeAboveFluid[0][0]=0;	
    	twttop[0][0]=ztop[0];
    	
    	kk = kk+1;;
    	for(int ii = 1 ; ii < nb_interfaces ; ii++) { //nb_interfaces
        	ztop[ii] = ztop[ii-1]+layerThick[ii-1];
        	itypeAboveRes[ii]=-itypeAboveRes[ii-1];
        	
        	ktypeAboveRes[0][kk]=itypeAboveRes[ii];
        	ktypeAboveFluid[0][kk]=1;
        	
        	twttop[0][kk] = ztop[ii];
        	kk=kk+1;
        }
    	
    	knb_interf[0] = kk;
    		
    	// positionne en X les contacts et en trouve le twt - utilise le tableau twttop[1][] temporairement
    		
    	for(int jj = 0 ; jj < nb_interfaces ; jj++) {
    		for(int jcontact = 0 ; jcontact < nb_contacts  ; jcontact++) {
    			xCon[jcontact][jj]=(zcontact[jcontact]-ztop[jj])/Math.tan(Math.PI*dip/180d);
    			double twtprevious = 0;
    			int ii = 0;
    	   		kk = 0;
    	   		itypeAboveRes[0]=-1;
    	   		ktypeAboveFluid[1][0]= 1;
    	   		whilebreakpoint:
    	   		while( (ii < nb_interfaces) ) {
    	   			double twtnext=ztop[ii] + zcontact[jcontact]-ztop[jj];
    	    		//if ( itypeAboveRes[ii] == 1 ) {
    	    		for(int icontact = 0 ; icontact < nb_contacts ; icontact++) {
    	    			if (Math.abs(twtnext-zcontact[icontact]) <= 0.001) { // to account with numerical inaccuracies
    	    				//if (twtnext == zcontact[icontact]) {
        	    			twttop[1][kk]=twtnext;
        	    			ktypeAboveRes[1][kk]= itypeAboveRes[ii];
        	    			ktypeAboveFluid[1][kk]= icontact+1;
        	    			kk = kk +1;
        	    			break whilebreakpoint;
        	    		} else if ((twtnext-zcontact[icontact])*(twtprevious-zcontact[icontact])<0 ) {
	    	    			twttop[1][kk] = zcontact[icontact];
	    	    			twtprevious = zcontact[icontact];
	    	    			ktypeAboveRes[1][kk]= 1;
	            			ktypeAboveFluid[1][kk]=icontact+1;
	            			kk = kk +1;
    	    			}
    	    		}
    	    		
    	    		twttop[1][kk]=twtnext;
    	        	twtprevious = twtnext;
    	        	ktypeAboveRes[1][kk]= itypeAboveRes[ii];	
    	        	ktypeAboveFluid[1][kk]= 3;
    	        	
    	        	for(int icontact = nb_contacts-1 ; icontact >= 0 ; icontact--) {
    	        		if (twttop[1][kk] <= zcontact[icontact]) ktypeAboveFluid[1][kk]= icontact+1;   	        			
    	        	}
    	        	
    	        	kk = kk +1;
    	        	ii = ii +1;
    	        	
    	        	if (( kk>0 ) && (kk < nb_interfaces+nb_contacts)) {
    	        		ktypeAboveRes[1][kk] = itypeAboveRes[ii];
    	        	}
    	        	
    	    	}
    	    	// exit from while loop
    	    	knb_interf[1] = kk;
    	    		
    	    	ktypeCoucheAbove[1][0]= 0 ; // overburden
    	    	for(int k = 1 ; k < knb_interf[1] ; k++) {
    	    		if ( ktypeAboveRes[1][k] > 0) {
    	    			ktypeCoucheAbove[1][k]= ktypeAboveFluid[1][k];
    	    		} else {
    	    			ktypeCoucheAbove[1][k]= 0;
    	    		}
    	    	}
    	    		
    	    	depthprevious=0;
    	    	depth = twttop[1][0];
    	    	veloc= typeCouches[ktypeCoucheAbove[1][0]].getVp();
    	    	twt = 2*(depth-depthprevious)/veloc ;
    	    	depthprevious = depth;
    	    	twttop[1][0] = twt;
    	    		
    	    	for(int k = 1 ; k < knb_interf[1] ; k++) {
    	    		depth = twttop[1][k];
    	    		veloc= typeCouches[ktypeCoucheAbove[1][k]].getVp();
    	    		twt += 2*(depth-depthprevious)/veloc;
    	    		depthprevious = depth;
    	    		twttop[1][k] = twt;
    	    	}
    	    	
    	    	tCon[jcontact][jj]=twt ; //twttop[1][knb_interf[1]-1];
    		}
    		
    	}

    	// For all traces, build associated models
    	for(int n = 1 ; n < ncrb ; n++) { // for all horizontal locations - traces
    		double twtprevious = 0;
    		int icontact = 0;
    		int ii = 0;
    		kk = 0;
    		itypeAboveRes[0]=-1; 
    		//System.out.println("trace "+n);
    		while( (ii < nb_interfaces) ) {
    			double twtnext=ztop[ii] + LM*Math.tan(Math.PI*dip/180d)*n/(ncrb-1);
    			if ( itypeAboveRes[ii] == 1 ) {
    				for( icontact = 0 ; icontact < nb_contacts ; icontact++) {
        				if ((twtnext-zcontact[icontact])*(twtprevious-zcontact[icontact])<0 ) { 
		        			twttop[n][kk] = zcontact[icontact];
		        			kContact[icontact][n]=kk;
		        			twtprevious = zcontact[icontact];
		        			ktypeAboveRes[n][kk]= 1;
		        			ktypeAboveFluid[n][kk]=icontact+1;
		        			kk = kk +1;
        				}	
        			}
        		}
        	
	        	twttop[n][kk]=twtnext; // here twttop is depth it will be turned into twt further
	        	twtprevious = twtnext;
	        	ktypeAboveRes[n][kk]= itypeAboveRes[ii];
	        	ktypeAboveFluid[n][kk]= 3; //2
	        	for(icontact = nb_contacts-1 ; icontact >= 0 ; icontact--) {
	        		
	        		if (twttop[n][kk] <= zcontact[icontact]) {
	        			ktypeAboveFluid[n][kk]= icontact+1;
	        		}
	        	}

	        	kk = kk +1;
	        	ii = ii +1;
	        	 
	        	if (( kk>0 ) && (kk < nb_interfaces+nb_contacts)) {
	        		ktypeAboveRes[n][kk] =itypeAboveRes[ii];
	        	}
	        	
	        	// alternating non-reservoir - reservoir
	        	
	    	} // end loop on all interfaces
	    	
    		knb_interf[n] = kk;
	    		
	    } // end loop on all traces
    	
    	// maps type of couche - fluid into itypeCouche index
    	for(int n = 0 ; n < ncrb ; n++) {
    		ktypeCoucheAbove[n][0]= 0 ; // overburden
    		for(int k = 1 ; k < knb_interf[n] ; k++) {
    			if ( ktypeAboveRes[n][k] > 0) {
    				ktypeCoucheAbove[n][k]= ktypeAboveFluid[n][k];
    			} else {
    				ktypeCoucheAbove[n][k]= 0;
    			}
    		}
    	}
    	
    	for(int n = 0 ; n < ncrb ; n++) {
    		for(int k = 0 ; k < knb_interf[n]-1 ; k++) {
    			ktypeCoucheBelow[n][k]=ktypeCoucheAbove[n][k+1];
    		}
    		ktypeCoucheBelow[n][knb_interf[n]-1]=4; // underburden 3
    	}
    	
    	// depth to time transform
    	for(int n = 0 ; n < ncrb ; n++) {
    		depthprevious=0;
    		depth = twttop[n][0];
    		veloc= typeCouches[ktypeCoucheAbove[n][0]].getVp();
    		twt = 2*(depth-depthprevious)/veloc ;
    		depthprevious = depth;
    		twttop[n][0] = twt;
    		
    		for(int k = 1 ; k < knb_interf[n] ; k++) {
	    		depth = twttop[n][k];
	    		veloc= typeCouches[(int) ktypeCoucheAbove[n][k]].getVp();
	    		twt += 2*(depth-depthprevious)/veloc;
	    		depthprevious = depth;
	    		twttop[n][k] = twt;
    		}
    	}
    	
    	ttop=new double [ncrb];
        tbase=new double [ncrb];
        
    	for(int n = 0 ; n < ncrb ; n++) {
    		ttop[n]=twttop[n][0];
    		tbase[n]=twttop[n][knb_interf[n]-1];
    	}
    	
    	int indexReflCon;
    	for(int icontact = 0 ; icontact < nb_contacts ; icontact++) {
    		for(int n = 0 ; n < ncrb ; n++) {
	    		indexReflCon = kContact[icontact][n];
	    		tcon[icontact][n]= 0.;
	    		if (indexReflCon >= 0)  {
	    			tcon[icontact][n]= twttop[n][indexReflCon];
	    		}
    		}
    	}
    }
    
    public void calculCourbes () {
    	constructModel();
    	calculReflectivities(this.refractionEffect);
  
        double y,tc,hre,him,omega;
        double Bufferf=1/freq;
        double Buffer5Hz=0.08;
        
        ttop=new double [ncrb];
        tbase=new double [ncrb];
        double[] DD=new double [ncrb];
        atop=new double [ncrb];
        abase=new double [ncrb];
       
        tc=TM/2; 
        
        for(int n = 1 ; n < ncrb ; n++) {
        	DD [n] = (double) n*(LM/(ncrb-1));	
        }
        
        /*** Convolution **/
        
        //timeshift = 1.2d/freq;
        float decalageY = 0f;
        for(int n = 0 ; n < ncrb ; n++) {    /** Pour chaque courbe **/
            
            //courbesPlus[n].reset();
            decalageY += 1f;
            courbes[n].reset();
            courbes[n].setOrigine((double) decalageY);
            courbes[n].addPoint((float) Tmin,decalageY);
            //courbesPlus[n].moveTo((float) Tmin,decalageY);
            // timeshift = 1.2d/freq;
            for(int i = 0 ; i < N/2 ; i++) {  /* ** pour chaque frequence **/
            	ttop[n]= twttop[n][0];
            	tbase[n]= twttop[n][knb_interf[n]-1];
            	
            	omega = (double)i*domega;
            	hre = 0;
            	him = 0;
           		for(int k = 0 ; k < knb_interf[n] ; k++) {
           			double twt_top = twttop[n][k];
           			int kAbove = ktypeCoucheAbove[n][k];
           			int kBelow = ktypeCoucheBelow[n][k];
           			double reflec = (double) R[kAbove][kBelow];
           			hre += reflec*Math.cos(-omega*(twt_top-timeshift));
           			him += reflec*Math.sin(-omega*(twt_top-timeshift));		
            	}
  
            	convolutionFourier[i][0] = hre*fourierRickDephase[i][0] - him*fourierRickDephase[i][1];
            	convolutionFourier[i][1] = him*fourierRickDephase[i][0] + hre*fourierRickDephase[i][1];
            		
            }
            
            FastFourierTransform.invfftReal(convolutionFourier,convolution);
            for (int i= 0 ; (i < N) && (X[i] <= TM) ; i++) {
                section[i][n]=convolution[i];
            }
                     	            	
            /*** pour chaque frequence extraction au temps precis des top / base **/
            atop[n]=0;
            abase[n]=0;
            
            for(int i = 1 ; i < N/2 ; i++) {  
            	omega = (double)i*domega; 	
            		
            	atop[n]+=convolutionFourier[i][0]*Math.cos(omega*(ttop[n])) -convolutionFourier[i][1]*Math.sin(omega*(ttop[n]));
            	abase[n]+=convolutionFourier[i][0]*Math.cos(omega*(tbase[n])) -convolutionFourier[i][1]*Math.sin(omega*(tbase[n]));
            	
            }
            
            /*** pour chaque frequence extraction au temps precis des contacts **/
            for(int j = 0 ; j< nb_contacts ; j++ ) {
            	acon[j][n]=0;
            	if( tcon[j][n] > 0 ) {
                	for(int i = 1 ; i < N/2 ; i++) {  
                		omega = (double)i*domega;
                   		acon[j][n]+=convolutionFourier[i][0]*Math.cos(omega*tcon[j][n])-convolutionFourier[i][1]*Math.sin(omega*tcon[j][n]);
                   	}
                }
            }	 	
        }
    }
   
    public void rasterCourbes() {
    	float decalageY = 0f;
    	double y;      
    	for(int n = 0 ; n < ncrb ; n++) {
    		decalageY += 1f;
    		courbes[n].reset();
            courbes[n].setOrigine((double) decalageY);
            courbes[n].addPoint((float) Tmin,decalageY);
            
            for (int i= 0 ; (i < N) && (X[i] <= TM) ; i++) {
            	y = section[i][n] ;
            	courbes[n].addPoint((float) X[i],decalageY+(gain*y));	
        	}         
            
        	courbes[n].addPoint((float)TM,decalageY);
    	}
    }
    
    public void createCourbeAmpFluid(Vector<GeneralPath> top, Vector<GeneralPath> base,
			GeneralPath contact0, GeneralPath contact1, GeneralPath ampNul ) {
	
		int nseg = top.size();
		int iseg, n;
		
		for (iseg = 0 ; iseg < nseg ; iseg++) {
		    top.elementAt(iseg).reset();
			base.elementAt(iseg).reset();
		}
		
		// amplitudes top
		iseg=0;
		n=0;
		double atopLast = atop[0]*1E-3;
		double ntopLast = 1d;
		
		while (iseg < nseg-1) {
			top.elementAt(iseg).moveTo(atopLast, ntopLast);
			
			while ((double) n < (double) ncrb * xCon[iseg][0]/LM) {		
				top.elementAt(iseg).lineTo(atop[n]*1E-3, (double) (n+1) );
				n = n+1;
			}
			atopLast = atop[n-1]*1E-3 ;
			ntopLast = (double) (n-1);
			iseg = iseg + 1;	
		}
		
		n = n+1;
		top.elementAt(iseg).moveTo(atopLast, ntopLast);
		while ( n < ncrb ) {
			top.elementAt(iseg).lineTo(atop[n]*1E-3, (double) (n+1) );
			n = n+1;
		}
		
		// amplitudes base
		iseg=0;
		n=0;
		double abaseLast = abase[0]*1E-3;
		double nbaseLast = 1d;
		
		while (iseg < nseg-1) {
			base.elementAt(iseg).moveTo(abaseLast, nbaseLast);
		
			while ((double) n < (double) ncrb * xCon[iseg][nb_interfaces-1]/LM) {		
				base.elementAt(iseg).lineTo(abase[n]*1E-3,(double) (n+1) );
				n = n+1;
			}
			iseg = iseg + 1;
			base.elementAt(iseg).moveTo(abase[n]*1E-3, (double) n);
			
		}
		
		iseg = nseg-1;
		n = n+1;
		while (n < ncrb) {
			base.elementAt(iseg).lineTo(abase[n]*1E-3,(double) (n+1) );
			n = n+1;
		}
		
		contact0.reset();
		int j = 0 ;
		while ( j < ncrb ) {
			while ( j < ncrb && tcon[0][j]==0.0 ) {
				j=j+1;
			}
			
			if (j >= ncrb) break;
			contact0.moveTo(acon[0][j]*1E-3, (double)j+ 0.1 );
			
			while ( j < ncrb && tcon[0][j]!=0.0 ) {
				contact0.lineTo(acon[0][j]*1E-3, (j+1));
				j=j+1;
			}
			
			if(j >= ncrb) break;
			j=j+1;
		}
	
		contact1.reset();
		j = 0 ;
		while ( j < ncrb ) {
			while ( j < ncrb && tcon[1][j]==0.0 ) {
				j=j+1;
			}
			
			if (j >= ncrb) break;
			contact1.moveTo(acon[1][j]*1E-3, (double)j+ 0.1 );
			while ( j < ncrb && tcon[1][j]!=0.0 ) {
				contact1.lineTo(acon[1][j]*1E-3, (j+1));
				j=j+1;
			}
		
			if(j >= ncrb) break;
			j=j+1;
		}
		
		ampNul.reset();
		ampNul.moveTo(0, 1);
		ampNul.lineTo(0, ncrb);
    }

    
    public void createCourbeAmpFluid(GeneralPath top, GeneralPath base,
    			GeneralPath contact0, GeneralPath contact1, GeneralPath ampNul ) {
    	
    	top.reset();
    	base.reset();
    	top.moveTo(atop[0]*1E-3, 1);
    	base.moveTo(abase[0]*1E-3, 1);
    	    
    	for (int n = 1 ; n <ncrb ; n++) {		    		
    		top.lineTo(atop[n]*1E-3, (n+1));
    		base.lineTo(abase[n]*1E-3, (n+1));
    	}
    	
    	contact0.reset();
    	int j = 0 ;
    	while ( j < ncrb ) {
    		while ( j < ncrb && tcon[0][j]==0.0 ) {
    			j=j+1;
    		}
    		
    		if(j >= ncrb) break;
    		contact0.moveTo(acon[0][j]*1E-3, (double)j+ 0.1 );
    		
    		while ( j < ncrb && tcon[0][j]!=0.0 ) {
	    		contact0.lineTo(acon[0][j]*1E-3, (j+1));
	    		j=j+1;
	    	}
    		
    		if(j >= ncrb) break;
    		j=j+1;
    	}

    	contact1.reset();
    	j = 0 ;
    	while ( j < ncrb ) {
    		while ( j < ncrb && tcon[1][j]==0.0 ) {
    			j=j+1;
    		}
    	
    		if(j >= ncrb) break;
    		contact1.moveTo(acon[1][j]*1E-3, (double)j+ 0.1 );
    	
    		while ( j < ncrb && tcon[1][j]!=0.0 ) {
    			contact1.lineTo(acon[1][j]*1E-3, (j+1));
    			j=j+1;
    		}
    	
    		if(j >= ncrb) break;
    		j=j+1;
    	}
		
		ampNul.reset();
		ampNul.moveTo(0, 1);
    	ampNul.lineTo(0, ncrb);
    }

    public void createCourbeTwtFluid(GeneralPath top, GeneralPath base,
    		GeneralPath contact0, GeneralPath contact1, GeneralPath ampNul ) {
       	
    	contact0.reset();
    	contact1.reset();
    	top.reset();
    	base.reset();
    	top.moveTo(ttop[0],atop[0]*1E-3);
    	base.moveTo(tbase[0], abase[0]*1E-3);
    	
    	for (int n = 1 ; n <ncrb-1 ; n++) {
    		top.lineTo(ttop[n],atop[n]*1E-3);
    		base.lineTo(tbase[n],abase[n]*1E-3);
    	}
    	
    	contact0.reset();
    	int j = 0 ;
    	while ( j < ncrb ) {
	    	while ( j < ncrb && tcon[0][j]==0.0 ) {
	    		j=j+1;
    		}
    	
	    	if(j >= ncrb) break;
	    	contact0.moveTo(tcon[0][j],acon[0][j]*1E-3);
	    	while ( j < ncrb && tcon[0][j]!=0.0 ) {
	    		contact0.lineTo(tcon[0][j], acon[0][j]*1E-3);
	    		j=j+1;
    		}
    	
	    	if(j >= ncrb) break;
    		j=j+1;
    	}
    	
    	contact1.reset();
    	j = 0 ;
    	while ( j < ncrb ) {
	    	while ( j < ncrb && tcon[1][j]==0.0 ) {
	    		j=j+1;
    		}
    	
	    	if(j >= ncrb) break;
	    	contact1.moveTo(tcon[1][j],acon[1][j]*1E-3);
	    	while ( j < ncrb && tcon[1][j]!=0.0 ) {
	    		contact1.lineTo(tcon[1][j], acon[1][j]*1E-3);
	    		j=j+1;
    		}
    	
	    	if(j >= ncrb) break;
    		j=j+1;
    	}
    	
    	ampNul.reset();
    	ampNul.moveTo(0, 0.0);
    	ampNul.lineTo(TM,0.0);
    }
    
    private Point pointTransf(double x , double y) {
	    Point2D.Double p = (Point2D.Double)transf.transform( new Point2D.Double(x,y),null);
	    return new Point((int)p.getX(),(int) p.getY());
    }

    public void dessin() {
	    dessinCourbes = true;
	    //repaint();
    }
    
    public double getAmpMaxAbs() {
    	double MaxAbs=0;
    	for(int n = 0 ; n < ncrb ; n++) {
    		if(Math.abs(atop[n])>MaxAbs) {
    			MaxAbs=Math.abs(atop[n]) ;
    		}
    		if(Math.abs(abase[n])>MaxAbs) {
    			MaxAbs=Math.abs(abase[n]) ;
    		}
    		for(int j = 0 ; j < nb_contacts ; j++) {
    			if(Math.abs(this.acon[j][n])>MaxAbs) {
    				MaxAbs=Math.abs(this.acon[j][n]) ;
    			}
    		}
    	}
    	return MaxAbs;   	
    }
    
    public void paintComponent(Graphics g) {
    	double[] pas=new double [ncrb];
    	Point p = new Point();
	   	
    	if (firstTime) {
	        Dimension dim = getSize();
	        double wd = dim.width; // getWidth();
	        double hd = dim.height;  // getHeight();
	     
	        biAxes = (BufferedImage)createImage(dim.width,dim.height);
	        biTot  = (BufferedImage)createImage(dim.width,dim.height);
	        gAxes = (Graphics2D) biAxes.createGraphics();
	        gTot = (Graphics2D) biTot.createGraphics();
	        gAxes.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        gAxes.setFont(style);
	        gAxes.setStroke(new BasicStroke(1f));
	        gTot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        gTot.setStroke(new BasicStroke(1f));
	        gTot.setFont(style);
	        gTot.setPaint(Color.black);
	
	        double factx = (wd-(double)(marge[0]+marge[2]))/dx ;
	        double facty = (hd-(double)(marge[1]+marge[3]))/TM;
	        transf.setTransform(0d,facty,factx,0d,(double)(marge[0])-factx*LMmin ,(double)(marge[1])-facty*Tmin);
	        Point p1,p2,tc2,tc1;
	        int wp,hp;
	        p = pointTransf(TM,0d);
	        p2 = pointTransf(Tmin,0d);
	        wp = p.x;
	        hp = p.y;
	 
	        gAxes.drawLine(p2.x,p2.y,wp,hp+tailleFleche);
	        gAxes.drawLine(wp,hp+tailleFleche,wp+tailleFleche/2,hp);
	        gAxes.drawLine(wp,hp+tailleFleche,wp-tailleFleche/2,hp);
	        gAxes.drawString(Xlabel,wp-5,hp+tailleFleche+8);
	        
	        p = pointTransf(0d,(double) dx);
	        p2 = pointTransf(0d,LMmin);
	        wp = p.x;
	        hp = p.y;
	  
	        gAxes.drawLine(p2.x,p2.y,wp+tailleFleche,hp);
	        gAxes.drawLine(wp+tailleFleche,hp,wp,hp-tailleFleche/2);
	        gAxes.drawLine(wp+tailleFleche,hp,wp,hp+tailleFleche/2);
	        gAxes.drawString(Ylabel,wp+tailleFleche+2,hp+2);
	        gAxes.drawString(" ",wp+tailleFleche+25,hp+12);
	        
	        firstTime = false;
	   	}
    	
    	Dimension dim2 = this.getSize();
        double wd2 = dim2.getWidth();
        double hd2 = dim2.getHeight();
    	
        gTot.drawImage(biAxes,0,0,this);

        if (echelleModif) {
	        wgc=(int)(wd2-marge[0]-marge[2]);
	        double factx2 = (wd2-(double)(marge[0]+marge[2]))/dx;
	        double facty2 = (hd2-(double)(marge[1]+marge[3]))/TM;
	        transf.setTransform(0d,facty2,factx2,0d,(double)(marge[0])-factx2*LMmin ,(double)(marge[1])-facty2*Tmin);
	        echelleModif = false;   
        }
        /** draws reservoir layers with contacts **/
        Point  tc0, tc1, tc2, tc3;
        for (int jj = 0 ; jj < nb_sands ; jj ++ ) {
        	
        	int jcontact = 0;  	
	
	        tc0= pointTransf(twttop[0][2*jj],1d);
	        tc1= pointTransf(tCon[jcontact][2*jj],xCon[jcontact][2*jj]*(dx)/LM); 
	        tc2= pointTransf(tCon[jcontact][2*jj+1],xCon[jcontact][2*jj+1]*(dx)/LM);
	        tc3=pointTransf(twttop[0][2*jj+1],1d);
	        absF1[0]=tc0.x;
	        absF1[1]=tc1.x;
	        absF1[2]=tc2.x;
	        absF1[3]=tc3.x;
	        ordF1[0]=tc0.y;
	        ordF1[1]=tc1.y;
	        ordF1[2]=tc2.y;
	        ordF1[3]=tc3.y;
	    	Color colorF1=new Color(cF1.getRed(),cF1.getGreen(),cF1.getBlue(),(int)transF1 );
	    	gTot.setPaint(colorF1);
	    	gTot.fillPolygon(absF1, ordF1, 4);
	    	
	    	while (jcontact < nb_contacts-1) {
		    	tc0= pointTransf(tCon[jcontact][2*jj],xCon[jcontact][2*jj]*(dx)/LM);
		        tc1= pointTransf(tCon[jcontact+1][2*jj],xCon[jcontact+1][2*jj]*(dx)/LM);
		        tc2= pointTransf(tCon[jcontact+1][2*jj+1],xCon[jcontact+1][2*jj+1]*(dx)/LM);
		        tc3=pointTransf(tCon[jcontact][2*jj+1],xCon[jcontact][2*jj+1]*(dx)/LM);
		        absF1[0]=tc0.x;
		        absF1[1]=tc1.x;
		        absF1[2]=tc2.x;
		        absF1[3]=tc3.x;
		        ordF1[0]=tc0.y;
		        ordF1[1]=tc1.y;
		        ordF1[2]=tc2.y;
		        ordF1[3]=tc3.y;
		        Color colorF2=new Color(cF2.getRed(),cF2.getGreen(),cF2.getBlue(),(int)transF2 );
		    	gTot.setPaint(colorF2);	
		    	gTot.fillPolygon(absF1, ordF1, 4);
		    	jcontact +=1;
	    	}
	    	
	    	tc0= pointTransf(tCon[nb_contacts-1][2*jj],xCon[nb_contacts-1][2*jj]*(dx)/LM);
	        tc1= pointTransf(twttop[ncrb-1][2*jj],dx);
	        tc2= pointTransf(twttop[ncrb-1][2*jj+1],dx);
	        tc3=pointTransf(tCon[nb_contacts-1][2*jj+1],xCon[nb_contacts-1][2*jj+1]*(dx)/LM);
	        
	        absF1[0]=tc0.x;
	        absF1[1]=tc1.x;
	        absF1[2]=tc2.x;
	        absF1[3]=tc3.x;
	        ordF1[0]=tc0.y;
	        ordF1[1]=tc1.y;
	        ordF1[2]=tc2.y;
	        ordF1[3]=tc3.y;
	        Color colorF2=new Color(cF1.getRed(),cF2.getGreen(),cF2.getBlue(),(int)transF1 );
	    	gTot.setPaint(colorF2);	
	    	gTot.fillPolygon(absF1, ordF1, 4);
    	
        }
        
        gTot.setPaint(Color.black);
        
        /** legende axe X **/
        
        for (int val = 0 ; val <= (int)(TM*1E3) ; val += 50) {
	        gTot.drawLine(p.x-2,p.y,p.x+2,p.y);
	        gTot.drawString(""+val,p.x-20,p.y+4);
        }
   
        /** legende axe Y **/
      
        PosiTrace =new double [ncrb];
        for (int i = 0 ; i < dx ; i+=1) {
        	PosiTrace[i] = (double) i*(LM/(ncrb-1));
        	Posi=PosiTrace[i];
        }
        
        PosiPixel =new Point [dx];
        for (int i = 0 ; i < dx ; i+=1) {
        	PosiPixel[i] = pointTransf(0d,(double) (i+1));
        }
        
        test0=PosiPixel[0].x-5;
        test1=PosiPixel[20].x-5; //  traiter ncrb < 20
      
    	Graphics2D g2 = (Graphics2D) g;
    	
    	/*Scale drawing*/
    	/*Axe X*/
    	
    	gTot.setPaint(Color.black);
    	if (test1>288 && Posi>30) { /* pour ncrb=100 l'�quivalent en pixel*/
    		test1=PosiPixel[5].x-5;  
    		for(int i = 0 ; i <ncrb ; i+=5) {
    			gTot.drawString(""+(int)Math.round(PosiTrace[i]/10) *10,PosiPixel[i].x-5,PosiPixel[i].y-7);      
    	    }
    	    gTot.drawString(""+0+" m",horizScaleX,(int)(horizScaleY+25));
          	gTot.drawString(""+(int)Math.round(PosiTrace[5]/10) *10+" m",horizScaleX+(test1-test0),(int)(horizScaleY+25));
        } else if (test1<=288 && Posi>30) {
        	test1=PosiPixel[20].x-5;
    		for(int i = 0 ; i <ncrb ; i+=20) {
  	            gTot.drawString(""+(int)Math.round(PosiTrace[i]/10) *10,PosiPixel[i].x-5,PosiPixel[i].y-7);
  	        }
    		gTot.drawString(""+0+" m",horizScaleX,(int)(horizScaleY+25));
    		gTot.drawString(""+(int)Math.round(PosiTrace[20]/10) *10+" m",horizScaleX+(test1-test0),(int)(horizScaleY+25));
    	} else {
    		for(int i = 0 ; i <ncrb ; i+=20) {
    			test1=PosiPixel[20].x-5;
    	        gTot.drawString(""+(int)PosiTrace[i],PosiPixel[i].x-5,PosiPixel[i].y-7); 
    	    }
    		gTot.drawString(""+0+" m",horizScaleX,(int)(horizScaleY+25));
      		gTot.drawString(""+(int)PosiTrace[20]+" m",horizScaleX+(test1-test0),(int)(horizScaleY+25));
    	}
    	  
    	/*Axe Y*/
    	for (int val = 0 ; val <= (int)(50) ; val += 50) {
            gTot.drawString(""+val+" ms",vertscaleX-20,(int)(vertscaleY+val*1.6));
        }
 
    	for (int i = 0 ; i < dx ; i++) {
    		Courbe courbe = (Courbe) vectCourbes.elementAt(i);
    		TraceSismique c = (TraceSismique) courbe;
    		c.setColorSet(indexOfcolorSet);
    		c.dessinerArea(gTot,transf);
    	}
	
    	for (int i = 0 ; i < dx ; i++) {
    		Courbe courbe = (Courbe) vectCourbes.elementAt(i);
    		TraceSismique c = (TraceSismique) courbe;
    		c.setColorSet(indexOfcolorSet);
    		c.dessinerWiggle(gTot,transf);
    	}
    	
    	gTot.clearRect(vertscaleX+20, vertscaleY, 100, 80);
    	gTot.drawString( "Thickness = " + Integer.toString((int)(getThickness()))+" m"
    			, vertscaleX+22, (int)(vertscaleY+14));
    	gTot.drawString( "Dip = " + Integer.toString((int)(getDip()))+" deg"
    			, vertscaleX+22, (int)(vertscaleY+30));//15*1.6
    	gTot.drawString( "Gain = " + Integer.toString((int)(getGain())*100)+" %"
    			, vertscaleX+22, (int)(vertscaleY+46));//25*1.6
      	gTot.drawString( "Phase = " + Integer.toString((int)(getPhase()))+" deg"
     			, vertscaleX+22, (int)(vertscaleY+62));//35*1.6
    	gTot.drawString( "Incidence = " + Integer.toString((int)(getAngleIncident()))+"deg"
    			, vertscaleX+22, (int)(vertscaleY+76));//45*1.6
    	gTot.setColor(new Color (194, 194, 194));
    	gTot.fillRect(horizScaleX, horizScaleY,(test1-test0), 10);
    	gTot.fillRect(vertscaleX, vertscaleY, 10, 69);
    	gTot.setColor(Color.black);
    	gTot.fillRect(vertscaleX, vertscaleY, 10, 34);
    	gTot.fillRect(horizScaleX, horizScaleY, (test1-test0)/2, 10);
    	gTot.drawRect(vertscaleX, vertscaleY, 10, 69);
    	gTot.drawRect(horizScaleX, horizScaleY,(test1-test0), 10);
    	
    	g2.drawImage(biTot,0,0,this);
    }

	public Model getModel() {
		return this.model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public double getFrequence() {
		return freq;
	}

}



