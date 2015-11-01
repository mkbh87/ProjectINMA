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

	@PostConstruct
	public void init() {
		currentConfig = new Config();

		configList = configService.lister();
		if (configList.size()>0)
		{
			currentConfig = configList.get(0);
			ImgFilePath = configList.get(0).getConfigImgpath();			
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
		String bodymsg="Evenement modifié avec succès";
		try {
			
			if(currentConfig.getConfigId() == null)
				configService.enregistrer(currentConfig);
			else
				configService.mettre_a_jour(currentConfig);

		} catch(Exception e) {
			//Error during hibernate query
 			bodymsg= e.getMessage().replace("'", "") + "      ";
 			if(e.getCause() != null)
 				bodymsg  += e.getCause().getMessage().replace("'", "");
		}


		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification de parametre",bodymsg );
		RequestContext.getCurrentInstance().showMessageInDialog(message);
		
		ImgFilePath = currentConfig.getConfigImgpath();
		
		return "configs.xhtml?faces-redirect=true&amp;includeViewParams=true";

	}

	public String showEdit(Config p){
		currentConfig = p;
		return "edit-configs.xhtml?faces-redirect=true&amp;includeViewParams=true";
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
}