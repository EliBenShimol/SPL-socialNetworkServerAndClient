package bgu.spl.net.api.bidi;

import bgu.spl.net.impl.BGSServer.Client;

public interface BidiMessagingProtocol<T>  {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, Connections<T> connections);
    
    void process(T message);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();

	public boolean isConnectedUser();
	public void terminate();
	public void connectUser(Client c);
	public Client getUser();
	public boolean follow(Client called, char follow);
	public void readAll();
}
