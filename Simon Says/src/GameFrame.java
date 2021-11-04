import javax.swing.JFrame;


public class GameFrame extends JFrame{

	GamePanel gamePanel;
		
	GameFrame() {		 
		gamePanel = new GamePanel();
		 		 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(gamePanel);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.pack();		 
	 }
	
}
