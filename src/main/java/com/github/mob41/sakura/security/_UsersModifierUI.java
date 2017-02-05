package com.github.mob41.sakura.security;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.awt.event.ActionEvent;

public class _UsersModifierUI {

	private JFrame frame;
	private JLabel lblStatus;
	private JTextField userField;
	private JPasswordField pwdField;
	private UserManager um;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					_UsersModifierUI window = new _UsersModifierUI();
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
	public _UsersModifierUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		um = new UserManager(null, 100);
		frame.setBounds(100, 100, 539, 155);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		lblStatus = new JLabel("Status: Ready");
		
		JLabel lblUsername = new JLabel("Username:");
		
		userField = new JTextField();
		userField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		
		pwdField = new JPasswordField();
		
		JButton btnRemoveUsername = new JButton("Remove username");
		btnRemoveUsername.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = um.getUsernameIndex(userField.getText());
				
				if (index == -1){
					lblStatus.setText("Status: Username does not exist (" + Calendar.getInstance().getTime().toString() + ")");
					return;
				}
				
				boolean success = um.removeUser(index);
				
				if (!success){
					lblStatus.setText("Status: Returned unsuccessful (" + Calendar.getInstance().getTime().toString() + ")");
					return;
				}

				try {
					lblStatus.setText("Status: Write file success. (" + Calendar.getInstance().getTime().toString() + ")");
					um.writeFile();
				} catch (IOException e1) {
					lblStatus.setText("Status: Errored. Check stack trace (" + Calendar.getInstance().getTime().toString() + ")");
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnAddUser = new JButton("Add user");
		btnAddUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = userField.getText();
				String pwd = new String(pwdField.getPassword());
				
				boolean success = um.addUser(user, pwd);
				
				if (!success){
					lblStatus.setText("Status: Returned unsuccessful (" + Calendar.getInstance().getTime().toString() + ")");
					return;
				}
				
				try {
					lblStatus.setText("Status: Write file success. (" + Calendar.getInstance().getTime().toString() + ")");
					um.writeFile();
				} catch (IOException e1) {
					lblStatus.setText("Status: Errored. Check stack trace (" + Calendar.getInstance().getTime().toString() + ")");
					e1.printStackTrace();
				}
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblUsername)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(userField, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblPassword)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pwdField, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnRemoveUsername, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAddUser, GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(userField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(pwdField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnRemoveUsername)
						.addComponent(btnAddUser))
					.addContainerGap(217, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
