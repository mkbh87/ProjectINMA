package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
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
import com.inmaa.admin.persistence.Project;
import com.inmaa.admin.service.IProjectService;

@Component("projectBean")
@ViewScoped
public class ProjectBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IProjectService projectService;
	private Project currentProject ;
	private transient DataModel<Project> es;
	private int id;
	private List<Project> projectList;
	List<Member> members = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private UploadedFile uploadedFile;
	private String fileName;

	@PostConstruct
	public void init() {
		projectList = projectService.lister();
		es = new ListDataModel<Project>();
		es.setWrappedData( projectService.lister());
//		source = getmemberList();
//		memberModel = new DualListModel<Member>(source, target);
		//vider();
	}

	private List<Member> getmemberList() {
		List<Member> members = null;

		members =  projectService.listerMember();

		return members;
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
				submitLogoFile();
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
		memberModel = null;
		fileName= null;
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
			desc = currentProject.getProjectDesc();

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
				currentProject.setProjectLogo(file.getName());
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
}
