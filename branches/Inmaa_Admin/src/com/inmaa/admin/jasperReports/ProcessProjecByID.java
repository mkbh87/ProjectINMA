package com.inmaa.admin.jasperReports;



import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.inmaa.admin.control.ConfigBean;

@ManagedBean(name = "projecByIDBean")
@SessionScoped

public class ProcessProjecByID extends AbstractReportBean {

	private final String COMPILE_FILE_NAME="projectByID";


	private int project_ID;
	private boolean with_Detail;
	private String img_Path;

	@Override
	protected String getCompileFileName() {
		return COMPILE_FILE_NAME;
	}

	@Override
	protected Map<String, Object> getReportParameters() {
		Map<String, Object> reportParameters = new HashMap<String, Object>();
        setImg_Path(ConfigBean.getImgFilePath());
		reportParameters.put("projectId", project_ID);// on passe le id selectionné fel xhtml lel application
		reportParameters.put("ImgPath", img_Path);
		reportParameters.put("withDetail", with_Detail);

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

	public int getProject_ID() {
		return project_ID;
	}

	public void setProject_ID(int project_ID) {
		this.project_ID = project_ID;
	}

	public boolean getWith_Detail() {
		return with_Detail;
	}

	public void setWith_Detail(boolean with_Detail) {
		this.with_Detail = with_Detail;
	}

	public Object getImg_Path() {
		return img_Path;
	}

	public void setImg_Path(String img_Path) {
		this.img_Path = img_Path;
	}
}