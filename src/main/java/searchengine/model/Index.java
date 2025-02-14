package searchengine.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Search_index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "page_id")
    private int pageId;

    @JoinColumn(name = "lemma_id")
    private int lemmaId;
    @Column(name = "`rank`", nullable = false)
    private Float rank;

    public Index(int pageId, int lemmaId, float ranking) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.rank = ranking;
    }

    public Index() {
    }

    @Override
    public String toString() {
        return "Index{" +
                "pageId=" + pageId +
                ", lemmaId=" + lemmaId +
                ", ranking=" + rank +
                '}';
    }
}


