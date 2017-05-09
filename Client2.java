import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
class Multicast2 implements Runnable
{

    Socket c0,c1;
    String type,ach;
    PrintWriter out0,out1;
    int pId;
    private Lock lock0 =new ReentrantLock(true);
    TimeNode2 time= TimeNode2.getInstance();
    Multicast2(Socket c0,Socket c1,String type,String ach) throws IOException
    {

    	this.c0=c0;
    	this.c1=c1;
    	this.type=type;
    	this.ach=ach;


    }
    Multicast2(Socket c0,Socket c1,String type,int pId) throws IOException
    {

    	this.c0=c0;
    	this.c1=c1;
    	this.type=type;
    	this.pId=pId;


    }

	public void run()
    {
		if(type.equalsIgnoreCase("ack")){
			try
	        {
	    		out0 =new PrintWriter(c0.getOutputStream(), true);
	    		out1 =new PrintWriter(c1.getOutputStream(), true);
	    		if(lock0.tryLock()){
	    		//time.setIncrementTime(time.getTime());
	    		//System.out.println(time.getTime()+" before send ach 1");
	    		out0.println(type+","+ach);
	    		out0.flush();
				 lock0.unlock();
	    		}
	    		if(lock0.tryLock()){
	        		//time.setIncrementTime(time.getTime());
	        		//System.out.println(time.getTime()+" before send ach 2");
	        		out1.println(type+","+ach);
	        		out1.flush();
	    			 lock0.unlock();
	        		}
	          }
	          catch(Exception e)
	          {
	              e.printStackTrace();
	          }
	    	}
    	if(type.equalsIgnoreCase("update")){
		try
        {
    		out0 =new PrintWriter(c0.getOutputStream(), true);
    		out1 =new PrintWriter(c1.getOutputStream(), true);
    		if(lock0.tryLock()){
    		//time.setIncrementTime(time.getTime());
    		//System.out.println(time.getTime()+" before send 1");
    		out0.println(type+","+ach);
    		out0.flush();
			 /*lock0.unlock();
    		}
    		if(lock0.tryLock()){
        		time.setIncrementTime(time.getTime());*/
        		//System.out.println(time.getTime()+" before send 2");
        		out1.println(type+","+ach);
        		out1.flush();
    			 lock0.unlock();
        		}
          }
          catch(Exception e)
          {
              e.printStackTrace();
          }
    	}
    }
}
public class Client2 {

	public static void main(String[] args) {
		 TreeMap<Double,Integer> tm = new TreeMap<Double,Integer>();
		 HashMap<Double,Integer> ackHash =new HashMap<Double,Integer>();

		 DecimalFormat df = new DecimalFormat("#.0");
		TimeNode2 time= TimeNode2.getInstance();
		double time1=0;
		long ave=0;
		 int multicastFlag=0;
		try{
			 Socket clientSock0=new Socket("localhost",1234);
			 Socket clientSock1=new Socket("localhost",1235);
		      BufferedReader input0 =new BufferedReader(new InputStreamReader(clientSock0.getInputStream()));
		      BufferedReader input1 =new BufferedReader(new InputStreamReader(clientSock1.getInputStream()));
		      PrintWriter out0 =new PrintWriter(clientSock0.getOutputStream(), true);
		      PrintWriter out1 =new PrintWriter(clientSock1.getOutputStream(), true);
			 System.out.println("Client2 is connected ");
			 out0.println("Set time");
			 out1.println("Set time");
			 String answer0 = input0.readLine();
			 String answer1 = input1.readLine();
			 time1=time1+  Double.parseDouble(answer0);
			 time1=time1+  Double.parseDouble(answer1);
			 time1=time1+System.currentTimeMillis();
			 ave=(long)(time1/3);
			 out0.println(ave);
			 out1.println(ave);
			 time.setTime(ave);
			 System.out.println("node 2 time is "+time.getTime());
			// multicast
			 try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			 double timeM;
			/* time.setIncrementTime(time.getTime());
			 timeM=Double.parseDouble(time.getTime()+".2");
			tm.put(timeM,0);
			 new Thread(new Multicast2(clientSock0,clientSock1,"UPDATE",2)).start();*/

			/* for(int i=0;i<2;i++){
		    	  time.setIncrementTime(time.getTime());
			       timeM=Double.parseDouble(time.getTime()+".2");
					tm.put(timeM,0);
			      new Thread(new Multicast2(clientSock0,clientSock1,"UPDATE",time.getTime()+".2")).start();
			 }*/
			 int j=1;
			    while(j<=6){
			    	j++;
			    	  String update0=input0.readLine();
			    	  String update1=input1.readLine();
			    	  //System.out.println(update0);

			    	  long t=0;
			    	  double dt=0;

			    	  try{
			      if(update0.contains("UPDATE")){

			    	  System.out.println(update0+" node 2");
			    	  update0=update0.replace("UPDATE,", "");
			    	  dt=Double.parseDouble(update0);
			    	  t=(long)dt;
			    	  if(t==time.getTime()){
			    		  time.setTime(time.getTime()+1);

			    	  }
			    	  else{
			    		  time.setTime(Math.max(t,time.getTime())+1);
			    	  }

			    	  if(ackHash.containsKey(dt)){

		    			  int k=(int) ackHash.get(dt);
		    			  tm.put(dt,k+1);
		    			  ackHash.remove(dt);
		    		  }
			    	  else{
			    	  tm.put(dt,1);
			    	  }
			    	  new Thread(new Multicast2(clientSock0,clientSock1,"ACK",update0)).start();
			    	  time.setIncrementTime(time.getTime());
				       timeM=Double.parseDouble(time.getTime()+".2");
						tm.put(timeM,0);
				      new Thread(new Multicast2(clientSock0,clientSock1,"UPDATE",time.getTime()+".2")).start();

			      }
			      if(update0.contains("ACK")){
			    	  System.out.println(update0+" node 2");
			    	  update0=update0.replace("ACK,", "");
			    	  dt=Double.parseDouble(update0);
			    	  if(tm.containsKey(dt)){
			    		  if(ackHash.containsKey(dt)){
			    			  int k=(int) ackHash.get(dt);

			    			  tm.put(dt,k+1);
			    			  ackHash.remove(dt);
			    		  }
			    		  else{
			    		  int v=(int) tm.get(dt);
			    		  tm.put(dt,v+1);
			    		  }

			    	  }
			    	  else{
			    		  ackHash.put(dt, ackHash.get(dt)+1);
			    	  }


			      }
			    	  }catch(Exception e){

			    		  //System.out.println(update1+" catch node 2 input 0");
				    	  //e.printStackTrace();
				      }
			    	  try{
					      if(update1.contains("UPDATE")){

					    	  System.out.println(update1+" node 2");
					    	  update1=update1.replace("UPDATE,", "");
					    	  dt=Double.parseDouble(update1);
					    	  t=(long)dt;
					    	  if(t==time.getTime()){
					    		  time.setTime(time.getTime()+1);

					    	  }
					    	  else{
					    		  time.setTime(Math.max(t,time.getTime())+1);
					    	  }

					    	  if(ackHash.containsKey(dt)){

				    			  int k=(int) ackHash.get(dt);
				    			  tm.put(dt,k+1);
				    			  ackHash.remove(dt);
				    		  }
					    	  else{
					    	  tm.put(dt,1);
					    	  }
					    	  new Thread(new Multicast2(clientSock0,clientSock1,"ACK",update1)).start();


					      }
					      if(update1.contains("ACK")){
					    	  System.out.println(update1+" node 2");
					    	  update1=update1.replace("ACK,", "");
					    	  dt=Double.parseDouble(update1);
					    	  if(tm.containsKey(dt)){
					    		  if(ackHash.containsKey(dt)){
					    			  int k=(int) ackHash.get(dt);

					    			  tm.put(dt,k+1);
					    			  ackHash.remove(dt);
					    		  }
					    		  else{
					    		  int v=(int) tm.get(dt);
					    		  tm.put(dt,v+1);
					    		  }

					    	  }
					    	  else{
					    		  ackHash.put(dt, ackHash.get(dt)+1);
					    	  }


					      }
					    	  }catch(Exception e){

					    		  //System.out.println(update1+" catch node 2 input 1");
						    	  //e.printStackTrace();
						      }
			    	  update0="";
			    	  update1="";

			    	  }

              System.out.println("FINAL ORDER");
              System.out.println("**********************");
			    for (Double treeKey : tm.keySet()) {
	    	        System.out.print(df.format(treeKey)+" Ack:");
	    	        System.out.println(tm.get(treeKey));
	    	    }



		}catch(Exception e){

		}

	}

}
