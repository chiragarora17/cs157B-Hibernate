package MusicStore;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Table;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;

@Entity
@Table(name="instruments")
public class Instruments 
{
    private long id;
    private String instrument_name;
    private Description Info;
    private List<Accessories> accessories;

    public Instruments() {}
    
    public Instruments(String insName,  Description info)
    {
        this.instrument_name = insName;
        this.Info = info;
    }
    
    @Id
    @GeneratedValue
    @Column(name="id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    @Column(name="instrument_name")
    public String getInstrumentname() {
        return instrument_name;
    }

    /**
     * @param instrumentname the instrumentname to set
     */
    public void setInstrumentname(String instrumentname) {
        this.instrument_name = instrumentname;
    }
    
    
    @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="desc_id")
    public Description getDescInfo() { return Info; }
    public void setDescInfo(Description info) { this.Info = info; }
    
    @OneToMany(mappedBy="instrument", targetEntity=Accessories.class,
               cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    public List<Accessories> getAcc() { return accessories; }
    public void setAcc(List<Accessories> klasses) { this.accessories = klasses; }
    
    /**
     * Print teacher attributes.
     */
    private void print()
    {
        System.out.printf("%d: %s  (%s)\n", getId(), getInstrumentname(), Info.getDescription());
    }
    
    /**
     * Load the Teacher table.
     */
    public static void load()
    {
        Session session = HibernateContext.getSession();
        Transaction tx = session.beginTransaction();
        {
            session.save(new Instruments("Flute", 
                                     new Description("This is flute description")));
            session.save(new Instruments("Clarinet", 
                                     new Description("This is clarinet description")));
            session.save(new Instruments("Basson",
                                     new Description("This is basson description")));
            session.save(new Instruments("Picolo",
                                     new Description("This is picolo description")));
        }
        tx.commit();
        session.close();
        
        System.out.println("Instruments table loaded.");
    }
    
    
    /**
     * List all the teachers.
     */
    public static void list()
    {
        Session session = HibernateContext.getSession();
        Criteria criteria = session.createCriteria(Instruments.class);
       // criteria.addOrder(Order.asc("instrument_name"));
        
        List<Instruments> ins = criteria.list();
        System.out.println("All instruments:");
        
        for (Instruments i : ins) {
            i.print();
        }
        
        session.close();
    }
    
    /**
     * Fetch the teacher with a matching last name.
     * @param instrumentname the last name to match.
     * @return the teacher or null.
     */
    public static Instruments find(String instrumentname)
    {
        // Query by example.
        Instruments prototype = new Instruments();
        prototype.setInstrumentname(instrumentname);
        Example example = Example.create(prototype);  
        
        Session session = HibernateContext.getSession();
        Criteria criteria = session.createCriteria(Instruments.class);
        criteria.add(example);

        Instruments ins = (Instruments) criteria.uniqueResult();
        
        session.close();
        return ins;
    }
}