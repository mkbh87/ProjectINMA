package com.inmaa.admin.control;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Abonnement;
import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.SubEvent;
import com.inmaa.admin.service.IAboService;
import com.inmaa.admin.service.IMemberService;


@Component("aboBean")
@ViewScoped
public class AboBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IAboService aboService;

	private Abonnement currentAbo ;

 	private DualListModel<Abonnement> aboModel;

	private List<Abonnement> aboList;

	private transient DataModel<Abonnement> abos;
	private int id;

	private int memberID;
	@Autowired
 	private IMemberService memberService;
 
	@PostConstruct
	public void init() {
		aboList = aboService.lister();
		abos = new ListDataModel<Abonnement>();
		abos.setWrappedData( aboService.lister());
	}

	public Abonnement getcurrentAbo() {
		return currentAbo;
	}

	public void setcurrentAbo(Abonnement p) {
		this.currentAbo = p;
	}

	public AboBean(){
		this.currentAbo = new Abonnement();	

	}

	public IAboService getAboService() {
		return aboService;
	}

	public void setAboService(IAboService aboService) {
		this.aboService = aboService;
	}

	public DataModel<Abonnement> getAbos() {
		return abos;
	}

	public void setAbos(DataModel<Abonnement> abos) {
		this.abos = abos;
	}

	public String vider(){
		currentAbo = new Abonnement();
		return null;
	}

	public List<Abonnement> getaboList() {

		return aboList;
	}

	public void setAboList(List<Abonnement> abos) {
		this.aboList = abos;
	}

	public String viewAboDetail(){
		currentAbo = getAbos().getRowData();
		return "detailAbo";
	}
	
	public void setaboModel(DualListModel<Abonnement> aboModel) {
		this.aboModel = aboModel;
	}
	public DualListModel<Abonnement> getaboModel() {
		return aboModel;
	}
	public void setId(int id) {
		this.id = id;
		currentAbo = getaboById(id);
	}
	public int getId() {
		return id;
	}

	public Abonnement getaboById(int id)
	{
		Iterator<Abonnement> itr = abos.iterator();
		while(itr.hasNext()) {
			Abonnement abo = itr.next();
			if(abo.getAbonnementId() == id)
			{
				return abo;
			}
		}
		return null;
	}
 	
	public String ajouter(){
		String bodymsg = "";
		try {
  			aboService.enregistrer(currentAbo);
			abos.setWrappedData( aboService.lister());
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
		return "table-abos.xhtml?faces-redirect=true";
	}

	public void delete(){
		try {

			aboService.supprimer(currentAbo);

		} catch(Exception e) {
			//Error during hibernate query
			System.out.print("Error: "+e.getMessage());
 			
		}
		currentAbo = new Abonnement();
		aboList = aboService.listerbyMember(memberID);

	}

	public String showEdit(Abonnement p){
		currentAbo = p;
		setId(currentAbo.getAbonnementId());
		return "edit-abos.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}
	
	public String edit(){
		setId(currentAbo.getAbonnementId());
		String bodymsg="Evenement modifié avec succès";
		try {
			
			aboService.mettre_a_jour(currentAbo);

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
	
	
	public void readyforDelete(Abonnement p){
		setcurrentAbo(p);
		setId(currentAbo.getAbonnementId());
		
	}
	
	public void onRowEdit(RowEditEvent event) {
		//currentAbonnement = new Abonnement((Abonnement) event.getObject());
 		try{
		if(  ((Abonnement) event.getObject()).getAbonnementId() == null )
		{
			
 			setcurrentAbo( (Abonnement) event.getObject());
 			currentAbo.setMember(MemberByID(memberID));
			
	        
	        aboService.enregistrer(currentAbo);
			//aboList = aboService.lister();

	        FacesMessage msg = new FacesMessage("Abonnement Saved", ""+currentAbo.getAbonnementYear());
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		else
		{
			setcurrentAbo( (Abonnement) event.getObject());
			aboService.mettre_a_jour(currentAbo);
			
			//aboList = aboService.lister();			

			FacesMessage msg = new FacesMessage("Abonnement Saved", ""+currentAbo.getAbonnementYear());
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		} catch(Exception e) {
			String bodymsg = null;
			//Error during hibernate query
			if(e.getCause() != null)
				bodymsg += e.getCause() + "  |  ";
			else
				bodymsg += e.getMessage() + "  |  ";

			bodymsg = bodymsg.replace("'", " ");
			e.printStackTrace();
			System.out.print("Error: "+e);
 
			FacesMessage msg    = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Enregistrement d'abo",bodymsg);
			FacesContext.getCurrentInstance().addMessage(null, msg);

		}
    }
	
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ""+((Abonnement) event.getObject()).getAbonnementId());
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
        
    public void newLine(ActionEvent actionEvent) {
    	aboList.add(new Abonnement());
    }

	public void getAbosbyMember(int id)
	{
		setMemberID(id);
		aboList = aboService.listerbyMember(id);
	}

	public int getMemberID() {
		return memberID;
	}

	public void setMemberID(int memberID) {
		this.memberID = memberID;
	}
	
    private Member MemberByID(int evID) {
    	Member ev = new Member();
		Iterator<Member> itr = memberService.lister().iterator();
		while(itr.hasNext()) {
			ev = itr.next();
			if(ev.getMemberId() == evID)
			{
				return ev;
			}
		}
		return null;
	
	}

}
