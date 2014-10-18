import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author Nihav Jain
 * @class Main
 * @desc contains main method, JFrame and program logic
 */
public class Main implements ActionListener{

	/**
	 * @param args
	 */
	public static Rectangle screenRect;
	public static Robot rt;
	public static BufferedImage capture;
	public static Timer tim;
	public static int j;
	public static String abc = "";
	public static Image cursor;
	
	private JFrame frame;
	private JPanel mainPanel;
	private JLabel renderFolderLoc;
	private JFileChooser renderFolderChooser;
	private JButton browseBtn;
	private JButton playButton;
	private JButton pauseButton;
	private JButton stopButton;
	
	private String path;
	
	/**
	 * @method init
	 * @desc initializes the variables
	 */
	private void init()
	{
		screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		try {
			rt = new Robot();
			cursor = ImageIO.read(resourseLoader("pointer.png"));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}
		tim = new Timer(1000, this);
		initializeImagePathVars();		
		initUI();
	}
	
	/**
	 * @method initUI
	 * @desc initializes the UI components
	 */
	private void initUI() {

		frame = new JFrame("NJ Screen Recorder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setVisible(true);
		
		mainPanel = new JPanel();
		frame.add(mainPanel);
		
		renderFolderLoc = new JLabel("Choose a folder...");
		mainPanel.add(renderFolderLoc);
		
		renderFolderChooser = new JFileChooser();
		renderFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		browseBtn = new JButton("Browse");
		browseBtn.addActionListener(this);
		mainPanel.add(browseBtn);
		
		Image playImage = null, stopImage = null, pauseImage = null;
		try {
			playImage = ImageIO.read(resourseLoader("play.png"));
			stopImage = ImageIO.read(resourseLoader("stop.png"));
			pauseImage = ImageIO.read(resourseLoader("pause.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Error loading resources");
			return;
		}
		
		playButton = new JButton(new ImageIcon(playImage));
		playButton.addActionListener(this);
		
		stopButton = new JButton(new ImageIcon(stopImage));
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		
		pauseButton = new JButton(new ImageIcon(pauseImage));
		pauseButton.addActionListener(this);
		pauseButton.setEnabled(false);
		
		mainPanel.add(playButton);
		mainPanel.add(pauseButton);
		mainPanel.add(stopButton);
		
		mainPanel.revalidate();
		mainPanel.repaint();
		
	}
	
	/**
	 * @method actionPerfomed
	 * @desc event handler for buttons and timer
	 * @param event
	 */
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getSource() == browseBtn){
			int fileChooserReturnVal = renderFolderChooser.showOpenDialog(frame);
			if(fileChooserReturnVal == JFileChooser.APPROVE_OPTION){
				File folder = renderFolderChooser.getSelectedFile();
				path = folder.getAbsolutePath();
				renderFolderLoc.setText(path);
			}
			else{
				// option cancelled by user
			}
		}
		else if(event.getSource() == playButton){
			if(path.length() > 0){
				disablePlay();
				tim.start();
			}
			else{
				JOptionPane.showMessageDialog(frame, "Please choose a destination folder to save the renders");
			}
		}
		else if(event.getSource() == pauseButton){
			tim.stop();
			enablePlay();
		}
		else if(event.getSource() == stopButton){
			tim.stop();
			enablePlay();
			initializeImagePathVars();
			renderFolderLoc.setText("Choose a folder...");
		}
		else{
			int i,x,y;
			Graphics2D graphics;
			for(i=1;i<=30;i++)
			{
				j++;
				if(j < 10)
					abc = "00000" + j;
				else if(j < 100)
					abc = "0000" + j;
				else if(j < 1000)
					abc = "000" + j;
				else if(j < 10000)
					abc = "00" + j;
				else if(j < 100000)
					abc = "0" + j;
				else
					abc = "" + j;
				capture = rt.createScreenCapture(screenRect);
				x = MouseInfo.getPointerInfo().getLocation().x;
				y = MouseInfo.getPointerInfo().getLocation().y;
				try {
					graphics = capture.createGraphics();
					graphics.drawImage(cursor, x, y, 13, 23, null);
					ImageIO.write(capture, "jpeg", new File(path + "/seq_" + abc + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	/**
	 * @method initializeImagePathVars
	 * @desc initializes the variables used to save image path
	 */
	private void initializeImagePathVars() {
		j = 0;
		abc = "";
		path = "";		
	}
	
	/**
	 * @method enablePlay
	 * @desc enables play button, disables pause and stop button
	 */
	private void enablePlay(){
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
	}
	
	/**
	 * @method disablePlay
	 * @desc enables pause and stop button, disables play button
	 */
	private void disablePlay(){
		playButton.setEnabled(false);
		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);		
	}
	
	private InputStream resourseLoader(String path){
		return getClass().getResourceAsStream(path);
	}
	
	/**
	 * @method main
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception {
		new Main().init();
	}

}
