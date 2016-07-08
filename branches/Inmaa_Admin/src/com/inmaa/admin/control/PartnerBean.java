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
import com.inmaa.admin.persistence.Partner;
import com.inmaa.admin.service.IPartnerService;
import com.inmaa.admin.tools.Utils;

@Component("partnerBean")
@ViewScoped
public class PartnerBean implements Serializable{
	/**
	 * 
	 */
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
	
	private Partner prevPartner;
	private Partner nextPartner;

	private static final long serialVersionUID = 1L;
		
	@PostConstruct
	public void init() {
		partnerList = partnerService.lister();
		es = new ListDataModel<Partner>();
		es.setWrappedData( partnerService.lister());
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
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement de partenaire",bodymsg );
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return "";
		}
		vider();
		return "table-partners.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {
			partnerService.supprimer(currentPartner);
			Utils.deletePicture(currentPartner.getPartnerLogo());

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
					Utils.deletePicture(currentPartner.getPartnerLogo());
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
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de partenaire",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}


	public String showEdit(Partner p){
		currentPartner = p;
		setId(currentPartner.getPartnerId());

		prevPartner = getpartnerBySeqNo(currentPartner.getSeqNo() ,-1);
		nextPartner = getpartnerBySeqNo(currentPartner.getSeqNo() , 1);

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
				msg="Erreur lors de l envoie d image, ";
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

	public void handleFileUpload(FileUploadEvent event) {

		uploadedFile = event.getFile();
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	public Partner getpartnerBySeqNo(int Seqno, int type)
	{
		es.setWrappedData( partnerService.lister());
		Partner previous = new Partner();
		previous=null;
		Iterator<Partner> itr = es.iterator();
		while(itr.hasNext()) {
			Partner pr  = itr.next();
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
	
	public Partner getPrevPartner() {
		return prevPartner;
	}

	public void setPrevPartner(Partner prevPartner) {
		this.prevPartner = prevPartner;
	}

	public Partner getNextPartner() {
		return nextPartner;
	}

	public void setNextPartner(Partner nextPartner) {
		this.nextPartner = nextPartner;
	}
}

 
