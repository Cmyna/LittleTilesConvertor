package littleTilesConvertor;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import littleTilesConvertor.convertorBackStage.BlockBuffer;
import littleTilesConvertor.convertorBackStage.schemReadIn;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.File;

import javax.swing.SwingConstants;

public class convertProgress extends JFrame {

	private JPanel contentPane;
	JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					convertProgress frame = new convertProgress(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
		});
	}

	/**
	 * Create the frame.
	 */
	public convertProgress(BlockBuffer bb) {
		setTitle("Converting");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 181);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(14, 73, 404, 48);
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		JLabel message = new JLabel("Converting, please wait.");
		message.setVerticalAlignment(SwingConstants.BOTTOM);
		message.setVerticalTextPosition(SwingConstants.BOTTOM);
		message.setFont(new Font("ËÎÌו", Font.PLAIN, 20));
		message.setBounds(14, 13, 404, 47);
		contentPane.add(message);
	}
	
}
