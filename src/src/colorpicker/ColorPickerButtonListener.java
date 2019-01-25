package src.colorpicker;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

public class ColorPickerButtonListener extends MouseInputAdapter {
	
    public void mouseEntered(MouseEvent e) {
        ((ColorPickerButton) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public void mouseExited(MouseEvent e) {
    	((ColorPickerButton) e.getSource()).setCursor(null);
    }
    public void mouseClicked(MouseEvent e) {
    	JColorChooser jColorChooser = new JColorChooser(((ColorPickerButton) e.getSource()).getCouleur());
    	jColorChooser.setPreviewPanel(new JPanel());
    	Color newColor = JColorChooser.showDialog(null, "Choose a color", ((ColorPickerButton) e.getSource()).getCouleur());
    	if (newColor != null) {
    		((ColorPickerButton) e.getSource()).setCouleur(newColor);
		}
    }
	
}
