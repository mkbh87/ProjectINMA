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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import com.inmaa.admin.persistence.Article;
import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Project;
import com.inmaa.admin.service.IArticleService;
import com.inmaa.admin.service.IArticleServiceImpl;
import com.inmaa.admin.service.IEventService;
import com.inmaa.admin.service.IProjectService;

@Component("articleBean")
@ViewScoped
public class ArticleBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IArticleService articleService;


	@Autowired
	IEventService eventService;	

	@Autowired
	IProjectService projectService;

	private Article currentArticle ;
	private transient DataModel<Article> es;
	private int id;
	private List<Article> articleList;
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
		articleList = articleService.lister();
		es = new ListDataModel<Article>();
		es.setWrappedData( articleService.lister());
	}

	public Article getcurrentArticle() {
		return currentArticle;
	}

	public void setcurrentArticle(Article p) {
		this.currentArticle = p;
	}

	public ArticleBean(){
		this.currentArticle = new Article();	

	}

	public IArticleService getArticleService() {
		return articleService;
	}

	public void setArticleService(IArticleServiceImpl articleService) {
		this.articleService = articleService;
	}

	public DataModel<Article> getEs() {
		return es;
	}

	public void setEs(DataModel<Article> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();
			int seqno = articleService.maxSeqno();
			currentArticle.setSeqNo(seqno + 10);

			Set<Project> tempP = new HashSet<Project>(listePro.getTarget());
			currentArticle.setProjects(tempP);

			Set<Event> tempE = new HashSet<Event>(listeEv.getTarget());
			currentArticle.setEvents(tempE);
			articleService.enregistrer(currentArticle);

			es.setWrappedData( articleService.lister());
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
		return "table-articles.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {
			articleService.supprimer(currentArticle);
			es.setWrappedData( articleService.lister());
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
				if(currentArticle.getArticlePicture() != null)
					deletePicture(currentArticle.getArticlePicture());
				submitLogoFile();
			}

			Set<Project> temp = new HashSet<Project>(listePro.getTarget());
			currentArticle.setProjects(temp);
			
			Set<Event> tempE = new HashSet<Event>(listeEv.getTarget());
			currentArticle.setEvents(tempE);

			articleService.mettre_a_jour(currentArticle);
			es.setWrappedData( articleService.lister());
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


	public String showEdit(Article p){
		currentArticle = p;
		setId(currentArticle.getArticleId());
		return "edit-articles.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public void readyforDelete(Article p){
		currentArticle = p;
		setId(currentArticle.getArticleId());
	}

	public void vider(){
		currentArticle = new Article();
		uploadedFile = null;
		fileName= null;
		
		prosTarget = new ArrayList<Project>();
		prosSource = projectService.lister();
		listePro  = new DualListModel<Project>(prosSource, prosTarget);
		
		evesTarget = new ArrayList<Event>();
		evesSource = eventService.lister();
		listeEv  = new DualListModel<Event>(evesSource, evesTarget);


	}

	public Article getarticleById(int p_id)
	{
		Iterator<Article> itr = es.iterator();
		while(itr.hasNext()) {
			currentArticle = itr.next();
			if(currentArticle.getArticleId() == p_id)
			{
				return currentArticle;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentArticle = getarticleById(id);
	}

	public int getId() {
		return id;
	}

	public List<Article> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<Article> articles) {
		this.articleList = articles;
	}


	@SuppressWarnings("null")
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			desc = currentArticle.getArticleDesc();

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
				currentArticle.setArticlePicture(file.getName());
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

	public void initializeLazyJoins()
	{
		articleService.initializeLazyJoins(currentArticle);
		
		prosTarget = new ArrayList<Project>();
		prosTarget = new ArrayList<Project>(currentArticle.getProjects());	
		prosSource = setprosSource(prosTarget);
		listePro  = new DualListModel<Project>(prosSource, prosTarget);

		evesTarget = new ArrayList<Event>();
		evesTarget = new ArrayList<Event>(currentArticle.getEvents());	
		evesSource = setevesSource(evesTarget);
		listeEv  = new DualListModel<Event>(evesSource, evesTarget);

	}

	public DualListModel<Project> getListePro() {
		return listePro;
	}

	public List<Project> setprosSource(List<Project> target) {
		List<Project> listSource = projectService.lister();
		listSource = subtract(listSource, target );
		return listSource;
	}
	
	public void setListePro(DualListModel<Project> listePro) {
		this.listePro = listePro;
	}
	
	public DualListModel<Event> getListeEv() {
		return listeEv;
	}
	
	public List<Event> setevesSource(List<Event> target) {
		List<Event> listSource = eventService.lister();
		listSource = subtract(listSource, target );
		return listSource;
	}


	public void setListeEv(DualListModel<Event> listeEv) {
		this.listeEv = listeEv;
	}

	public<T> List<T> subtract(List<T> list1, List<T> list2) {
		boolean found = false;
		List<T> result = new ArrayList<T>();

		for (T t1 : list1) {
			for (T t2 : list2) {
				if (t1 instanceof Project) {
					if( ((Project) t1).getProjectId().equals(((Project) t2).getProjectId()))  {
						found = true;
						break;
					}
				}else if (t1 instanceof Event) {
					if( ((Event) t1).getEventId().equals(((Event) t2).getEventId()))  {
						found = true;
						break;
					}
				}
			}
			if(!found)
				result.add(t1);
			found = false;
		}
		return result;
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
