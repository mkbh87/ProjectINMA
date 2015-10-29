//package com.inmaa.admin.control;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.annotation.PostConstruct;
//import javax.faces.application.FacesMessage;
//import javax.faces.bean.SessionScoped;
//import javax.faces.model.DataModel;
//import javax.faces.model.ListDataModel;
//
//import org.primefaces.context.RequestContext;
//import org.primefaces.model.DualListModel;
//import org.primefaces.model.UploadedFile;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.inmaa.admin.persistence.Config;
//import com.inmaa.admin.persistence.Member;
//import com.inmaa.admin.service.IConfigService;
//
//@Component("configBean")
//@SessionScoped
//public class ConfigBean  implements Serializable {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Autowired
//	IConfigService configService;
//
//	private Config currentConfig ;
//	private List<Config> configList;
//	private transient DataModel<Config> configs;
//
//	List<Member> target = new ArrayList<Member>();
//	List<Member> source = new ArrayList<Member>();
//	private DualListModel<Member> memberModel;
//	private int id;
//	private UploadedFile uploadedFile;
//	private String fileName;
//
//	@PostConstruct
//	public void init() {
//		currentConfig = new Config();
//
//		configList = configService.lister();
//		configs = new ListDataModel<Config>();
//		configs.setWrappedData( configService.lister());
//	}
//
//	public Config getcurrentConfig() {
//		return currentConfig;
//	}
//
//	public void setcurrentConfig(Config p) {
//		this.currentConfig = p;
//	}
//
//	public ConfigBean(){
//		this.currentConfig = new Config();	
//
//	}
//
//	public IConfigService getConfigService() {
//		return configService;
//	}
//
//	public void setConfigService(IConfigService configService) {
//		this.configService = configService;
//	}
//
//	public DataModel<Config> getConfigs() {
//		return configs;
//	}
//
//	public void setConfigs(DataModel<Config> configs) {
//		this.configs = configs;
//	}
//
//	public String edit(){
//		setId(currentConfig.getConfigId());
//		String bodymsg="Evenement modifié avec succès";
//		try {
//			
//			
//
//			configService.mettre_a_jour(currentConfig);
//
//		} catch(Exception e) {
//			//Error during hibernate query
// 			bodymsg= e.getMessage().replace("'", "") + "      ";
// 			if(e.getCause() != null)
// 				bodymsg  += e.getCause().getMessage().replace("'", "");
//		}
//
//
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification évenement",bodymsg );
//		RequestContext.getCurrentInstance().showMessageInDialog(message);
//		vider();
//
//		return "";
//	}
//
//	public String showEdit(Config p){
//		currentConfig = p;
//		setId(currentConfig.getConfigId());
//		return "edit-configs.xhtml?faces-redirect=true&amp;includeViewParams=true";
//	}
//
//	
//	public String vider(){
//
//		currentConfig = new Config();
//		id = 0;
//		uploadedFile = null;
//		memberModel = null;
//		fileName= null;
//		return "OK";
//
//	}
//
//	public List<Config> getConfigList() {
//		configList = configService.lister();
//		return configList;
//	}
//
//	public void setConfigList(List<Config> configs) {
//		this.configList = configs;
//	}
//
//	public void setMemberModel(DualListModel<Member> memberModel) {
//		this.memberModel = memberModel;
//	}
//
//	public DualListModel<Member> getMemberModel() {
//		return memberModel;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//		if (id>0)
//			currentConfig = getconfigtById(id);
//
//	}
//
//	private Config getconfigtById(int id2) {
//		Iterator<Config> itr =configList.iterator();
//		while(itr.hasNext()) {
//			currentConfig = itr.next();
//			if(currentConfig.getConfigId() == id)
//			{
//				return currentConfig;
//			}
//		}
//		return null;
//	}
//
//	public int getId() {
//		return id;
//	}
//
//
//	public UploadedFile getUploadedFile() {
//		return uploadedFile;
//	}
//
//	public String getFileName() {
//		return fileName;
//	}
//
//	public void setUploadedFile(UploadedFile uploadedFile) {
//		this.uploadedFile = uploadedFile;
//	}
//
//	public String getDateFormated(Date d)
//	{
//		if(d != null)
//		{
//			String date;
//			Calendar startdate = new GregorianCalendar();
//			startdate.setTime(d);
//			date = "" + startdate.get(Calendar.DATE) 
//			+ " " +startdate.get(Calendar.MONTH)
//			+ " " + startdate.get(Calendar.YEAR);
//			return date;
//		}
//		return null;
//	}
//
//}