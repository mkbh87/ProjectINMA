package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.service.IEventService;

@Component("eventBean")
@ViewScoped
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
	private UploadedFile uploadedFile;
	private String fileName;

	@PostConstruct
	public void init() {
		currentEvent = new Event();

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
		String bodymsg = "Evenement a été modifié avec succes";
		submitLogoFile();
		try {

			int seqno = eventService.maxSeqno();
			currentEvent.setSeqNo(seqno + 10);
			eventService.enregistrer(currentEvent);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");

 
		}

		vider();

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement évenement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);

		return bodymsg;
	}

	public String delete(Event p){
		String bodymsg="Evenement suprimé avec succès";
		try {

			eventService.supprimer(p);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}
		currentEvent = new Event();
		events.setWrappedData( eventService.lister());

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Suppression évenement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);

		return bodymsg;
	}

	public String edit(){
		setId(currentEvent.getEventId());
		String bodymsg="Evenement modifié avec succès";
		try {

			eventService.mettre_a_jour(currentEvent);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification évenement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
		vider();

		return "";
	}

	public String showEdit(Event p){
		currentEvent = p;
		setId(currentEvent.getEventId());
		return "edit-events.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public String viewEventDetail(){
		currentEvent = getEvents().getRowData();
		setId(currentEvent.getEventId());
		return "form-events.xhtml?faces-redirect=true&includeViewParams=true";

	}
	
	public String vider(){

		currentEvent = new Event();
		id = 0;
		uploadedFile = null;
		memberModel = null;
		fileName= null;
		return "OK";

	}

	public List<Event> getEventList() {
		eventList = eventService.lister();
		return eventList;
	}

	public void setEventList(List<Event> events) {
		this.eventList = events;
	}

	public void setMemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getMemberModel() {
		return memberModel;
	}

	public void setId(int id) {
		this.id = id;
		currentEvent = geteventtById(id);

	}

	private Event geteventtById(int id2) {
		Iterator<Event> itr =eventList.iterator();
		while(itr.hasNext()) {
			currentEvent = itr.next();
			if(currentEvent.getEventId() == id)
			{
				return currentEvent;
			}
		}
		return null;
	}

	public int getId() {
		return id;
	}

	@SuppressWarnings("null")
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			desc = currentEvent.getEventDesc();

		if(desc != null && desc.length()>200)
			desc = desc.substring(0, 200) + " ...";

		return desc;
	}


	public void submitLogoFile() {

		if (uploadedFile != null){
			// Just to demonstrate what information you can get from the uploaded file.
			System.out.println("File type: " + uploadedFile.getContentType());
			System.out.println("File name: " + uploadedFile.getFileName());
			System.out.println("File size: " + uploadedFile.getSize() + " bytes");

			// Prepare filename prefix and suffix for an unique filename in upload folder.
			String prefix = FilenameUtils.getBaseName(uploadedFile.getFileName());
			String suffix = FilenameUtils.getExtension(uploadedFile.getFileName());

			// Prepare file and outputstream.
			File file = null;
			OutputStream output = null;

			try {
				// Create file with unique name in upload folder and write to it.
				file = File.createTempFile(prefix + "_", "." + suffix, new File("/var/www/resources/images/"));
				output = new FileOutputStream(file);
				IOUtils.copy(uploadedFile.getInputstream(), output);
				fileName = file.getName();

				// Show succes message.
				FacesContext.getCurrentInstance().addMessage("uploadForm", new FacesMessage(
						FacesMessage.SEVERITY_INFO, "File upload succeed!", null));
			} catch (IOException e) {
				// Cleanup.
				if (file != null) file.delete();

				// Show error message.
				FacesContext.getCurrentInstance().addMessage("uploadForm", new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "File upload failed with I/O error.", null));

				// Always log stacktraces (with a real logger).
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(output);
				currentEvent.setEventLogo(fileName);

			}

		}
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public void handleFileUpload(FileUploadEvent event) {

		uploadedFile = event.getFile();
		currentEvent.setEventLogo(event.getFile().getFileName() );

		System.out.println("File type: " + uploadedFile.getContentType());
		System.out.println("File name: " + uploadedFile.getFileName());
	}


	public String getDateFormated(Date d)
	{
		if(d != null)
		{
			String date;
			Calendar startdate = new GregorianCalendar();
			startdate.setTime(d);
			date = "" + startdate.get(Calendar.DATE) 
			+ " " +startdate.get(Calendar.MONTH)
			+ " " + startdate.get(Calendar.YEAR);
			return date;
		}
		return null;
	}

}