package com.inmaa.admin.control;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Media;
import com.inmaa.admin.service.IMediaService;

@Component("mediaBean")
@ViewScoped
public class MediaBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IMediaService mediaService;

	private Media currentMedia ;

	private List<Media> pictureList;
	private List<Media> videoList;

	private transient DataModel<Media> pictures;
	private transient DataModel<Media> videos;

	private int id;
	private UploadedFile uploadedFile;
	private String fileName;
 

	@PostConstruct
	public void init() {
		pictures = new ListDataModel<Media>();
		pictures.setWrappedData( mediaService.listerPic());
		pictures = new ListDataModel<Media>();
		pictures.setWrappedData( mediaService.listerVideos());
	
		pictureList = mediaService.listerPic();
 		
		videoList = mediaService.listerVideos();
 		
	}
	public Media getcurrentMedia() {
		return currentMedia;
	}

	public void setcurrentMedia(Media p) {
		this.currentMedia = p;
	}

	public MediaBean(){
		this.currentMedia = new Media();	

	}

	public IMediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(IMediaService mediaService) {
		this.mediaService = mediaService;
	}

	public DataModel<Media> getPictures() {
		return pictures;
	}

	public void setPictures(DataModel<Media> medias) {
		this.pictures = medias;
	}

	public String vider(){
		currentMedia = new Media();
		return null;
	}

	public List<Media> getPictureList() {
		return pictureList;
	}

	public void setPictureList(List<Media> medias) {
		this.pictureList = medias;
	}

	public String viewMediaDetail(){
		currentMedia = getPictures().getRowData();
		return "detailMedia";
	}
	
	public void setId(int id) {
		this.id = id;
		currentMedia = getmediaById(id);
	}
	public int getId() {
		return id;
	}

	public Media getmediaById(int id)
	{
		Iterator<Media> itr = pictures.iterator();
		while(itr.hasNext()) {
			currentMedia = itr.next();
			if(currentMedia.getMediaId() == id)
			{
				return currentMedia;
			}
		}
		
		Iterator<Media> itr2 = videos.iterator();
		while(itr2.hasNext()) {
			currentMedia = itr2.next();
			if(currentMedia.getMediaId() == id)
			{
				return currentMedia;
			}
		}
		return null;
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
				fileName = file.getName();
				currentMedia.setMediaLink(fileName);
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
		}
		else
			msg="il y a pas d image, ";
		
		return msg;
	}	
	
	public void handleFileUpload(FileUploadEvent event) {

		uploadedFile = event.getFile();
	}

	
	public String ajouter(){
		String bodymsg = "";
		try {
			bodymsg = submitLogoFile();
			int seqno = mediaService.maxSeqno();
			currentMedia.setSeqNo(seqno + 10);
			mediaService.enregistrer(currentMedia);
			pictures.setWrappedData( mediaService.listerPic());
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
		return "table-medias.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {

			mediaService.supprimer(currentMedia);

		} catch(Exception e) {
			//Error during hibernate query
			System.out.print("Error: "+e.getMessage());
 			
		}
		currentMedia = new Media();
		pictures.setWrappedData( mediaService.listerPic());
		
		pictureList = mediaService.listerPic();
	
		videoList = mediaService.listerVideos();
	}

	public String showEdit(Media p){
		currentMedia = p;
		setId(currentMedia.getMediaId());
		return "edit-medias.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}
	
	public void readyforDelete(Media p){
		currentMedia = p;
		setId(currentMedia.getMediaId());
		
	}
	public DataModel<Media> getVideos() {
		return videos;
	}
	public void setVideos(DataModel<Media> videos) {
		this.videos = videos;
	}

	public void onPicRowEdit(RowEditEvent event) {
		//currentMedia = new Media((Media) event.getObject());
		if(  ((Media) event.getObject()).getMediaId() == null )
		{
			int seqno = mediaService.maxSeqno();
			((Media) event.getObject()).setSeqNo(seqno + 10);
			((Media) event.getObject()).setMediaType(true);
		}
			
        FacesMessage msg = new FacesMessage("Picture Edited", ((Media) event.getObject()).getMediaName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        mediaService.enregistrer((Media) event.getObject());
		pictureList = mediaService.listerPic();

    }
     
	public void onVidRowEdit(RowEditEvent event) {
		//currentMedia = new Media((Media) event.getObject());
		if(  ((Media) event.getObject()).getMediaId() == null )
		{
			int seqno = mediaService.maxSeqno();
			((Media) event.getObject()).setSeqNo(seqno + 10);
			((Media) event.getObject()).setMediaType(false);
		}
			
        FacesMessage msg = new FacesMessage("Video Edited", ((Media) event.getObject()).getMediaName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
        mediaService.enregistrer((Media) event.getObject());
		videoList = mediaService.listerVideos();
    }
	
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Media) event.getObject()).getMediaName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
     
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
         
        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
        
    public void newPicLine(ActionEvent actionEvent) {
    	pictureList.add(new Media());
    }

    public void newVidLine(ActionEvent actionEvent) {
    	videoList.add(new Media());
    }
    
	public List<Media> getVideoList() {
		return videoList;
	}

	public void setVideoList(List<Media> videoList) {
		this.videoList = videoList;
	}
}
