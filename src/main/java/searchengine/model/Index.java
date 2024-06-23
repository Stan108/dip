package searchengine.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Search_index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;
    @ManyToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;
    @Column(name = "`rank`", nullable = false)
    private Float rank;

    public Lemma getLemma() {
        return lemma;
    }


    public String toString() {
        return "Index{" +
                "pageId=" + page.getId() +
                ", lemmaId=" + lemma.getId() +
                ", ranking=" + rank +
                '}';
    }
}


