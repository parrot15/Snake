import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;
import static java.lang.System.*;

public class Snake extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	private char[][] positions = new char[48][31];
	private ArrayList<Coordinates> snakeCoords = new ArrayList<Coordinates>();
	
	private int score = 0;
	private boolean addNextCoords = false;
	private int timerCallCount = 0;
	
	private boolean isStartGame = true;
	
	private String currentDir = "";
//	private ArrayList<String> directions = new ArrayList<String>();
	private Timer moveTimer = new Timer(75, new MoveListener());
	
	public Snake() throws IOException {
		setTitle("Snake");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.white);
		
		JPanel basePanel = new JPanel();
		basePanel.setBackground(Color.white);
		basePanel.setPreferredSize(new Dimension(1000, 710));
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		
		JPanel scorePanel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2d.setFont(new Font("SansSerif", Font.PLAIN, 15));
				g2d.setColor(Color.white);
				g2d.drawString("Score: " + score, 450, 40);
			}
		};
		scorePanel.setBackground(Color.black);
		scorePanel.setPreferredSize(new Dimension(1000, 50));
		basePanel.add(scorePanel);
		
		JPanel gamePanel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				if (isStartGame) {
					snakeCoords.add(new Coordinates((new Random()).nextInt(48), (new Random()).nextInt(31)));
					addFood();
					isStartGame = false;
				}
				for (int row = 0; row < positions.length; row++) {
					for (int col = 0; col < positions[row].length; col++) {
						if (positions[row][col] != 'f') {
							positions[row][col] = '\u0000';
						}
					}
				}
				if (!isFoodPresent()) {
					out.println("Food put");
					addFood();
					score += 5;
					addNextCoords = true;
				}
				for (int i = 0; i < snakeCoords.size(); i++) {
					if ((snakeCoords.get(i).getX() < 0 || snakeCoords.get(i).getX() > 47 || 
							snakeCoords.get(i).getY() < 0 || snakeCoords.get(i).getY() > 30) || 
							(snakeCoords.get(i).getX() == snakeCoords.get(snakeCoords.size() - 1).getX() && 
							snakeCoords.get(i).getY() == snakeCoords.get(snakeCoords.size() - 1).getY() && 
							i != snakeCoords.size() - 1 && snakeCoords.size() > 1)) {
						moveTimer.stop();
						handleLoss();
						break;
					} else {
						out.println(snakeCoords.size());
						positions[snakeCoords.get(i).getX()][snakeCoords.get(i).getY()] = 's';
					}
				}
				for (int row = 0; row < positions.length; row++) {
					for (int col = 0; col < positions[row].length; col++) {
						if (positions[row][col] == 's') {
							g2d.setColor(Color.lightGray);
							g2d.fillRect(row * 20 + 20 + 1, col * 20 + 20 + 1, 18, 18);
						}
						if (positions[row][col] == 'f') {
							g2d.setColor(Color.gray);
							g2d.fillRect(row * 20 + 20 + 1, col * 20 + 20 + 1, 18, 18);
						}
						if (positions[row][col] == '\u0000') {
							g2d.setColor(Color.white);
							g2d.fillRect(row * 20 + 20, col * 20 + 20, 20, 20);
						}
					}
				}
			}
		};
		gamePanel.setBackground(Color.white);
		gamePanel.setPreferredSize(new Dimension(1000, 660));
		gamePanel.setBorder(BorderFactory.createLineBorder(Color.black, 20));
		gamePanel.addKeyListener(this);
		gamePanel.setFocusable(true);
		gamePanel.requestFocusInWindow();
		basePanel.add(gamePanel);
		
		getContentPane().add(basePanel);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new Snake();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	
	public void addFood() {
		int foodX = (new Random()).nextInt(48);
		int foodY = (new Random()).nextInt(31);
		if (positions[foodX][foodY] == '\u0000') {
			positions[foodX][foodY] = 'f';
		} else {
			addFood();
		}
	}
	
	public boolean isFoodPresent() {
		for (int row = 0; row < positions.length; row++) {
			for (int col = 0; col < positions[row].length; col++) {
				if (positions[row][col] == 'f') {
					return true;
				}
			}
		}
		return false;
	}
	
	public void handleLoss() {
		JDialog loseDialog = new JDialog(this, "You lost");
		loseDialog.setBackground(Color.white);
		loseDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		loseDialog.setResizable(false);
		loseDialog.setLayout(new BorderLayout());
		
		JPanel basePanel = new JPanel();
		basePanel.setBackground(Color.white);
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(Color.white);
		messagePanel.setPreferredSize(new Dimension(250, 30));
		messagePanel.add(new JLabel("You lost. Do you want to play again?"));
		basePanel.add(messagePanel);
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(Color.white);
		buttonPanel.setPreferredSize(new Dimension(250, 30));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		JButton yes = new JButton("Yes");
		yes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				positions = new char[48][31];
				snakeCoords.clear();
				score = 0;
				addNextCoords = false;
				timerCallCount = 0;
				isStartGame = true;
				currentDir = "";
				moveTimer.stop();
				loseDialog.dispose();
				repaint();
			}
		});
		buttonPanel.add(yes);
		
		JButton no = new JButton("No");
		no.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		buttonPanel.add(no);
		
		basePanel.add(buttonPanel);
		
		loseDialog.getContentPane().add(basePanel);
		loseDialog.pack();
		loseDialog.setModal(true);
		loseDialog.setLocationRelativeTo(this);
		loseDialog.setVisible(true);
	}
	
	private class Coordinates {
		int x, y;
		
		public Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}
	
	private class MoveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			timerCallCount++;
			out.println("timer action called");
			out.println(timerCallCount);
			if (currentDir.equals("left")) {
				snakeCoords.add(new Coordinates(snakeCoords.get(snakeCoords.size() - 1).getX() - 1, 
						snakeCoords.get(snakeCoords.size() - 1).getY()));
			} else if (currentDir.equals("right")) {
				snakeCoords.add(new Coordinates(snakeCoords.get(snakeCoords.size() - 1).getX() + 1, 
						snakeCoords.get(snakeCoords.size() - 1).getY()));
			} else if (currentDir.equals("up")) {
				snakeCoords.add(new Coordinates(snakeCoords.get(snakeCoords.size() - 1).getX(), 
						snakeCoords.get(snakeCoords.size() - 1).getY() - 1));
			} else if (currentDir.equals("down")) {
				snakeCoords.add(new Coordinates(snakeCoords.get(snakeCoords.size() - 1).getX(), 
						snakeCoords.get(snakeCoords.size() - 1).getY() + 1));
			}
			if (!addNextCoords) {
				snakeCoords.remove(0);
				timerCallCount = 0;
			}
			if (timerCallCount == 5) {
				addNextCoords = false;
				timerCallCount = 0;
			}
			repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// make an arrayList and add the directions to it, then loop through the arrayList in the moveTimer
		// probably won't work because there won't be any delay between processing the directions in the arrayList
		// to fix this, maybe just dedicate the moveTimer to delay, and put the movement processes somewhere else
		
//		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
//			directions.add("left");
//		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
//			directions.add("right");
//		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
//			directions.add("up");
//		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
//			directions.add("down");
//		}
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			out.println("Left key pressed");
			if (currentDir != "right") {
				currentDir = "left";
			}
			moveTimer.start();
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			out.println("Right key pressed");
			if (currentDir != "left") {
				currentDir = "right";
			}
			moveTimer.start();
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			out.println("Up key pressed");
			if (currentDir != "down") {
				currentDir = "up";
			}
			moveTimer.start();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			out.println("Down key pressed");
			if (currentDir != "up") {
				currentDir = "down";
			}
			moveTimer.start();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}