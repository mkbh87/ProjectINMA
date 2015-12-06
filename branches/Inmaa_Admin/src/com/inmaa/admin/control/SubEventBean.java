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
import com.inmaa.admin.persistence.SubEvent;
import com.inmaa.admin.service.IEventService;
import com.inmaa.admin.service.ISubEventService;

@Component("subEventBean")
@ViewScoped
public class SubEventBean  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IEventService eventService;

	@Autowired
	ISubEventService subEventService;


	private SubEvent currentSubEvent ;
 	private transient DataModel<SubEvent> subEvents;

	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private int id;
	private UploadedFile uploadedFile;
	private String fileName;

	private DualListModel<Event> listeEv;
	List<Event> eventsSource = new ArrayList<Event>();
	List<Event> eventsTarget = new ArrayList<Event>();


	@PostConstruct
	public void init() {
 		subEvents = new ListDataModel<SubEvent>();
		subEvents.setWrappedData( subEventService.lister());
	}

	public SubEvent getcurrentSubEvent() {
		return currentSubEvent;
	}

	public void setcurrentSubEvent(SubEvent p) {
		this.currentSubEvent = p;
	}

	public SubEventBean(){
		this.currentSubEvent = new SubEvent();	

	}

	public ISubEventService getSubEventService() {
		return subEventService;
	}

	public void setSubEventService(ISubEventService subEventService) {
		this.subEventService = subEventService;
	}

	public DataModel<SubEvent> getSubEvents() {
		return subEvents;
	}

	public void setSubEvents(DataModel<SubEvent> subEvents) {
		this.subEvents = subEvents;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			Set<Event> temp = new HashSet<Event>(listeEv.getTarget());
			currentSubEvent.setEvents(temp);

			bodymsg = submitLogoFile();
			int seqno = subEventService.maxSeqno();
			currentSubEvent.setSeqNo(seqno + 10);
			subEventService.enregistrer(currentSubEvent);
			subEvents.setWrappedData( subEventService.lister());
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
		return "table-subevents.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {

			subEventService.supprimer(currentSubEvent);
			subEvents.setWrappedData( subEventService.lister());

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

			Set<Event> temp = new HashSet<Event>(listeEv.getTarget());
			currentSubEvent.setEvents(temp);

			subEventService.mettre_a_jour(currentSubEvent);
			subEvents.setWrappedData( subEventService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			bodymsg= e.getMessage().replace("'", "") + "      ";
			if(e.getCause() != null)
				bodymsg  += e.getCause().getMessage().replace("'", "");
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de l événement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public String showEdit(SubEvent p){
		currentSubEvent = p;
		setId(currentSubEvent.getSubEventId());
		return "edit-subevents.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(SubEvent p){
		currentSubEvent = p;
		setId(currentSubEvent.getSubEventId());
	}

	public void vider(){
		currentSubEvent = new SubEvent();
		uploadedFile = null;
		memberModel = null;
		fileName= null;
		eventsTarget = new ArrayList<Event>();
		eventsSource = eventService.lister();
		listeEv  = new DualListModel<Event>(eventsSource, eventsTarget);

	}

	private SubEvent getsubEventtById(int e_id) {
		Iterator<SubEvent> itr =subEvents.iterator();
		while(itr.hasNext()) {
			currentSubEvent = itr.next();
			if(currentSubEvent.getSubEventId() == e_id)
			{
				return currentSubEvent;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentSubEvent = getsubEventtById(id);
	}

	public int getId() {
		return id;
	}

	public void setMemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getMemberModel() {
		return memberModel;
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
				currentSubEvent.setSubEventLogo(fileName);
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

	public void handleFileUpload(FileUploadEvent subEvent) {

		uploadedFile = subEvent.getFile();
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
		subEventService.initializeLazyJoins(currentSubEvent);

		eventsTarget = new ArrayList<Event>();
		eventsTarget = new ArrayList<Event>(currentSubEvent.getEvents());	
		eventsSource = seteventsSource(eventsTarget);
		listeEv  = new DualListModel<Event>(eventsSource, eventsTarget);
	}

	public DualListModel<Event> getListeEv() {
		return listeEv;
	}

	public List<Event> seteventsSource(List<Event> target) {
		List<Event> listSource = eventService.lister();
		listSource.removeAll(target);
		listSource = subtract(listSource, target );
		return listSource;
	}


	public void setListeEv(DualListModel<Event> listeEv) {
		this.listeEv = listeEv;
	}
	
	public List<Event> subtract(List<Event> list1, List<Event> list2) {
		boolean found = false;
        List<Event> result = new ArrayList<Event>();
        for (Event t1 : list1) {
        	for (Event t2 : list2) {
        		if( t1.getEventId().equals(t2.getEventId()))  {
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
