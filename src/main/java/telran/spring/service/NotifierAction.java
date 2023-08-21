package telran.spring.service;

public class NotifierAction {

	public ActionType actionType;
	public Object object;

	NotifierAction(ActionType actionType, Object object){
		this.actionType = actionType;
		this.object = object;
	}
	
}
