package src;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;


public class BoutonCouleur extends JPanel {
    
	/* auto UID serial Version */
	private static final long serialVersionUID = 1L;

    private static final Color [] couleurs = {Color.gray,Color.red,Color.green,Color.blue,Color.white};

    private static int lg = couleurs.length;
    private int index = 0;

    /* Constructor */
    public BoutonCouleur() {
	    super();
	    setBorder(BorderFactory.createRaisedBevelBorder());
	    
	    final MouseInputAdapter mia = new MouseInputAdapter (){
	    	
	        Color color; 
	        
	        public void mouseEntered(MouseEvent e) {
	            setCursor(new Cursor(Cursor.HAND_CURSOR));
	        }
	        public void mouseExited(MouseEvent e) {
	            setCursor(null);
	        }
	        
	        public void mouseClicked(MouseEvent e) {
	        	//Alt+click
	        	if (e.isAltDown()) {
	                color = getCouleur();
	                couleurs[index] = color.darker();
	            }
	        	//Ctrl+click
	            if (e.isControlDown()) {
	                color = getCouleur();
	                couleurs[index] = color.brighter();
	            }
	            if (SwingUtilities.isRightMouseButton( e ) ) {           		
	                index --;
	                if (index < 0) index+= lg;
	            } else if (SwingUtilities.isLeftMouseButton( e )) {
	                index ++;
	                if (index >= lg) index-= lg;
	            }
	            dessin();
	        }
	    };
	        
	    addMouseMotionListener(mia);
	    addMouseListener(mia);
	    addMouseWheelListener(mia);
	    dessin();
    }

    public void dessin() {
    	setBackground(couleurs[index]);
    }

    public Color getCouleur() {
    	return couleurs[index];
    }
    public void setCouleur(int _index) {
    	if (_index > couleurs.length-1) {
    		index = couleurs.length-1;
    	} else if (_index < 0){
    		index = 0;
    	} else {
    		index = _index;
    	}
    	dessin();
    }
}
