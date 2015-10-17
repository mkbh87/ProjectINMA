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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Project;
import com.inmaa.admin.service.IProjectService;
 
@Component("projectBean")
@SessionScoped
public class ProjectBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IProjectService projectService;

	private Project currentProject ;
	//    private Part Image;  

	private List<Project> projectList;

	private transient DataModel<Project> es;

	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private int id;
    private UploadedFile uploadedFile;
    private String fileName;


	@PostConstruct
	public void init() {
		currentProject = new Project();

		projectList = projectService.lister();
		es = new ListDataModel<Project>();
		es.setWrappedData( projectService.lister());

		source = getmemberList();
		memberModel = new DualListModel<Member>(source, target);
 		vider();
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
		//currentProject.setProjectLogo(getFilename(Image));

		//		List<Member> items = memberModel.getTarget() ;
		//		Set<Member> items2 = new HashSet<Member>();  
		//
		//		Integer id;
		//		Member member;
		//		for(int i = 0; i<items.size(); i++) {
		//			 id = items.get(i).getMemberId();
		//			 member = projectService.getMember(id);
		//			items2.add(member);
		//		}
		//		currentProject.setMembers(items2);
		submitLogoFile();
		projectService.enregistrer(currentProject);
		vider();

		return null;
	}

	public String delete(Project p){
		projectService.supprimer(p);
		currentProject = new Project();
		es.setWrappedData( projectService.lister());
		return null;
	}

	public String edit(){
		setId(currentProject.getProjectId());
		projectService.mettre_a_jour(currentProject);
		vider();
		return "success";
	}

	
	public String showEdit(Project p){
		currentProject = p;
		setId(currentProject.getProjectId());
		return "edit-projects.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}
	
	public String vider(){
		
		currentProject = new Project();
		id = 0;
		uploadedFile = null;
		memberModel = null;
	    fileName= null;
		return "OK";
		
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
	}

	public int getId() {
		return id;
	}


	@SuppressWarnings("null")
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			desc = currentProject.getProjectDesc();

		if(desc != null && desc.length()>200)
			desc = desc.substring(0, 200) + " ...";

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
    			file = File.createTempFile(prefix + "_", "." + suffix, new File("/home/khalil/Bureau/"));
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
    			setUploadedFile(null);
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
		currentProject.setProjectLogo(event.getFile().getFileName() );

		System.out.println("File type: " + uploadedFile.getContentType());
		System.out.println("File name: " + uploadedFile.getFileName());
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
