package tk.maxuz.blog.connection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import tk.maxuz.blog.db.entity.Category;
import tk.maxuz.blog.db.entity.Note;
import tk.maxuz.blog.db.entity.Tag;

@Named
@ApplicationScoped
public class SessionProvider {

	private SessionFactory sessionFactory;

	public SessionProvider() {
		try {
			StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure().build();
			MetadataSources ms = new MetadataSources(serviceRegistry)
					.addAnnotatedClass(Category.class)
					.addAnnotatedClass(Tag.class)
					.addAnnotatedClass(Note.class);

			Metadata metadata = ms.getMetadataBuilder().build();
			sessionFactory = metadata.getSessionFactoryBuilder().build();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
