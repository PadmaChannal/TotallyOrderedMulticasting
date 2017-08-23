import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Multicast1 implements Runnable
{

    Socket c0,c1;
    String type,ach;
    PrintWriter out0,out1;
    int pId;
    private Lock lock0 =new ReentrantLock(true);
    TimeNode1 time= TimeNode1.getInstance();
    Multicast1(Socket c0,Socket c1,String type,int pId) throws IOException
    {

    	this.c0=c0;
    	this.c1=c1;
    	this.type=type;
    	this.pId=pId;


    }
    Multicast1(Socket c0,Socket c1,String type,String ach) throws IOException
    {

    	this.c0=c0;
    	this.c1=c1;
    	this.type=type;
    	this.ach=ach;


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
    	    		//System.out.println(time.getTime()+" before send ach 0");
    	    		out0.println(type+","+ach);
    	    		out0.flush();
    				 /*lock0.unlock();
    	    		}
    	    		if(lock0.tryLock()){*/
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
    		//System.out.println(time.getTime()+" before send 0");
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

public class Client1 {
static int connection2=0;
	public static void main(String[] args) throws IOException {
		TimeNode1 time= TimeNode1.getInstance();
		 TreeMap<Double,Integer> tm = new TreeMap<Double,Integer>();
		 HashMap<Double,Integer> ackHash =new HashMap<Double,Integer>();
		 Socket clientSock2=null;
		 int timeFlag=0;
		 int multicastFlag=0;
		 DecimalFormat df = new DecimalFormat("#.0");
	      ServerSocket server1Sock = new ServerSocket(1235);

	      while(connection2==0){
	    	  System.out.println("Listening 1");
	    	   clientSock2 = server1Sock.accept();
    		   System.out.println(clientSock2.getPort()+" Client1 is connected to client 2");
    		  connection2=1;

	      }
	      Socket clientSock0=new Socket("localhost",1234);
		   System.out.println(clientSock0.getPort()+" Client1 is connected to client 0");

	      BufferedReader input0 =new BufferedReader(new InputStreamReader(clientSock0.getInputStream()));

	      BufferedReader input2 =new BufferedReader(new InputStreamReader(clientSock2.getInputStream()));
	      PrintWriter out0 =new PrintWriter(clientSock0.getOutputStream(), true);
	      PrintWriter out2 =new PrintWriter(clientSock2.getOutputStream(), true);
	      while(timeFlag==0){
			  if(input2.readLine().contains("Set time")){
				  try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				  out2.println(System.currentTimeMillis());
				  String timeS = input2.readLine();
				  time.setTime(Long.parseLong(timeS));
				  System.out.println("node 1! time: "+time.getTime()+"\n");
				  timeFlag=1;
			  }
	      }
	      double timeM;
	      /*time.setIncrementTime(time.getTime());
	       timeM=Double.parseDouble(time.getTime()+".1");
	      tm.put(timeM,0);
	      new Thread(new Multicast1(clientSock0,clientSock2,"UPDATE",1)).start();*/
	      for(int i=0;i<2;i++){
	    	  time.setIncrementTime(time.getTime());
		       timeM=Double.parseDouble(time.getTime()+".1");
				tm.put(timeM,0);
		      new Thread(new Multicast1(clientSock0,clientSock2,"UPDATE",time.getTime()+".1")).start();
	      }
	      int j=1;
	      while(j<=6){
	    	  //System.out.println("j is "+j);
	    	  j++;
	    	  String update2=input2.readLine();
	    	  String update0=input0.readLine();
	    	  long t=0;
	    	  double dt=0;
	    	  //System.out.println(update0);
	    	  //System.out.println(update2);

	    	  try{
	      if(update2.contains("UPDATE")){

	    	  System.out.println(update2+" node 1");
	    	  update2=update2.replace("UPDATE,", "");
	    	  dt=Double.parseDouble(update2);
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
	    	  new Thread(new Multicast1(clientSock0,clientSock2,"ACK",update2)).start();


	      }
	      if(update2.contains("ACK")){

	    	  System.out.println(update2+" node 1");
	    	  update2=update2.replace("ACK,", "");
	    	  dt=Double.parseDouble(update2);

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

	    		  ackHash.put(dt, 1);

	    		  //System.out.println("ackHasg "+ackHash.entrySet().toString());

	    	  }
	      }
	    	  }catch(Exception e){

	    		  //System.out.println(update2+" catch node 1 input 2");
		    	  //e.printStackTrace();
		      }
	    	  try{
	    	      if(update0.contains("UPDATE")){

	    	    	  System.out.println(update0+" node 1");
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
			    			// System.out.println("hoda");
		    			  int k=(int) ackHash.get(dt);
		    			  tm.put(dt,k+1);
		    			  ackHash.remove(dt);
		    		  }
			    	  else{
			    	  tm.put(dt,1);
			    	  }

	    	    	  new Thread(new Multicast1(clientSock0,clientSock2,"ACK",update0)).start();



	    	      }
	    	      if(update0.contains("ACK")){

			    	  System.out.println(update0+" node 1");
			    	  update0=update0.replace("ACK,", "");
			    	  dt=Double.parseDouble(update0);
			    	  //System.out.println(ackHash.containsKey(dt)+" yoooib "+dt);
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

			    		  ackHash.put(dt, 1);

			    		  System.out.println("ackHasg "+ackHash.entrySet().toString());
			    	  }

			      }
	    	    	  }catch(Exception e){

	    	    		 // System.out.println(update0+" catch node 1 input 0");
	    		    	  //e.printStackTrace();
	    		      }
	    	  update0="";
	    	  update2="";

	    	  }

          System.out.println("FINAL ORDER");
          System.out.println("**********************");
	      for (Double treeKey : tm.keySet()) {
  	        System.out.print(df.format(treeKey)+" Ack:");
  	        System.out.println(tm.get(treeKey));
  	    }


	}

}
