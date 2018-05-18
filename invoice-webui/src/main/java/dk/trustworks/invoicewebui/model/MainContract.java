package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ContractType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class MainContract extends Contract {

    @OneToMany(mappedBy = "mainContract", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    private List<Consultant> consultants = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    @JoinTable(name = "projectcontracts",
            joinColumns = @JoinColumn(name = "contractuuid"),
            inverseJoinColumns = @JoinColumn(name = "projectuuid")
    )
    private Set<Project> projects = new HashSet<>();

    @Column(name = "activefrom")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate activeFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientuuid")
    private Client client;

    @OneToMany(mappedBy="parent", fetch = FetchType.LAZY)
    private List<SubContract> children = new ArrayList<>();

    public MainContract() {
        super();
    }

    public MainContract(ContractType contractType, LocalDate activeFrom, LocalDate activeTo, double amount, Client client) {
        super(amount, contractType, activeTo);
        this.activeFrom = activeFrom;
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public void addProject(Project project) {this.projects.add(project); }

    public void addProjects(Set<Project> projects) {this.projects.addAll(projects); }

    public List<Consultant> getConsultants() {
        return consultants;
    }

    public Consultant findByUser(User user) {
        Optional<Consultant> first = consultants.stream().filter(consultant -> consultant.getUser().getUuid().equals(user.getUuid())).findFirst();
        return first.isPresent()?first.get():null;
    }

    public LocalDate getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(LocalDate activeFrom) {
        this.activeFrom = activeFrom;
    }

    public void addConsultants(List<Consultant> consultants) {
        for (Consultant newConsultant : consultants) {
            boolean consultantExists = false;
            for (Consultant consultant : this.consultants) {
                if(consultant.getUser().getUuid().equals(newConsultant.getUser().getUuid())) consultantExists = true;
            }
            if(!consultantExists) this.consultants.add(newConsultant);
        }
    }

    public void addConsultant(Consultant newConsultant) {
        boolean consultantExists = false;
        for (Consultant consultant : this.consultants) {
            if(consultant.getUser().getUuid().equals(newConsultant.getUser().getUuid())) consultantExists = true;
        }
        if(!consultantExists) this.consultants.add(newConsultant);
    }

    public List<SubContract> getChildren() {
        return children;
    }

    public void setChildren(List<SubContract> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "MainContract{" +
                "consultants=" + consultants +
                ", projects=" + projects +
                ", activeFrom=" + activeFrom +
                ", client=" + client +
                ", children=" + children +
                '}';
    }
}
