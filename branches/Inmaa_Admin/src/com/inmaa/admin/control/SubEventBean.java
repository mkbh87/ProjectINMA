package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.SubEvent;
import com.inmaa.admin.service.IEventService;
import com.inmaa.admin.service.IMemberService;
import com.inmaa.admin.service.IPartnerService;
import com.inmaa.admin.service.ISubEventService;
import com.inmaa.admin.tools.Utils;

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
		
	@Autowired
	IPartnerService partnerService;

	@Autowired
	IMemberService memberService;
	private List<SubEvent> subEventList;

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

	private int eventID;

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
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement de sous-événement",bodymsg );
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
			if (eventID > 0)
				subEventList = subEventService.listerbyEvent(eventID);
		} catch(Exception e) {
			//Error during hibernate query
			System.out.print("Error: "+e.getMessage());

		}
	}

	public void edit(){
		String bodymsg="Événement modifié avec succès";
		try {

			if(uploadedFile != null)
			{
				if(currentSubEvent.getSubEventLogo() != null)
					Utils.deletePicture(currentSubEvent.getSubEventLogo());
				submitLogoFile();
			}

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
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de sous-événement",bodymsg );
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
				msg="Erreur lors de l_envoie d_image, ";
				// Always log stacktraces (with a real logger).
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(output);
			}
			uploadedFile = null;
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

	public void initializeLazyJoins()
	{
		subEventService.initializeLazyJoins(currentSubEvent);

		eventsTarget = new ArrayList<Event>();
		eventsTarget = new ArrayList<Event>(currentSubEvent.getEvents());	
		eventsSource = Utils.setListSource(eventsTarget, eventService.lister());
		listeEv  = new DualListModel<Event>(eventsSource, eventsTarget);
	}

	public DualListModel<Event> getListeEv() {
		return listeEv;
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

	public void getSubsbyEvent(int id)
	{
		eventID = id;
		subEventList = subEventService.listerbyEvent(id);
	}
	
	public void onRowEdit(RowEditEvent event) {
		//currentMedia = new Media((Media) event.getObject());
		if(  ((SubEvent) event.getObject()).getSubEventId() == null )
		{
			int seqno = subEventService.maxSeqno();
			((SubEvent) event.getObject()).setSeqNo(seqno + 10);
			ArrayList<Event> events = new ArrayList<Event>();
			events.add(EventByID(eventID));
			if (events.isEmpty())
				return;
			Set<Event> temp = new HashSet<Event>(events);
			((SubEvent) event.getObject()).setEvents(temp);
	        FacesMessage msg = new FacesMessage("Sous événement enregister", ((SubEvent) event.getObject()).getSubEventName());
	        FacesContext.getCurrentInstance().addMessage(null, msg);
	        currentSubEvent= new SubEvent((SubEvent) event.getObject());
	        subEventService.enregistrer(currentSubEvent);
	        subEventList = subEventService.listerbyEvent(eventID);

		}
		else
		{
			FacesMessage msg = new FacesMessage("Sous événement enregister", ((SubEvent) event.getObject()).getSubEventName());
			FacesContext.getCurrentInstance().addMessage(null, msg);
			currentSubEvent= new SubEvent((SubEvent) event.getObject());
			subEventService.mettre_a_jour(currentSubEvent);
			subEventList = subEventService.listerbyEvent(eventID);			
		}
			
    }
	
    private Event EventByID(int evID) {
    	Event ev = new Event();
		Iterator<Event> itr = eventService.lister().iterator();
		while(itr.hasNext()) {
			ev = itr.next();
			if(ev.getEventId() == evID)
			{
				return ev;
			}
		}
		return null;
	
	}

	public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("modification annuler", ((SubEvent) event.getObject()).getSubEventName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

	public List<SubEvent> getSubEventList() {
		return subEventList;
	}

	public void setSubEventList(List<SubEvent> subEventList) {
		this.subEventList = subEventList;
	}
	
    public void newLine(ActionEvent actionEvent) {
    	subEventList.add(new SubEvent());
    }
    
    
    public String getFirstEventUrl() {
    	Event e = currentSubEvent.getEvents().iterator().next();
    	return e.getEventUrl();
    }
}
