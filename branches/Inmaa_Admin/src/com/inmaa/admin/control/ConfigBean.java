package com.inmaa.admin.control;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;

import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Config;
import com.inmaa.admin.service.IConfigService;

@Component("configBean")
@SessionScoped
public class ConfigBean  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IConfigService configService;

	private Config currentConfig ;
	private List<Config> configList;
	private static String ImgFilePath;
	public static int nbrVisite;
	

	@PostConstruct
	public void init() {
		currentConfig = new Config();

		configList = configService.lister();
		if (configList.size()>0)
		{
			currentConfig = configList.get(0);
			ImgFilePath = configList.get(0).getConfigImgpath();
			nbrVisite = configList.get(0).getConfigNbrVisite();
		}
	}

	public Config getcurrentConfig() {
		return currentConfig;
	}

	public void setcurrentConfig(Config p) {
		this.currentConfig = p;
	}

	public ConfigBean(){
		this.currentConfig = new Config();	

	}

	public IConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(IConfigService configService) {
		this.configService = configService;
	}


	public String edit(){
		String bodymsg="Configuration modifié avec succès";
		try {
			
			if(currentConfig.getConfigId() == null)
				configService.enregistrer(currentConfig);
			else
			{
				Config  conf = getTheConfiguration();
				conf.setConfigIntroInma(currentConfig.getConfigIntroInma());
				conf.setConfigIntroMaamoura(currentConfig.getConfigIntroMaamoura());
				conf.setConfigInmaGoal(currentConfig.getConfigInmaGoal());
				conf.setConfigPrincipalEmail(currentConfig.getConfigPrincipalEmail());
				
				configService.mettre_a_jour(conf);
			}

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de parametre",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
		
		ImgFilePath = currentConfig.getConfigImgpath();
		
		return "config.xhtml?faces-redirect=true&amp;includeViewParams=true";

	}

	public String showEdit(Config p){
		currentConfig = p;
		return "edit-config.xhtml?faces-redirect=true&amp;includeViewParams=true";
	}

	
	public String vider(){

		currentConfig = new Config();
		return "OK";

	}

	public List<Config> getConfigList() {
		configList = configService.lister();
		return configList;
	}

	public void setConfigList(List<Config> configs) {
		this.configList = configs;
	}

	public static String getImgFilePath() {
		return ImgFilePath;
	}

	public static void setImgFilePath(String imgFilePath) {
		ImgFilePath = imgFilePath;
	}

	public static int getNbrVisite() {
		return nbrVisite;
	}

	public static void setNbrVisite(int nbrVisite) {
		ConfigBean.nbrVisite = nbrVisite;
	}
	
	public void actualize()
	{
		configList = configService.lister();
		if (configList.size()>0)
		{
			currentConfig = configList.get(0);
			ImgFilePath = configList.get(0).getConfigImgpath();
			nbrVisite = configList.get(0).getConfigNbrVisite();
		}
	}

	public String editSmtp(){
		String bodymsg="Configuration modifié avec succès";
		try {
			
			
			if(currentConfig.getConfigId() == null)
				configService.enregistrer(currentConfig);
			else{
				Config  conf = getTheConfiguration();
				conf.setConfigSmtpHost(currentConfig.getConfigSmtpHost());
				conf.setConfigSmtpPort(currentConfig.getConfigSmtpPort());
				conf.setConfigSmtpPwd(currentConfig.getConfigSmtpPwd());
				conf.setConfigSmtpUser(currentConfig.getConfigSmtpUser());
				
				configService.mettre_a_jour(conf);
			}

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de parametre",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
				
		return "config.xhtml?faces-redirect=true&amp;includeViewParams=true";


	}
	
	private Config getTheConfiguration(){
		Config c = new Config();

		configList = configService.lister();
		if (configList.size()>0)
			c = configList.get(0);
		
		return c;
	}
}