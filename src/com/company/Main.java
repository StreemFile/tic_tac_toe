package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static Scanner in = new Scanner(System.in);
	private static Board board = new Board();

	private static boolean gameEnded = false;
	private static boolean player = true;

	public static void main(String[] args) {
		System.out.println(board);
		while (!gameEnded) {
			Position position = null;
			if (player) {
				position = makeMove();
				board = new Board(board, position, PlayerSign.Cross);
			} else {
				board = findBestMove(board);
			}
			player = !player;
			System.out.println(board);
			evaluateGame();
		}
	}

	private static Board findBestMove(Board board) {
		List<Position> positions = board.getFreePositions();
		Board bestChild = null;
		int previous = -1;
		for (Position p : positions) {
			Board child = new Board(board, p, PlayerSign.Circle);
			int current = supposePlayerMove(child);
			if (current > previous) {
				bestChild = child;
				previous = current;
			}
		}
		return bestChild;
	}

	public static int supposePlayerMove(Board board) {
		GameState gameState = board.getGameState();
		if (gameState == GameState.CircleWin) {
			return 1;
		} else if (gameState == GameState.CrossWin) {
			return -1;
		} else if (gameState == GameState.Draw) {
			return 0;
		}
		List<Position> positions = board.getFreePositions();
		int best = 1;
		for (Position p : positions) {
			Board b = new Board(board, p, PlayerSign.Cross);
			int move = supposeComputerMove(b);
			if (move < best) {
				best = move;
			}
		}
		return best;
	}

	public static int supposeComputerMove(Board board) {
		GameState gameState = board.getGameState();
		if (gameState == GameState.CircleWin) {
			return 1;
		} else if (gameState == GameState.CrossWin) {
			return -1;
		} else if (gameState == GameState.Draw) {
			return 0;
		}
		List<Position> positions = board.getFreePositions();
		int best = -1;
		for (Position p : positions) {
			Board b = new Board(board, p, PlayerSign.Circle);
			int move = supposePlayerMove(b);
			if (move > best) {
				best = move;
			}
		}
		return best;
	}

	private static void evaluateGame() {
		GameState gameState = board.getGameState();
		gameEnded = true;
		switch (gameState) {
		case CrossWin:
			System.out.println("You Won!");
			break;
		case CircleWin:
			System.out.println("Computer Won!");
			break;
		case Draw:
			System.out.println("Draw!");
			break;
		default:
			gameEnded = false;
			break;
		}
	}

	public static Position makeMove() {
		Position position = null;
		while (true) {
			System.out.print("Pick 0, 1 or 2 for column: ");
			int column = getColOrRow();
			System.out.print("Pick 0, 1 or 2 for row: ");
			int row = getColOrRow();
			position = new Position(column, row);
			if (board.isMarked(position)) {
				System.out.println("Already marked!");
			} else {
				break;
			}
		}
		return position;
	}

	private static int getColOrRow() {
		int ret = -1;
		while (true) {
			try {
				ret = Integer.parseInt(in.nextLine());
			} catch (NumberFormatException e) {
			}
			if (ret < 0 || ret > 2) {
				System.out.print("\nInvalid input. Please pick 0, 1 or 2: ");
			} else {
				break;
			}
		}
		return ret;
	}
}

final class Position {
	private final int column;
	private final int row;

	public Position(int column, int row) {
		this.column = column;
		this.row = row;
	}

	public int getRow() {
		return this.row;
	}

	public int getColumn() {
		return this.column;
	}
}

enum PlayerSign {
	Cross, Circle
}

enum GameState {
	Incomplete, CrossWin, CircleWin, Draw
}

class Board {
	private char[][] board; //e = empty, x = cross, o = circle.

	public Board() {
		board = new char[3][3];
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[x][y] = 'e'; //Board initially empty
			}
		}
	}

	public Board(Board from, Position position, PlayerSign sign) {
		board = new char[3][3];
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				board[x][y] = from.board[x][y];
			}
		}
		board[position.getColumn()][position.getRow()] = sign == PlayerSign.Cross ? 'x' : 'o';
	}

	public List<Position> getFreePositions() {
		List<Position> retArr = new ArrayList<>();
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (board[x][y] == 'e') {
					retArr.add(new Position(x, y));
				}
			}
		}
		return retArr;
	}

	public GameState getGameState() {
		if (hasWon('x')) {
			return GameState.CrossWin;
		} else if (hasWon('o')) {
			return GameState.CircleWin;
		} else if (getFreePositions().size() == 0) {
			return GameState.Draw;
		} else {
			return GameState.Incomplete;
		}
	}

	private boolean hasWon(char sign) {
		int x, y;

		//Check diagonals
		if (board[0][0] == sign && board[1][1] == sign && board[2][2] == sign) {
			return true;
		}
		if (board[0][2] == sign && board[1][1] == sign && board[2][0] == sign) {
			return true;
		}

		//Check row
		for (x = 0; x < 3; x++) {
			for (y = 0; y < 3; y++) {
				if (board[x][y] != sign) {
					break;
				}
			}
			if (y == 3) {
				return true;
			}
		}

		//Check col
		for (x = 0; x < 3; x++) {
			for (y = 0; y < 3; y++) {
				if (board[y][x] != sign) {
					break;
				}
			}
			if (y == 3) {
				return true;
			}
		}
		return false;
	}

	public boolean isMarked(Position position) {
		if (board[position.getColumn()][position.getRow()] != 'e') {
			return true;
		}
		return false;
	}

	public String toString() {
		String retString = "\n";
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (board[x][y] == 'x' || board[x][y] == 'o') {
					retString += "[" + board[x][y] + "]";
				} else {
					retString += "[ ]";
				}
			}
			retString += "\n";
		}
		return retString;
	}

}

