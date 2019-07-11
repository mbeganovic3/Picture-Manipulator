package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ImageAdjuster extends JPanel implements ChangeListener {

	private Picture _picture;
	private PictureView picture_view;
	private JSlider blur = new JSlider(0, 5, 0);
	private JSlider saturation = new JSlider(-100, 100, 0);
	private JSlider brightness = new JSlider(-100, 100, 0);
	private JLabel label;
	private JLabel label2;
	private JLabel label3;
	private Pixel[][] copyPixels;
	
	public ImageAdjuster(Picture picture) {
		_picture = picture;	
		setLayout(new BorderLayout());

		picture_view = new PictureView(picture.createObservable());
		add(picture_view, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3,1));
		
		// blur slider
		blur.setMajorTickSpacing(1);
		blur.setPaintTicks(true);
		blur.setPaintLabels(true);
		label = new JLabel("Blur: ");
		panel.add(label);
		panel.add(blur);
		
		// saturation slider
		saturation.setMajorTickSpacing(25);
		saturation.setPaintTicks(true);
		saturation.setPaintLabels(true);
		label2 = new JLabel("Saturation: ");
		panel.add(label2);
		panel.add(saturation);
		
		// brightness slider
		brightness.setMajorTickSpacing(25);
		brightness.setPaintTicks(true);
		brightness.setPaintLabels(true);
		label3 = new JLabel("Brightness: ");
		panel.add(label3);
		panel.add(brightness);
		add(panel, BorderLayout.SOUTH);
		
		blur.addChangeListener(this);
		saturation.addChangeListener(this);
		brightness.addChangeListener(this);		
	}

	public void stateChanged(ChangeEvent arg0) {
		if(!((JSlider) arg0.getSource()).getValueIsAdjusting()) {
			copyArray();
			Picture pic = new MutablePixelArrayPicture(copyPixels, _picture.getCaption());
			ObservablePicture result = pic.createObservable();
			picture_view.setPicture(result);
		}
	}
	
	public void copyArray() {
		copyPixels = new Pixel[_picture.getWidth()][_picture.getHeight()];
		for (int x = 0; x < _picture.getWidth(); x++) {
			for (int y = 0; y < _picture.getHeight(); y++) {
				copyPixels[x][y] = _picture.getPixel(x, y);
			}
		}
		
		// Brightness Slider 
        double brightnessAmount = brightness.getValue() / 100.0;

        for (int row = 0; row < _picture.getWidth(); row++) {
            for (int col = 0; col < _picture.getHeight(); col++) {
            	if(brightnessAmount > 0) {
            		copyPixels[row][col] = copyPixels[row][col].lighten(Math.abs(brightnessAmount));
            	}else if(brightnessAmount < 0) {
            		copyPixels[row][col] = copyPixels[row][col].darken(Math.abs(brightnessAmount));
            	}
            }
        }
        
        // Saturation Slider 
        double f = saturation.getValue();
        double b = 0; 
        double lc = 0;
        double newRed = 0;
        double newGreen = 0;
        double newBlue = 0;
        
        // For saturation factors from -100 to 0
        if(f < 0) {
        	for (int x = 0; x < _picture.getWidth(); x++) {
        		for (int y = 0; y < _picture.getHeight(); y++) {
        			b = copyPixels[x][y].getIntensity();
        			newRed = copyPixels[x][y].getRed() * (1.0 + (f / 100.0) ) - (b * f / 100.0);
        			newGreen = copyPixels[x][y].getGreen() * (1.0 + (f / 100.0) ) - (b * f / 100.0);
        			newBlue = copyPixels[x][y].getBlue() * (1.0 + (f / 100.0) ) - (b * f / 100.0);
        			
        			// Check to see if the components are out of range
        			if(newRed < 0) newRed = 0;
        			if(newRed > 1) newRed = 1;
        			if(newGreen < 0) newGreen = 0;
        			if(newGreen > 1) newGreen = 1;
        			if(newBlue < 0) newBlue = 0;
        			if(newBlue > 1) newBlue = 1;
        			
        			copyPixels[x][y] = new ColorPixel(newRed, newGreen, newBlue);
        		}
        	}
        }if(f > 0) {
        	for (int x = 0; x < _picture.getWidth(); x++) {
        		for (int y = 0; y < _picture.getHeight(); y++) {
        			// First determine which component (i.e., red, green, or blue) of the pixel is largest.
        			lc = Math.max(copyPixels[x][y].getRed(), copyPixels[x][y].getGreen());
        			lc = Math.max(lc, copyPixels[x][y].getBlue());
        			
        			// If the saturation value is greater than 0, 
        			// and the value of the largest component for a specific pixel is less than 0.01, 
        			// then do not do any formula; skip the saturation step for this pixel.
        			if(lc > 0.01) {
        				newRed = copyPixels[x][y].getRed() * ((lc + ((1.0 - lc) * (f / 100.0))) / lc);
        				newGreen = copyPixels[x][y].getGreen() * ((lc + ((1.0 - lc) * (f / 100.0))) / lc);
        				newBlue = copyPixels[x][y].getBlue() * ((lc + ((1.0 - lc) * (f / 100.0))) / lc);

        				// Check to see if the components are out of range
        				if(newRed < 0) newRed = 0;
        				if(newRed > 1) newRed = 1;
        				if(newGreen < 0) newGreen = 0;
        				if(newGreen > 1) newGreen = 1;
        				if(newBlue < 0) newBlue = 0;
        				if(newBlue > 1) newBlue = 1;

        				copyPixels[x][y] = new ColorPixel(newRed, newGreen, newBlue);
        			}
        		}
        	}
        }

        // Blur Slider 
        if(blur.getValue() != 0) {
        int blurAmount = blur.getValue();
        ArrayList<Double> totalRed = new ArrayList<Double>();
        ArrayList<Double> totalGreen = new ArrayList<Double>();
        ArrayList<Double> totalBlue = new ArrayList<Double>();
        double avgRed = 0;
        double avgGreen = 0;
        double avgBlue = 0;
        double amount = 0;
        
        // Instead, for each (x,y) position, use a double for loop 
        // that goes from the current (x-blur_size, y-blur_size) to (x+blur_size, y+blur_size) 
        // accumulating the red, green, and blue components
        for (int x = 0; x < _picture.getWidth(); x++) {
    		for (int y = 0; y < _picture.getHeight(); y++) {
    			for (int newX = x - blurAmount; newX < (x + blurAmount); newX++) {
    				for (int newY = y - blurAmount; newY < (y + blurAmount); newY++) {
    					try {
    						if(copyPixels[newX][newY] != null) {
    							totalRed.add(copyPixels[newX][newY].getRed());
    							totalGreen.add(copyPixels[newX][newY].getGreen());
    							totalBlue.add(copyPixels[newX][newY].getBlue());

    						}
    					} catch (Exception e) {
    					}
    				}
    			}
    			
    			// calculates average for RGB
    			for(int i = 0; i < totalRed.size(); i++) {
    				amount += totalRed.get(i);
    			}
    			avgRed = amount / totalRed.size();
    			amount = 0;

    			for(int i = 0; i < totalGreen.size(); i++) {
    				amount += totalGreen.get(i);
    			}
    			avgGreen = amount / totalGreen.size();
    			amount = 0;

    			for(int i = 0; i < totalBlue.size(); i++) {
    				amount += totalBlue.get(i);
    			}
    			avgBlue = amount / totalBlue.size();
    			amount = 0;

    			copyPixels[x][y] = new ColorPixel(avgRed, avgGreen, avgBlue);
    			totalRed.clear();
    			totalGreen.clear();
    			totalBlue.clear();
    		}
        }
        }
	}
        
	public static void main(String[] args) throws IOException {
		Picture p = A7Helper.readFromURL("http://www.cs.unc.edu/~kmp/kmp-in-namibia.jpg");
		ImageAdjuster image_adjuster = new ImageAdjuster(p);

		JFrame main_frame = new JFrame();
		main_frame.setTitle("Image Adjuster");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(image_adjuster, BorderLayout.CENTER);
		main_frame.setContentPane(top_panel);

		main_frame.pack();
		main_frame.setVisible(true);
	}
}