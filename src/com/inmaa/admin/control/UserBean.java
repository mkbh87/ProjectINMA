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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Project;
import com.inmaa.admin.persistence.Users;
import com.inmaa.admin.service.IUsersService;
import com.inmaa.admin.service.IUsersServiceImpl;

@Component("userBean")
@ViewScoped
public class UserBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String logedUser;

	@Autowired
	IUsersService userService;

	private Users currentUser ;
	private transient DataModel<Users> es;
	private String id;
	private List<Users> userList;
	private UploadedFile uploadedFile;
	private String fileName;

	private DualListModel<Project> listePro;
	List<Project> prosSource = new ArrayList<Project>();
	List<Project> prosTarget = new ArrayList<Project>();

	private DualListModel<Event> listeEv;
	List<Event> evesSource = new ArrayList<Event>();
	List<Event> evesTarget = new ArrayList<Event>();


	@PostConstruct
	public void init() {
		currentUser = new Users("1");
	}

	public Users getcurrentUser() {
		return currentUser;
	}

	public void setcurrentUser(Users p) {
		this.currentUser = p;
	}

	public UserBean(){
		this.currentUser = new Users();	

	}

	public IUsersService getUserService() {
		return userService;
	}

	public void setUserService(IUsersServiceImpl userService) {
		this.userService =  userService;
	}

	public DataModel<Users> getEs() {
		return es;
	}

	public void setEs(DataModel<Users> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();
  
			userService.enregistrer(currentUser);

			es.setWrappedData( userService.lister());
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
		return "table-users.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {
			userService.supprimer(currentUser);
			es.setWrappedData( userService.lister());
		} catch(Exception e) {
			//Error during hibernate query

			System.out.print("Error: "+e.getMessage());
		}
	}

	public void edit(){	
		String bodymsg="Projet modifié avec succès";
		try {

 
			userService.mettre_a_jour(currentUser);
			es.setWrappedData( userService.lister());
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


	public String showEdit(Users p){
		currentUser = p;
		setId(""+currentUser.getId());
		return "edit-users.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(Users p){
		currentUser = p;
		setId(currentUser.getId());
	}

	public void vider(){
		currentUser = new Users();
		uploadedFile = null;
		fileName= null;
		


	}

	public Users getuserById(String string)
	{
		Iterator<Users> itr = es.iterator();
		while(itr.hasNext()) {
			currentUser = itr.next();
			if(currentUser.getId() == string)
			{
				return currentUser;
			}
		}
		return null;
	}

	public void setId(String string) {
		this.id = string;
		currentUser = getuserById(string);
	}

	public String getId() {
		return id;
	}

	public List<Users> getUserList() {
		return userList;
	}

	public void setUserList(List<Users> users) {
		this.userList = users;
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


	public void initializeLazyJoins()
	{
		userService.initializeLazyJoins(currentUser);
 
	}

	public DualListModel<Project> getListePro() {
		return listePro;
	}

	 
	
	public void setListePro(DualListModel<Project> listePro) {
		this.listePro = listePro;
	}
	
	public DualListModel<Event> getListeEv() {
		return listeEv;
	}
	
	public void setListeEv(DualListModel<Event> listeEv) {
		this.listeEv = listeEv;
	}

	public static void setLogedUser(String userName) {
		// TODO Auto-generated method stub
		logedUser = userName;
	}
	
	public static String getLogedUser() {
		// TODO Auto-generated method stub
		return logedUser;
	}

}
