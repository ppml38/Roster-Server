import java.io.*;
import java.net.*;
import java.util.*;
class PlanServer
{

public static void main(String argv[]) throws Exception
{
	Server srvr=new Server();
	new Thread(srvr).start();
        srvr.log("Server Started");
}

}


class Server implements Runnable
{

String           clientSentence=null;
ServerSocket     welcomeSocket=null;
Socket           connectionSocket=null;

BufferedReader   inFromClient;
PrintWriter      outToClient;
//Scanner opnScanner;
BufferedReader br;


public void run()
{


	try{welcomeSocket = new ServerSocket(80);}
	catch(Exception e){e.printStackTrace();log("Error occured on creating server socket");}
          while(true)
          {
			process();
    	  }


}

private void process()
{
		try
		{
			connectionSocket = welcomeSocket.accept();
			inFromClient     = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			outToClient      = new PrintWriter(connectionSocket.getOutputStream(), true);
			clientSentence   = inFromClient.readLine();
			log(clientSentence);
			if(clientSentence!=null)
			{
			String[] result  = clientSentence.split("\\s");
			send(result[1]);
			}
			else{log("null request");}
			connectionSocket.close();
		}
		catch(Exception e){
			e.printStackTrace();
			log("Error occured on processing request");
			if(!connectionSocket.isClosed()){
	                		 outToClient.println(msgtoclient("Red Request/Response"));
			 	         outToClient.println("\r\n");
					 try{connectionSocket.close();}catch(Exception e1){log("Could not close the opened socket");}
					}
			}

}

public void send(String name)
{
	File fl;
	String s;
	if(name.equals("/")){
	fl=new File("Pages/SupportPlan.html");
	}
	else{
	fl=new File(name.substring(1));
	}

	if(fl.exists())
	{
	try
	{
		//opnScanner = new Scanner(fl);
	        //while(opnScanner.hasNext())
		//{
                 // outToClient.println(opnScanner.nextLine());
		//}
		br=new BufferedReader(new FileReader(fl));
		while((s = br.readLine()) != null)
		{ 
                  outToClient.println(s); 
		} 
		br.close();
	        outToClient.println("\r\n");
	}
	catch(Exception e)
	{
		e.printStackTrace();
		log("Error occured on sending reply \""+fl+"\"");
		outToClient.println(msgtoclient("Bad URL"));
	}
	}
	else
	{
		if(name.length()>=4&&name.substring(0,4).equals("/PWD"))
		{
		try{
		br=new BufferedReader(new FileReader(new File("were12wolves/err")));
		if(br.readLine().equals(name.substring(4))){
		outToClient.println("ok");
		outToClient.println("\r\n");
		}
		else
		{
		outToClient.println("no");
		outToClient.println("\r\n");
		}
		}
		catch(Exception e){outToClient.println("no");outToClient.println("\r\n");}
		}
		else
		{


		if(name.length()>=4&&name.substring(0,4).equals("/BCL"))
		{
		try{
		backup(java.net.URLDecoder.decode(name.substring(4))); /* Backing up the message come to substitue if any thing happened wrong */
		FileWriter indexfile = new FileWriter("Pages/SupportPlan.html");
		br=new BufferedReader(new FileReader(new File("Snippets/start.txt")));
		while((s = br.readLine()) != null)
		{ 
			indexfile.write(s);
		} 
		br.close();
		indexfile.write(java.net.URLDecoder.decode(name.substring(4)));
		br=new BufferedReader(new FileReader(new File("Snippets/end.txt")));
		while((s = br.readLine()) != null)
		{ 
			indexfile.write(s);
		} 
		br.close();
		indexfile.close();
			log("New index file is generated");

/*------------------------------------------BCL page creation-----------------------------------------------------------*/
		FileWriter bclfile = new FileWriter("Pages/SupportPlan_BCL.html");
		br=new BufferedReader(new FileReader(new File("Snippets/startb.txt")));
		while((s = br.readLine()) != null)
		{ 
			bclfile.write(s);
		} 
		br.close();
		bclfile.write(java.net.URLDecoder.decode(name.substring(4)));
		br=new BufferedReader(new FileReader(new File("Snippets/endb.txt")));
		while((s = br.readLine()) != null)
		{ 
			bclfile.write(s);
		} 
		br.close();
		bclfile.close();
			log("New index file for BCL is generated");
		outToClient.println("Plan Produced and Published to associates.\nThank you."); /* Message to be displayed in alert dialog box in web page */
		outToClient.println("\r\n");
		}
		catch(Exception e){
			log("Error occured while generating index file");
		outToClient.println("Bad network connection.\nPlan cannot be produced."); /* Message to be displayed in alert dialog box in web page */
		outToClient.println("\r\n");
		}
		}
		else{
		outToClient.println(msgtoclient("Oops !!. Looks like a Bad URL"));
		outToClient.println("\r\n");
		}
		}
	}
	
}

public void backup(String msg)
{

	try
	{
		FileWriter wrtbkup = new FileWriter("Backup/Backup.txt",true);
		wrtbkup.write(new Date().toString());
		wrtbkup.write(System.getProperty("line.separator"));
		wrtbkup.write(msg);
		wrtbkup.write(System.getProperty("line.separator"));
		wrtbkup.write(System.getProperty("line.separator"));
		wrtbkup.close();
		log("Message backed up successfully");
	}
	catch(Exception e)
	{
		log("Error in backing up the message");
		e.printStackTrace();
	}
}

public void log(String msg)
{
	String Remote = new String("");
	try
	{
		FileWriter wrtlog = new FileWriter("log.txt",true);
		Remote+=connectionSocket==null?" ":connectionSocket.getInetAddress().toString()+":"+connectionSocket.getPort();
		wrtlog.write(new Date().toString()+"\t"+Remote+"\t\""+msg+"\"");
		wrtlog.write(System.getProperty("line.separator"));
		wrtlog.close();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}

public String msgtoclient(String msg)
{

return "<html><head><title>Message</title><style>.fnt{font-family: \"Segoe UI\",Helvetica,Arial,sans-serif;font-size: 2.1875rem;font-weight: 400;}</style></head><body><table height=100% width=100%><tr><td align=center class=fnt>"+msg+"</td></tr></table></body></html>";

}

}