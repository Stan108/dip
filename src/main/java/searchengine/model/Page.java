package searchengine.model;
import lombok.Data;
import javax.persistence.*;
import javax.persistence.Index;

@Data
@Entity
@Table(name = "Search_page", indexes = {@Index(name = "Path_INDX", columnList = "path")})
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "site_id")
    private int siteId;
    @Column(name = "path", columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "site")
    private Site site;
}