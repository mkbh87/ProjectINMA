package com.inmaa.admin.jasperReports;



import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.inmaa.admin.control.ConfigBean;

@ManagedBean(name = "memberListBean")
@SessionScoped

public class ProcessMemberList extends AbstractReportBean {

	private final String COMPILE_FILE_NAME="MemberList";
	private String img_Path;

	@Override
	protected String getCompileFileName() {
		return COMPILE_FILE_NAME;
	}

	@Override
	protected Map<String, Object> getReportParameters() {
        setImg_Path(ConfigBean.getImgFilePath());
		Map<String, Object> reportParameters = new HashMap<String, Object>();
		reportParameters.put("ImgPath", img_Path);

		return reportParameters;
	}

	public String execute() {
		try {
			super.prepareReport();
		} catch (Exception e) {
			// make your own exception handling
			e.printStackTrace();
		}

		return null;
	}
	public String getImg_Path() {
		return img_Path;
	}

	public void setImg_Path(String img_Path) {
		this.img_Path = img_Path;
	}
}