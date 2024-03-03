package com.yusufsezer.bean;

import com.yusufsezer.ejb.PersonService;
import com.yusufsezer.entity.Person;
import com.yusufsezer.helper.Helper;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class PersonBean implements Serializable {

    private Person person;
    private List<Person> persons;

    @EJB
    private PersonService personService;

    public PersonBean() {
    }

    @PostConstruct
    public void init() {
        persons = personService.findAll();
        person = new Person();
    }

    public Person getPerson() {
        return person;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public String login() {
        String passwordMd5 = Helper.md5(person.getPassword());
        Person foundPerson = personService
                .login(person.getEmail(), passwordMd5);

        if (foundPerson == null) {
            String summary = Helper.getResourceBundle("text")
                    .getString("msgNotFound");
            Helper.getFacesContext()
                    .addMessage(null, new FacesMessage(summary));
            return "login";
        }
        HttpSession session = (HttpSession) Helper.getFacesContext()
                .getExternalContext()
                .getSession(true);
        session.setAttribute("person", foundPerson);
        return "index.xhtml?faces-redirect=true";
    }

    public String register() {
        Person foundPerson = personService
                .checkPersonWithEmail(person.getEmail());

        if (foundPerson != null) {
            String summary = Helper.getResourceBundle("text")
                    .getString("msgAlreadyRegister");
            Helper.getFacesContext()
                    .addMessage(null, new FacesMessage(summary));
            return "register";
        }

        String encryptPassword = Helper.md5(person.getPassword());
        person.setPassword(encryptPassword);
        personService.create(person);
        return "login.xhtml?faces-redirect=true";
    }

}
