import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Multicast0 implements Runnable
{

    Socket c0,c1;
    String type,ach;
    PrintWriter out0,out1;
    int pId;
    private Lock lock0 =new ReentrantLock(true);
    TimeNode0 time= TimeNode0.getInstance();
    Multicast0(Socket c0,Socket c1,String type,int pId) throws IOException
    {

    	this.c0=c0;
    	this.c1=c1;
    	this.type=type;
    	this.pId=pId;


    }
    Multicast0(Socket c0,Socket c1,String type,String ach) throws IOException
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
	    		//System.out.println(time.getTime()+" before send ach 1");
	    		out0.println(type+","+ach);
	    		out0.flush();
				/* lock0.unlock();
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
    			//System.out.println("hooooo "+time.getTime());
    		//time.setIncrementTime(time.getTime());
    		//System.out.println(time.getTime()+" before send 1");
    		out0.println(type+","+ach);
    		out0.flush();
			/* lock0.unlock();
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


public class Client0 {
static int connection1=0,connection2=0;

	public static void main(String[] args) throws IOException {
		TimeNode0 time= TimeNode0.getInstance();
		 TreeMap<Double,Integer> tm = new TreeMap<Double,Integer>();
		 HashMap<Double,Integer> ackHash =new HashMap<Double,Integer>();

		 DecimalFormat df = new DecimalFormat("#.0");
		 Socket clientSock2=null,clientSock1 = null;
		 int timeFlag=0;
		 int multicastFlag=0;
	      ServerSocket server0Sock = new ServerSocket(1234);
	      while(connection1==0){
	    	  System.out.println("Listening 0");
	    	   clientSock1 = server0Sock.accept();
    		   System.out.println(clientSock1.getPort()+" Client0 is connected to client 1");


    		  connection1=1;
	      }
	      while(connection2==0){
	    	  System.out.println("Listening 0");
	    	   clientSock2 = server0Sock.accept();
    		   System.out.println(clientSock2.getPort()+" Client0 is connected to client 2");


    		  connection2=1;

	      }

	      BufferedReader input1 =new BufferedReader(new InputStreamReader(clientSock1.getInputStream()));
	      BufferedReader input2 =new BufferedReader(new InputStreamReader(clientSock2.getInputStream()));
	      PrintWriter out1 =new PrintWriter(clientSock1.getOutputStream(), true);
	      PrintWriter out2 =new PrintWriter(clientSock2.getOutputStream(), true);
	      while(timeFlag==0){
		  if(input1.readLine().contains("Set time")){
			  try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			  out1.println(System.currentTimeMillis());
			  String timeS = input1.readLine();
			  time.setTime(Long.parseLong(timeS));
			  System.out.println("node 0! time: "+time.getTime()+"\n");
			  timeFlag=1;
		  }
		  else if(input2.readLine().contains("Set time")){
			  try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  out2.println(System.currentTimeMillis());
			  String timeS = input2.readLine();
			  time.setTime(Long.parseLong(timeS));
			  System.out.println("node 0! time: "+time.getTime()+"\n");
			  timeFlag=1;
		  }

	  }
	      double timeM;
	      /*time.setIncrementTime(time.getTime());
	       timeM=Double.parseDouble(time.getTime()+".0");
			tm.put(timeM,0);
	      new Thread(new Multicast0(clientSock1,clientSock2,"UPDATE",0)).start();*/
	      while(multicastFlag==0){
    		for(int i=0;i<2;i++){
    	  time.setIncrementTime(time.getTime());

	       timeM=Double.parseDouble(time.getTime()+".0");
	       //System.out.println("i is "+i+":  "+time.getTime()+ "time M "+timeM);
			tm.put(timeM,0);
	      new Thread(new Multicast0(clientSock1,clientSock2,"UPDATE",time.getTime()+".0")).start();
    		}
    		multicastFlag=1;
	      }
	      int j=1;
	      while(j<=6){
	    	  j++;
	    	  String update2=input2.readLine();
	    	  String update1=input1.readLine();

	    	  long t=0;
	    	  double dt=0;
	    	  //System.out.println(update1+" 111");
	    	  //System.out.println(update2+" 222");

	    	  try{
	      if(update2.contains("UPDATE")){
	    	  System.out.println(update2+" node 0");
	    	  update2=update2.replace("UPDATE,", "");

	    	  dt=Double.parseDouble(update2);
	    	  t=(long)dt;
	    	  if(t==time.getTime()){
	    		  time.setTime(time.getTime()+1);

	    	  }
	    	  else{
	    		  time.setTime(Math.max(t,time.getTime())+1);
	    	  }
	    	  //System.out.println("UPDATE,"+update2+" node 0 "+ time.getTime());
	    	  if(ackHash.containsKey(dt)){

    			  int k=(int) ackHash.get(dt);
    			  tm.put(dt,k+1);
    			  ackHash.remove(dt);
    		  }
	    	  else{
	    	  tm.put(dt,1);
	    	  }
	    	  new Thread(new Multicast0(clientSock1,clientSock2,"ACK",update2)).start();

	      }
	      if(update2.contains("ACK")){

	    	  System.out.println(update2+" node 0");
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
	    		  ackHash.put(dt, ackHash.get(dt)+1);
	    	  }

	      }
	    	  }catch(Exception e){

	    		 // System.out.println(update2+" catch node 0 input 2");
		    	  //e.printStackTrace();
		      }
	    	  try{
	       if(update1.contains("UPDATE")){
	    	  System.out.println(update1+" node 0");
	    	  update1=update1.replace("UPDATE,", "");

	    	  dt=Double.parseDouble(update1);
	    	  t=(long)dt;
	    	  if(t==time.getTime()){
	    		  time.setTime(time.getTime()+1);

	    	  }
	    	  else{
	    		  time.setTime(Math.max(t,time.getTime())+1);
	    	  }
	    	  //System.out.println("UPDATE,"+update1+" node 0 "+ time.getTime());
	    	  if(ackHash.containsKey(dt)){

    			  int k=(int) ackHash.get(dt);
    			  tm.put(dt,k+1);
    			  ackHash.remove(dt);
    		  }
	    	  else{
	    	  tm.put(dt,1);
	    	  }
	    	  new Thread(new Multicast0(clientSock1,clientSock2,"ACK",update1)).start();

	      }
	       if(update1.contains("ACK")){

		    	  System.out.println(update1+" node 0");
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
	    		 // System.out.println(update1+" catch node 0");

		    	  //e.printStackTrace();
		      }
	    	  update1="";
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
