package com.inmaa.admin.control;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Contact;
import com.inmaa.admin.service.IContactService;
import com.inmaa.admin.tools.Utils;


@Component("contactBean")

public class ContactBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IContactService contactService;

	private Contact currentContact ;

	List<Contact> target = new ArrayList<Contact>();
	List<Contact> source = new ArrayList<Contact>();
	private DualListModel<Contact> contactModel;

	private List<Contact> contactList;

	private transient DataModel<Contact> contacts;
	private int id;
	private UploadedFile uploadedFile;
	private String fileName;   
    private String[] listTypes;
    private List<Contact> filteredContacts;

    private boolean editmode;

	private boolean addmode;
    
	@PostConstruct
	public void init() {
 
		setListTypes(contactService.getListOfTypes().toArray(new String[0]));
		editmode = false;
		addmode = false;
		
		contactList = new ArrayList<Contact>();
 		contactList = contactService.lister();
		contacts = new ListDataModel<Contact>();
		contacts.setWrappedData( contactService.lister());

		source = getcontactList();
		List<Contact> target = new ArrayList<Contact>();
		contactModel = new DualListModel<Contact>(source, target);


	}
	public Contact getcurrentContact() {
		return currentContact;
	}

	public void setcurrentContact(Contact p) {
		this.currentContact = p;
	}

	public ContactBean(){
		this.currentContact = new Contact();	

	}

	public IContactService getContactService() {
		return contactService;
	}

	public void setContactService(IContactService contactService) {
		this.contactService = contactService;
	}

	public DataModel<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(DataModel<Contact> contacts) {
		this.contacts = contacts;
	}

	public String vider(){
		currentContact = new Contact();
		editmode = false;
		addmode = false;
		return null;
	}

	public List<Contact> getcontactList() {

		return contactList;
	}

	public void setContactList(List<Contact> contacts) {
		this.contactList = contacts;
	}

	public String viewContactDetail(){
		currentContact = getContacts().getRowData();
		return "detailContact";
	}

	public void setcontactModel(DualListModel<Contact> contactModel) {
		this.contactModel = contactModel;
	}
	public DualListModel<Contact> getcontactModel() {
		return contactModel;
	}
	public void setId(int id) {
		this.id = id;
		currentContact = getcontactById(id);
	}
	public int getId() {
		return id;
	}

	public Contact getcontactById(int id)
	{
		Iterator<Contact> itr = contacts.iterator();
		while(itr.hasNext()) {
			currentContact = itr.next();
			if(currentContact.getContactId() == id)
			{
				return currentContact;
			}
		}
		return null;
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
				currentContact.setContactPicture(fileName);
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

	public void handleFileUpload(FileUploadEvent event) {

		uploadedFile = event.getFile();
	}


	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();

			if(currentContact.getContactPicture() == null)
			{
				if(null == currentContact.getContactGender())
					currentContact.setContactPicture("logo-org.png");
				else if(!currentContact.getContactGender())
					currentContact.setContactPicture("homme.jpg");
				else if(currentContact.getContactGender())
					currentContact.setContactPicture("femme.jpg");

				bodymsg = "";
			}

			int seqno = contactService.maxSeqno();
			currentContact.setSeqNo(seqno + 1);
			contactService.enregistrer(currentContact);
			contacts.setWrappedData( contactService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			if(e.getCause() != null)
				bodymsg += e.getCause() + "  |  ";
			else
				bodymsg += e.getMessage() + "  |  ";

			bodymsg = bodymsg.replace("'", " ");
			e.printStackTrace();
			System.out.print("Error: "+e);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement du membre",bodymsg );
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return "";
		}
		return "table-contacts.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {

			contactService.supprimer(currentContact);
			Utils.deletePicture(currentContact.getContactPicture());
			currentContact = new Contact();
			contacts.setWrappedData( contactService.lister());

		} catch(Exception e) {
			//Error during hibernate query
			System.out.print("Error: "+e.getMessage());

		}
	}

	public String showEdit(Contact p){
		currentContact = p;
		setId(currentContact.getContactId());
		return "edit-contacts.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public String saveAndshowEdit(){
		ajouter();
		setId(currentContact.getContactId());
		return "edit-contacts.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void refresh()
	{
		contacts = new ListDataModel<Contact>();
		contacts.setWrappedData(contactService.lister());
		
		contactList = new ArrayList<Contact>();
		contactList = contactService.lister();
	}

	public void edit(){
		setId(currentContact.getContactId());
		String bodymsg="Evenement modifié avec succès";
		try {

			if(uploadedFile != null)
			{
				if(currentContact.getContactPicture() != null)
					Utils.deletePicture(currentContact.getContactPicture());
				submitLogoFile();
			}

			if (null == currentContact.getContactGender()  && !currentContact.getContactPicture().equals("logo-org.png") )
				currentContact.setContactPicture("logo-org.png");
			else if(currentContact.getContactPicture().equals("femme.jpg") && !currentContact.getContactGender())
				currentContact.setContactPicture("homme.jpg");
			else if (currentContact.getContactPicture().equals("homme.jpg") && currentContact.getContactGender())
				currentContact.setContactPicture("femme.jpg");



			contactService.mettre_a_jour(currentContact);
			editmode = false;
		} catch(Exception e) {
			//Error during hibernate query
			bodymsg= e.getMessage().replace("'", "") + "      ";
			if(e.getCause() != null)
				bodymsg  += e.getCause().getMessage().replace("'", "");
		}
 
	}

	public void editable() {
		
	    editmode = true;
	}
	
	public void adding() {
		editmode = true;
	    setAddmode(true);
	}
	
	public void readyforDelete(Contact p){
		currentContact = p;
		setId(currentContact.getContactId());

	}
	
	public Contact getcontactBySeqNo(int Seqno, int type)
	{
		contacts.setWrappedData( contactService.lister());
		Contact previous = new Contact();
		previous=null;
		Iterator<Contact> itr = contacts.iterator();
		while(itr.hasNext()) {
			Contact pr  = itr.next();
			if(pr.getSeqNo() == Seqno)
			{
				if( type == -1)
					return previous;
				else if ( type == 1)
					if(itr.hasNext())
						return itr.next();
					else
						return null;
				else
					return pr;
			}
			previous = pr;
		}
		return null;
	}

	 public void onRowSelect(SelectEvent event) {
	       setcurrentContact(((Contact) event.getObject()));
 	    }
	 
    public List<Contact> getFilteredContacts() {
        return filteredContacts;
    }
 
    public void setFilteredContacts(List<Contact> filteredContacts) {
        this.filteredContacts = filteredContacts;
    }
    
	public String[] getListTypes() {
		return listTypes;
	}
	
	public void setListTypes(String[] listType) {
		listTypes = listType;
	}
	
	public boolean isEditmode() {
		return editmode;
	}
	
	public void setEditmode(boolean editmode) {
		this.editmode = editmode;
	}
	public boolean isAddmode() {
		return addmode;
	}
	public void setAddmode(boolean addmode) {
		this.addmode = addmode;
	}
 
 
}
