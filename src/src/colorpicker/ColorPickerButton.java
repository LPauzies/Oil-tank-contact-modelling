package src.colorpicker;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ColorPickerButton extends JPanel {

	private static final long serialVersionUID = 1L;
	private Color currentColor;
	
	public ColorPickerButton(Color initColor) {
		super();
		setBorder(BorderFactory.createRaisedBevelBorder());
		
	    ColorPickerButtonListener colorPickerButtonListener = new ColorPickerButtonListener();
	    
	    addMouseMotionListener(colorPickerButtonListener);
	    addMouseListener(colorPickerButtonListener);
	    addMouseWheelListener(colorPickerButtonListener);
	    setCouleur(initColor);
	}
	
	public void dessin() {
    	setBackground(this.currentColor);
    }
    public Color getCouleur() {
    	return this.currentColor;
    }
    public void setCouleur(Color color) {
    	this.currentColor = color;
    	dessin();
    }
}
