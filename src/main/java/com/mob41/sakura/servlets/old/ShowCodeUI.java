package com.mob41.sakura.servlets.old;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ShowCodeUI {

	private int count = 10;

	protected static boolean isRunning = false;
	private ActionListener countdown = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			count--;
			lblTimeout.setText("Code will be invaild in " + count + " seconds");
			if (count <= 0){
				LoginServlet.removeCode(localcode);
				timer.stop();
				isRunning = false;
				frame.dispose();
			}
		}
		
	};
	
	private String localcode = "-- Error --";
	private JFrame frame;
	private Timer timer = new Timer(1000, countdown);
	private JLabel lblTimeout;
	
	

	/**
	 * Launch the application.
	 */
	protected static void start(final String code) {
		isRunning = true;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try{
					ShowCodeUI window = new ShowCodeUI(code);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	protected ShowCodeUI(String code) {
		localcode = code;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("ShowCodeUI");
		frame.setUndecorated(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setBounds(100, 100, 480, 320);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JLabel lblRegisteringCode = new JLabel("Verify Code");
		lblRegisteringCode.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegisteringCode.setFont(new Font("Tahoma", Font.BOLD, 45));
		
		JLabel lblCode = new JLabel("<Code>");
		lblCode.setText(localcode);
		lblCode.setHorizontalAlignment(SwingConstants.CENTER);
		lblCode.setFont(new Font("Tahoma", Font.PLAIN, 38));
		
		lblTimeout = new JLabel("Code will be invaild in 10 seconds");
		lblTimeout.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTimeout.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblRegisteringCode, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
				.addComponent(lblCode, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
				.addComponent(lblTimeout, GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(33)
					.addComponent(lblRegisteringCode, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCode, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
					.addComponent(lblTimeout)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
		timer.start();
		
	}
}
