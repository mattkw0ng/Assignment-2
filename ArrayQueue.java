public class ArrayQueue<T>{
	public T[] a = (T[]) new Object[10];
	public int first = 0;
	public int last =  -1;

	public T dequeue(){
		//if (empty()){throw new Exception();} else{follow code below}
		T item = a[first];
		first = (first+1)%a.length; //if first exceeds the length of the array then it set it to the first position
		return item;


	}
	public void enqueue(T item){
		int afterlast = (last+1)%a.length; 	//equal to the next index after the last, and if it is at the end of the array, it will equal zero
		int beforefirst = first -1;			//equal to the index before first
		if(first == 0){						//if first is at the beginning, then sets before first to the last element of the array
			beforefirst = a.length-1;
		}
		if(afterlast == beforefirst){	//if the front of the queue is one space away from the back to the queue it will grow the array
			grow_array();
		}
		afterlast = (last+1)%a.length;	//if grow_array is called than the afterlast variable must be reinitialized
		a[afterlast] = item;	//adds the next item to the queue
		last = afterlast;		//increments the last value to last++ but if it goes past the array length, it goes to the beginning of the array
	}
	public boolean empty(){
		if(last-first == -1){	//if the last value is one space behind the first value
			return true;
		}else if(last==a.length-1&&first==0){	//if the last value is at the end of the array and the first value is at the beginning
			return true;
		}else{
			return false;
		}
	}
	private void grow_array(){
		int len = a.length;
		int index = 0;
		T[] new_a = (T[]) new Object[len*2];
		if(first > last){	//if there are values in front of the first value in the array
			for(int i = first; i < a.length ; i++){ //prints from first to the end of the array
				new_a[index++] = a[i];
			}
			for(int j = 0 ; j <= last ; j++){ //prints from the beginning of the array to the last index
				new_a[index++] = a[j];
			}
		}else{
			for(int i = first ; i<=last ; i++){ //prints from the first to last
				new_a[index++] = a[i];
			}
		}

		//resetting the values
		a = new_a;
		first = 0;
		last = index -1;
		
	}
	
}