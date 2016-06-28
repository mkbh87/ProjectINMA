package com.inmaa.admin.jasperReports;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.inmaa.admin.control.ConfigBean;
 
@ManagedBean(name = "projecByDateBean")
@SessionScoped
 
public class ProcessProjecByDate extends AbstractReportBean {
 
    private final String COMPILE_FILE_NAME="projectByDate";
 
    private Date dateFrom;
	
	private Date dateTo;

	private String img_Path;


 

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	@Override
    protected String getCompileFileName() {
        return COMPILE_FILE_NAME;
    }
 
    @Override
    protected Map<String, Object> getReportParameters() {
        Map<String, Object> reportParameters = new HashMap<String, Object>();
        setImg_Path(ConfigBean.getImgFilePath());
        reportParameters.put("ImgPath", img_Path);
        reportParameters.put("DateFrom", dateFrom);
        reportParameters.put("DateTo", dateTo);
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

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

}