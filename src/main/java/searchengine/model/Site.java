package searchengine.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Entity
@Table(name = "Search_site")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IndexingStatus status;
    @Column(nullable = false)
    private LocalDateTime statusTime;
    @Column(columnDefinition = "TEXT")
    private String lastError;
    @Column(nullable = false, length = 255)
    private String url;
    @Column(nullable = false, length = 255)
    private String name;
    @OneToMany(mappedBy="site")
    private List<Page> pages;

    public Site(){}

    public Site(String url) {
        this.url = url;
    }

    public Site(String url, String name) {
        this.url = url;
        this.name = name;
    }
}
