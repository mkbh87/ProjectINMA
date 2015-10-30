package com.inmaa.admin.tools;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inmaa.admin.control.MemberBean;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.service.IMemberService;


@FacesConverter("memberConverter")
public class MemberConverter implements Converter {

	private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/application-context.xml");
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if(value != null && value.trim().length() > 0) {
			try {  
			///MemberBean bean = (MemberBean) fc.getCurrentInstance().getApplication().getClass();
			IMemberService service =(IMemberService) context.getBean("MemberService");
			Member entity = service.lister().get(5);
			MemberBean bean = fc.getApplication().evaluateExpressionGet(fc, "#{memberBean}", MemberBean.class);
			//bean.getmemberById(Integer.parseInt(value));
			return entity;
			} catch(NumberFormatException e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid member."));
			}
		}
		else {
			return null;
		}
	}

	public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if(object != null && object != "") {
			return String.valueOf(((Member) object).getMemberId());
		}
		else {
			return null;
		}
	}   
} 