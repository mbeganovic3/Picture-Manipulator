package a7;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelInspector extends JPanel implements MouseListener {

	private PictureView picture_view;
	// Initializing all the labels
	private JLabel x = new JLabel("X: ");
	private JLabel y = new JLabel("Y: ");
	private JLabel red = new JLabel("Red: ");
	private JLabel green = new JLabel("Green: ");
	private JLabel blue = new JLabel("Blue: ");
	private JLabel brightness = new JLabel("Brightness: ");

	public PixelInspector(Picture picture) {
		setLayout(new BorderLayout());

		picture_view = new PictureView(picture.createObservable());
		picture_view.addMouseListener(this);
		add(picture_view, BorderLayout.CENTER);
		
		// Adds all the labels to the panel to the left 
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		panel.add(x);
		panel.add(y);
		panel.add(red);
		panel.add(green);
		panel.add(blue);
		panel.add(brightness);
		add(panel, BorderLayout.WEST);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {		
		// Retrieves the info for the pixel on each click 
		x.setText("X: " + e.getX());
		y.setText("Y: " + e.getY());
		red.setText("Red: " + (Math.round(picture_view.getPicture().getPixel(e.getX(), e.getY()).getRed() * 100.00) / 100.00));
		green.setText("Green: " + (Math.round(picture_view.getPicture().getPixel(e.getX(), e.getY()).getGreen() * 100.00) / 100.00));
		blue.setText("Blue: " + (Math.round(picture_view.getPicture().getPixel(e.getX(), e.getY()).getBlue() * 100.00) / 100.00));
		brightness.setText("Brightness: " + (Math.round(picture_view.getPicture().getPixel(e.getX(), e.getY()).getIntensity() * 100.00) / 100.00));
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public static void main(String[] args) throws IOException {
		Picture p = A7Helper.readFromURL("http://www.cs.unc.edu/~kmp/kmp-in-namibia.jpg");
		PixelInspector pixel_inspector = new PixelInspector(p);
		
		JFrame main_frame = new JFrame();
		main_frame.setTitle("Pixel Inspector");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(pixel_inspector, BorderLayout.CENTER);
		main_frame.setContentPane(top_panel);
		
		main_frame.pack();
		main_frame.setVisible(true);
	}
}

