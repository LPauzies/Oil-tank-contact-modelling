package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import src.colorpicker.ColorPickerButton;
import src.debugger.DebugLog;
import src.graph.Graphique;
import src.graph.GraphiqueWavelet;
import src.graph.tracesismique.TraceSismique;
import src.spinner.CustomSpinner;
import src.typecouche.TypeCouche;

public class ContactModellingApplet extends JApplet {

	/* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	/*************** Attributes ********************/
	public static final Font style = new Font("SansSerif",Font.BOLD,12);
	public static final Font styleSmall = new Font("SansSerif",Font.PLAIN,10);
	public static final Font styleBord = new Font("Helvetica",Font.BOLD,14);
	public static final String user = System.getProperty("user.name");

	final Color couleurCourbe = new Color(6,140,10);
	public static final Cursor cursorWait = new Cursor(Cursor.WAIT_CURSOR);
	public static final int NB_SANDSMAX = 31;
	public static final int NB_CRBMAX = 80;
	public static final int NB_CONTACTSMAX = 2;
	public static final int NB_TYPES = 6;

	public final ImageIcon RunAleaOnceGreen = createImageIcon("images/colorpicker/RunAleaOnceGreen.JPG");
	public final ImageIcon RunAleaOnceGrey = createImageIcon("images/colorpicker/RunAleaOnceGrey.jpg");
	public final ImageIcon RunAleaOnce = createImageIcon("images/colorpicker/RunAleaOnce.jpg");
	public final ImageIcon RunAleaTwiceGreen = createImageIcon("images/colorpicker/RunAleaTwiceGreen.JPG");
	public final ImageIcon RunAleaTwiceGrey = createImageIcon("images/colorpicker/RunAleaTwiceGrey.jpg");
	public final ImageIcon RunAleaTwice = createImageIcon("images/colorpicker/RunAleaTwice.jpg");


	public final ImageIcon[] SeqIcons = {
			createImageIcon("images/sequence/Sequence1.JPG"),
			createImageIcon("images/sequence/Sequence2.JPG"),
			createImageIcon("images/sequence/Sequence0.JPG"),
			createImageIcon("images/sequence/Sequence3.JPG")
	};

	public final ImageIcon[] SeqIconsGrey = {
			createImageIcon("images/sequence/Sequence0grey.JPG"),
			createImageIcon("images/sequence/Sequence1grey.JPG"),
			createImageIcon("images/sequence/Sequence2grey.JPG"),
			createImageIcon("images/sequence/Sequence3grey.JPG")
	};

	public final ImageIcon[] RunAlea = {
			createImageIcon("images/colorpicker/RunAlea1.JPG"),
			createImageIcon("images/colorpicker/RunAlea2.JPG"),
			createImageIcon("images/colorpicker/RunAlea3.JPG"),
			createImageIcon("images/colorpicker/RunAlea4.JPG"),
			createImageIcon("images/colorpicker/RunAlea5.JPG"),
			createImageIcon("images/colorpicker/RunAlea6.JPG"),
			createImageIcon("images/colorpicker/RunAlea7.JPG"),
			createImageIcon("images/colorpicker/RunAlea8.JPG"),
			createImageIcon("images/colorpicker/RunAlea9.JPG"),
			createImageIcon("images/colorpicker/RunAlea10.JPG"),
			createImageIcon("images/colorpicker/RunAlea11.JPG"),
			createImageIcon("images/colorpicker/RunAlea12.JPG")		
	};

	GraphContact gc = new GraphContact();
	private Vector<TypeCouche> types = gc.getModel().generateTypeCouche();

	Boolean booleanRunAlea = false;

	JPanel panelMain = new JPanel();
	Dimension dim = panelMain.getSize();

	/*** ******************Left********************* **/
	JPanel panelGauche = new JPanel();
	JPanel panelLayers = new JPanel();
	JPanel panelGeom = new JPanel();
	JPanel panelPub = new JPanel();

	JPanel panelLegende = new JPanel(new GridLayout(1, 5));
	JLabel labelname = new JLabel("Name", JLabel.CENTER);
	JLabel labelcolor = new JLabel("Color", JLabel.CENTER);
	JLabel labelRho = new JLabel("<html>&#961; (g/cc)</html>", JLabel.CENTER);
	JLabel labelVp = new JLabel("Vp(m/s)", JLabel.CENTER);
	JLabel labelVs = new JLabel("Vs(m/s)", JLabel.CENTER);
	JLabel labelPr = new JLabel("Poisson", JLabel.CENTER);
	JLabel labelTransparence = new JLabel("Transparency", JLabel.CENTER);

	JPanel panelLayerTop = new JPanel();
	JPanel panelLayerBase = new JPanel();

	JLabel labelThickness = new JLabel("Thickness (m):");
	JLabel labelDip = new JLabel("Dip (deg):");
	JLabel labelN2G = new JLabel("Net:Gross");
	JSlider net2gross = new JSlider(JSlider.HORIZONTAL,10,100,100);
	JLabel labelNbsands = new JLabel("# Reservoirs");
	JSlider nbsands = new JSlider(JSlider.VERTICAL,1,NB_SANDSMAX,1);
	JLabel labelGOC = new JLabel("GOC");
	JLabel labelGOCv = new JLabel("-0m");
	JSlider GOContact = new JSlider(JSlider.VERTICAL,-50,0,0);
	JLabel labelOWC = new JLabel("OWC");
	JLabel labelOWCv = new JLabel("+0m");
	JSlider OWContact = new JSlider(JSlider.VERTICAL,0,50,0);

	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
	final DateFormat format = DateFormat.getInstance();
	Calendar cal = Calendar.getInstance();
	String date = new String(dateFormat.format(cal.getTime()));
	String hour = new String(hourFormat.format(cal.getTime()));

	JLabel labelUser = new JLabel(
			"<html><font size='3'><center>"
					+ user  + "<br>"
					+ date + "<br>" + hour +"</center> <br><br></font> "
					+ "</html>");

	JLabel labelInformationAmp = new JLabel(
			"<html><huge> "	
					+ "<font color='red'><font size='4'> <i>Top</i></font>"+ "<br>"
					+ "<font color='rgb(255,0,255)'><font size='4'> <i>Contact</i></font>"+ "<br>"
					+"<font color='blue'><font size='4'> <i>Base</i></font>"+ "<br>"				
					+ "</huge></html>");
	/*** ******************Right********************* **/
	JPanel panelDroite = new JPanel();

	JPanel panelLeg = new JPanel();

	JPanel panelWindow = new JPanel();
	JPanel panelWavelet = new JPanel();
	JPanel wavePhasePanel= new JPanel();

	JPanel panelTopRock = new JPanel();
	JPanel panelFluid1 = new JPanel(); //Seal
	JPanel panelFluid2 = new JPanel(); //HC Sand
	JPanel panelBaseRock = new JPanel();

	JPanel panelSaisie = new JPanel();
	JPanel panelBoutons = new JPanel();

	JPanel panelTopRight = new JPanel();
	JPanel panelGraphContact = new JPanel();
	JPanel panelParams = new JPanel();
	JPanel panelGraphAmpTwt =new JPanel();

	JPanel panelBuffer = new JPanel();

	JLabel labelInformation = new JLabel(
			"<html>"
					+ "<u><font size='14'>User:</font></font></u>   "  + user  + "<br>"
					+ "<u>Date:</u>   "  + date + "<br>"
					+ "<br>"
					+ "</html>");

	JLabel labelFlatSize = new JLabel(
			"<html>Maximum and Minimum Ratios to control the length of the contact relative to that of the overall model"
					+ "</html>");

	/**RayTracing**/
	private JLabel labelRayTracing = new JLabel("Reflectivity proxy :");
	private JPanel panelOnOff = new JPanel();
	private JPanel panelAngleIncidence = new JPanel();
	private JRadioButton refractionEffectOn = new JRadioButton("Zoeppritz(w/o RT)");
	private JRadioButton refractionEffectOff = new JRadioButton("Aki-Richards");
	private ButtonGroup groupRefractionEffect = new ButtonGroup();
	private boolean refractionEffect = false;
	/**Reflection**/
	private JPanel panelOnOffReflection = new JPanel();
	private JLabel labelReflection = new JLabel("Reflection approximation :");
	private ButtonGroup groupReflection = new ButtonGroup();
	private JRadioButton reflectionApproxOn  = new JRadioButton("Aki-Richards");
	private JRadioButton reflectionApproxOff = new JRadioButton("Zoeppritz");

	private JTabbedPane tabParams = new JTabbedPane();
	private JPanel panelScale = new JPanel();
	private JPanel panelWave = new JPanel();
	private JPanel panelRayTracing = new JPanel();
	private JPanel panelReflection = new JPanel();
	private JPanel panelGraphSet = new JPanel();
	private JPanel panelContacts = new JPanel();

	JTextField nomCoucheTopRock = new JTextField("Seal",6);
	JTextField nomCoucheFluid1 = new JTextField("HC sand",6);
	JTextField nomCoucheFluid2 = new JTextField("Brine Sand",6);
	JTextField nomCoucheBaseRock = new JTextField("Seal",6);

	ColorPickerButton boutonCouleur1 = new ColorPickerButton(Color.RED);
	ColorPickerButton boutonCouleur2 = new ColorPickerButton(Color.BLUE);

	CustomSpinner rho0 = new CustomSpinner(2.55d,0.1d,3d,0.05d,2); //seal top
	CustomSpinner rho1 = new CustomSpinner(2d   ,0.1d,3d,0.05d,2); //HC sand
	CustomSpinner rho2 = new CustomSpinner(2.25d,0.1d,3d,0.05d,2); //Brine sand
	CustomSpinner rho3 = new CustomSpinner(2.55d,0.1d,3d,0.05d,2); //seal bottom

	CustomSpinner vp0 = new CustomSpinner(3200d,100d,6000d,100d,0);
	CustomSpinner vp1 = new CustomSpinner(2200d,100d,6000d,100d,0);
	CustomSpinner vp2 = new CustomSpinner(2800d,100d,6000d,100d,0);
	CustomSpinner vp3 = new CustomSpinner(3200d,100d,6000d,100d,0);

	CustomSpinner vs0 = new CustomSpinner(1600d,100d,6000d,100d,0);
	CustomSpinner vs1 = new CustomSpinner(1400d,100d,6000d,100d,0);
	CustomSpinner vs2 = new CustomSpinner(1500d,100d,6000d,100d,0);
	CustomSpinner vs3 = new CustomSpinner(1600d,100d,6000d,100d,0);

	CustomSpinner pr0 = new CustomSpinner(0.33d,0.0d,0.5d,0.01d,3);
	CustomSpinner pr1 = new CustomSpinner(0.33d,0.0d,0.5d,0.01d,3);
	CustomSpinner pr2 = new CustomSpinner(0.33d,0.0d,0.5d,0.01d,3);
	CustomSpinner pr3 = new CustomSpinner(0.33d,0.0d,0.5d,0.01d,3);

	/**panelWave**/
	JSlider frequence = new JSlider(JSlider.HORIZONTAL,5,150,100);
	CustomSpinner phase = new CustomSpinner(0d,-180d,180d,5d,0);
	CustomSpinner angleIncident = new CustomSpinner(0d,0d,90d,5d,0);
	JSlider dip = new JSlider(JSlider.VERTICAL,0,45,15);
	JSlider thickness = new JSlider(JSlider.HORIZONTAL,0,100,20);
	CustomSpinner NBtrace = new CustomSpinner(80d,30d,(double) NB_CRBMAX,10d,0);

	JLabel labelAngleIncident = new JLabel("Incidence angle (deg)");

	CustomSpinner tmax = new CustomSpinner(500d,50d,1000d,50d,0);

	String [] gains = {"25 %","50 %","100 %", "150 %","200 %","300 %","400 %","500 %","800 %","1000 %","1250 %","1500 %","2000 %"};
	JComboBox<String> choixgain = new JComboBox<String>(gains);

	String [] MinRatio = {"10 %","20 %","30 %","40 %"};
	JComboBox<String> choixMinRatio = new JComboBox<String>(MinRatio);
	String [] MaxRatio = {"60 %","70 %","80 %","90 %"};
	JComboBox<String> choixMaxRatio = new JComboBox<String>(MaxRatio);

	String [] Screen = {"Big screen","Medium Screen","Small Screen"};
	JComboBox<String> choixSizeScreen = new JComboBox<String>(Screen);

	String [] transparence = {"0 %","10 %","20 %","30 %","50 %","80 %","100 %"};
	JComboBox<String> choixtTransparenceF1 = new JComboBox<String>(transparence);
	JComboBox<String> choixtTransparenceF2 = new JComboBox<String>(transparence);

	JButton resetValues = new JButton("Reset");
	JButton runAleaOnce = new JButton(RunAleaOnceGrey);
	JButton runAlea = new JButton(RunAleaTwiceGrey);
	JButton Sequence = new JButton(SeqIcons[3]);
	public int iSeq = 3;

	JLabel labelTopRock = new JLabel("Overburden",JLabel.CENTER);
	JLabel labelBaseRock = new JLabel("Underburden",JLabel.CENTER);

	JLabel labelFrequence = new JLabel("Frequency (Hz)");
	JLabel labelPhase = new JLabel("Phase (deg)");
	JLabel labelAngle = new JLabel("Incidence angle (deg)");

	JLabel labelTmax = new JLabel("Window size (ms)");
	JLabel labelPendage = new JLabel("Dip (deg)");
	JLabel labelGain = new JLabel("Gain");
	JLabel labelNBtrace = new JLabel("Number of traces");
	JLabel labelMinRatio = new JLabel("Minimum Ratio");
	JLabel labelMaxRatio = new JLabel("Maximum Ratio");
	JLabel labelScreenSize = new JLabel("Screen size");

	private double AmpTopMax;
	private double AmpBaseMax;

	/* GRAPH CONTACT MANAGER */
	public int heightGc, widthGc;
	private GraphiqueWavelet graphWavelet = new GraphiqueWavelet(-50d,50d,-10d,10d);
	private TraceSismique courbeWavelet = new TraceSismique();

	Graphique gampZ = new Graphique(); 
	Graphique gampTWT = new Graphique(); 

	private Vector<GeneralPath> courbeTop = new Vector<GeneralPath>(NB_CONTACTSMAX+1);
	private Vector<GeneralPath> courbeBase = new Vector<GeneralPath>(NB_CONTACTSMAX+1);
	GeneralPath courbeAmpCont0 = new GeneralPath();
	GeneralPath courbeAmpCont1 = new GeneralPath();
	GeneralPath courbeAmpNul = new GeneralPath();

	GeneralPath courbeTwtTop= new GeneralPath();
	GeneralPath courbeTwtCont0= new GeneralPath();
	GeneralPath courbeTwtCont1= new GeneralPath();
	GeneralPath courbeTwtBase= new GeneralPath();
	GeneralPath courbeAmpTwtNul = new GeneralPath();

	GeneralPath limF1 = new GeneralPath();
	GeneralPath limF2 = new GeneralPath();

	GeneralPath limF1F2= new GeneralPath();

	Line2D.Double horiz1 = new Line2D.Double();

	GeneralPath courbeEpaisseur = new GeneralPath();
	Line2D.Double diag = new Line2D.Double();   

	/******************** CONSTRUCTOR ********************/
	public ContactModellingApplet() {
		super();
		//Here vectorMapTCNameCustomSpinner contains all CustomSpinner to get data from it
	}

	/* A rationaliser */
	private void calculpr0() {
		pr0.setValeur(gc.poissonRatio(vp0.getValeur(),vs0.getValeur()));
	}

	private void calculpr1() {
		pr1.setValeur(gc.poissonRatio(vp1.getValeur(),vs1.getValeur()));
	}

	private void calculpr2() {
		pr2.setValeur(gc.poissonRatio(vp2.getValeur(),vs2.getValeur()));
	}

	private void calculpr3() {
		pr3.setValeur(gc.poissonRatio(vp3.getValeur(),vs3.getValeur()));
	}

	public double getVsOverVp0() {
		double pr = pr0.getValeur();
		double ratio = 0.5d*(1-2d*pr)/(1d-pr);
		return Math.sqrt(ratio);
	}

	public double getVsOverVp1() {
		double pr = pr1.getValeur();
		double ratio = 0.5d*(1-2d*pr)/(1d-pr);
		return Math.sqrt(ratio);
	}

	public double getVsOverVp2() {
		double pr = pr2.getValeur();
		double ratio = 0.5d*(1-2d*pr)/(1d-pr);
		return Math.sqrt(ratio);
	}

	public double getVsOverVp3() {
		double pr = pr3.getValeur();
		double ratio = 0.5d*(1-2d*pr)/(1d-pr);
		return Math.sqrt(ratio);
	}

	public double computeVsOverVp(double pr) {
		double ratio = 0.5d*(1-2d*pr)/(1d-pr);
		return Math.sqrt(ratio);
	}
	/*** **********Panels Size function with screen Size **************** **/
	/** Screen Size**/
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();  	

	private int width_window = (int)(0.95*dimension.getWidth());
	private int height_window = (int)(0.85*dimension.getHeight());

	/* Quart de fenêtre */
	int width_panelGauche = (int)(width_window*0.27);
	int height_panelGauche = height_window;

	int width_panelDroite = (int)(width_window*0.73);
	int height_panelDroite = height_window;

	int width_panelTopRight = width_panelDroite;
	int height_panelTopRight = (int)(height_panelDroite*0.2);

	int width_gampZ = (int)(width_panelDroite*0.75);
	int height_gampZ = height_panelTopRight - 20;	

	int width_panelGraphContact = (int)(width_panelDroite*0.75);
	int height_panelGraphContact =(int)(height_panelDroite*0.75);

	int width_gc = (int)(width_panelDroite*0.70);
	int height_gc = height_panelGraphContact - 20;

	int width_gampTwt = height_gampZ; 
	int height_gampTwt = height_panelGraphContact - 20;

	/**************** Reset ****************/

	public void reset() {
		thickness.setValue((int) gc.getThickness());
		dip.setValue((int) gc.getDip());
		frequence.setValue(150);
		NBtrace.reset();
		phase.reset();  
		rho0.reset();
		rho1.reset();
		rho2.reset();
		rho3.reset();
		vp0.reset();
		vp1.reset();
		vp2.reset();
		vp3.reset();
		vs0.reset();
		vs1.reset();
		vs2.reset();
		vs3.reset();
		pr0.reset();
		pr1.reset();
		pr2.reset();
		pr3.reset();
		resetTypeCouche();

		angleIncident.reset();

		choixgain.setSelectedIndex(4); 
		choixMinRatio.setSelectedIndex(2);
		choixMaxRatio.setSelectedIndex(2);  
		choixSizeScreen.setSelectedIndex(1);
		choixtTransparenceF1.setSelectedIndex(3);
		choixtTransparenceF2.setSelectedIndex(3);

		net2gross.setValue(100);
		nbsands.setValue(1);

		gc.getModel().ZContact[0]= gc.getModel().ZM/2;
		gc.getModel().ZContact[1]= gc.getModel().ZM/2;

		
		for (int i = 0; i < types.size(); i++) {
			gc.setVp(i,types.elementAt(i).getVp());
			gc.setVs(i,types.elementAt(i).getVs());
			gc.setPr(i,types.elementAt(i).getPr());
			gc.setRho(i,types.elementAt(i).getRho());
		}
		
		/* ORIGINAL
	    gc.setRho(0,rho0.getValeur());
	    gc.setRho(1,rho1.getValeur());
	    gc.setRho(2,rho2.getValeur());
	    gc.setRho(3,rho3.getValeur()); 
	    gc.setVp(0,vp0.getValeur());
	    gc.setVp(1,vp1.getValeur());
	    gc.setVp(2,vp2.getValeur());
	    gc.setVp(3,vp3.getValeur());
	    gc.setVs(0,vs0.getValeur());
	    gc.setVs(1,vs1.getValeur());
	    gc.setVs(2,vs2.getValeur());
	    gc.setVs(3,vs3.getValeur());
	    gc.setPr(0,pr0.getValeur());
	    gc.setPr(1,pr1.getValeur());
	    gc.setPr(2,pr2.getValeur());
	    gc.setPr(3,pr3.getValeur());
		 */

		gc.setValEchelle(80,height_gc-70,80,height_gc-160);
		gc.setFrequence((double) frequence.getValue());
		gc.setNBtrace(NBtrace.getValeur());
		gc.setThickness(thickness.getValue());
		gc.setDip((double) dip.getValue());
		gc.setNet2Gross(((double) net2gross.getValue())/ 100d);
		gc.setNBsands(nbsands.getValue());
		gc.setPhase(phase.getValeur());
		gc.setAngleIncident(angleIncident.getValeur());
		gc.setIndexOfcolorSet(1);
		gc.calculRicker();
		affichageOndelette();

		gc.setGain(getGain());
		gc.setMinRatio(getMinRatio());
		gc.setMaxRatio(getMaxRatio());
		gc.setTransF1(getTransF1());
		gc.setTransF2(getTransF2());

		gc.createNet2Gross(iSeq);
		gc.checkModeldimension();
		gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
		gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
		labelGOCv.setText(GOContact.getValue()+"m");
		labelOWCv.setText(OWContact.getValue()+"m");
		gc.calculCourbes();
		gc.rasterCourbes();
		gc.dessin();

		//GAMPZ manager
		gampZ.setX(0d,NBtrace.getValeur());
		AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
		AmpTopMax=-AmpBaseMax;
		gampZ.setY(AmpBaseMax, AmpTopMax);
		double pasEchelle = Math.max((Math.rint(gc.getAmpMaxAbs()*1E-2))/10d , 0.1);
		// System.out.println(" pasEchelle= "+Math.rint(gc.getAmpMaxAbs()*1E-2));
		gampZ.setPasEchelle(20d,pasEchelle);
		gampZ.setYlabel("Amplitude  " + (int) angleIncident.getValeur() + "deg");
		gampZ.setPositionLabel(Graphique.LABEL_AUTRE);
		gampZ.setMarge(40,20,50,30);
		gampZ.setPrecision(0,2);
		gampZ.clear();

		gc.createCourbeAmpFluid(courbeTop,courbeBase,courbeAmpCont0,courbeAmpCont1,courbeAmpNul);
		for (int iseg = 0; iseg < gc.nb_contacts+1 ; iseg += 1) {
			gampZ.addCourbe(courbeBase.elementAt(iseg),Color.blue);
			gampZ.addCourbe(courbeTop.elementAt(iseg),Color.red);
		}
		//gc.createCourbeAmpFluid(courbeAmpTop,courbeAmpBase,courbeAmpCont0,courbeAmpCont1,courbeAmpNul);
		//gampZ.addCourbe(courbeAmpTop,Color.red);
		//gampZ.addCourbe(courbeAmpBase,Color.blue);	
		gampZ.addCourbe(courbeAmpCont0,Color.magenta);
		gampZ.addCourbe(courbeAmpCont1,Color.green);
		gampZ.addCourbe(courbeAmpNul,Color.black);
		gampZ.repaint();

		//GAMPTWT manager
		gampTWT.setX(AmpTopMax,AmpBaseMax);
		gampTWT.setY(0d,gc.getTmax());
		gampTWT.setPasEchelle(pasEchelle,0.05d);
		gampTWT.setYlabel("TWT (s)");
		gampTWT.setPositionLabel(Graphique.LABEL_AUTRE);
		gampTWT.setMarge(30,20,20,30);
		//gampTWT.setMarge(40,20,50,30);
		gampTWT.setPrecision(1,2);
		gampTWT.clear();
		gc.createCourbeTwtFluid(courbeTwtTop,courbeTwtBase,courbeTwtCont0,courbeTwtCont1,courbeAmpTwtNul);
		gampTWT.addCourbe(courbeTwtTop,Color.red);
		gampTWT.addCourbe(courbeTwtBase,Color.blue);
		gampTWT.addCourbe(courbeTwtCont0,Color.magenta);
		gampTWT.addCourbe(courbeTwtCont1,Color.green);
		gampTWT.addCourbe(courbeAmpTwtNul,Color.black);
		gampTWT.repaint();  
	};



	public void init() {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					initInterface();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}        
		SwingUtilities.updateComponentTreeUI(this);
		reset();
	}

	public void gampZColor() {

		for (int iseg = 0; iseg < courbeTop.size() ; iseg++ ) {
			gampZ.addCourbe(courbeTop.elementAt(iseg),Color.red);
			gampZ.addCourbe(courbeBase.elementAt(iseg),Color.blue);
		}

		gampZ.addCourbe(limF1,Color.black);
		gampZ.addCourbe(limF2,Color.black);
		gampZ.repaint();
	}

	public void gampTWTColor() {
		gampTWT.addCourbe(courbeTwtTop,Color.red);
		gampTWT.addCourbe(courbeTwtBase,Color.blue);
		gampTWT.addCourbe(limF1F2,Color.black);
		gampTWT.repaint();
	}


	public void initInterface () {        

		/** Divers **/
		choixgain.setEditable(false);
		choixgain.setSelectedIndex(4);

		choixMinRatio.setEditable(false);
		choixMinRatio.setSelectedIndex(2);

		choixMaxRatio.setEditable(false);
		choixMaxRatio.setSelectedIndex(2);

		choixSizeScreen.setEditable(true);
		choixSizeScreen.setSelectedIndex(1);

		choixtTransparenceF1.setEditable(false);
		choixtTransparenceF1.setSelectedIndex(6);
		choixtTransparenceF2.setEditable(false);
		choixtTransparenceF2.setSelectedIndex(6);

		pr0.setForeground(Color.black);
		pr1.setForeground(Color.black);
		pr1.setBackground(Color.white);
		pr2.setForeground(Color.black);
		pr2.setBackground(Color.white);
		pr3.setForeground(Color.black);
		pr3.setBackground(Color.white);

		resetValues.setRequestFocusEnabled(false);
		runAleaOnce.setRequestFocusEnabled(false);
		runAlea.setRequestFocusEnabled(false);
		Sequence.setRequestFocusEnabled(false);

		labelTopRock .setForeground(Color.black);
		labelBaseRock .setForeground(Color.black);
		labelcolor .setForeground(Color.black);
		labelRho .setForeground(Color.black);
		labelVp .setForeground(Color.black);
		labelVs .setForeground(Color.black);
		labelPr .setForeground(Color.black);
		labelTransparence .setForeground(Color.black);

		nomCoucheTopRock.setFont(style);
		nomCoucheTopRock.setHorizontalAlignment(JTextField.LEFT);
		nomCoucheFluid1.setHorizontalAlignment(JTextField.LEFT);
		nomCoucheFluid1.setFont(style);
		nomCoucheFluid2.setHorizontalAlignment(JTextField.LEFT);
		nomCoucheFluid2.setFont(style);
		nomCoucheBaseRock.setHorizontalAlignment(JTextField.LEFT);
		nomCoucheBaseRock.setFont(style);

		groupRefractionEffect.add(refractionEffectOn);
		groupRefractionEffect.add(refractionEffectOff);
		refractionEffectOn.setActionCommand("on");
		refractionEffectOff.setActionCommand("off");
		refractionEffectOff.setSelected(true);
		refractionEffectOff.setRequestFocusEnabled(false);
		refractionEffectOn.setRequestFocusEnabled(false);

		groupReflection.add(reflectionApproxOn);
		groupReflection.add(reflectionApproxOff);
		reflectionApproxOn.setActionCommand("on");
		reflectionApproxOff.setActionCommand("off");
		reflectionApproxOff.setSelected(true);
		reflectionApproxOff.setRequestFocusEnabled(false);
		reflectionApproxOn.setRequestFocusEnabled(false);

		thickness.setFont(styleSmall);
		thickness.setMajorTickSpacing(20);
		thickness.setMinorTickSpacing(5);
		thickness.setSnapToTicks(false);
		thickness.setPaintTicks(true);
		thickness.setPaintLabels(true);
		labelThickness.setFont(styleSmall);
		labelThickness.setText("Thickness "+thickness.getValue()+" m");

		labelDip.setFont(styleSmall);
		dip.setFont(styleSmall);
		dip.setMajorTickSpacing(10);
		dip.setMinorTickSpacing(2);
		dip.setSnapToTicks(true);
		dip.setPaintTicks(true);
		dip.setPaintLabels(true);

		net2gross.setFont(styleSmall);
		net2gross.setMajorTickSpacing(20);
		net2gross.setMinorTickSpacing(10);
		net2gross.setSnapToTicks(true);
		//net2gross.setPaintTicks(true);
		//net2gross.setPaintLabels(true);
		labelN2G.setFont(styleSmall);
		labelN2G.setText("Net:Gross "+net2gross.getValue()+"%");

		labelNbsands.setFont(styleSmall);
		nbsands.setFont(styleSmall);
		nbsands.setMajorTickSpacing(10);
		nbsands.setMinorTickSpacing(1);
		nbsands.setSnapToTicks(true);
		nbsands.setPaintTicks(true);
		nbsands.setPaintLabels(true);
		nbsands.setInverted(true);

		frequence.setFont(style);
		frequence.setMajorTickSpacing(10);
		frequence.setMinorTickSpacing(5);
		frequence.setSnapToTicks(false);
		frequence.setPaintTicks(true);
		frequence.setPaintLabels(true);

		GOContact.setFont(style);	
		GOContact.setInverted(true);
		GOContact.setForeground(Color.red);
		OWContact.setFont(style);
		OWContact.setInverted(true);
		OWContact.setForeground(Color.blue);

		/*** ****************** Layout ****************** ***/
		panelMain.setLayout(new BoxLayout(panelMain,BoxLayout.LINE_AXIS));
		/**Left**/
		panelGauche.setLayout(new BoxLayout(panelGauche, BoxLayout.PAGE_AXIS));
		panelLayers.setLayout(new GridLayout(19,1));//9
		panelGeom.setLayout(null);

		//panelContacts.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
		panelContacts.setLayout(new BoxLayout(panelContacts,BoxLayout.PAGE_AXIS));

		panelLegende.setLayout(new FlowLayout(FlowLayout.LEFT,20,0));

		panelTopRock.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));

		panelFluid1.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
		panelFluid2.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));

		panelBaseRock.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));

		panelScale.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
		panelWindow.setLayout(null);
		panelWave.setLayout(null);
		panelAngleIncidence.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
		panelRayTracing.setLayout(new FlowLayout(FlowLayout.CENTER,10,15));
		panelOnOff.setLayout(new GridLayout(2,1));
		panelReflection.setLayout(new FlowLayout(FlowLayout.CENTER,10,15));
		panelGraphSet.setLayout(null);
		panelOnOffReflection.setLayout(new GridLayout(2,1));


		wavePhasePanel.setLayout(new BorderLayout());
		panelWindow.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		panelPub.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));

		/*************** Right ****************/
		panelDroite.setLayout (new BorderLayout());
		panelLeg.setLayout(new BoxLayout(panelLeg, BoxLayout.Y_AXIS));
		panelTopRight.setLayout(new FlowLayout(FlowLayout.LEFT,10,0));
		panelGraphContact.setLayout(new FlowLayout(FlowLayout.LEFT,10,0));
		panelGraphAmpTwt.setLayout(null);
		panelParams.setLayout(new FlowLayout(FlowLayout.LEFT,200,0));

		/***************** Borders ********************/
		panelGauche.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createRaisedBevelBorder(), 
						BorderFactory.createLoweredBevelBorder()
						)
				));
		panelGeom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Geometry "));
		panelLayers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()," TypeCouches "));

		/********************* panelGauche ***********************/
		panelGauche.setPreferredSize(new Dimension(width_panelGauche, height_panelGauche));
		panelGeom.setPreferredSize(new Dimension(200, 150)); 
		panelPub.setPreferredSize(new Dimension(200,120));

		labelname.setSize(25,26);
		labelcolor.setSize(25,26);
		labelRho.setSize(25,26);
		labelVp.setSize(25,26);
		labelVs.setSize(25,26);
		labelPr.setSize(25,26);
		labelTransparence.setSize(25,26);

		/********************* setSize *********************/
		nomCoucheTopRock.setSize(50,26);
		nomCoucheFluid1.setSize(50,26);
		nomCoucheFluid2.setSize(50,26);
		nomCoucheBaseRock.setSize(50,26);
		/*
	    rho0.setSize(25,26);
	    rho1.setSize(25,26);
	    rho2.setSize(25,26);
	    rho3.setSize(25,26);
	    vp0.setSize(25,26);
	    vp1.setSize(25,26);
	    vp2.setSize(25,26);
	    vp3.setSize(25,26);
	    vs0.setSize(25,26);
	    vs1.setSize(25,26);
	    vs2.setSize(25,26);
	    vs3.setSize(25,26);    
	    pr0.setSize(25,26); 
	    pr1.setSize(25,26); 
	    pr2.setSize(25,26);
	    pr3.setSize(25,26); 
		 */
		boutonCouleur1.setSize(20,26);
		boutonCouleur2.setSize(20,26);
		choixtTransparenceF1.setSize(25,26);
		choixtTransparenceF2.setSize(25,26);

		labelThickness.setSize(150,26);
		labelDip.setSize(120,26);
		thickness.setSize(200,40);
		dip.setSize(40,110);
		net2gross.setSize(180,30);//40
		labelN2G.setSize(100,26);
		nbsands.setSize(40,120);
		labelNbsands.setSize(100,26);

		labelGain.setSize(55,26);
		choixgain.setSize(65,26);
		labelFrequence.setSize(150,26);
		frequence.setSize(410,40);
		labelPhase.setSize(85,26); 
		phase.setSize(55,26);
		labelAngleIncident.setSize(125,26);
		angleIncident.setSize(55,26);

		labelNBtrace.setSize(120,26);
		NBtrace.setSize(65,26);
		labelMinRatio.setSize(120,26);
		choixMinRatio.setSize(65,26);
		labelMaxRatio.setSize(120,26);
		choixMaxRatio.setSize(65,26);

		labelScreenSize.setSize(120,26);
		choixSizeScreen.setSize(120,26);
		labelFlatSize.setSize(300,50);

		resetValues.setSize(70,25);
		runAleaOnce.setSize(60,25); //80
		runAlea.setSize(60,25);//80
		Sequence.setSize(40,80);

		/**setLocation**/
		labelThickness.setLocation(190, 5); //150,20
		thickness.setLocation(140, 30); //100,50
		labelDip.setLocation(10, 10); //20,20
		dip.setLocation(10, 30); //20,45

		resetValues.setLocation(148, 118);//105
		runAleaOnce.setLocation(225, 118);//105
		runAlea.setLocation(290, 118);//105
		Sequence.setLocation(80, 40);//80 60

		net2gross.setLocation(160,70);//140,160
		labelN2G.setLocation(180,90);//180,135
		nbsands.setLocation(370,25); //45
		labelNbsands.setLocation(350,5); //20

		labelNBtrace.setLocation(80,20);
		NBtrace.setLocation(180,20);
		labelMinRatio.setLocation(80,50);
		choixMinRatio.setLocation(180,50);
		labelMaxRatio.setLocation(80,80);
		choixMaxRatio.setLocation(180,80);

		labelScreenSize.setLocation(80,80);
		choixSizeScreen.setLocation(180,80); 
		labelFlatSize.setLocation(80, 120);

		labelGain.setLocation(20,5);
		choixgain.setLocation(140,5);
		labelFrequence.setLocation(170,30);//55
		frequence.setLocation(10,55);
		labelPhase.setLocation(260,5);
		phase.setLocation(380,5);
		labelAngleIncident.setLocation(20,70);
		angleIncident.setLocation(140,70);

		/**Graph_Wavelet**/
		graphWavelet.setPreferredSize(new Dimension(130,130));//150,150
		graphWavelet.setMarges(new Insets(5,30,30,5));
		graphWavelet.setYlabel("Wavelet x10");
		graphWavelet.setXlabel("Time (ms)");
		graphWavelet.setZoomEnabled(true);
		graphWavelet.setVisibleOfAxes(false,true);
		graphWavelet.setInverseAxes(true);
		graphWavelet.setReverseX(true);
		graphWavelet.addObjet(courbeWavelet);

		/********************* panelDroite ***********************/
		panelDroite.setPreferredSize(new Dimension(width_panelDroite, height_panelDroite));
		panelBuffer.setPreferredSize(new Dimension(width_panelDroite, 5));
		panelTopRight.setPreferredSize(new Dimension(width_panelTopRight,height_panelTopRight));
		gampZ.setPreferredSize(new Dimension(width_gampZ,height_gampZ));
		panelLeg.setPreferredSize(new Dimension(width_gampTwt,height_gampZ));

		panelGraphContact.setPreferredSize(new Dimension(width_panelGraphContact,height_panelGraphContact));
		gc.setPreferredSize(new Dimension(width_gc,height_gc));
		gampTWT.setPreferredSize(new Dimension(width_gampTwt,height_gampTwt));

		tabParams.setPreferredSize(new Dimension(200,150));
		panelOnOff.setPreferredSize(new Dimension(150,50));
		panelOnOffReflection.setPreferredSize(new Dimension(150,50));

		panelContacts.setSize(80,500);
		labelGOC.setSize(20,20);
		labelGOCv.setSize(20,20);
		GOContact.setSize(20,500);//gampTWT.getHeight());
		GOContact.setLocation(20,20);
		//GOContact.setBounds(gampTWT.getBounds());

		labelOWC.setSize(20,20);
		labelOWCv.setSize(20,20);
		OWContact.repaint(); 
		OWContact.setSize(20,500);//gampTWT.getHeight());
		OWContact.setLocation(20,20);
		//OWContact.setBounds(gampTWT.getBounds());

		OWContact.repaint();

		/********************* Mise_en_place *********************/

		/** Left **/
		panelLegende.add(labelname);
		panelLegende.add(labelRho);
		panelLegende.add(labelVp);
		panelLegende.add(labelVs);
		panelLegende.add(labelPr);
		panelLegende.add(labelTransparence);

		/* Left panel with changing values */
		panelTopRock.add(nomCoucheTopRock);
		panelTopRock.add(rho0);
		panelTopRock.add(vp0);
		panelTopRock.add(vs0);
		panelTopRock.add(pr0);

		panelFluid1.add(nomCoucheFluid1);
		panelFluid1.add(rho1);
		panelFluid1.add(vp1);
		panelFluid1.add(vs1);
		panelFluid1.add(pr1);
		panelFluid1.add(boutonCouleur1);
		panelFluid1.add(choixtTransparenceF1);

		panelFluid2.add(nomCoucheFluid2);
		panelFluid2.add(rho2);
		panelFluid2.add(vp2);
		panelFluid2.add(vs2);
		panelFluid2.add(pr2);
		panelFluid2.add(boutonCouleur2);
		panelFluid2.add(choixtTransparenceF2);

		panelBaseRock.add(nomCoucheBaseRock);
		panelBaseRock.add(rho3);
		panelBaseRock.add(vp3);
		panelBaseRock.add(vs3);
		panelBaseRock.add(pr3);

		panelGeom.add(labelThickness);
		panelGeom.add(thickness);
		panelGeom.add(labelDip);
		panelGeom.add(dip);
		panelGeom.add(Sequence);
		panelGeom.add(resetValues);
		panelGeom.add(runAleaOnce);
		panelGeom.add(runAlea);
		//panelGeom.add(new JSeparator(SwingConstants.VERTICAL));
		panelGeom.add(net2gross);
		panelGeom.add(labelN2G);
		panelGeom.add(nbsands);
		panelGeom.add(labelNbsands);

		Sequence.setVisible(false);
		Sequence.setEnabled(false);
		labelN2G.setVisible(false);
		net2gross.setVisible(false);
		net2gross.setEnabled(false);

		panelReflection.add(labelReflection);
		panelReflection.add(panelOnOffReflection);

		panelGraphSet.add(labelNBtrace);
		panelGraphSet.add(NBtrace);
		panelGraphSet.add(labelMinRatio);
		panelGraphSet.add(choixMinRatio);
		panelGraphSet.add(labelMaxRatio);
		panelGraphSet.add(choixMaxRatio);
		panelGraphSet.add(labelFlatSize);

		panelOnOffReflection.add(reflectionApproxOn);
		panelOnOffReflection.add(reflectionApproxOff);

		panelLayers.add(panelLegende);
		/* Add TypeCouche to the layer */
		buildTypeCouche(panelLayers);
		panelLayers.add(new JSeparator(SwingConstants.HORIZONTAL));
		/*
		ajouterTypeCouche(rockTopCouche, panelTopRockTest);
		ajouterTypeCouche(gasCouche, panelGas);
		ajouterTypeCouche(oilCouche, panelOil);
		ajouterTypeCouche(waterCouche, panelWater);
		//ajouterTypeCouche(otherCouche, panelOther);
		ajouterTypeCouche(rockBottomCouche, panelBaseRockTest);

		// MY PANE TEST
		panelLayers.add(new JSeparator(SwingConstants.HORIZONTAL));
		panelLayers.add(panelTopRockTest);
		panelLayers.add(new JSeparator(SwingConstants.HORIZONTAL));	
		panelLayers.add(panelGas);
		panelLayers.add(panelOil);
		panelLayers.add(panelWater);
		//panelLayers.add(panelOther);
		panelLayers.add(new JSeparator(SwingConstants.HORIZONTAL));	
		panelLayers.add(panelBaseRockTest);

		 */

		//PANE WORKING CURRENTLY
		/*
		panelLayers.add(new JLabel("!! Trucs qui marchent !!"));
		panelLayers.add(panelTopRock);
		panelLayers.add(new JSeparator(SwingConstants.HORIZONTAL));
		panelLayers.add(panelFluid1);
		panelLayers.add(panelFluid2);
		panelLayers.add(new JSeparator(SwingConstants.HORIZONTAL));
		//panelLayers.add(Box.createHorizontalStrut(10));
		panelLayers.add(panelBaseRock);
		*/

		panelWave.add(labelFrequence);
		panelWave.add(frequence);
		panelWave.add(labelPhase);
		panelWave.add(phase);
		//panelWave.add(labelAngleIncident);
		//panelWave.add(angleIncident);
		panelWave.add(labelGain);
		panelWave.add(choixgain);

		panelScale.add(Box.createHorizontalStrut(10));

		panelAngleIncidence.add(labelAngleIncident); // change
		panelAngleIncidence.add(angleIncident); 
		panelRayTracing.add(labelRayTracing);
		panelRayTracing.add(panelOnOff);
		//panelRayTracing.add(new JSeparator(SwingConstants.HORIZONTAL));
		panelRayTracing.add(panelAngleIncidence);

		panelOnOff.add(refractionEffectOn);
		panelOnOff.add(refractionEffectOff);

		tabParams.addTab("Wavelet",null,panelWave,"Wavelet parameters");
		tabParams.addTab("AVO param",null,panelRayTracing,"Set ray tracing");
		tabParams.addTab("Graph settings ",null,panelGraphSet,"Choose the reflection approximation");


		JLabel label_total = new JLabel(createImageIcon("images/logo_total.gif"));
		label_total.setBorder(BorderFactory.createRaisedBevelBorder());
		label_total.setSize(70,80);
		label_total.setLocation(90,10);
		label_total.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label_total.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://www.total.com/fr"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		JLabel label_club_amplitude = new JLabel(createImageIcon("images/logo_club_amplitude.jpg"));
		label_club_amplitude.setBorder(BorderFactory.createRaisedBevelBorder());
		label_club_amplitude.setSize(120,80);
		label_club_amplitude.setLocation(160,10);
		label_club_amplitude.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label_club_amplitude.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://wat.corp.local/sites/s45/fr-FR/Pages/Savoir-Faire/cmg/Club-Amplitude/Outils.aspx"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		panelPub.add(label_total);
		panelPub.add(Box.createHorizontalStrut(10));
		panelPub.add(label_club_amplitude);
		panelPub.add(Box.createHorizontalStrut(20));
		panelPub.add(graphWavelet);
		panelPub.add(Box.createHorizontalStrut(10));

		/** PANEL GAUCHE ADD COMPONENTS **/
		panelGauche.add(panelLayers);
		panelGauche.add(panelGeom);
		panelGauche.add(tabParams);
		panelGauche.add(panelPub);

		getContentPane().add(panelGauche);

		/** Right **/
		panelLeg.add(Box.createHorizontalStrut(10));
		panelLeg.add(labelUser);
		labelInformationAmp.setBorder(BorderFactory.createLoweredBevelBorder());
		panelLeg.add(labelInformationAmp);

		panelTopRight.add(gampZ);
		panelTopRight.add(panelLeg);
		panelGraphContact.add(gc);
		panelContacts.add(labelGOC);
		panelContacts.add(labelGOCv);
		panelContacts.add(GOContact);
		panelContacts.add(OWContact);
		panelContacts.add(labelOWCv);
		panelContacts.add(labelOWC);
		panelGraphContact.add(panelContacts);

		panelGraphContact.add(gampTWT);
		panelDroite.add(panelTopRight,BorderLayout.NORTH);
		panelDroite.add(panelGraphContact,BorderLayout.CENTER);
		panelDroite.add(panelBuffer,BorderLayout.SOUTH);

		getContentPane().add(panelDroite);

		/** Both **/

		panelMain.add(panelGauche);
		panelMain.add(Box.createHorizontalStrut(10));
		panelMain.add(panelDroite);

		final ComponentListener panelListener = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gc.dessin();
				gc.repaint();
			}
		};

		panelMain.addComponentListener(panelListener);

		getContentPane().add(panelMain);
		/*
	    gc.setColorF1(boutonCouleur1.getCouleur());
	    gc.setColorF2(boutonCouleur2.getCouleur());
		 */
		gc.setColorF1(types.elementAt(1).getColor());
		gc.setColorF2(types.elementAt(2).getColor());
		gc.setNet2Gross(((double) net2gross.getValue())/ 100d);
		gc.setNBsands(nbsands.getValue());

		/** Listeners TO GRAPHCONTACT **/
		//On change RHO CustomSpinner do this :
		final ChangeListener clrho = new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				CustomSpinner obj = (CustomSpinner) e.getSource();
				if (obj == rho0) {
					gc.setRho(0,rho0.getValeur());
					DebugLog.log("behaviour rho0");
				}
				else if (obj == rho1) {
					gc.setRho(1,rho1.getValeur());
					DebugLog.log("behaviour rho1");
				}
				else if (obj == rho2) {
					gc.setRho(2,rho2.getValeur());
					DebugLog.log("behaviour rho2");
				}
				else if (obj == rho3) {
					gc.setRho(3,rho3.getValeur());
					DebugLog.log("behaviour rho3");
				}
				else {
					DebugLog.log("behaviour unknown object");
					throw new IllegalArgumentException("Objet inconnu");
				}
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
			}
		};
		rho0.addChangeListener(clrho);
		rho1.addChangeListener(clrho);
		rho2.addChangeListener(clrho);
		rho3.addChangeListener(clrho);

		//My change RHO CustomSpinner
		final ChangeListener changeListenerRhoI = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				CustomSpinner obj = (CustomSpinner) e.getSource();
				for (int i = 0; i < types.size(); i++) {
					if (obj == types.elementAt(i).getCustomSpinners().get("Rho")) {
						gc.setRho(i, types.elementAt(i).getRho());
						break;
					}
				}
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
			}
		};

		for (Iterator<TypeCouche> iterator = types.iterator(); iterator.hasNext();) {
			TypeCouche tc = (TypeCouche) iterator.next();
			tc.addChangeListenerRho(changeListenerRhoI);
		}

		//ORIGINAL CHANGE LISTENER
		final ChangeListener clVpVs = new ChangeListener() {

			private final double sqrt2 = Math.sqrt(2d);

			public void stateChanged(ChangeEvent e) {
				CustomSpinner obj = (CustomSpinner) e.getSource();
				if ((obj == vp0) || (obj==vs0)) {
					gc.setVp(0, vp0.getValeur());
					gc.setVs(0, vs0.getValeur());
					double vp=vp0.getValeur();
					double vs=vs0.getValeur();
					if (vp < vs*sqrt2) {
						if (vp/sqrt2 >= vs0.getValMin()) {
							vs0.setValeur(vp/sqrt2);
						} else {
							vs0.setValeur(vs0.getValMin());
							//vs0.setValeur(vs0.getValMin()*sqrt2);
							vp0.setValeur(vs0.getValMin()*sqrt2);
						}
					}
					DebugLog.log("behaviour clVpVs0");
				} else if ((obj == vp1) || (obj==vs1)) { 
					gc.setVp(1, vp1.getValeur());
					gc.setVs(1, vs1.getValeur());
					double vp=vp1.getValeur();
					double vs=vs1.getValeur();
					if (vp < vs*sqrt2) {
						if (vp/sqrt2 >= vs1.getValMin()) {
							vs1.setValeur(vp/sqrt2);
						} else {
							vs1.setValeur(vs1.getValMin());
							//vs1.setValeur(vs1.getValMin()*sqrt2);
							vp1.setValeur(vs1.getValMin()*sqrt2);
						}
					}
					DebugLog.log("behaviour clVpVs1");
				} else if ((obj == vp2) || (obj==vs2)) {
					gc.setVp(2, vp2.getValeur());
					gc.setVs(2, vs2.getValeur());
					double vp=vp2.getValeur();
					double vs=vs2.getValeur();
					if (vp < vs*sqrt2) {
						if (vp/sqrt2 >= vs2.getValMin()) {
							vs2.setValeur(vp/sqrt2);
						} else {
							vs2.setValeur(vs2.getValMin());
							//vs2.setValeur(vs2.getValMin()*sqrt2);
							vp2.setValeur(vs2.getValMin()*sqrt2);
						}
					}
					DebugLog.log("behaviour clVpVs2");
				} else if ((obj == vp3) || (obj==vs3)){
					gc.setVp(3, vp3.getValeur());
					gc.setVs(3, vs3.getValeur());
					double vp=vp3.getValeur();
					double vs=vs3.getValeur();
					if (vp < vs*sqrt2) {
						if (vp/sqrt2 >= vs3.getValMin()) {
							vs3.setValeur(vp/sqrt2);
						} else {
							vs3.setValeur(vs3.getValMin());
							//vs3.setValeur(vs3.getValMin()*sqrt2);
							vp3.setValeur(vs3.getValMin()*sqrt2);
						}
					}
					DebugLog.log("behaviour clVpVs3");
				} else {
					throw new IllegalArgumentException("Objet inconnu");
				}

				calculpr0();
				calculpr1();
				calculpr2();
				calculpr3();
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes(); 
				gc.rasterCourbes();
				//gc.getAmpMaxAbs();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
				
			};
		};

		vp0.addChangeListener(clVpVs);
		vp1.addChangeListener(clVpVs);
		vp2.addChangeListener(clVpVs);
		vp3.addChangeListener(clVpVs);

		vs0.addChangeListener(clVpVs);
		vs1.addChangeListener(clVpVs);
		vs2.addChangeListener(clVpVs);
		vs3.addChangeListener(clVpVs);

		//My change VpVs CustomSpinner
		final ChangeListener changeListenerVpVsI =new ChangeListener () {
			private final double sqrt2 = Math.sqrt(2d);

			public void stateChanged(ChangeEvent e) {
				CustomSpinner obj = (CustomSpinner) e.getSource();
				double current_vp;
				double current_vs;
				for (int i = 0; i < types.size(); i++) {
					if (obj == types.elementAt(i).getCustomSpinners().get("Vp") || 
						obj == types.elementAt(i).getCustomSpinners().get("Vs")) {
						current_vp = types.elementAt(i).getVp();
						current_vs = types.elementAt(i).getVs();
						gc.setVp(i, current_vp);
						gc.setVs(i, current_vs);
						break;
					}
				}

				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes(); 
				gc.rasterCourbes();
				//gc.getAmpMaxAbs();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();	  
			}  
		};

		for (Iterator<TypeCouche> iterator = types.iterator(); iterator.hasNext();) {
			TypeCouche tc = (TypeCouche) iterator.next();
			tc.addChangeListenerVp(changeListenerVpVsI);
			tc.addChangeListenerVs(changeListenerVpVsI);
		}

		final ChangeListener clPoisson0 = new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				double vp = vp0.getValeur();
				double ratio = getVsOverVp0();
				double vs = vp * ratio ; 
				vs0.setValeur(vs);
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
				DebugLog.log("behaviour clPoisson0");
			}
		};

		pr0.addChangeListener(clPoisson0);
		
		final ChangeListener clPoisson1 = new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				double vp = vp1.getValeur();
				double ratio = getVsOverVp1();
				double vs = vp * ratio ; 
				vs1.setValeur(vs);
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
				DebugLog.log("behaviour clPoisson1");
			}
		};

		pr1.addChangeListener(clPoisson1);

		final ChangeListener clPoisson2 = new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				double vp = vp2.getValeur();
				double ratio = getVsOverVp2();
				double vs = vp * ratio ; 
				vs2.setValeur(vs);
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
				DebugLog.log("behaviour clPoisson2");
			}
		};

		pr2.addChangeListener(clPoisson2);

		final ChangeListener clPoisson3 = new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				double vp = vp3.getValeur();
				double ratio = getVsOverVp3();
				double vs = vp * ratio ; 
				vs3.setValeur(vs);
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
				DebugLog.log("behaviour clPoisson3");
			}
		};

		pr3.addChangeListener(clPoisson3);
		
		//My ChangeListener for Poisson
		final ChangeListener changeListenerPoisson = new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				CustomSpinner obj = (CustomSpinner) e.getSource();
				double current_pr;
				for (int i = 0; i < types.size(); i++) {
					if (obj == types.elementAt(i).getCustomSpinners().get("Pr")) {
						current_pr = types.elementAt(i).getPr();
						gc.setPr(i, current_pr);
						break;
					}
				}
				/*
				CustomSpinner obj = (CustomSpinner) e.getSource();
				double current_vp;
				double current_vs;
				double ratio;
				int index = -1;
				//get index of CustomSpinner
				for (int i = 0; i < types.size(); i++) {
					if (obj == types.elementAt(i).getCustomSpinners().get("Pr")) {
						index = i;
						break;
					}
				}
				//Treatment for specific index
				if (index != -1) {
					current_vp = types.elementAt(index).getVp();
					double pr = types.elementAt(index).getCustomSpinners().get("Pr").getValeur();
					ratio = 0.5d*(1-2d*pr)/(1d-pr);
					ratio = Math.sqrt(ratio);
					current_vs = current_vp * ratio;
					types.elementAt(index).setVs(current_vs);
				} else {
					try {
						throw new Exception("Cant reach index from Vector");
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				}
				*/
				//Apply treatment
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
			}
		};
		
		for (Iterator<TypeCouche> iterator = types.iterator(); iterator.hasNext();) {
			TypeCouche tc = (TypeCouche) iterator.next();
			tc.addChangeListenerPr(changeListenerPoisson);
		}

		/** Listeners pour refraction **/
		final ActionListener alRefr = new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				refractionEffect =false;
				if (e.getActionCommand().equals("on")) refractionEffect = true ;

				System.out.println(" Ray tracing "+ (boolean) refractionEffect);
				gc.setRaytracing(refractionEffect);

				// gc.checkModeldimension();
				// gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				// gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				gc.dessin();
				//AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				dessinAmpFluid();
				dessinTwtFluid();
				gc.repaint();
			}
		};

		refractionEffectOn.addActionListener(alRefr);
		refractionEffectOff.addActionListener(alRefr);

		frequence.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				labelFrequence.setText("Frequency "+frequence.getValue()+" Hz");
				labelFrequence.setForeground(Color.red);
				frequence.setForeground(Color.red);
				//frequence.setName("Frequency "+frequence.getValue()+" Hz");
				if(frequence.getValueIsAdjusting() == false) {
					setCursor(cursorWait);

					double freqOld = gc.getFrequence();

					gc.setFrequence(frequence.getValue());          
					gc.calculRicker();
					affichageOndelette();

					if(freqOld > frequence.getValue()) {	 // new frequency lower needs a bigger buffer for convolution	
						gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
						gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
						gc.checkModeldimension();
					}

					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;
					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					gc.repaint(); // nouveau
					frequence.setForeground(Color.black);
					labelFrequence.setForeground(Color.black);
					setCursor(null);
				}
			}
		});

		NBtrace.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				gc.setNBtrace(NBtrace.getValeur());
				gc.initArray();
				gc.calculRicker();
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				gc.setX(0, NBtrace.getValeur());

				gampZ.setX(0d,NBtrace.getValeur());
				//AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gampTWT.setY(0d,gc.getTmax());

				gc.dessin();
				dessinAmpFluid();
				dessinTwtFluid();
			}
		});  

		phase.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				gc.setPhase(phase.getValeur());    

				gc.calculRicker();
				affichageOndelette();

				//gc.checkModeldimension();
				//gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				//gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();

				gc.calculCourbes();
				gc.rasterCourbes();
				gc.dessin();           
				gampZ.setX(0d,NBtrace.getValeur());
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampTWT.setY(0d,gc.getTmax()); 
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				dessinAmpFluid();
				dessinTwtFluid();
				gc.repaint();
			}
		});

		angleIncident.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				gc.setAngleIncident(angleIncident.getValeur());
				//gc.calculRicker();  // uniquement si stretch implemente

				//gc.checkModeldimension();
				//gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				//gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				//gc.getAmpMaxAbs();
				gc.dessin();
				gampZ.setX(0d,NBtrace.getValeur());
				gampZ.setYlabel("Amplitude  " + (int) angleIncident.getValeur() + " deg"); // nouveau
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				dessinAmpFluid();
				dessinTwtFluid();
				gc.repaint();
			}
		});

		MouseInputAdapter mia = new MouseInputAdapter () {
			Cursor curseurShift = new Cursor(Cursor.CROSSHAIR_CURSOR);
			Cursor curseurControl = new Cursor(Cursor.WAIT_CURSOR);
			Cursor curseurAlt = new Cursor(Cursor.HAND_CURSOR);

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (gc.isInside(e.getX(),e.getY())) {
					int nscroll = e.getWheelRotation();
					if(e.isShiftDown()) {
						//firePropertyChange("mouseScrolled",false,true);
						/* setCursor(cursorWait);
	            		double thick = gc.getThickness();
	            		thick += nscroll;
	            		thick = Math.min(Math.max(thickness.getValMin(),thick ),thickness.getValMax() );
	            		thickness.setValeur(thick);
	            		gc.setThickness(thick);
	            		gc.checkModeldimension();
	            		gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
	            		gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
	                    gc.calculCourbes();
	                    gc.rasterCourbes();
	          		    AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
	          		    AmpTopMax=-AmpBaseMax;
	                    gampZ.setY(AmpBaseMax, AmpTopMax);
	                    gampTWT.setX(AmpTopMax,AmpBaseMax);
	                    gc.dessin();
	                    dessinAmpFluid();
	                    dessinTwtFluid();
	                    gc.requestFocusInWindow();
	                    setCursor(null); */

					} else if (e.isControlDown()){
						//firePropertyChange("mouseScrolled",false,true);
						setCursor(curseurControl);
						dip.setForeground(Color.red);
						double dipp = gc.getDip();
						dipp += nscroll;
						dipp = Math.min(Math.max(dip.getMinimum(),dipp ),dip.getMaximum() );
						dip.setValue((int) dipp);
						gc.setDip(dipp);
						setCursor(cursorWait);
						gc.checkModeldimension();
						gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
						gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
						gc.calculCourbes();
						gc.rasterCourbes();
						AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
						AmpTopMax=-AmpBaseMax;
						gampZ.setY(AmpBaseMax, AmpTopMax);
						gampTWT.setX(AmpTopMax,AmpBaseMax);
						gc.dessin();
						dessinAmpFluid();
						dessinTwtFluid();
						setCursor(null);
						gc.requestFocusInWindow();
						gc.repaint();
						dip.setForeground(Color.black);
					} else if (e.isAltDown()){
						setCursor(curseurAlt);
						int Index = gc.getIndexOfcolorSet(); 
						Index += nscroll;
						gc.setIndexOfcolorSet(Index);
						gc.dessin();
						affichageOndelette();
						setCursor(null);
						//gc.repaint();
						//firePropertyChange("mouseScrolled",false,true);
					} else {
						setCursor(curseurShift);
						int igain = choixgain.getSelectedIndex();
						igain = igain + nscroll;
						//while (igain > gains.length-1) { igain -= gains.length;}
						//while (igain < 0) { igain += gains.length;}
						igain = Math.min(Math.max(igain, 0), gains.length-1);
						choixgain.setSelectedIndex(igain);
						setCursor(cursorWait);
						// gc.checkModeldimension();
						//gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
						//gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
						//gc.calculCourbes();
						gc.rasterCourbes();
						AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
						AmpTopMax=-AmpBaseMax;
						gampZ.setY(AmpBaseMax, AmpTopMax);
						gampTWT.setX(AmpTopMax,AmpBaseMax);
						gc.dessin();
						gc.repaint();
						gc.requestFocusInWindow();
						setCursor(null);
					}
				}

				setCursor(null);
			}
		};

		gc.addMouseWheelListener(mia);

		//ORIGINAL BOUTON COULEUR
		boutonCouleur1.addMouseListener(new MouseAdapter() {	
			public void mouseClicked(MouseEvent e) {
				gc.setColorF1(boutonCouleur1.getCouleur());
				gc.dessin();
				gampZColor();
				gampTWTColor();
				repaint();
			}
		});

		boutonCouleur2.addMouseListener(new MouseAdapter() {	
			public void mouseClicked(MouseEvent e) {
				gc.setColorF2(boutonCouleur2.getCouleur());
				gc.dessin();
				gampZColor();
				gampTWTColor();
				repaint();
			}
		});
		
		/* MY COLORPICKERBUTTON HANDLER WORKING */
		types.elementAt(1).getColorPickerButton().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				gc.setColorF1(types.elementAt(1).getColorPickerButton().getCouleur());
				gc.dessin();
				gampZColor();
				gampTWTColor();
				repaint();
			}
		});
		
		types.elementAt(2).getColorPickerButton().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				gc.setColorF2(types.elementAt(2).getColorPickerButton().getCouleur());
				gc.dessin();
				gampZColor();
				gampTWTColor();
				repaint();
			}
		});

		thickness.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {

				labelThickness.setForeground(Color.red);
				thickness.setForeground(Color.red);

				if(net2gross.getValue() == 100 ) {
					labelThickness.setText("Thickness "+ thickness.getValue() + "(m)");
				} else {
					labelThickness.setText("Gross Thickness "+ thickness.getValue() + "(m)");	
				}

				if(thickness.getValueIsAdjusting() == false) {

					setCursor(cursorWait);
					double oldThick = gc.getThickness() ;
					double newThick = thickness.getValue();

					gc.setThickness(newThick);
					gc.createNet2Gross(iSeq);

					if(newThick > oldThick) {
						gc.checkModeldimension();
						gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
						gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
					}

					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					gampZ.setX(0d,NBtrace.getValeur());
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;
					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					gc.repaint(); // nouveau
					setCursor(null);
					labelThickness.setForeground(Color.black);
					thickness.setForeground(Color.black);
				}
			}
		});

		dip.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				labelDip.setText("Dip "+dip.getValue()+" deg");
				labelDip.setForeground(Color.red);
				dip.setForeground(Color.red);

				if(dip.getValueIsAdjusting() == false) {

					setCursor(cursorWait);
					double dipOld = gc.getDip();

					if (dip.getValue() == 0 ) {
						dip.setValue(1);
						labelDip.setText("Dip "+dip.getValue()+" deg");
					}

					gc.setDip((double) dip.getValue());

					if(dipOld < dip.getValue()) {
						gc.checkModeldimension();
						gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
						gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
					}

					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;

					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					setCursor(null);
					labelDip.setForeground(Color.black);
					dip.setForeground(Color.black);
				}	
			}
		});

		GOContact.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {

				labelGOCv.setText(GOContact.getValue()+"m");
				labelGOCv.setForeground(Color.red);
				labelGOC.setForeground(Color.red);
				GOContact.setForeground(Color.red);

				if(GOContact.getValueIsAdjusting() == false) {
					setCursor(cursorWait);
					double oldContact = gc.getModel().ZContact[0];
					double newContact = (gc.getModel().ZM/2d)+(double) GOContact.getValue();

					System.out.println(" new GOC contact :"+ newContact);
					gc.getModel().ZContact[0]=newContact;

					if(newContact < oldContact) {
						gc.checkModeldimension();
					}

					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;

					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					setCursor(null);
					labelGOC.setForeground(Color.black);
					labelGOCv.setForeground(Color.black);
					GOContact.setForeground(Color.black);

					if(GOContact.getValue() == 0 && OWContact.getValue() == 0 ) {
						labelInformationAmp.setText(
								"<html><huge> "	
										+ "<font color='red'><font size='4'> <i>Top</i></font>"+ "<br>"
										+ "<font color='rgb(255,0,255)'><font size='4'> <i>Contact</i></font>"+ "<br>"
										+"<font color='blue'><font size='4'> <i>Base</i></font>"+ "<br>"				
										+ "</huge></html>");
					} else {
						labelInformationAmp.setText(
								"<html><huge> "	
										+ "<font color='red'><font size='4'> <i>Top</i></font>"+ "<br>"
										+ "<font color='rgb(255,0,255)'><font size='4'> <i>GOC & WOC</i></font>"+ "<br>"
										+"<font color='blue'><font size='4'> <i>Base</i></font>"+ "<br>"				
										+ "</huge></html>");	
					}
				}
			}
		});

		OWContact.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				labelOWCv.setText(OWContact.getValue()+"m");
				labelOWC.setForeground(Color.red);
				labelOWCv.setForeground(Color.red);
				OWContact.setForeground(Color.red);

				if(OWContact.getValueIsAdjusting() == false) {
					setCursor(cursorWait);
					double oldContact = gc.getModel().ZContact[1];
					double newContact = (gc.getModel().ZM/2d)+(double) OWContact.getValue();
					System.out.println(" new OWC contact :"+ newContact);
					gc.getModel().ZContact[1]=newContact;

					if(newContact > oldContact) {
						gc.checkModeldimension();
					}

					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;

					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					setCursor(null);
					labelOWC.setForeground(Color.black);
					labelOWCv.setForeground(Color.black);
					OWContact.setForeground(Color.black);
					if(GOContact.getValue() == 0 && OWContact.getValue() == 0 ) {
						labelInformationAmp.setText(
								"<html><huge> "	
										+ "<font color='red'><font size='4'> <i>Top</i></font>"+ "<br>"
										+ "<font color='rgb(255,0,255)'><font size='4'> <i>Contact</i></font>"+ "<br>"
										+"<font color='blue'><font size='4'> <i>Base</i></font>"+ "<br>"				
										+ "</huge></html>");
					} else {
						labelInformationAmp.setText(
								"<html><huge> "	
										+ "<font color='red'><font size='4'> <i>Top</i></font>"+ "<br>"
										+ "<font color='rgb(255,0,255)'><font size='4'> <i>GOC & WOC</i></font>"+ "<br>"
										+"<font color='blue'><font size='4'> <i>Base</i></font>"+ "<br>"				
										+ "</huge></html>");	
					}
				}
			}
		});

		net2gross.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {
				labelN2G.setText("Net:Gross "+net2gross.getValue()+"%");
				labelN2G.setForeground(Color.red);
				net2gross.setForeground(Color.red);
				if(net2gross.getValueIsAdjusting() == false) {

					gc.setNet2Gross(((double) net2gross.getValue())/100d);

					if(net2gross.getValue() == 100 ) {
						gc.setNBsands(1);
						nbsands.setValue(1);
						labelN2G.setVisible(false);
						net2gross.setVisible(false);
						net2gross.setEnabled(false);
						//Sequence.setIcon(SeqIconsGrey[iSeq]);
						Sequence.setVisible(false);
						Sequence.setEnabled(false);
						labelThickness.setText("Thickness "+ thickness.getValue() + "(m)");
					} else {
						labelThickness.setText("Gross Thickness "+ thickness.getValue() + "(m)");
						Sequence.setIcon(SeqIcons[iSeq]);
						Sequence.setVisible(true);
						Sequence.setEnabled(true);
					}

					setCursor(cursorWait);

					gc.createNet2Gross(iSeq);
					//gc.checkModeldimension();
					//gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
					//gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;

					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					labelN2G.setForeground(Color.black);
					net2gross.setForeground(Color.black);
					setCursor(null);
				}
			}
		});

		nbsands.addChangeListener(new ChangeListener () {
			public void stateChanged(ChangeEvent e) {

				labelNbsands.setForeground(Color.red);
				nbsands.setForeground(Color.red);

				labelNbsands.setText(nbsands.getValue()+" reservoirs");
				if( nbsands.getValue() == 1 ) labelNbsands.setText("Single Reservoir");
				if(nbsands.getValueIsAdjusting() == false) {

					gc.setNBsands((nbsands.getValue()));

					if( nbsands.getValue() == 1 ) {
						gc.setNet2Gross(1.0d);
						net2gross.setValue(100);
						net2gross.setEnabled(false);
						labelThickness.setText("Thickness "+ thickness.getValue() + "(m)");
						labelN2G.setText("Net:Gross "+net2gross.getValue()+"%");
						labelN2G.setVisible(false);
						net2gross.setVisible(false);
						runAleaOnce.setIcon(RunAleaOnce);
						runAleaOnce.setEnabled(false);
						runAlea.setIcon(RunAleaTwice);
						runAlea.setEnabled(false);
						//Sequence.setIcon(SeqIconsGrey[iSeq]);
						Sequence.setVisible(false);
						Sequence.setEnabled(false);
					} else {
						runAleaOnce.setIcon(RunAleaOnce);
						runAleaOnce.setEnabled(true);
						runAlea.setIcon(RunAleaTwice);
						runAlea.setEnabled(true);
						Sequence.setIcon(SeqIcons[iSeq]);
						Sequence.setVisible(true);
						Sequence.setEnabled(true);
						labelThickness.setText("Gross thickness "+ thickness.getValue() + "(m)");
						net2gross.setEnabled(true);
						labelN2G.setVisible(true);
						net2gross.setVisible(true);
					}

					setCursor(cursorWait);

					gc.createNet2Gross(iSeq);
					//gc.checkModeldimension();
					//gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
					//gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;

					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					nbsands.setForeground(Color.black);
					labelNbsands.setForeground(Color.black);
					setCursor(null);
				}
			}
		});

		choixgain.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				gc.setGain(getGain()) ;
				gc.rasterCourbes();
				/*AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
	  		  	AmpTopMax=-AmpBaseMax;
	            gampZ.setY(AmpBaseMax, AmpTopMax);
	            gampTWT.setX(AmpTopMax,AmpBaseMax);*/
				gc.dessin();
				gc.repaint(); // nouveau
			}
		});

		choixMinRatio.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				gc.setMinRatio(getMinRatio()) ;
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				gc.repaint(); // nouveau
			}
		});

		choixMaxRatio.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				gc.setMaxRatio(getMaxRatio()) ;
				gc.checkModeldimension();
				gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;
				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				gc.dessin();
				gc.repaint(); // nouveau
			}
		});

		choixtTransparenceF1.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				gc.setTransF1(getTransF1()) ;
				gc.dessin();
				gc.repaint(); // nouveau
			}
		});

		choixtTransparenceF2.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				gc.setTransF2(getTransF2()) ;
				gc.dessin();
				gc.repaint(); // nouveau
			}
		});
		
		/* MY Transparence Handler */
		types.elementAt(1).addActionListenerTransparence(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				gc.setTransF1(types.elementAt(1).getTransparence());
				gc.dessin();
				gc.repaint();
			}
		});
		types.elementAt(2).addActionListenerTransparence(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				gc.setTransF2(types.elementAt(2).getTransparence());
				gc.dessin();
				gc.repaint();
			}
		});

		/*gc.addPropertyChangeListener("mouseScrolled",new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent e) {
	        	int nscroll = gc.nscroll;
	        	int igain = choixgain.getSelectedIndex();
	        	igain = igain + nscroll;
	        	igain = Math.min(Math.max(igain, 0), gains.length-1);
	        	choixgain.setSelectedIndex(igain);
	        	gc.calculCourbes();
	        	gc.rasterCourbes();
	  		  	AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
	  		  	AmpTopMax=-AmpBaseMax;
	            gampZ.setY(AmpBaseMax, AmpTopMax);
	            gampTWT.setX(AmpTopMax,AmpBaseMax);
	        	gc.dessin();

	        }      
	   	});*/
		/* gc.addPropertyChangeListener("colorScrolled",new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent e) {
	        	int nscroll = gc.nscroll;
	        	affichageOndelette();

	        	//gc.repaint(); // nouveau
	        }      
	    });*/

		resetValues.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		Sequence.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				iSeq = iSeq+1;
				if(iSeq > 3 ) {
					iSeq = 0;
				}
				Sequence.setIcon(SeqIcons[iSeq]);	
				setCursor(cursorWait);

				gc.createNet2Gross(iSeq);
				//gc.checkModeldimension();
				//gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
				//gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
				gc.calculCourbes();
				gc.rasterCourbes();
				gc.dessin();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;

				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				dessinAmpFluid();
				dessinTwtFluid();
				setCursor(null);
			}
		});

		runAleaOnce.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				setCursor(cursorWait);
				gc.createNet2Gross(iSeq);
				gc.checkModeldimension();
				gc.calculCourbes();
				gc.rasterCourbes();
				gc.dessin();
				AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
				AmpTopMax=-AmpBaseMax;

				gampZ.setY(AmpBaseMax, AmpTopMax);
				gampZ.setX(0d,NBtrace.getValeur());
				gampTWT.setY(0d,gc.getTmax());
				gampTWT.setX(AmpTopMax,AmpBaseMax);
				dessinAmpFluid();
				dessinTwtFluid();
				setCursor(null);
			}
		});

		runAlea.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				booleanRunAlea = !booleanRunAlea ;
				runAleaOnce.setEnabled(!booleanRunAlea);
				setCursor(cursorWait);
				int i = 1;
				int j = 0;
				while(booleanRunAlea) {
					runAlea.setIcon(RunAlea[j]);
					runAlea.setText(" "+i);
					runAlea.repaint();
					gc.createNet2Gross(iSeq);
					gc.checkModeldimension();
					gc.getModel().ZContact[0]=(gc.getModel().ZM/2d)+(double) GOContact.getValue();
					gc.getModel().ZContact[1]=(gc.getModel().ZM/2d)+(double) OWContact.getValue();
					gc.calculCourbes();
					gc.rasterCourbes();
					gc.dessin();
					gc.repaint();
					AmpBaseMax=( 1.1*gc.getAmpMaxAbs())*1E-3 ;
					AmpTopMax=-AmpBaseMax;

					gampZ.setY(AmpBaseMax, AmpTopMax);
					gampZ.setX(0d,NBtrace.getValeur());
					gampTWT.setY(0d,gc.getTmax());
					gampTWT.setX(AmpTopMax,AmpBaseMax);
					dessinAmpFluid();
					dessinTwtFluid();
					i += 1;
					j += 1;
					if(j == RunAlea.length) {
						j = 0 ;
						runAlea.setIcon(RunAleaTwice);
						runAlea.repaint();
						runAlea.requestFocus();
						break ;
					}
				}
				runAleaOnce.setEnabled(true);
				setCursor(null); 
			}
		}); 
	}

	private ImageIcon createImageIcon(String path) {
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			return null;   
		}
	}

	private double getGain() {
		StringTokenizer tk = new StringTokenizer((String)choixgain.getSelectedItem());
		return Double.parseDouble(tk.nextToken())*1E-2 ;
	}

	private double getMinRatio() {
		StringTokenizer tk = new StringTokenizer((String)choixMinRatio.getSelectedItem());
		return Double.parseDouble(tk.nextToken())*1E-2 ;
	}

	private double getMaxRatio() {
		StringTokenizer tk = new StringTokenizer((String)choixMaxRatio.getSelectedItem());
		return Double.parseDouble(tk.nextToken())*1E-2 ;
	}

	private double getTransF1() {
		StringTokenizer tk = new StringTokenizer((String)choixtTransparenceF1.getSelectedItem());
		return Double.parseDouble(tk.nextToken())*255/100;
	}

	private double getTransF2() {
		StringTokenizer tk = new StringTokenizer((String)choixtTransparenceF2.getSelectedItem());
		return Double.parseDouble(tk.nextToken())*255/100;
	}

	public void affichageOndelette() {
		courbeWavelet.reset();
		//gc.calculRicker();
		double dt = gc.getDt();
		int N = gc.getN();
		double rick [] = gc.getRick();        
		double timeshift, freq;
		freq = (double) frequence.getValue();
		timeshift = 1.2d/freq;
		courbeWavelet.addPoint(-timeshift*1E3,0d);
		for (int i = 0 ; ((double)i*dt < 2d*timeshift) && (i < N) ; i++) {
			courbeWavelet.addPoint(((double)i*dt-timeshift)*1E3,rick[i]*10d);
		}
		courbeWavelet.addPoint(timeshift*1E3,0d);
		courbeWavelet.setColorSet(gc.getIndexOfcolorSet());
		graphWavelet.setMaximumBounds(-timeshift*1.1E3,timeshift*1.1E3,-10d,15d);
		graphWavelet.resetZoom();  
		graphWavelet.repaint();  
	} 

	public void setIndex(int _index) {
		choixSizeScreen.setSelectedIndex(_index);
	}

	public void dessinAmpFluid() {
		courbeTop.clear();
		courbeBase.clear();
		for (int iseg = 0; iseg < gc.nb_contacts+1 ; iseg += 1) {
			//GeneralPath courbeT = new GeneralPath();
			courbeTop.add(new GeneralPath());
			//GeneralPath courbeB = new GeneralPath();
			courbeBase.add(new GeneralPath());
		}
		// gc.createCourbeAmpFluid(courbeAmpTop,courbeAmpBase,courbeAmpCont0,courbeAmpCont1,courbeAmpNul);
		gc.createCourbeAmpFluid(courbeTop,courbeBase,courbeAmpCont0,courbeAmpCont1,courbeAmpNul);
		gampZ.repaint();
		gc.repaint();
	}


	public void dessinTwtFluid() {
		gc.createCourbeTwtFluid(courbeTwtTop,courbeTwtBase,courbeTwtCont0,courbeTwtCont1,courbeAmpTwtNul);
		gampTWT.repaint();
		gc.repaint();
	}

	/* Creating TypeCouche */
	public void buildTypeCouche(JPanel jPanelParent) {
		JPanel jPanelForThisTypeCouche;
		for (Iterator<TypeCouche> iterator = types.iterator(); iterator.hasNext();) {
			TypeCouche tc = (TypeCouche) iterator.next();
			tc.setAlignmentX(Component.CENTER_ALIGNMENT);
			jPanelForThisTypeCouche = new JPanel();
			jPanelForThisTypeCouche.setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
			jPanelForThisTypeCouche.add(tc);
			jPanelForThisTypeCouche.validate();
			jPanelForThisTypeCouche.repaint();
			jPanelParent.add(jPanelForThisTypeCouche);
		}
	}

	public void resetTypeCouche() {
		for (Iterator<TypeCouche> iterator = types.iterator(); iterator.hasNext();) {
			TypeCouche tc = (TypeCouche) iterator.next();
			tc.reset();
		}
	}

	/***********************Simulation de l'applet*************************/
	public static void main(String[] args) {

		ContactModellingApplet applet = new ContactModellingApplet();
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		final JFrame frame = new JFrame("Contact Modelling Applet");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Frame [] frames = Frame.getFrames();
				for (int i = 0 ; i < frames.length ; i++) frames[i].dispose();
				frame.dispose();
				System.exit(0);
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(applet);
		frame.setSize(dimension);
		frame.setUndecorated(true);
		frame.setResizable(true);
		applet.init();
		applet.start();
		frame.setVisible(true);
		frame.toFront();

	}


}

