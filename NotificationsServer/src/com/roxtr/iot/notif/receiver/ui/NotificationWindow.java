package com.roxtr.iot.notif.receiver.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import com.roxtr.iot.notif.receiver.NotificationManager;

public class NotificationWindow extends JWindow implements MouseListener,
		MouseMotionListener {

	private static final long serialVersionUID = 4242691207900739762L;

	private JLabel mTitle;
	private JLabel mMessage;
	private ImageIcon mBgImage = null;
	private JLabel mCloseBtn = null;
	
	private JPanel mCompPanel = null;

	public NotificationWindow(String title, String message) {
		mTitle = new JLabel(title);
		mMessage = new JLabel(message);

		_init();
	}

	private void _init() {
		mBgImage = new ImageIcon(getClass().getResource("background.png"));
		Dimension dims = new Dimension(mBgImage.getIconWidth(),
				mBgImage.getIconHeight());
		
		mCompPanel = newComponentPanel();
		mCompPanel.setSize(dims);

		getContentPane().add("Center", mCompPanel);
		
		pack();

		setSize(dims);
		setAlwaysOnTop(true);
		setLocation(0, 0);
	}

	private JPanel newComponentPanel() {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				g.drawImage(mBgImage.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
		panel.setLayout(null);
		panel.setOpaque(false);

		mTitle.setForeground(Color.WHITE);
		Insets insets = panel.getInsets();
		Dimension size = mTitle.getPreferredSize();
		mTitle.setBounds(5 + insets.left, 5 + insets.top,
	             size.width, size.height);
		
		Dimension size2 = mMessage.getPreferredSize();
		mMessage.setBounds(5 + insets.left, 30 + insets.top,
				size2.width, size2.height);
		
		mCloseBtn = new JLabel(new ImageIcon(
				getClass().getResource("close.png")));
		mCloseBtn.setBackground(new Color(Color.OPAQUE));
		
		Dimension size3 = mCloseBtn.getPreferredSize();
		mCloseBtn.setBounds(insets.left + mBgImage.getIconWidth() - size3.width - 5,
				1 + insets.top,
				size3.width, size3.height);
		
		mCloseBtn.addMouseListener(this);
		mCloseBtn.addMouseMotionListener(this);
		
		// add title, message!
		panel.add(mTitle);
		panel.add(mMessage);
		panel.add(mCloseBtn);

		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);

		return panel;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource().equals(mCloseBtn)) {
			dispose();
			NotificationManager.getInstance().closeNotification();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(e.getSource().equals(mCloseBtn)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			mBgImage = new ImageIcon(getClass().getResource("background_hover.png"));
			repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(e.getSource().equals(mCloseBtn)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			mBgImage = new ImageIcon(getClass().getResource("background.png"));
			repaint();
		}
	}

	int preX, preY;
	@Override
	public void mousePressed(MouseEvent e) {
		if(!e.getSource().equals(mCloseBtn)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
		
		preX = e.getX();
		preY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!e.getSource().equals(mCloseBtn)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setLocation(getLocation().x + (e.getX() - preX), 
				getLocation().y +  (e.getY() - preY));
		
		repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
