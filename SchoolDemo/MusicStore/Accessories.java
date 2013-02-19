package MusicStore;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@Entity
@Table(name="accessories")
public class Accessories 
{
    private long code;
    private Instruments instrument;
    private String name;
    private List<Student> students = new ArrayList<Student>();
    
    public Accessories() {}
    
    public Accessories(String Name)
    {
        this.name = Name;
    }
    
    @Id
    @GeneratedValue
    @Column(name="code")
    public long getCode() { return code; }
    public void setCode(long code) { this.code = code; }
    
    @ManyToOne
    @JoinColumn(name="instrumentid")
    public Instruments getInstrument() { return instrument; }
    public void setInstrument(Instruments ii) { this.instrument = ii; }
    
    @Column(name="name")
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    
    @ManyToMany
    @JoinTable(name="Student_Accessories", 
               joinColumns={@JoinColumn(name="accessory_code")},
               inverseJoinColumns={@JoinColumn(name="student_id")})
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    
    /**
     * Print class attributes.
     */
    private void print()
    {
        System.out.printf("%d: %s\n", code, name);
    }
    
    /**
     * Load the Class table.
     */
    public static void load()
    {
        Session session = HibernateContext.getSession();
        
        Instruments flute = Instruments.find("Flute");
        Instruments clarinet = Instruments.find("Clarinet");
        Instruments basson = Instruments.find("Basson");
        Instruments picolo = Instruments.find("Picolo");
        
        Accessories cleaning = new Accessories("Cleaning Rod");
        cleaning.setInstrument(flute);

        Accessories reeds = new Accessories("Reeds");
        reeds.setInstrument(clarinet);

        Accessories brush = new Accessories("Reed Brush");
        brush.setInstrument(basson);
        
        Accessories strap = new Accessories("Strap");
        strap.setInstrument(basson);

        Accessories pad = new Accessories("Pad");
        pad.setInstrument(picolo);
        
        Student doe = Student.find("Doe");
        Student jane = Student.find("Jane");
        Student novak = Student.find("Novak");
        Student smith = Student.find("Smith");
       // Assign classes to students.
        cleaning.getStudents().add(smith);
        cleaning.getStudents().add(doe);
        reeds.getStudents().add(doe);
        reeds.getStudents().add(novak);
        brush.getStudents().add(doe);
        strap.getStudents().add(novak);
        strap.getStudents().add(smith);
        pad.getStudents().add(smith);
        pad.getStudents().add(jane);

        Transaction tx = session.beginTransaction();
        {
            session.save(cleaning);
            session.save(reeds);
            session.save(brush);
            session.save(strap);
            session.save(pad);
        }
        tx.commit();
        session.close();
        
        System.out.println("Accessories table loaded.");
    }
    
    /**
     * List all the classes.
     */
    public static void list()
    {
        Session session = HibernateContext.getSession();
        Criteria criteria = session.createCriteria(Accessories.class);
        criteria.addOrder(Order.asc("name"));
        List<Accessories> accessories = criteria.list();
               
        System.out.println("All Accessories:");
        
        for (Accessories acc : accessories) {
            acc.print();
        }
        
        session.close();
    }
    
    /**
     * Fetch the class with a matching subject.
     * @param subject the subject to match.
     * @return the class or null.
     */
    public static Accessories find(String nametofind)
    {
        // Query by example.
        Accessories prototype = new Accessories();
        prototype.setName(nametofind);
        Example example = Example.create(prototype);
        
        Session session = HibernateContext.getSession();
        Criteria criteria = session.createCriteria(Accessories.class);
        criteria.add(example);
        
        Accessories klass = (Accessories) criteria.uniqueResult();
        
        session.close();
        return klass;
    }
    
    /**
     * Print a professor's classes.
     * @param instrumentname the professor's first name.
     */
    public static void instrumentOf(String instrumentname)
    {
        Session session = HibernateContext.getSession();
        Criteria classCriteria = session.createCriteria(Accessories.class);
        Criteria instrumentCriteria = classCriteria.createCriteria("instrument");

        // Match the first and last names.
        instrumentCriteria.add(Restrictions.eq("instrumentname",instrumentname ));
        
        // Distinct classes sorted by subject.
        classCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        classCriteria.addOrder(Order.asc("name"));
        List<Accessories> acc = (List<Accessories>) classCriteria.list();
        
        System.out.printf("Accessories of %s:\n", instrumentname);
        for (Accessories a : acc) {
            System.out.printf("    %s\n", a.getName());
        }
        session.close();
    }
}
