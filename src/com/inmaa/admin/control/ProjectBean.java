package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.inmaa.admin.persistence.Project;
import com.inmaa.admin.service.IMemberService;
import com.inmaa.admin.service.IPartnerService;
import com.inmaa.admin.service.IProjectService;
import com.inmaa.admin.tools.Utils;

@Component("projectBean")
@ViewScoped
public class ProjectBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IProjectService projectService;
	
	@Autowired
	IPartnerService partnerService;

	@Autowired
	IMemberService memberService;

	private Project currentProject ;
	private transient DataModel<Project> es;
	private int id;
	private List<Project> projectList;
	private UploadedFile uploadedFile;
	private String fileName;
	
	private DualListModel<Member> listeMembers;
	List<Member> MemberSource = new ArrayList<Member>();
	List<Member> MemberTarget = new ArrayList<Member>();
	
	private DualListModel<Partner> listePartners;
	List<Partner> PartnerSource = new ArrayList<Partner>();
	List<Partner> PartnerTarget = new ArrayList<Partner>();


	@PostConstruct
	public void init() {
		projectList = projectService.lister();
		es = new ListDataModel<Project>();
		es.setWrappedData( projectService.lister());
	}

	public Project getcurrentProject() {
		return currentProject;
	}

	public void setcurrentProject(Project p) {
		this.currentProject = p;
	}

	public ProjectBean(){
		this.currentProject = new Project();	

	}

	public IProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(IProjectService projectService) {
		this.projectService = projectService;
	}

	public DataModel<Project> getEs() {
		return es;
	}

	public void setEs(DataModel<Project> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();
			int seqno = projectService.maxSeqno();
			currentProject.setSeqNo(seqno + 10);
			currentProject.setProjectNbrVisite(0);
			
			currentProject.setPartners(new HashSet<Partner>(listePartners.getTarget()));
			currentProject.setMembers( new HashSet<Member>(listeMembers.getTarget()));
			
			projectService.enregistrer(currentProject);
			es.setWrappedData( projectService.lister());
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
		return "table-projects.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {
			projectService.supprimer(currentProject);
			es.setWrappedData( projectService.lister());
		} catch(Exception e) {
			//Error during hibernate query

			System.out.print("Error: "+e.getMessage());
		}
	}

	public void edit(){	
		String bodymsg="Projet modifié avec succès";
		try {

			if(uploadedFile != null)
			{
				if(currentProject.getProjectLogo() != null)
					Utils.deletePicture(currentProject.getProjectLogo());
				submitLogoFile();
			}
			currentProject.setPartners(new HashSet<Partner>(listePartners.getTarget()));
			currentProject.setMembers( new HashSet<Member>(listeMembers.getTarget()));
			
			projectService.mettre_a_jour(currentProject);
			es.setWrappedData( projectService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			bodymsg= e.getMessage().replace("'", "") + "      ";
			if(e.getCause() != null)
				bodymsg  += e.getCause().getMessage().replace("'", "");

			System.out.print("Error: "+e.getMessage());
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification du projet",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}


	public String showEdit(Project p){
		currentProject = p;
		setId(currentProject.getProjectId());
		return "edit-projects.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(Project p){
		currentProject = p;
		setId(currentProject.getProjectId());
	}

	public void vider(){
		currentProject = new Project();
		uploadedFile = null;
 		fileName= null;
		
		PartnerTarget = new ArrayList<Partner>();
		PartnerSource = partnerService.lister();
		setListePartners(new DualListModel<Partner>(PartnerSource, PartnerTarget));

		MemberTarget = new ArrayList<Member>();
		MemberSource = memberService.lister();
		setListeMembers(new DualListModel<Member>(MemberSource, MemberTarget));
	}

	public Project getprojectById(int p_id)
	{
		Iterator<Project> itr = es.iterator();
		while(itr.hasNext()) {
			currentProject = itr.next();
			if(currentProject.getProjectId() == p_id)
			{
				return currentProject;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentProject = getprojectById(id);
	}

	public int getId() {
		return id;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projects) {
		this.projectList = projects;
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
				currentProject.setProjectLogo(file.getName());
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

	public void handleFileUpload(FileUploadEvent event) {

		uploadedFile = event.getFile();
	}

	public void initializeLazyJoins()
	{
		projectService.initializeLazyJoins(currentProject);

		PartnerTarget = new ArrayList<Partner>();
		PartnerTarget = new ArrayList<Partner>(currentProject.getPartners());	
		PartnerSource = Utils.setListSource(PartnerTarget, partnerService.lister());
		setListePartners(new DualListModel<Partner>(PartnerSource, PartnerTarget));
		
		MemberTarget = new ArrayList<Member>();
		MemberTarget = new ArrayList<Member>(currentProject.getMembers());	
		MemberSource = Utils.setListSource(MemberTarget, memberService.lister());
		setListeMembers(new DualListModel<Member>(MemberSource, MemberTarget));
	}
	

	public DualListModel<Partner> getListePartners() {
		return listePartners;
	}

	public void setListePartners(DualListModel<Partner> listePartners) {
		this.listePartners = listePartners;
	}

	public DualListModel<Member> getListeMembers() {
		return listeMembers;
	}

	public void setListeMembers(DualListModel<Member> listeMembers) {
		this.listeMembers = listeMembers;
	}

	
	public void actualize()
	{
		projectList = new ArrayList<Project>();
		projectList = projectService.lister();
		es = new ListDataModel<Project>();
		es.setWrappedData( projectService.lister());
	}
}
