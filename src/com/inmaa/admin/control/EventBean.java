package com.inmaa.admin.control;

import java.io.Serializable;
import java.sql.Timestamp;


import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.inmaa.admin.dao.EventDAO;
import com.inmaa.admin.persistence.Event;

@ManagedBean
@RequestScoped
public class EventBean  implements Serializable {
    private static final long serialVersionUID = 1L;

    private Event event;

    // Injection de notre EJB (Session Bean Stateless)
    @EJB
    private EventDAO    eventDAOH;

    // Initialisation de l'entité Event
    public EventBean() {
        event = new Event();
        eventDAOH = new EventDAO();
    }

    // Méthode d'action appelée lors du clic sur le bouton du formulaire
    // d'inscription
    public void creer() {
        initialiserDateInscription();
//        eventDAOH.ajouter( event );
        FacesMessage message = new FacesMessage( "Succès de l'inscription !" );
        FacesContext.getCurrentInstance().addMessage( null, message );
    }

    public Event getEvent() {
        return event;
    }

    private void initialiserDateInscription() {
        Timestamp date = new Timestamp( System.currentTimeMillis() );
        event.setEventStartDate( date );
    }
}