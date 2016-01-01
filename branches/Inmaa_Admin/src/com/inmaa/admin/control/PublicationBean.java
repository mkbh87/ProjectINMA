package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

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

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Publication;
import com.inmaa.admin.service.IPublicationService;

@Component("publicationBean")
@ViewScoped
public class PublicationBean implements Serializable{
	/**
	 * 
	 */
	@Autowired
	IPublicationService publicationService;
	private Publication currentPublication ;
	private transient DataModel<Publication> es;
	private int id;
	private List<Publication> publicationList;
	List<Member> members = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private UploadedFile uploadedLogo;
	private UploadedFile uploadedDoc;

	private String fileName;

	private static final long serialVersionUID = 1L;
		
	@PostConstruct
	public void init() {
		publicationList = publicationService.lister();
		es = new ListDataModel<Publication>();
		es.setWrappedData( publicationService.lister());
		//		source = getmemberList();
		//		memberModel = new DualListModel<Member>(source, target);
		//vider();
	}

	public Publication getcurrentPublication() {
		return currentPublication;
	}

	public void setcurrentPublication(Publication p) {
		this.currentPublication = p;
	}

	public PublicationBean(){
		this.currentPublication = new Publication();	

	}

	public IPublicationService getPublicationService() {
		return publicationService;
	}

	public void setPublicationService(IPublicationService publicationService) {
		this.publicationService = publicationService;
	}

	public DataModel<Publication> getEs() {
		return es;
	}

	public void setEs(DataModel<Publication> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile(uploadedLogo,"pic");
			bodymsg += submitLogoFile(uploadedDoc,"doc");

			int seqno = publicationService.maxSeqno();
			currentPublication.setSeqNo(seqno + 10);
			publicationService.enregistrer(currentPublication);
			es.setWrappedData( publicationService.lister());
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
		return "table-publications.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {
			publicationService.supprimer(currentPublication);
			es.setWrappedData( publicationService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			e.printStackTrace();
			System.out.print("Error: "+e.getMessage());
		}
	}

	public void edit(){	
		String bodymsg="Projet modifié avec succès";
		try {

			if(uploadedLogo != null)
			{
				if(currentPublication.getPublicationPicture() != null)
					deletePicture(currentPublication.getPublicationPicture());
				submitLogoFile(uploadedLogo,"pic");
 
			}
			
			if(uploadedDoc != null)
			{
				if(currentPublication.getPublicationPicture() != null)
					deletePicture(currentPublication.getPublicationPicture());
 				submitLogoFile(uploadedDoc,"doc");

			}
			
			publicationService.mettre_a_jour(currentPublication);
			es.setWrappedData( publicationService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			bodymsg= e.getMessage().replace("'", "") + "      ";
			if(e.getCause() != null)
				bodymsg  += e.getCause().getMessage().replace("'", "");
			e.printStackTrace();
			System.out.print("Error: "+e.getMessage());
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification du projet",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}


	public String showEdit(Publication p){
		currentPublication = p;
		setId(currentPublication.getPublicationId());
		return "edit-publications.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(Publication p){
		currentPublication = p;
		setId(currentPublication.getPublicationId());
	}

	public void vider(){
		currentPublication = new Publication();
		uploadedLogo = null;
		uploadedDoc = null;
		memberModel = null;
		fileName= null;
	}

	public Publication getpublicationById(int p_id)
	{
		Iterator<Publication> itr = es.iterator();
		while(itr.hasNext()) {
			currentPublication = itr.next();
			if(currentPublication.getPublicationId() == p_id)
			{
				return currentPublication;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentPublication = getpublicationById(id);
	}

	public int getId() {
		return id;
	}

	public List<Publication> getPublicationList() {
		return publicationList;
	}

	public void setPublicationList(List<Publication> publications) {
		this.publicationList = publications;
	}

	public void setmemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getmemberModel() {
		return memberModel;
	} 

	public String submitLogoFile(UploadedFile filetoUp,String type ) {

		String msg = "";
		if (filetoUp != null){
			// Prepare filename prefix and suffix for an unique filename in upload folder.
			String suffix = FilenameUtils.getExtension(filetoUp.getFileName());
			// Prepare file and outputstream.
			File file = null;
			OutputStream output = null;

			try {
				// Create file with unique name in upload folder and write to it.
				file = File.createTempFile("img", "." + suffix, new File(ConfigBean.getImgFilePath()));
				output = new FileOutputStream(file);
				IOUtils.copy(filetoUp.getInputstream(), output);
				if (type.equals("pic"))
					currentPublication.setPublicationPicture(file.getName());
				else if (type.equals("doc"))
					currentPublication.setPublicationLink(file.getName());

				msg="Document Envoyé, ";

			} catch (Exception e) {
				// Cleanup.
				if (file != null) file.delete();
				msg="Erreur lors de l'envoie de document, ";
				// Always log stacktraces (with a real logger).
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(output);
			}
		}
		else
			msg="il y a pas d document, ";
		
		return msg;
	}

	public UploadedFile getUploadedFile() {
		return uploadedLogo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedLogo = uploadedFile;
	}

	public void handleFileUpload(FileUploadEvent event) {

		uploadedLogo = event.getFile();
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
	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	private void deletePicture(String pictureName) {
		File file = new File(ConfigBean.getImgFilePath() +"/"+ pictureName);
		Path path = file.toPath();
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
	}

	public void setUploadedDoc(UploadedFile uploadedFile) {
		this.uploadedDoc = uploadedFile;
	}

	public void handleDocUpload(FileUploadEvent event) {

		uploadedDoc = event.getFile();
	}
}

 
