package searchengine.model;



import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

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


//@Data
//@Entity
//@Table(name = "Search_page",indexes = {@Index(name = "Path_INDX", columnList = "path")})
//public class Page {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private int id;
//    @Column(nullable = false)
//    private int siteId;
//    @Column(nullable = false, length = 255)
//    private String path;
//    @Column(nullable = false)
//    private int code;
//    @Lob
//    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
//    private String content;
//
//    public Page() {}
//
//}
