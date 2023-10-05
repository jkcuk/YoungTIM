package javawaveoptics.utility;

public class SimulationException extends Exception
{
	private static final long serialVersionUID = 3897303586586429251L;

	private String userMessage;
	
	public SimulationException(String originMessage, String userMessage)
	{
		super(originMessage + ": " + userMessage);
		
		setUserMessage(userMessage);
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
}
