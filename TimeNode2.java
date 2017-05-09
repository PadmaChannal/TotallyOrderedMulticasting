import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeNode2 {
	private long time;
	

	private Lock lock0 =new ReentrantLock(true);
	private TimeNode2 (long t){
		time=t;
	}
	private TimeNode2 (){
		
	}
	 private static final  TimeNode2 instance =new TimeNode2();
	 public static TimeNode2 getInstance(){
	    	
	        return instance;}
	public void setTime(long t){
		if(lock0.tryLock()){
			time=t;
			lock0.unlock();
			}
	}
	public long getTime(){
		return time;
	}


	public void setIncrementTime(long t){
		if(lock0.tryLock()){
		t=time+1;
		time=t;
		lock0.unlock();
		}
	}

}
