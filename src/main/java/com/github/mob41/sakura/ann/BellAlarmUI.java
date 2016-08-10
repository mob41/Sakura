package com.github.mob41.sakura.ann;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BellAlarmUI {
	
	public static boolean running = false;

	private int time = 10;
	private JFrame frame;
	private JLabel lblClose;
	private ActionListener timeout = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			time--;
			lblClose.setText("Automatically close in: " + time + " second(s)");
			if (time <= 0){
				running = false;
				frame.dispose();
			}
		}
		
	};

	/**
	 * Launch the application.
	 */
	public static void start() {
		if (!running){
			running = true;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						BellAlarmUI window = new BellAlarmUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Create the application.
	 */
	public BellAlarmUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				running = false;
				frame.dispose();
			}
		});
		frame.setBounds(100, 100, 480, 320);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JLabel lblImg = new JLabel("");
		lblImg.setIcon(new ImageIcon(BellAlarmUI.class.getResource("/image/icn_bellalarm.png")));
		lblImg.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblDoorBellIs = new JLabel("Door bell is rang!");
		lblDoorBellIs.setHorizontalAlignment(SwingConstants.CENTER);
		lblDoorBellIs.setFont(new Font("Tahoma", Font.BOLD, 34));
		
		JButton btnNotified = new JButton("Notified");
		btnNotified.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				running = false;
				frame.dispose();
			}
		});
		
		lblClose = new JLabel("Automatically close in: 10 second(s)");
		lblClose.setHorizontalAlignment(SwingConstants.CENTER);
		lblClose.setFont(new Font("Tahoma", Font.PLAIN, 27));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDoorBellIs, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
						.addComponent(lblImg, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
						.addComponent(btnNotified, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
						.addComponent(lblClose, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblImg)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDoorBellIs, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblClose)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnNotified, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		Timer timer = new Timer(1000, timeout );
		timer.start();
	}
}
