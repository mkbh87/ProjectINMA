package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import com.inmaa.admin.tools.Utils;

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
		articleList = new ArrayList<Article>();
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
			currentArticle.setArticleNbrVisite(0);

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
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement article",bodymsg );
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
					Utils.deletePicture(currentArticle.getArticlePicture());
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
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification article",bodymsg );
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
				msg="Erreur lors de l_envoie d_image, ";
				// Always log stacktraces (with a real logger).
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(output);
			}
			uploadedFile = null;
		}
		else
			msg="il y a pas d_image, ";

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
		articleService.initializeLazyJoins(currentArticle);
		
		prosTarget = new ArrayList<Project>();
		prosTarget = new ArrayList<Project>(currentArticle.getProjects());	
		prosSource = Utils.setListSource(prosTarget,projectService.lister());
		listePro  = new DualListModel<Project>(prosSource, prosTarget);

		evesTarget = new ArrayList<Event>();
		evesTarget = new ArrayList<Event>(currentArticle.getEvents());	
		evesSource = Utils.setListSource(evesTarget,eventService.lister());
		listeEv  = new DualListModel<Event>(evesSource, evesTarget);

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
	
	public void actualize()
	{
		articleList = new ArrayList<Article>();
		articleList = articleService.lister();
		es = new ListDataModel<Article>();
		es.setWrappedData( articleService.lister());
	}

}
