package jp.co.worksap.global.test;

import jp.co.worksap.global.*;

public class ImmutableQueueTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ImmutableQueueTest().testImmutableQueue();
	}
	
	public void testImmutableQueue(){
		ImmutableQueue<String> ins[] = new ImmutableQueue[5];
		ImmutableQueue<String> origin = new ImmutableQueue<String>();
		ins[0] = origin.enqueue("hello");
		ins[1] = ins[0].enqueue("I");
		ins[2] = ins[1].enqueue("am");
		ins[3] = ins[2].enqueue("Zhengkang");
		ins[4] = ins[3].dequeue();
		
		for (int i = 0; i < 5; i++){
			System.out.print("Queue " + i + ":");
			while(ins[i].size() != 0){
				System.out.print(ins[i].peek() + " ");
				ins[i] = ins[i].dequeue();
			}
			System.out.println();
		}
		
	}

}
