package tk.maxuz.blog.beans;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import tk.maxuz.blog.navigation.HtmlPage;

@ManagedBean
@SessionScoped
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private String username;
    private String password;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    
    
    public String login() {
        try {

            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).login(username, password);

            return HtmlPage.MAIN;
        } catch (ServletException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("Логин и пароль не подходят");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("login_form", message);

        }

        return HtmlPage.LOGIN;

    }

    public String logout() {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout();
        } catch (ServletException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        return HtmlPage.LOGIN;
    }
    
}
