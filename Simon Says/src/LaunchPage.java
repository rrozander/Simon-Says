import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LaunchPage extends JFrame{
	
	JButton startButton;
	JLabel gameTitle;
	
	LaunchPage() {	
		startButton = new JButton();
		
		startButton.setFont(new Font("MV Boli",Font.BOLD,40));
		startButton.setForeground(Color.red);
		startButton.setText("Start");
		startButton.setBackground(Color.blue);
		startButton.setBounds(150, 300, 250, 80);
		startButton.setFocusable(false);
		startButton.addActionListener(e -> {
			this.dispose();		// Gets rid of launch page after next page is opened
			new GameFrame();
		});
		
		gameTitle = new JLabel();
		
		gameTitle.setFont(new Font("Comic Sans",Font.BOLD,60));
		gameTitle.setForeground(Color.red);
		gameTitle.setText("Simon Says");
		gameTitle.setBounds(100, 100, 400, 200);
		
		this.setTitle("Simon Says");	
		this.getContentPane().setBackground(GamePanel.BACKGROUND);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(null);
		this.add(gameTitle);
		this.add(startButton);
		this.setSize(GameFrame.WIDTH, GameFrame.HEIGHT);
		this.setLocationRelativeTo(null);
		this.setVisible(true);	
	}
}
