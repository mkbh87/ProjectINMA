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

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Partner;
import com.inmaa.admin.service.IPartnerService;

@Component("partnerBean")
@ViewScoped
public class PartnerBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IPartnerService partnerService;
	private Partner currentPartner ;
	private transient DataModel<Partner> es;
	private int id;
	private List<Partner> partnerList;
	List<Member> members = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private UploadedFile uploadedFile;
	private String fileName;

	@PostConstruct
	public void init() {
		partnerList = partnerService.lister();
		es = new ListDataModel<Partner>();
		es.setWrappedData( partnerService.lister());
		//		source = getmemberList();
		//		memberModel = new DualListModel<Member>(source, target);
		//vider();
	}

	private List<Member> getmemberList() {
		List<Member> members = null;

		members =  partnerService.listerMember();

		return members;
	}

	public Partner getcurrentPartner() {
		return currentPartner;
	}

	public void setcurrentPartner(Partner p) {
		this.currentPartner = p;
	}

	public PartnerBean(){
		this.currentPartner = new Partner();	

	}

	public IPartnerService getPartnerService() {
		return partnerService;
	}

	public void setPartnerService(IPartnerService partnerService) {
		this.partnerService = partnerService;
	}

	public DataModel<Partner> getEs() {
		return es;
	}

	public void setEs(DataModel<Partner> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();
			int seqno = partnerService.maxSeqno();
			currentPartner.setSeqNo(seqno + 10);
			partnerService.enregistrer(currentPartner);
			es.setWrappedData( partnerService.lister());
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
		return "table-partners.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {
			partnerService.supprimer(currentPartner);
			es.setWrappedData( partnerService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			e.printStackTrace();
			System.out.print("Error: "+e.getMessage());
		}
	}

	public void edit(){	
		String bodymsg="Projet modifié avec succès";
		try {

			if(uploadedFile != null)
			{
				if(currentPartner.getPartnerLogo() != null)
					deletePicture(currentPartner.getPartnerLogo());
				submitLogoFile();
			}
			
			
			partnerService.mettre_a_jour(currentPartner);
			es.setWrappedData( partnerService.lister());
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


	public String showEdit(Partner p){
		currentPartner = p;
		setId(currentPartner.getPartnerId());
		return "edit-partners.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(Partner p){
		currentPartner = p;
		setId(currentPartner.getPartnerId());
	}

	public void vider(){
		currentPartner = new Partner();
		uploadedFile = null;
		memberModel = null;
		fileName= null;
	}

	public Partner getpartnerById(int p_id)
	{
		Iterator<Partner> itr = es.iterator();
		while(itr.hasNext()) {
			currentPartner = itr.next();
			if(currentPartner.getPartnerId() == p_id)
			{
				return currentPartner;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentPartner = getpartnerById(id);
	}

	public int getId() {
		return id;
	}

	public List<Partner> getPartnerList() {
		return partnerList;
	}

	public void setPartnerList(List<Partner> partners) {
		this.partnerList = partners;
	}

	public void setmemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getmemberModel() {
		return memberModel;
	} 

	@SuppressWarnings("null")
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			desc = currentPartner.getPartnerDesc();

		if(desc != null && desc.length()>100)
			desc = desc.substring(0, 100) + " ...";

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
				currentPartner.setPartnerLogo(file.getName());
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
}
