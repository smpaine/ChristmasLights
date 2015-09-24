package com.smpaine.christmasLights;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.Queue;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ChristmasLighsGUI implements WindowListener, WindowFocusListener,
		WindowStateListener {

	private static JFrame frame;
	private static JToolBar toolBar = new JToolBar();
	private static JToggleButton[] lights;
	private static int[] redLeds, blueLeds, greenLeds, yellowLeds;
	private static boolean fastMode = false;
	private static YunRESTApi conn = null;
	private volatile ArrayList<String[]> instructionQueue = new ArrayList<String[]>();
	
	private JPanel me = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		int numGroups = 3;
		int i, j;

		redLeds = new int[27];
		blueLeds = new int[27];
		greenLeds = new int[27];
		yellowLeds = new int[27];

		for (i = 0; i < numGroups; i++) {
			for (j = 0; j < 9; j++) {
				redLeds[(i * 9) + j] = (i * 9 * 4) + j;
			}
		}

		for (i = 0; i < numGroups; i++) {
			for (j = 0; j < 9; j++) {
				blueLeds[(i * 9) + j] = (i * 9 * 4) + j + 9;
			}
		}

		for (i = 0; i < numGroups; i++) {
			for (j = 0; j < 9; j++) {
				greenLeds[(i * 9) + j] = (i * 9 * 4) + j + 18;
			}
		}

		for (i = 0; i < numGroups; i++) {
			for (j = 0; j < 9; j++) {
				yellowLeds[(i * 9) + j] = (i * 9 * 4) + j + 27;
			}
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChristmasLighsGUI window = new ChristmasLighsGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		conn = new YunRESTApi("http://192.168.0.74");
		
		try {
			conn.initLights();
			/*
			Thread.sleep(100);
			conn.setGroup("red", "255");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("red", "0");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("green", "255");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("green", "0");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("blue", "255");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("blue", "0");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("yellow", "255");
			conn.refresh();
			Thread.sleep(100);
			conn.setGroup("yellow", "0");
			conn.refresh();
			Thread.sleep(100);
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Create the application.
	 */
	public ChristmasLighsGUI() {
		(new UpdaterThread(instructionQueue)).start();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws InterruptedException 
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 832, 587);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		frame.addWindowListener(this);
		frame.addWindowFocusListener(this);
		frame.addWindowStateListener(this);

		JMenuBar menuBar = new JMenuBar();
		toolBar.add(menuBar);

		JMenu mnSerial = new JMenu("Serial");
		menuBar.add(mnSerial);

		JMenuItem mntmPort = new JMenuItem("Port");
		mnSerial.add(mntmPort);

		JMenuItem mntmSpeed = new JMenuItem("Speed");
		mnSerial.add(mntmSpeed);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(12, 10, 0, 0));

		lights = new JToggleButton[115];
		JToggleButton temp;
		int i;

		for (i = 1; i < 116; i++) {
			if (i == 109) {
				temp = new JToggleButton("All Red");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.RED);
			} else if (i == 110) {
				temp = new JToggleButton("All Blue");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.BLUE);
			} else if (i == 111) {
				temp = new JToggleButton("All Green");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.GREEN);
			} else if (i == 112) {
				temp = new JToggleButton("All Yellow");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.YELLOW);
			} else if (i == 113) {
				temp = new JToggleButton("All ON");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.GRAY);
			} else if (i == 114) {
				temp = new JToggleButton("All OFF");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.GRAY);
			} else if (i == 115) {
				temp = new JToggleButton("DEMO");
				temp.addChangeListener(changeListener);
				temp.setBackground(Color.GRAY);
			} else {
				temp = new JToggleButton("" + i);
				temp.addChangeListener(changeListener);
			}
			temp.setOpaque(true);
			panel.add(temp);
			lights[i - 1] = temp;
		}

		for (i = 0; i < 27; i++) {
			lights[redLeds[i]].setBackground(Color.RED);
			lights[blueLeds[i]].setBackground(Color.BLUE);
			lights[greenLeds[i]].setBackground(Color.GREEN);
			lights[yellowLeds[i]].setBackground(Color.YELLOW);
		}
		me = panel;
	}

	ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent changeEvent) {
			AbstractButton abstractButton = (AbstractButton) changeEvent
					.getSource();
			ButtonModel buttonModel = abstractButton.getModel();
			boolean armed = buttonModel.isArmed();
			boolean pressed = buttonModel.isPressed();
			boolean selected = buttonModel.isSelected();

			javax.swing.JToggleButton temp = (javax.swing.JToggleButton) changeEvent
					.getSource();
			if (fastMode) {
				//don't do anything!
			} else if (temp.getText().length() > 3) {
				// not a #, so use text to determine action
				if (!armed && !pressed) {
					//System.out.println("Button " + temp.getText() + " is selected.");
					//System.out.println("Turning on " + temp.getText());
					if (temp.getText().equals("All Red")) {
						allRed(temp.isSelected());
					} else if (temp.getText().equals("All Green")) {
						allGreen(temp.isSelected());
					} else if (temp.getText().equals("All Yellow")) {
						allYellow(temp.isSelected());
					} else if (temp.getText().equals("All Blue")) {
						allBlue(temp.isSelected());
					} else if (temp.getText().equals("All ON")) {
						if (temp.isSelected()) {
							allOn();
						} else {
							allOff();
						}
					} else if (temp.getText().equals("All OFF")) {
						allOff();
					} else if (temp.getText().equals("DEMO")) {
						if (temp.isSelected()) {
							instructionQueue.add(new String[] {"demoMode"});
						} else {
							instructionQueue.add(new String[] {"initLEDs"});
						}
					}
				}
			} else {
				int num = Integer.parseInt(temp.getText());
				if (!armed && !pressed && selected) {
					//System.out.println("Button " + num + " is selected.");
					//System.out.println("Turning on " + num);
					instructionQueue.add(new String[] {"setLed", "" + (num - 1), "255"});
				} else if (!armed && !pressed && !selected) {
					//System.out.println("Button " + num + " is not selected.");
					//System.out.println("Turning off " + num);
					instructionQueue.add(new String[] {"setLed", "" + (num - 1), "0"});
				}
				//instructionQueue.add(new String[] {"refresh"});
			}
		}
	};
	
	public void allRed(boolean state) {
		int i;
		fastMode=true;
		if (state) {
			//System.out.println("Turning on all red");
			instructionQueue.add(new String[] {"setGroup", "red", "255"});
		} else {
			//System.out.println("Turning off all red");
			instructionQueue.add(new String[] {"setGroup", "red", "0"});
		}
		//instructionQueue.add(new String[] {"refresh"});

		for (i = 0; i < redLeds.length; i++) {
			lights[redLeds[i]].setSelected(state);
		}
		me.repaint();
		fastMode=false;
	}

	public void allBlue(boolean state) {
		int i;
		fastMode=true;
		if (state) {
			//System.out.println("Turning on all blue");
			instructionQueue.add(new String[] {"setGroup", "blue", "255"});
		} else {
			//System.out.println("Turning off all blue");
			instructionQueue.add(new String[] {"setGroup", "blue", "0"});
		}
		//instructionQueue.add(new String[] {"refresh"});

		for (i = 0; i < blueLeds.length; i++) {
			lights[blueLeds[i]].setSelected(state);
		}
		me.repaint();
		fastMode=false;
	}

	public void allGreen(boolean state) {
		int i;
		fastMode=true;
		if (state) {
			//System.out.println("Turning on all green");
			instructionQueue.add(new String[] {"setGroup", "green", "255"});
		} else {
			//System.out.println("Turning off all green");
			instructionQueue.add(new String[] {"setGroup", "green", "0"});
		}
		//instructionQueue.add(new String[] {"refresh"});

		for (i = 0; i < greenLeds.length; i++) {
			lights[greenLeds[i]].setSelected(state);
		}
		me.repaint();
		fastMode=false;
	}

	public void allYellow(boolean state) {
		int i;
		fastMode=true;
		if (state) {
			//System.out.println("Turning on all yellow");
			instructionQueue.add(new String[] {"setGroup", "yellow", "255"});
		} else {
			//System.out.println("Turning off all yellow");
			instructionQueue.add(new String[] {"setGroup", "yellow", "0"});
		}
		//instructionQueue.add(new String[] {"refresh"});

		for (i = 0; i < yellowLeds.length; i++) {
			lights[yellowLeds[i]].setSelected(state);
		}
		me.repaint();
		fastMode=false;
	}

	public void allOn() {
		int i;
		fastMode=true;
		//System.out.println("Turning on all leds");
		instructionQueue.add(new String[] {"allOn"});
		for (i = 0; i < lights.length - 3; i++) {
			lights[i].setSelected(true);
		}
		// turn off the allOn button
		//lights[i].setSelected(false);
		me.repaint();
		fastMode=false;
		//System.out.println("Turning on all leds: Done.");
	}

	public void allOff() {
		int i;
		fastMode=true;
		//System.out.println("Turning all leds off");
		instructionQueue.add(new String[] {"allOff"});
		for (i = 0; i < lights.length; i++) {
			lights[i].setSelected(false);
		}
		me.repaint();
		fastMode=false;
		//System.out.println("Turning all leds off: Done.");
	}
	
	private class UpdaterThread extends Thread{
		ArrayList<String[]> instructionQueue = null;
		
		public UpdaterThread(ArrayList<String[]> instructionQueue) {
			this.instructionQueue = instructionQueue;
		}
		
		public void run() {
			System.out.println("Hello from UpdaterThread (" + this + ")!");
			while(true) {
				while(!instructionQueue.isEmpty()) {
					//System.out.println("Have instructions to process!");
					String[] instruction = instructionQueue.remove(0);
					if (instruction != null && instruction.length > 0) {
						//System.out.println("Instruction: " + instruction[0]);
						switch(instruction[0]) {
							case "allOn":
								conn.allOn();
								break;
							case "allOff":
								conn.allOff();
								break;
							case "setLed":
								conn.setLed(instruction[1], instruction[2]);
								break;
							case "setGroup":
								conn.setGroup(instruction[1], instruction[2]);
								break;
							case "refresh":
								conn.refresh();
								break;
							case "demoMode":
								conn.initDemo();
								break;
							case "initLEDs":
								conn.initLights();
								break;
							default:
								System.out.println("Unhandled instruction: " + instruction[0]);
								break;
						}
					} else {
						System.out.println("Received empty instruction!");
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void windowStateChanged(WindowEvent e) {
		displayStateMessage(
				"WindowStateListener method called: windowStateChanged.", e);
	}

	void displayMessage(String msg) {
		System.out.println(msg);
	}

	void displayStateMessage(String prefix, WindowEvent e) {
		int state = e.getNewState();
		int oldState = e.getOldState();
		String msg = prefix + "\n " + "New state: "
				+ convertStateToString(state) + "\n " + "Old state: "
				+ convertStateToString(oldState);
		System.out.println(msg);
	}

	String convertStateToString(int state) {
		if (state == Frame.NORMAL) {
			return "NORMAL";
		}
		if ((state & Frame.ICONIFIED) != 0) {
			return "ICONIFIED";
		}
		// MAXIMIZED_BOTH is a concatenation of two bits, so
		// we need to test for an exact match.
		if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
			return "MAXIMIZED_BOTH";
		}
		if ((state & Frame.MAXIMIZED_VERT) != 0) {
			return "MAXIMIZED_VERT";
		}
		if ((state & Frame.MAXIMIZED_HORIZ) != 0) {
			return "MAXIMIZED_HORIZ";
		}
		return "UNKNOWN";
	}

	public void windowClosing(WindowEvent e) {
		System.out.println("WindowListener method called: windowClosing.");
		System.out.println("Going back to demo mode");
		conn.initDemo();
		System.out.println("Disconnected.");
		frame.dispose();
		System.exit(0);
	}

	public void windowGainedFocus(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowLostFocus(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}
