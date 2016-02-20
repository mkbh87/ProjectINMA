package com.inmaa.admin.control;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
@ManagedBean(name="navigator")
@SessionScoped
public class Navigator {
    private String pageToNavigate = "";
     
    public String navigateTo(){
        if("ToSecure".equalsIgnoreCase(pageToNavigate)){
            return "Secured";
        } else if("ToUnSecure".equalsIgnoreCase(pageToNavigate)){
            return "UnSecured";
        } else {
            //This will never happen but we will use this to extend this application
            return "none";
        }
    }
     
    public String navigateTo(String ch ){
        if("table-projects".equalsIgnoreCase(ch)){
            return "table-projects";
        } else if("form-projects".equalsIgnoreCase(ch)){
            return "form-projects";
        } else {
            //This will never happen but we will use this to extend this application
            return "none";
        }
    }
    public String getPageToNavigate() {
        return pageToNavigate;
    }
    public void setPageToNavigate(String option) {
        this.pageToNavigate = option;
    }
}