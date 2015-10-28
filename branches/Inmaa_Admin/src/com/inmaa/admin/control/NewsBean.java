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
	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private UploadedFile uploadedFile;
	private String fileName;

	@PostConstruct
	public void init() {
 		es = new ListDataModel<News>();
		es.setWrappedData( newsService.lister());
		this.currentNews = new News();	

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
		es.setWrappedData( newsService.lister());
		return es;
	}

	public void setEs(DataModel<News> es) {
		this.es = es;
	}

	public String ajouter(){
		String bodymsg = "L'article a été modifié avec succes";
		submitLogoFile();
		try {
			
			int seqno = newsService.maxSeqno();
			currentNews.setSeqNo(seqno + 10);
			newsService.enregistrer(currentNews);

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

	public String delete(News p){
		String bodymsg="Article suprimé avec succès";
		try {

			newsService.supprimer(p);

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
		String bodymsg="Article modifié avec succès";
		try {

			if(uploadedFile != null)
				submitLogoFile();
			newsService.mettre_a_jour(currentNews);

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


	public String showEdit(News p){
		currentNews = p;
		setId(currentNews.getNewsId());
		return "edit-articles.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public String vider(){

		currentNews = new News();
 		uploadedFile = null;
		memberModel = null;
		fileName= null;
		return "OK";

	}
	
	public News getnewsById(int id)
	{
		Iterator<News> itr = es.iterator();
		while(itr.hasNext()) {
			currentNews = itr.next();
			if(currentNews.getNewsId() == id)
			{
				return currentNews;
			}
		}
		return null;
	}

	public void setmemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getmemberModel() {
		return memberModel;
	} 

	public void setId(int id) {
		this.id = id;
		if (id>0)
			currentNews = getnewsById(id);
	}

	public int getId() {
		return id;
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


	public void submitLogoFile() {

		if (uploadedFile != null){
			// Prepare filename prefix and suffix for an unique filename in upload folder.
			//String prefix = FilenameUtils.getBaseName(uploadedFile.getFileName());
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
				currentNews.setNewsPicture(fileName);

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
