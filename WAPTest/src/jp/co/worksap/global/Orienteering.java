package jp.co.worksap.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * This problem is a litter bit like TSP(Traveling Salesman Problem), since we all have to pass 
 * all the checkpoints before reaching the final point. We can first solve a sub-problem, that 
 * is the min distance between every meaningful points(start, checkpoint, end). After this, we
 * can enumerate all the permutations of all the points, and sum up all the distance in each 
 * permutation, the minimum value will be the answer. However, this violent method is time-consuming. 
 * 
 * We can use dynamic programming to optimize it. We can use f(s, i) to denote the minimum step that 
 * we are now at point i and have passed previous state s. s is an Integer represented in binary 
 * formation. For instance, f(11010, 2) denotes the minimum step that we are in point 2 and have 
 * already passed point 4,5. So the answer to this question is f(11111, 5), which means we have 
 * passed all the points and now at the last point. 
 *
 * So the transition function is : f(s, i) = min(f(s' , k) + minDis(k, i)),where  k & s == 1; 
 * s' means the state which we are at point k, so we plus the distance for k to i will reach the state i.
 * 
 * 
 * @author Zhengkang Wang
 *
 * @param <E>
 */

public class Orienteering {
	private static final char START = 'S';
	private static final char GOAL = 'G';
	private static final char CHECKPOINT = '@';
	private static final char PASSABLE = '.';
	private static final char BLOCK = '#';

	private static final int N = 100;
	
	private char[][] board;
	private int boardRow;
	private int boardCol;
	private Point startPoint;
	private Point endPoint;
	private Map<Integer, Integer> specialPoint = new HashMap<Integer, Integer>();
	
	/**
	 * Inner class, used to encapsulate point in input map. 
	 *
	 */
	class Point{
		private int dx;
		private int dy;
		private int step;
		public Point(int x, int y, int step){
			this.dx = x;
			this.dy = y;
			this.step = step;
		}
	}

	/**
	 * function to load map, if input is not valid, return -1
	 * @param sc
	 * @return
	 */
	public int loadMap(Scanner sc){
		 boardCol = sc.nextInt();
		 boardRow = sc.nextInt();
		if (boardRow <= 0 || boardRow > N || boardCol <= 0 || boardCol > N){
			return -1;
		}
		board = new char[boardRow][boardCol];
		boolean findStart = false;
		boolean findEnd = false;
		int len = 1;
		for (int i = 0; i < boardRow; i++){
			String temp = sc.next();
			for (int j = 0; j < temp.length(); j++){
				board[i][j] = temp.charAt(j);
				if (board[i][j] == START){
					if (findStart){  // more than one Start;
						return -1;
					}else{
						findStart = true;
						startPoint = new Point(i,j,0);
						specialPoint.put(convertPointToInt(i, j), 0);
					}
				}else if (board[i][j] == GOAL){
					if (findEnd){    // more than one End;
						return -1;
					}else{
						findEnd = true;
						endPoint = new Point(i,j,0); 
					}
				}else if (board[i][j] == CHECKPOINT){
					specialPoint.put(convertPointToInt(i, j), len++);
				}else if (board[i][j] != BLOCK && board[i][j] != PASSABLE){
					return -1;
				}
			}
		}
		specialPoint.put(convertPointToInt(endPoint.dx, endPoint.dy), len);
		if (findStart && findEnd){   
			return 0;
		}else{
			return -1;
		}
	}
	
	/**
	 * Main function to compute the minimum distance.
	 * @return
	 */
	public int computeMinDis(){
		int index = specialPoint.size();
		int minDis[][] = new int[index][index];
		for (int i = 0; i < index; i++){
			for (int j = 0; j < index; j++){
				if (i == j){
					minDis[i][j] = 0;
				}else{
					minDis[i][j] = Integer.MAX_VALUE;
				}
			}
		}
		int res = computeEveryPoints(minDis);
		if (res == -1){          
			return -1;
		}
		if (index == 2){
			return 2*minDis[0][1];
		}
		int dp[][] = new int[1<<index][index+1];
		return findMinDisBack(minDis, dp);
	}
	
	/**
	 * Compute the minimum distance between every two special points.
	 * If any two points cannot connect to each other, return -1
	 * @param minDis
	 * @return
	 */
	public int computeEveryPoints(int[][] minDis){
		int dir[][] = {{-1,0},{1,0},{0,-1},{0,1}};
		for (int i = 0; i < boardRow; i++){
			for (int j = 0; j < boardCol; j++){
				if (isSpecial(i,j)){
					boolean visited[][] = new boolean[boardRow][boardCol];
					Queue<Point> bfsQueue = new LinkedList<Point>();
					visited[i][j] = true;
					bfsQueue.add(new Point(i,j,0));
		
					while (!bfsQueue.isEmpty()){
						Point head = bfsQueue.remove();
						int dx = head.dx;
						int dy = head.dy;
						for (int k = 0; k < 4; k++){
							int nextx = dx + dir[k][0];
							int nexty = dy + dir[k][1];
							if (nextx >= 0 && nextx < boardRow && nexty >= 0 && nexty < boardCol
									&& !visited[nextx][nexty] && board[nextx][nexty] != BLOCK){
								Point next = new Point(nextx,nexty,head.step+1);
								visited[nextx][nexty] = true;
								bfsQueue.add(next);
								
								if (isSpecial(nextx, nexty)){
									int xx = specialPoint.get(i*boardCol + j);
									int yy = specialPoint.get(nextx*boardCol + nexty);
									minDis[xx][yy] = Math.min(minDis[xx][yy], next.step);
								}
							}
						}
					}
				}
			}
		}
			
		int index = specialPoint.size();
		for (int i = 0; i < index; i++){
			for (int j = 0; j < index; j++){
				if (minDis[i][j] == Integer.MAX_VALUE){
					return -1;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Use Dynamic Programming to compute the final result.
	 * @param minDis
	 * @param dp
	 * @return
	 */
	public int findMinDis(int[][] minDis, int[][] dp){
		int len = minDis.length - 2;
		int stateMax = 1 << len;
		for (int s = 0; s < stateMax; s++){
			for (int j = 0; j < len; j++){
				if (s == (1 << j)){
					dp[s][j] = minDis[0][j+1];
					continue;
				}
				dp[s][j] = Integer.MAX_VALUE;
				if (!checkIsBit1(s, j)){
					continue;
				}
				int preState = changeBit(s, j);
				for (int k = 0; k < len; k++){
					if (k != j && checkIsBit1(s, k)){
						dp[s][j] = Math.min(dp[s][j], dp[preState][k] + minDis[k+1][j+1]);
					}
				}
			}
		}
		
		int res = Integer.MAX_VALUE;
		for (int i = 0; i < len; i++){
			res = Math.min(res, dp[stateMax - 1][i] + minDis[i+1][len+1]);
		}
		return res;
	}
	
	public int findMinDisBack(int[][] minDis, int[][] dp){
		int len = minDis.length - 1;
		int stateMax = 1 << len;
		for (int s = 0; s < stateMax; s++){
			for (int j = 0; j < len; j++){
				if (s == (1 << j)){
					dp[s][j] = minDis[0][j+1];
					continue;
				}
				dp[s][j] = Integer.MAX_VALUE;
				if (!checkIsBit1(s, j)){
					continue;
				}
				int preState = changeBit(s, j);
				for (int k = 0; k < len; k++){
					if (k != j && checkIsBit1(s, k)){
						dp[s][j] = Math.min(dp[s][j], dp[preState][k] + minDis[k+1][j+1]);
					}
				}
			}
		}
		
		int res = Integer.MAX_VALUE;
		for (int i = 0; i < len; i++){
			res = Math.min(res, dp[stateMax - 1][i] + minDis[i+1][0]);
		}
		return res;
	}
	
	/**
	 * Check the given bit is 1 or not in the binary representation of data
	 * @param data
	 * @param bitIndex
	 * @return
	 */
	public boolean checkIsBit1(int data, int bitIndex){
		return ((data & (1<<bitIndex)) == 0) ? false : true;
	}
	
	/**
	 * Negation the given bit in the binary representation of input data
	 * @param data
	 * @param bitIndex
	 * @return
	 */
	public int changeBit(int data, int bitIndex){
		return data^(1<<bitIndex);
	}
	
	/**
	 * Convert Point to Integer number, used to map a point to index.
	 * @param dx
	 * @param dy
	 * @return
	 */
	public int convertPointToInt(int dx, int dy){
		return dx*boardCol + dy;
	}
	
	/**
	 * Check whether a point is special point
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isSpecial(int x, int y){
		return board[x][y] == START || board[x][y] == GOAL || board[x][y] == CHECKPOINT;
	}
	
	/**
	 * Entrance of the program, main function.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		while (sc.hasNext()){
			Orienteering ins = new Orienteering();
			if (ins.loadMap(sc) == -1){
				System.out.println(-1);
				break;
			}else{
				System.out.println(ins.computeMinDis());
			}
		}
		sc.close();
	}
}

