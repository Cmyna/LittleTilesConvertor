package littleTilesConvertor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class progressWaiting extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private String message = "Converting to LittleTiles";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			//progressWaiting dialog = new progressWaiting();
			//dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			//dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public progressWaiting(JFrame f) {
		super(f);
		setTitle("Converting");
		setName("Converting");
		setResizable(false);
		setBounds(100, 100, 300, 87);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Exporting to Schematic...");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setFont(new Font("ËÎÌו", Font.PLAIN, 16));
		lblNewLabel.setBounds(29, 15, 234, 25);
		contentPanel.add(lblNewLabel);
	}
}
