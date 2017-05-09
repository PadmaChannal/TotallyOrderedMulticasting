Special Credits to: Hoda Moradi without whom this Project would not have been completed!

Introduction:

What is the Project about?
To augment our theoretical knowledge acquired during lecture and gain some hands-on experience we build a simple Distributed system. 

We implemented n-node distributed system that provides a totally ordered multicasting service and a distributed locking scheme. 


Algorithms used:
•	Berkley’s Algorithm
•	Lamport ‘s Algorithm	

Assignments completed:
•	Assignment-1
•	Assignment-2

Technologies Used:
•	Java (Multithreading)


Our Learning: 

Berkley’s Algorithm:	
Server process in the Berkeley algorithm, called the master, periodically polls other slave processes. The algorithm is:
1.	A master is chosen via an election process such as Chang and Roberts algorithm.
2.	The master polls the slaves who reply with their time in a similar way to Cristian's algorithm.
3.	The master observes the round-trip time (RTT) of the messages and estimates the time of each slave and its own.
4.	The master then averages the clock times, ignoring any values it receives far outside the values of the others.
5.	Instead of sending the updated current time back to the other process, the master then sends out the amount (positive or negative) that each slave must adjust its clock. This avoids further uncertainty due to RTT at the slave processes.

Lamports Algorithm
Requesting process:
1.	Pushing its request in its own queue (ordered by time stamps)
2.	Sending a request to every node.
3.	Waiting for replies from all other nodes.
4.	If own request is at the head of its queue and all replies have been received, enter critical section.
5.	Upon exiting the critical section, remove its request from the queue and send a release message to every process.
Other processes:
1.	After receiving a request, pushing the request in its own request queue (ordered by time stamps) and reply with a time stamp.
2.	After receiving release message, remove the corresponding request from its own request queue.
3.	If own request is at the head of its queue and all replies have been received, enter critical section.

1.	After receiving a request, pushing the request in its own request queue (ordered by time stamps) and reply with a time stamp.
2.	After receiving release message, remove the corresponding request from its own request queue.
3.	If own request is at the head of its queue and all replies have been received, enter critical section.


How we implemented our program?
There are three processes:
•	Client 0
•	Client 1
•	Client 2

Then there are three time nodes: (These classes are needed for sync of timing in the processes: Like the timer of Processes)
•	TimeNode0
•	TimeNode1
•	TimeNode2

What does the Program do?

Client0 acts as server to both client1 and client 2
Client1 is the server to client2 and client for0
Client2 is clients for both.

The acknowledgements from the clients are named as ack0, ack1 and ack2.
Once the multicasting begins the timestamps are noted by each process and the time stamp matches.


Code Snippet:

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
	      for (Double treeKey : tm.keySet()) { 
  	        System.out.print(df.format(treeKey)+" Ack:");
  	        System.out.println(tm.get(treeKey));



Sample output:
We can see that all the processes are synchronized with time:








What issues did we encounter?

If the ackhash (the token used in code to keep track of acknowledgements) is not empty or used before in the processes, then it disturbs the whole algorithm.

Also, we used tree map as the data-structure: This helped us to keep track of how many keys we added as in the table (sorted order).

Comparison of the results of the two programs 

Snap shot of the output:
(Implementation of two versions of the program, one without totally ordered multicasting and one with this feature. )

Totally Ordered




Without Totally ordered:



What to note from the above Comparison? * The timestamps are wrong

References:
•	Wikipedia
•	Lecture notes
•	Research paper : “Logical clocks” by Leslee lamport et.al.
