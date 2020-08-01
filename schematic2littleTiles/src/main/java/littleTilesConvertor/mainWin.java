package littleTilesConvertor;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JTextPane;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JTextArea;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import littleTilesConvertor.convertorBackStage.BlockBuffer;
import littleTilesConvertor.convertorBackStage.schemReadIn;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.DefaultComboBoxModel;

public class mainWin extends JFrame {

	private JPanel contentPane;
	private String littleTilesJson = "";
	private JTextArea textArea;
	private JLabel scheDir;
	private File file;
	private JComboBox Grid;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//setting UI form as system form
		if(UIManager.getLookAndFeel().isSupportedLookAndFeel()){
			final String platform = UIManager.getSystemLookAndFeelClassName();
			// If the current Look & Feel does not match the platform Look & Feel,
			// change it so it does.
			if (!UIManager.getLookAndFeel().getName().equals(platform)) {
				try {
					UIManager.setLookAndFeel(platform);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainWin frame = new mainWin();
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
	public mainWin() {
		setResizable(false);
		setTitle("LittleTiles Convertor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 664, 454);
		contentPane = new JPanel();
		contentPane.setAlignmentX(2.0f);
		contentPane.setAlignmentY(2.0f);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel schem = new JPanel();
		schem.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		schem.setBounds(14, 13, 630, 142);
		contentPane.add(schem);
		schem.setLayout(null);
		
		JLabel stitle = new JLabel("Schematic to LittleTiles");
		stitle.setFont(new Font("Calibri", Font.BOLD, 24));
		stitle.setHorizontalAlignment(SwingConstants.CENTER);
		stitle.setBounds(181, 5, 267, 40);
		schem.add(stitle);
		
		JLabel text1 = new JLabel("choose file");
		text1.setFont(new Font("宋体", Font.PLAIN, 18));
		text1.setHorizontalAlignment(SwingConstants.CENTER);
		text1.setBounds(31, 51, 110, 30);
		schem.add(text1);
		
		scheDir = new JLabel("...");
		scheDir.setFont(new Font("宋体", Font.PLAIN, 16));
		scheDir.setBorder(new LineBorder(new Color(0, 0, 0)));
		scheDir.setBounds(143, 54, 300, 28);
		schem.add(scheDir);
		
		JButton fileChooser = new JButton("...");
		fileChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// a file selector view
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(schematicFilter);
				chooser.setFileFilter(schematicFilter);
				chooser.setMultiSelectionEnabled(false);
				chooser.showDialog(new JLabel(), "select");
				file = chooser.getSelectedFile();
				if (file == null) return;
				scheDir.setText(file.getPath()); 
			}
		});
		fileChooser.setBounds(452, 54, 30, 28);
		schem.add(fileChooser);
		
		JButton toLT = new JButton("convert");
		toLT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file == null) return;
				//converting schematic to ltjson
				//make another frame to show a progress bar
				BlockBuffer bb = new BlockBuffer();
				Thread back = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						bb.initBySchemFile(file,Integer.parseInt(Grid.getSelectedItem().toString()));
						bb.greedyMeshing();
						String lt = bb.getLTJson();
						if (lt.length()>=250000) {
							littleTilesJson = lt;
							lt = lt.substring(0, 200) + "...\n\n\n\n\nUse Copy Buttion to get full Text";
						}
						textArea.setText(lt);
					}
					
				}, "back");
				/*convertProgress cp = new convertProgress(bb);
				cp.setVisible(true);*/
				
				back.start();
			}
		});
		toLT.setFont(new Font("宋体", Font.PLAIN, 20));
		toLT.setBounds(204, 95, 223, 34);
		schem.add(toLT);
		
		JLabel gridText = new JLabel("Grid");
		gridText.setFont(new Font("宋体", Font.PLAIN, 18));
		gridText.setBounds(504, 55, 72, 25);
		schem.add(gridText);
		
		Grid = new JComboBox();
		Grid.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "4", "8", "16", "32"}));
		Grid.setBounds(546, 56, 53, 25);
		schem.add(Grid);
		
		JPanel littleTiles = new JPanel();
		littleTiles.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		littleTiles.setBounds(14, 162, 630, 247);
		contentPane.add(littleTiles);
		littleTiles.setLayout(null);
		
		JLabel ttitle = new JLabel("LittleTiles to Schematic");
		ttitle.setBounds(200, 10, 229, 30);
		ttitle.setHorizontalAlignment(SwingConstants.CENTER);
		ttitle.setFont(new Font("Calibri", Font.BOLD, 24));
		littleTiles.add(ttitle);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(14, 50, 455, 184);
		littleTiles.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		//textArea.setWrapStyleWord(true);
		scrollPane.setViewportView(textArea);
		
		JButton toSche = new JButton("export");
		toSche.setFont(new Font("宋体", Font.PLAIN, 20));
		toSche.setBounds(495, 204, 121, 30);
		littleTiles.add(toSche);
		
		JButton copy = new JButton("copy");
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//littleTilesJson = textArea.getText();
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(littleTilesJson), null);
			}
		});
		copy.setFont(new Font("宋体", Font.PLAIN, 20));
		copy.setBounds(495, 50, 121, 30);
		littleTiles.add(copy);
		
		JButton paste = new JButton("paste");
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// get the clipboard string from system
				Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if (trans != null) {
					if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						try {
							littleTilesJson = (String) trans.getTransferData(DataFlavor.stringFlavor);
							String lt = littleTilesJson;
							if (littleTilesJson.length() >= 200000) lt = littleTilesJson.substring(0, 200);
							textArea.setText(lt);
						} catch (UnsupportedFlavorException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
					
			}
		});
		paste.setFont(new Font("宋体", Font.PLAIN, 20));
		paste.setBounds(495, 87, 121, 30);
		littleTiles.add(paste);
	}
	
	
	private FileFilter schematicFilter = new FileFilter() {

		@Override
		public boolean accept(File arg0) {
			// TODO Auto-generated method stub
			if (arg0.getName().endsWith("schematic")) return true;
			if (arg0.isDirectory()) return true;
			return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "schematic";
		}
		
	};
}
