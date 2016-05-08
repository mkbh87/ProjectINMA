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
import com.inmaa.admin.service.IMemberService;
import com.inmaa.admin.tools.Utils;


@Component("memberBean")
@ViewScoped
public class MemberBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IMemberService memberService;

	private Member currentMember ;

	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;

	private List<Member> memberList;

	private transient DataModel<Member> members;
	private int id;
	private UploadedFile uploadedFile;
	private String fileName;



	@PostConstruct
	public void init() {
		memberList = memberService.lister();
		members = new ListDataModel<Member>();
		members.setWrappedData( memberService.lister());
		
		source = getmemberList();
		List<Member> target = new ArrayList<Member>();
		memberModel = new DualListModel<Member>(source, target);


	}
	public Member getcurrentMember() {
		return currentMember;
	}

	public void setcurrentMember(Member p) {
		this.currentMember = p;
	}

	public MemberBean(){
		this.currentMember = new Member();	

	}

	public IMemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(IMemberService memberService) {
		this.memberService = memberService;
	}

	public DataModel<Member> getMembers() {
		return members;
	}

	public void setMembers(DataModel<Member> members) {
		this.members = members;
	}

	public String vider(){
		currentMember = new Member();
		return null;
	}

	public List<Member> getmemberList() {

		return memberList;
	}

	public void setMemberList(List<Member> members) {
		this.memberList = members;
	}

	public String viewMemberDetail(){
		currentMember = getMembers().getRowData();
		return "detailMember";
	}
	
	public void setmemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}
	public DualListModel<Member> getmemberModel() {
		return memberModel;
	}
	public void setId(int id) {
		this.id = id;
		currentMember = getmemberById(id);
	}
	public int getId() {
		return id;
	}

	public Member getmemberById(int id)
	{
		Iterator<Member> itr = members.iterator();
		while(itr.hasNext()) {
			currentMember = itr.next();
			if(currentMember.getMemberId() == id)
			{
				return currentMember;
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
				currentMember.setMemberImage(fileName);
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
			int seqno = memberService.maxSeqno();
			currentMember.setSeqNo(seqno + 10);
			memberService.enregistrer(currentMember);
			members.setWrappedData( memberService.lister());
		} catch(Exception e) {
			//Error during hibernate query
			if(e.getCause() != null)
				bodymsg += e.getCause() + "  |  ";
			else
				bodymsg += e.getMessage() + "  |  ";

			bodymsg = bodymsg.replace("'", " ");
			e.printStackTrace();
			System.out.print("Error: "+e);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enregistrement du membre",bodymsg );
			RequestContext.getCurrentInstance().showMessageInDialog(message);
			return "";
		}
		vider();
		return "table-members.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {

			memberService.supprimer(currentMember);

		} catch(Exception e) {
			//Error during hibernate query
			System.out.print("Error: "+e.getMessage());
 			
		}
		currentMember = new Member();
		members.setWrappedData( memberService.lister());

	}

	public String showEdit(Member p){
		currentMember = p;
		setId(currentMember.getMemberId());
		return "edit-members.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}
	
	public String edit(){
		setId(currentMember.getMemberId());
		String bodymsg="Evenement modifié avec succès";
		try {
			
			if(uploadedFile != null)
			{
				if(currentMember.getMemberImage() != null)
					Utils.deletePicture(currentMember.getMemberImage());
				submitLogoFile();
			}

			memberService.mettre_a_jour(currentMember);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de membre",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
		vider();

		return "";
	}
	
	
	public void readyforDelete(Member p){
		currentMember = p;
		setId(currentMember.getMemberId());
		
	}

}
