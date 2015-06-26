package jp.co.worksap.global;

/**
 * ImmutableList is an implementation of immutable stack . It is designed to reuse the 
 * original object to construct new object. Both push and pop  operation cost O(1). 
 * Several things to notice :
 * <p> This class is designed as final, so it avoids the situation that other class 
 * extends ImmutableList and changes it.</p>
 * <p> All the data members of this class is designed as final , which forbids them 
 * to be modified.</p>
 * <p> It uses Singleton to provide empty instance, because this class is immutable, 
 * so we only need one empty instance, Singleton fits well</p>
 * 
 * @author Zhengkang Wang
 *
 * @param <E>
 */

public final class ImmutableList<E> {
	
	private final E value;
	private final ImmutableList<E> next;
	private final int size;
	
	private static ImmutableList instance = null;
	
	private ImmutableList(){
		this.value = null;
		this.next = null;
		this.size = 0;
	}
	
	public static synchronized ImmutableList getEmptyInstance(){
		if (instance == null){
				instance = new ImmutableList();
		}
		return instance;
	}
	
	public ImmutableList(ImmutableList<E> ins, E value){
		this.next = ins;
		this.value = value;
		this.size = ins.size + 1;
	}
	
	public ImmutableList<E> push(E e){
		return new ImmutableList<E>(this, e);
	}
	
	public ImmutableList<E> pop(){
		return this.next;
	}
	
	public E peek(){
		return this.value;
	}
	
	public int size(){
		return this.size;
	}
	
	public boolean isEmpty(){
		return this.size == 0;
	}
	
	public ImmutableList<E> reverse(){
		ImmutableList<E> ins = ImmutableList.getEmptyInstance();
		ImmutableList<E> temp = this;
		while (temp.isEmpty() == false){
			ins = ins.push(temp.peek());
			temp = temp.pop();
		}
		return ins;
	}
}
