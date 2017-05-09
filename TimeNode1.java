import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeNode1 {
	private long time;
	

	 private Lock lock0 =new ReentrantLock(true);
	private TimeNode1 (long t){
		time=t;
	}
	private TimeNode1 (){
		
	}
	 private static final  TimeNode1 instance =new TimeNode1();
	 public static TimeNode1 getInstance(){
	    	
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
