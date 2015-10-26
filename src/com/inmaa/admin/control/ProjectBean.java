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
	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
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
		es.setWrappedData( projectService.lister());
		return es;
	}

	public void setEs(DataModel<Project> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "le projet a été modifié avec succes";
		submitLogoFile();
		try {
			
			int seqno = projectService.maxSeqno();
			currentProject.setSeqNo(seqno + 10);
			projectService.enregistrer(currentProject);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");

			System.out.print("Error: "+e);
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement du projet",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);

		vider();
 
		return bodymsg;
	}

	public String delete(Project p){
		String bodymsg="Projet suprimé avec succès";
		try {

			projectService.supprimer(p);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");

			System.out.print("Error: "+e);
		}

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Suppression du projet",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);

 		return bodymsg;
	}

	public String edit(){	
		String bodymsg="Projet modifié avec succès";
		try {

			if(uploadedFile != null)
				submitLogoFile();
			projectService.mettre_a_jour(currentProject);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");

			System.out.print("Error: "+e);
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification du projet",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
		
		return "";
	}


	public String showEdit(Project p){
		currentProject = p;
		setId(currentProject.getProjectId());
		return "edit-projects.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public String vider(){

		currentProject = new Project();
 		uploadedFile = null;
		memberModel = null;
		fileName= null;
		return "OK";

	}
	
	public Project getprojectById(int id)
	{
		Iterator<Project> itr = es.iterator();
		while(itr.hasNext()) {
			currentProject = itr.next();
			if(currentProject.getProjectId() == id)
			{
				return currentProject;
			}
		}
		return null;
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

	public void setId(int id) {
		this.id = id;
		currentProject = getprojectById(id);
	}

	public int getId() {
		return id;
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
				file = File.createTempFile("img", "." + suffix, new File("/usr/share/apache-tomcat-7.0.23-2/webapps/ROOT/resources/images/"));
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
				currentProject.setProjectLogo(fileName);

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
