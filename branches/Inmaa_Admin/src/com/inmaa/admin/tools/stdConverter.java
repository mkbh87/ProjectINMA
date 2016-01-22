package com.inmaa.admin.tools;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Partner;
import com.inmaa.admin.persistence.Project;

@FacesConverter(value = "stdConverter", forClass = stdConverter.class)
public class stdConverter implements Converter {

	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/application-context.xml");

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if (arg1 instanceof PickList) {
			Object dualList = ((PickList) arg1).getValue();
			DualListModel dl = (DualListModel) dualList;

			for (Object o : dl.getSource()) {
				if (o instanceof Project) {
					String id = "" + ((Project) o).getProjectId();
					if (arg2.equals(id)) {
						ret = o; break;						
					}
				}else if (o instanceof Event){
					String id = "" + ((Event) o).getEventId();
					if (arg2.equals(id)) {
						ret = o; break;
					}
				}else if (o instanceof Partner){
					String id = "" + ((Partner) o).getPartnerId();
					if (arg2.equals(id)) {
						ret = o; break;
					}
				}else if (o instanceof Member){
					String id = "" + ((Member) o).getMemberId();
					if (arg2.equals(id)) {
						ret = o; break;
					}
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					if (o instanceof Project) {
						String id = "" + ((Project) o).getProjectId();
						if (arg2.equals(id)) {
							ret = o; break;
						}

					}
					else if (o instanceof Event){
						String id = "" + ((Event) o).getEventId();
						if (arg2.equals(id)) {
							ret = o; break;
						}
					}else if (o instanceof Partner){
						String id = "" + ((Partner) o).getPartnerId();
						if (arg2.equals(id)) {
							ret = o; break;
						}
					}else if (o instanceof Member){
						String id = "" + ((Member) o).getMemberId();
						if (arg2.equals(id)) {
							ret = o; break;
						}
					}
				}

		}
		return ret;
	}


	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String str = "";
		if (arg2 instanceof Project) 
			str = "" + ((Project) arg2).getProjectId();
		else if(arg2 instanceof Event)
			str = "" + ((Event) arg2).getEventId();
		else if(arg2 instanceof Partner)
			str = "" + ((Partner) arg2).getPartnerId();
		else if(arg2 instanceof Member)
			str = "" + ((Member) arg2).getMemberId();
		return str;
	}

	//	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
	//		if(value != null && value.trim().length() > 0) {
	//			try {  
	////			ProjectBean bean = (ProjectBean) fc.getCurrentInstance().getApplication().getClass();
	////			IProjectService service =(IProjectService) context.getBean("projectService");
	////			Project entity = service.lister().get(5);
	//			ProjectBean bean = fc.getApplication().evaluateExpressionGet(fc, "#{projectBean}", ProjectBean.class);
	//			Project entity = bean.getprojectById(Integer.parseInt(value));
	//			return entity;
	//			} catch(NumberFormatException e) {
	//				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid project."));
	//			}
	//		}
	//		else {
	//			return null;
	//		}
	//	}

	//	@Override
	//	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
	//		if(object != null && object != "") {
	//			return String.valueOf(((Project) object).getProjectId());
	//		}
	//		else {
	//			return null;
	//		}
	//	}   
} 