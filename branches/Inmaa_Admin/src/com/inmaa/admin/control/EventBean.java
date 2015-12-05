package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
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
import com.inmaa.admin.persistence.Project;
import com.inmaa.admin.service.IEventService;
import com.inmaa.admin.service.IProjectService;

@Component("eventBean")
@ViewScoped
public class EventBean  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IEventService eventService;

	@Autowired
	IProjectService projectService;

	private Event currentEvent ;
 	private transient DataModel<Event> events;

	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private int id;
	private UploadedFile uploadedFile;
	private String fileName;

	private DualListModel<Project> listePro;
	List<Project> prosSource = new ArrayList<Project>();
	List<Project> prosTarget = new ArrayList<Project>();


	@PostConstruct
	public void init() {
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
		String bodymsg = "";
		try {
			Set<Project> temp = new HashSet<Project>(listePro.getTarget());
			currentEvent.setProjects(temp);

			bodymsg = submitLogoFile();
			int seqno = eventService.maxSeqno();
			currentEvent.setSeqNo(seqno + 10);
			eventService.enregistrer(currentEvent);
			events.setWrappedData( eventService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			if(e.getCause() != null)
				bodymsg += e.getCause() + "  |  ";
			else
				bodymsg += e.getMessage() + "  |  ";

			bodymsg = bodymsg.replace("'", " ");
			e.printStackTrace();
			System.out.print("Error: "+e);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement du projet",bodymsg );
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return "";
		}
		vider();
		return "table-events.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {

			eventService.supprimer(currentEvent);
			events.setWrappedData( eventService.lister());

		} catch(Exception e) {
			//Error during hibernate query
			System.out.print("Error: "+e.getMessage());

		}
	}

	public void edit(){
		String bodymsg="Événement modifié avec succès";
		try {

			if(uploadedFile != null)
				submitLogoFile();

			Set<Project> temp = new HashSet<Project>(listePro.getTarget());
			currentEvent.setProjects(temp);

			eventService.mettre_a_jour(currentEvent);
			events.setWrappedData( eventService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			bodymsg= e.getMessage().replace("'", "") + "      ";
			if(e.getCause() != null)
				bodymsg  += e.getCause().getMessage().replace("'", "");
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de l événement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public String showEdit(Event p){
		currentEvent = p;
		setId(currentEvent.getEventId());
		return "edit-events.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(Event p){
		currentEvent = p;
		setId(currentEvent.getEventId());
	}

	public void vider(){
		currentEvent = new Event();
		uploadedFile = null;
		memberModel = null;
		fileName= null;
		prosTarget = new ArrayList<Project>();
		prosSource = projectService.lister();
		listePro  = new DualListModel<Project>(prosSource, prosTarget);

	}

	private Event geteventtById(int e_id) {
		Iterator<Event> itr =events.iterator();
		while(itr.hasNext()) {
			currentEvent = itr.next();
			if(currentEvent.getEventId() == e_id)
			{
				return currentEvent;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentEvent = geteventtById(id);
	}

	public int getId() {
		return id;
	}

	//	public String viewEventDetail(){
	//		currentEvent = getEvents().getRowData();
	//		setId(currentEvent.getEventId());
	//		return "form-events.xhtml?faces-redirect=true&includeViewParams=true";
	//
	//	}

	public void setMemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getMemberModel() {
		return memberModel;
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

	public String submitLogoFile() {

		String msg = "";
		if (uploadedFile != null){
			// Prepare filename prefix and suffix for an unique filename in upload folder.
			String suffix = FilenameUtils.getExtension(uploadedFile.getFileName());
			// Prepare file and outputstream.
			File file = null;
			OutputStream output = null;

			try {
				// Create file with unique name in upload folder and write to it.
				file = File.createTempFile("img", "." + suffix, new File(ConfigBean.getImgFilePath()));
				output = new FileOutputStream(file);
				IOUtils.copy(uploadedFile.getInputstream(), output);
				fileName = file.getName();
				currentEvent.setEventLogo(fileName);
				msg="Image Envoyé, ";

			} catch (Exception e) {
				// Cleanup.
				if (file != null) file.delete();
				msg="Erreur lors de l'envoie d'image, ";
				// Always log stacktraces (with a real logger).
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(output);
			}
		}
		else
			msg="il y a pas d image, ";

		return msg;
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

	public void initializeLazyJoins()
	{
		eventService.initializeLazyJoins(currentEvent);

		prosTarget = new ArrayList<Project>();
		prosTarget = new ArrayList<Project>(currentEvent.getProjects());	
		prosSource = setprosSource(prosTarget);
		listePro  = new DualListModel<Project>(prosSource, prosTarget);
	}

	public DualListModel<Project> getListePro() {
		return listePro;
	}

	public List<Project> setprosSource(List<Project> target) {
		List<Project> listSource = projectService.lister();
		listSource.removeAll(target);
		listSource = subtract(listSource, target );
		return listSource;
	}


	public void setListePro(DualListModel<Project> listePro) {
		this.listePro = listePro;
	}
	
	public List<Project> subtract(List<Project> list1, List<Project> list2) {
		boolean found = false;
        List<Project> result = new ArrayList<Project>();
        for (Project t1 : list1) {
        	for (Project t2 : list2) {
        		if( t1.getProjectId().equals(t2.getProjectId()))  {
        			found = true;
        			break;
        		}
        	}
        	if(!found)
        		result.add(t1);
        	found = false;
        }
        return result;
    }
}
