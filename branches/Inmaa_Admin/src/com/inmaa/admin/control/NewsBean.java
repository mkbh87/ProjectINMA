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
public class NewsBean  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	INewsService newsService;

	private News currentNews ;
	private transient DataModel<News> newsList;

	public DataModel<News> getNewsList() {
		return newsList;
	}

	public void setNewsList(DataModel<News> newsList) {
		this.newsList = newsList;
	}


	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;
	private int id;
	private UploadedFile uploadedFile;
	private String fileName;

	@PostConstruct
	public void init() {
		currentNews = new News();

		newsList = new ListDataModel<News>();
		newsList.setWrappedData( newsService.lister());
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

	public String ajouter(){
		String bodymsg = "Evenement a été modifié avec succes";
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

 
		}

		vider();

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement évenement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);

		return bodymsg;
	}

	public String delete(News p){
		String bodymsg="Evenement suprimé avec succès";
		try {

			newsService.supprimer(p);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}
		currentNews = new News();
		newsList.setWrappedData( newsService.lister());

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Suppression évenement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);

		return bodymsg;
	}

	public String edit(){
		setId(currentNews.getNewsId());
		String bodymsg="Evenement modifié avec succès";
		try {
			
			if(uploadedFile != null)
				submitLogoFile();

			newsService.mettre_a_jour(currentNews);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification évenement",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
		vider();

		return "";
	}

	public String showEdit(News p){
		currentNews = p;
		setId(currentNews.getNewsId());
		return "edit-newss.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	public String viewNewsDetail(){
		currentNews = getNewsList().getRowData();
		setId(currentNews.getNewsId());
		return "form-newss.xhtml?faces-redirect=true&includeViewParams=true";

	}
	
	public String vider(){

		currentNews = new News();
		id = 0;
		uploadedFile = null;
		memberModel = null;
		fileName= null;
		return "OK";

	}

	public void setMemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}

	public DualListModel<Member> getMemberModel() {
		return memberModel;
	}

	public void setId(int id) {
		this.id = id;
		currentNews = getnewstById(id);

	}

	private News getnewstById(int id2) {
		Iterator<News> itr =newsList.iterator();
		while(itr.hasNext()) {
			currentNews = itr.next();
			if(currentNews.getNewsId() == id)
			{
				return currentNews;
			}
		}
		return null;
	}

	public int getId() {
		return id;
	}

	@SuppressWarnings("null")
	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			desc = currentNews.getNewsDesc();

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
				//currentNews.setNewsLogo(fileName);

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

	public void handleFileUpload(FileUploadEvent news) {

		uploadedFile = news.getFile();
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