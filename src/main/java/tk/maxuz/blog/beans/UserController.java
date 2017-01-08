package tk.maxuz.blog.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import tk.maxuz.blog.navigation.HtmlPage;

@Named
@SessionScoped
public class UserController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String username;
	
	public String getUsername() {
		if (username == null) {
			throw new IllegalStateException("User is not signed in");
		}
		return username;
	}
	
	public String login(User user) {
        try {
        	HttpServletRequest request = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
        	
        	if (request.getUserPrincipal()==null || (request.getUserPrincipal()!=null && !request.getUserPrincipal().getName().equals(username))) {
                request.logout();
                request.login(user.getUsername(), user.getPassword());
                username = user.getUsername();
            }
            
            return HtmlPage.MAIN;
        } catch (ServletException ex) {
            ex.printStackTrace();
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage("Login or password wrong");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("login_form", message);

        }

        return HtmlPage.LOGIN;

    }

    public String logout() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        try {
            request.logout();
        } catch (ServletException ex) {
        	ex.printStackTrace();
        }

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        return HtmlPage.LOGIN;
    }
}
