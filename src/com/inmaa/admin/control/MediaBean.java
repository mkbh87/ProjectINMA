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
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DualListModel;
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

	List<Media> target = new ArrayList<Media>();
	List<Media> source = new ArrayList<Media>();
	private DualListModel<Media> mediaModel;

	private List<Media> mediaList;

	private transient DataModel<Media> medias;
	private transient DataModel<Media> videos;

	private int id;
	private UploadedFile uploadedFile;
	private String fileName;



	@PostConstruct
	public void init() {
		mediaList = mediaService.lister();
		medias = new ListDataModel<Media>();
		medias.setWrappedData( mediaService.lister());
		
		
		mediaList = mediaService.listerVideos();
		videos = new ListDataModel<Media>();
		videos.setWrappedData( mediaService.listerVideos());
		
		source = getmediaList();
		List<Media> target = new ArrayList<Media>();
		mediaModel = new DualListModel<Media>(source, target);


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

	public DataModel<Media> getMedias() {
		return medias;
	}

	public void setMedias(DataModel<Media> medias) {
		this.medias = medias;
	}

	public String vider(){
		currentMedia = new Media();
		return null;
	}

	public List<Media> getmediaList() {

		return mediaList;
	}

	public void setMediaList(List<Media> medias) {
		this.mediaList = medias;
	}

	public String viewMediaDetail(){
		currentMedia = getMedias().getRowData();
		return "detailMedia";
	}
	
	public void setmediaModel(DualListModel<Media> mediaModel) {
		this.mediaModel = mediaModel;
	}
	public DualListModel<Media> getmediaModel() {
		return mediaModel;
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
		Iterator<Media> itr = medias.iterator();
		while(itr.hasNext()) {
			currentMedia = itr.next();
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
			medias.setWrappedData( mediaService.lister());
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
		medias.setWrappedData( mediaService.lister());

	}

	public String showEdit(Media p){
		currentMedia = p;
		setId(currentMedia.getMediaId());
		return "edit-medias.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}
	
	public String edit(){
		setId(currentMedia.getMediaId());
		String bodymsg="Evenement modifié avec succès";
		try {
			
			if(uploadedFile != null)
				submitLogoFile();

			mediaService.mettre_a_jour(currentMedia);

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

	public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Media Edited", ((Media) event.getObject()).getMediaName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
    
    public void addNewLine() {
        Media currentMedia = new Media();
          ((List<Media>) videos).add(currentMedia);
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
