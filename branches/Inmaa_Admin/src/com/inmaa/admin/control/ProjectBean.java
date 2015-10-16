package com.inmaa.admin.control;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
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


	@PostConstruct
	public void init() {
		currentProject = new Project();
		
		projectList = projectService.lister();
		es = new ListDataModel<Project>();
		es.setWrappedData( projectService.lister());
		
		source = getmemberList();
		memberModel = new DualListModel<Member>(source, target);
		id = 0;
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
		
		List<Member> items = memberModel.getTarget() ;
		Set<Member> items2 = new HashSet<Member>();  

		Integer id;
		Member member;
		for(int i = 0; i<items.size(); i++) {
			 id = items.get(i).getMemberId();
			 member = projectService.getMember(id);
			items2.add(member);
		}
		currentProject.setMembers(items2);
		projectService.enregistrer(currentProject);
		currentProject = new Project();
		
		return null;
	}

	public String delete(Project p){
		projectService.supprimer(p);
		currentProject = new Project();
		es.setWrappedData( projectService.lister());
		return null;
	}

	public String edit(Project p){
		projectService.mettre_a_jour(p);
		currentProject = p;
		
		
 		setId(currentProject.getProjectId());
		return "form-projects.xhtml?faces-redirect=true&includeViewParams=true";
 	}
	
	public String vider(){
		currentProject = new Project();
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
	
	public Project getprojectBySeqNo(int Seqno)
	{
		Iterator<Project> itr = es.iterator();
		while(itr.hasNext()) {
			currentProject = itr.next();
			if(currentProject.getSeqNo() == Seqno)
			{
				return currentProject;
			}
		}
		return null;
	}

    public void handleFileUpload(FileUploadEvent event) {
//    	Image = (Part) event.getFile();
    	currentProject.setProjectLogo(event.getFile().getFileName() );
//        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
//        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
    
    
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			 desc = currentProject.getProjectDesc();
		
		if(desc.length()>200)
			desc = desc.substring(0, 200) + " ...";
		
		return desc;
	}

	
//    private static String getFilename(Part part) {  
//        for (String cd : part.getHeader("content-disposition").split(";")) {  
//            if (cd.trim().startsWith("filename")) {  
//                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");  
//                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.  
//            }  
//        }  
//        return null;  
//    }

//	public void setImage(Part tmp_logo) {
//		this.Image = tmp_logo;
//	}
//
//	public Part getImage() {
//		return Image;
//	}

}
