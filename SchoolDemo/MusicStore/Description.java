package MusicStore;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;

@Entity
@Table(name="desc_Info")
public class Description 
{
    private long id;
    private String desc;
    
    public Description() {}
    
    public Description(String d)
    {
        this.desc = d;
    }
    
    @Id
    @GeneratedValue
    @Column(name="id")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Column(name="description")
    public String getDescription() { return desc; }
    public void setDescription(String s) { this.desc = s; }
}
