package com.inmaa.admin.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.service.IEventService;

@Component("eventBean")
@SessionScoped
public class EventBean  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IEventService eventService;

	private Event currentEvent ;
	private List<Event> eventList;
	private transient DataModel<Event> events;

	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private int id;

	
	@PostConstruct
	public void init() {
		eventList = eventService.lister();
		events = new ListDataModel<Event>();
		events.setWrappedData( eventService.lister());
	}
	public Event getcurrentEvent() {
		return currentEvent;
	}

	public void setcurrentEvent(Event p) {
		this.currentEvent = p;
	}

	public EventBean(){
		this.currentEvent = new Event();	

	}

	public IEventService getEventService() {
		return eventService;
	}

	public void setEventService(IEventService eventService) {
		this.eventService = eventService;
	}

	public DataModel<Event> getEvents() {
		return events;
	}

	public void setEvents(DataModel<Event> events) {
		this.events = events;
	}

	public String ajouter(){
		eventService.enregistrer(currentEvent);
		currentEvent = new Event();
		return null;
	}

	public String vider(){
		currentEvent = new Event();
		return null;
	}

	public List<Event> getEventList() {
		return eventList;
	}

	public void setEventList(List<Event> events) {
		this.eventList = events;
	}
	public String viewEventDetail(){
		currentEvent = getEvents().getRowData();
		setId(currentEvent.getEventId());
		return "form-events.xhtml?faces-redirect=true&includeViewParams=true";

	}
	public void setMemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}
	public DualListModel<Member> getMemberModel() {
		return memberModel;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

}