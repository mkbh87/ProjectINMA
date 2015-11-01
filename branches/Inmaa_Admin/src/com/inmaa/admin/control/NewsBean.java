package com.inmaa.admin.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.News;
import com.inmaa.admin.service.INewsService;

@Component("newsBean")
@ViewScoped
public class NewsBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	INewsService newsService;
	private News currentNews ;
	private transient DataModel<News> es;
	private int id;
	private List<News> newsList;
 	private UploadedFile uploadedFile;
	private String fileName;

	@PostConstruct
	public void init() {
		newsList = newsService.lister();
		es = new ListDataModel<News>();
		es.setWrappedData( newsService.lister());
//		source = getmemberList();
//		memberModel = new DualListModel<Member>(source, target);
		//vider();
	}

	public News getcurrentNews() {
		return currentNews;
	}

	public void setcurrentNews(News p) {
		this.currentNews = p;
	}

	public NewsBean(){
		this.currentNews = new News();	

	}

	public INewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(INewsService newsService) {
		this.newsService = newsService;
	}

	public DataModel<News> getEs() {
		return es;
	}

	public void setEs(DataModel<News> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();
			int seqno = newsService.maxSeqno();
			currentNews.setSeqNo(seqno + 10);
			newsService.enregistrer(currentNews);
			es.setWrappedData( newsService.lister());
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
			newsService.supprimer(currentNews);
			es.setWrappedData( newsService.lister());
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
				newsService.mettre_a_jour(currentNews);
				es.setWrappedData( newsService.lister());
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


	public String showEdit(News p){
		currentNews = p;
		setId(currentNews.getNewsId());
		return "edit-articles.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}
	
	public void readyforDelete(News p){
		currentNews = p;
		setId(currentNews.getNewsId());
	}
	
	public void vider(){
		currentNews = new News();
 		uploadedFile = null;
		fileName= null;
	}
	
	public News getnewsById(int p_id)
	{
		Iterator<News> itr = es.iterator();
		while(itr.hasNext()) {
			currentNews = itr.next();
			if(currentNews.getNewsId() == p_id)
			{
				return currentNews;
			}
		}
		return null;
	}

	public void setId(int id) {
		this.id = id;
		currentNews = getnewsById(id);
	}

	public int getId() {
		return id;
	}
	
	public List<News> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<News> newss) {
		this.newsList = newss;
	}


	@SuppressWarnings("null")
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			desc = currentNews.getNewsDesc();

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
				currentNews.setNewsPicture(file.getName());
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

}
