package searchengine.model;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Lemma")
public class Lemma {
    private static final Logger logger = LoggerFactory.getLogger(Lemma.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "site_id", nullable = false)
    private int siteId;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String lemma;

    @Column(nullable = false)
    private int frequency;

    public Lemma() {
    }

    public Lemma(String lemmaName, Integer frequency, int siteId) {
        if (lemmaName == null || lemmaName.isEmpty()) {
            logger.error("Attempting to set null or empty value to lemma");
            throw new IllegalArgumentException("Lemma cannot be null or empty");
        }
        this.lemma = lemmaName;
        this.frequency = frequency;
        this.siteId = siteId;
    }
}
