package com.inmaa.admin.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.inmaa.admin.control.ConfigBean;
import com.inmaa.admin.persistence.Article;
import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Partner;
import com.inmaa.admin.persistence.Project;

public class Utils {


	public static <T> List<T> subtract(List<T> list1, List<T> list2) {
		boolean found = false;
		List<T> result = new ArrayList<T>();

		for (T t1 : list1) {
			for (T t2 : list2) {
				if (t1 instanceof Partner) {
					if( ((Partner) t1).getPartnerId().equals(((Partner) t2).getPartnerId()))  {
						found = true;
						break;
					}
				}else if (t1 instanceof Member) {
					if( ((Member) t1).getMemberId().equals(((Member) t2).getMemberId()))  {
						found = true;
						break;
					}
				}else if (t1 instanceof Project) {
					if( ((Project) t1).getProjectId().equals(((Project) t2).getProjectId()))  {
						found = true;
						break;
					}
				}else if (t1 instanceof Event) {
					if( ((Event) t1).getEventId().equals(((Event) t2).getEventId()))  {
						found = true;
						break;
					}
				}else if (t1 instanceof Article) {
					if( ((Article) t1).getArticleId().equals(((Article) t2).getArticleId()))  {
						found = true;
						break;
					}
				}
			}
			if(!found)
				result.add(t1);
			found = false;
		}
		return result;
	}

 	public static <T>  List<T> setListSource( List<T> target, List<T> source) {
		source = subtract(source, target );
		return  source;
	}

	public static void deletePicture(String pictureName) {
		if(pictureName.startsWith("img"))
		{
			File file = new File(ConfigBean.getImgFilePath() +"/"+ pictureName);
			Path path = file.toPath();
			try {
				Files.delete(path);
			} catch (NoSuchFileException x) {
				System.err.format("%s: no such" + " file or directory%n", path);
			} catch (DirectoryNotEmptyException x) {
				System.err.format("%s not empty%n", path);
			} catch (IOException x) {
				// File permission problems are caught here.
				System.err.println(x);
			}			
		}
	}

	public String getDateFormated(Date d)
	{
		if(d != null)
		{
			String date;
			Calendar startdate = new GregorianCalendar();
			startdate.setTime(d);
			date = "" + startdate.get(Calendar.DATE) 
			+ " " +startdate.get(Calendar.MONTH)
			+ " " + startdate.get(Calendar.YEAR);
			return date;
		}
		return null;
	}

	public String limitedDesc(String desc)
	{
		if(desc == null || desc == "")
			return null;

		if(desc != null && desc.length()>100)
			desc = desc.substring(0, 100) + " ...";

		return desc;
	}
}
