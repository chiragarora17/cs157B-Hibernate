package MusicStore;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateContext 
{
    public static AnnotationConfiguration config = null;
    public static SessionFactory factory = null;
    
    /**
     * Set the configuration if it is null.
     */
    private static void setConfiguration()
    {
        if (config == null) {
            config = new AnnotationConfiguration();
            config.configure();
        }
    }
    
    /**
     * Set the factory if it is null.
     */
    private static void setFactory()
    {
        if (factory == null) {
            setConfiguration();
            factory = config.buildSessionFactory();
        }
    }
    
    /**
     * Open a session from the factory.
     * @return a session.
     */
    public static Session getSession()
    {
        setFactory();
        return factory.openSession();
    }
    
    /**
     * Create a new schema (database) from the configuration.
     */ 
    public static void createSchema()
    {
        setConfiguration();
        (new SchemaExport(config)).create(true, true);
    }

    /**
     * Add a new class object to the database.
     * @param klass the class object.
     */
    public static void addClass(Class klass)
    {
        setConfiguration();
        config.addAnnotatedClass(klass);
    }
    
    /**
     * Add a list of class objects to the database.
     * @param klasses the list of class objects.
     */
    public static void addClasses(Class klasses[])
    {
        for (Class klass : klasses) {
            addClass(klass);
        }
    }
}
