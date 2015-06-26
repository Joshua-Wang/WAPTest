package jp.co.worksap.global;

import java.util.NoSuchElementException;

/**
 * This is an implementation of ImmutableQueue. The key point in designing immutable data structure 
 * is to reuse the original object to construct the new object. Since queue has two pointers(one for 
 * head, one for tail),so we can first design ImmutableList,which has pointers only in one direction.
 * This ImmutableQueue has two ImmutableList : in and out. When enqueue, we add it to in List(cost O(1)); 
 * when dequeue, we pop from the out List, generally it cost O(1),but when the out List is empty, 
 * we need to reverse in List and add them to out List, it cost O(n). So in average , all operation costs O(1).
 * Several things to notice :
 * <p> This class is designed as final, so it avoids the situation that other class extends 
 * ImmutableList and changes it.</p>
 * <p> All the data members of this class is designed as final , which forbids them to be modified.</p>
 * <p> Since ImmutableQueue is immutable , so it's naturally thread-safe.</p> 
 * 
 * @author Zhengkang Wang
 *
 * @param <E>
 */

public final class ImmutableQueue<E> {
	
	private final ImmutableList<E> in;
	private final ImmutableList<E> out;
	
	public ImmutableQueue(){
		this.in = ImmutableList.getEmptyInstance();
		this.out = ImmutableList.getEmptyInstance();
	}
	
	public ImmutableQueue(ImmutableList<E> in, ImmutableList<E> out){
		this.in = in;
		this.out = out;
	}
	
	 /** 
     * Returns the queue that adds an item into the tail of this queue without modifying this queue. 
     * <pre> 
     * e.g. 
     *  When this queue represents the queue (2,1,2,2,6) and we enqueue the value 4 into this queue, 
     *  this method returns a new queue (2,1,2,2,6,4) 
     *  and this object still represents the queue (2,1,2,2,6) 
     * </pre> 
     * @param e 
     * @return 
     */  
	public ImmutableQueue<E> enqueue(E e){
		return new ImmutableQueue<E>(in.push(e), this.out);
		
	}
	
	/** 
     * Returns the queue that removes the object at the head of this queue without modifying this queue. 
     * <pre> 
     * e.g. 
     *  When this queue represents the queue (7,1,3,3,5,1) . 
     *  this method returns a new queue (1,3,3,5,1) 
     *  and this object still represents the queue (7,1,3,3,5,1) 
     * </pre> 
     * If this queue is empty, throws java.util.NoSuchElementException. 
     * @param e 
     * @return 
     */  
	public ImmutableQueue<E> dequeue(){
		if (this.size() == 0){
			throw new NoSuchElementException();
		}
		if (!out.isEmpty()){
			return new ImmutableQueue<E>(in, out.pop());
		}else{
			return new ImmutableQueue<E>(ImmutableList.getEmptyInstance(), in.reverse().pop());
		}
	}
	
	 /** 
     * Looks at the object which is the head of this queue without removing it from the queue. 
     * <pre> 
     * e.g. 
     *  When this queue represents the queue (7,1,3,3,5,1) . 
     *  this method returns 7 and this object still represents the queue (7,1,3,3,5,1) 
     * </pre> 
     * If this queue is empty, throws java.util.NoSuchElementException. 
     * @param e 
     * @return 
     */  
	public E peek(){
		if (this.size() == 0){
			throw new NoSuchElementException();
		}
		if (out.isEmpty()){
			return in.reverse().peek();
		}
		return out.peek();
	}
	
	 /** 
     * Returns the number of objects in this queue. 
     *  
     * @return 
     */  
	public int size(){
		return in.size() + out.size();
	}
	
}
