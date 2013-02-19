package MusicStore;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Table;
import org.hibernate.Criteria;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@Entity
@Table(name="student")
public class Student 
{
    private long id;
    private String firstName;
    private String lastName;
    private List<Accessories> accessreyes = new ArrayList<Accessories>();
    
    public Student() {}
    
    public Student(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    @Id
    @GeneratedValue
    @Column(name="id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    @Column(name="first_name")
    public String getFirstName() { return firstName; }
    public void setFirstName(String name) { this.firstName = name; }
    
    @Column(name="last_name")
    public String getLastName() { return lastName; }
    public void setLastName(String name) { this.lastName = name; }
        
    @ManyToMany
    @JoinTable(name="Student_Accessories", 
               joinColumns={@JoinColumn(name="student_id")},
               inverseJoinColumns={@JoinColumn(name="accessory_code")})
    public List<Accessories> getAccessories() { return accessreyes; }
    public void setAccessories(List<Accessories> acc) { this.accessreyes = acc; }
    
    /**
     * Print student attributes.
     */
    public void print()
    {
        System.out.printf("%d: %s %s \n", id, firstName, lastName);
    }
    
    /**
     * Print student attributes within a session.
     */
    public void printInSession()
    {
        Session session = HibernateContext.getSession();
        session.update(this);
        print();
        session.close();
    }
    
    /**
     * Load the Student table.
     */
    public static void load()
    {
        Session session = HibernateContext.getSession();
        
        // Load the Student table in a transaction.
        Transaction tx = session.beginTransaction();
        {
            session.save(new Student("Mary", "Jane"));
            session.save(new Student("Kim", "Smith"));
            session.save(new Student("John", "Doe"));
            session.save(new Student("Tim", "Novak"));
            session.save(new Student("Leslie", "Klein"));
        }
        tx.commit();
        session.close();

        System.out.println("Student table loaded.");
    }  
    
    /**
     * List all the students and their classes and classmates.
     */
    public static void list()
    {
        Session session = HibernateContext.getSession();
        Criteria criteria = session.createCriteria(Student.class);
        criteria.addOrder(Order.asc("lastName"));
        
        List<Student> students = criteria.list();       
        System.out.println("All students:");
        
        // Loop over each student.
        for (Student student : students) {
            student.print();
            
            // Loop over the student's classes.
            for (Accessories acc : student.getAccessories()) {
                System.out.printf("    Accessory owned: %s\n", acc.getName());
                
          /*      // Loop over the classmates in each class.
                for (Student mate : (List<Student>) acc.getStudents()) {
                    if (student.getId() != mate.getId()) {
                        System.out.printf("        Owned by Friend: %s %s\n",
                                          mate.getFirstName(),
                                          mate.getLastName());
                    }
                }*/
            }
        }
        
        session.close();
    }
    
    /**
     * Fetch the student with a matching id.
     * @param id the id to match.
     * @return the student or null.
     */
    public static Student find(long id)
    {
        // Query using HQL.
        Session session = HibernateContext.getSession();
        Query query = session.createQuery("from Student where id = :idvar");
        
        query.setLong("idvar", id);
        Student student = (Student) query.uniqueResult();
        
        session.close();
        return student;
    }
    
    /**
     * Fetch the student with a matching last name.
     * @param lastName the last name to match.
     * @return the student or null.
     */
    public static Student find(String lastName)
    {
        // Query by example.
        Student prototype = new Student();
        prototype.setLastName(lastName);
        Example example = Example.create(prototype);  
        
        Session session = HibernateContext.getSession();
        Criteria criteria = session.createCriteria(Student.class);
        criteria.add(example);

        Student student = (Student) criteria.uniqueResult();
        
        session.close();
        return student;
    }
    
    /**
     * Add a student to the Student table.
     */
/*    public static void add()
    {
        Student newbie = new Student("Katerina", "Zelkowitsky",
                                     new ContactInfo("kzelkowitsky@sjsu.edu"));
        
        // Classes the new student takes.
        Accessories ds = Accessories.find("Data structures");
        Accessories os = Accessories.find("Operating systems");
        newbie.getAccessories().add(ds);
        newbie.getAccessories().add(os);
        
        Session session = HibernateContext.getSession();
        Transaction tx = session.beginTransaction();
        {
            session.save(newbie);
        }
        tx.commit();
        session.close();
        
        System.out.println("Added student:");
        newbie.print();
    }
    
    /**
     * Delete a student from the Student table.
     */
  /*  public static void delete()
    {
        System.out.println("Deleting student:");        
        Student zelkowitsky = Student.find("Zelkowitsky");
        
        Session session = HibernateContext.getSession();
        Transaction tx = session.beginTransaction();
        {
            session.update(zelkowitsky);
            zelkowitsky.print();
            session.delete(zelkowitsky);
        }
        tx.commit();
        session.close();
    }
    
    /**
     * Print a professor's students.
     * @param first the professor's first name.
     * @param last the professor's last name.
     */
   public static void studentsOf(String instrumentname)
    {
        Session session = HibernateContext.getSession();
        Criteria classCriteria = session.createCriteria(Student.class);
        Criteria instrumentCriteria = classCriteria.createCriteria("accessories");

        // Match the first and last names.
        instrumentCriteria.add(Restrictions.eq("name",instrumentname ));
        
        // Distinct classes sorted by subject.
        classCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        classCriteria.addOrder(Order.asc("lastName"));
        List<Student> acc = (List<Student>) classCriteria.list();
        
        System.out.printf("Student with accessory %s are:\n", instrumentname);
        for (Student a : acc) {
            System.out.printf("    %s,%s\n", a.getFirstName(),a.getLastName());
        }
        session.close();
    }
}
