package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Project {
    @Id
    private String uuid;
    private boolean active;
    private Double budget;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    private String customerreference;
    private String name;
    private boolean locked;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startdate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate enddate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clientuuid")
    private Client client;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="clientdatauuid")
    private Clientdata clientdata;

    private String userowneruuid;

    @Transient
    private User owner;

    @ManyToMany(mappedBy = "projects", fetch = FetchType.LAZY)
    private Set<Contract> contracts = new HashSet<>();

    private double longitude;
    private double latitude;
    private String address;

    public Project() {
    }

    public Project(String name, Client client, Clientdata clientdata) {
        this.clientdata = clientdata;
        uuid = UUID.randomUUID().toString();
        active = true;
        budget = 0.0;
        created = new Date();
        startdate = LocalDate.now();
        enddate = startdate.plusMonths(3);
        tasks = new ArrayList<>();
        customerreference = "";
        this.name = name;
        this.client = client;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCustomerreference() {
        return customerreference;
    }

    public void setCustomerreference(String customerreference) {
        this.customerreference = customerreference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDate startdate) {
        this.startdate = startdate;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDate enddate) {
        this.enddate = enddate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public boolean isActive() {
        return active;
    }

    public Clientdata getClientdata() {
        return clientdata;
    }

    public void setClientdata(Clientdata clientdata) {
        this.clientdata = clientdata;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(Set<Contract> contracts) {
        this.contracts = contracts;
    }

    public void addContract(Contract Contract) {this.contracts.add(Contract); }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "Project{" + "uuid='" + uuid + '\'' +
                ", active=" + active +
                ", created=" + created +
                ", customerreference='" + customerreference + '\'' +
                ", name='" + name + '\'' +
                ", startdate=" + startdate +
                ", enddate=" + enddate +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return uuid.equals(project.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public String getUserowneruuid() {
        return userowneruuid;
    }

    public void setUserowneruuid(String userowneruuid) {
        this.userowneruuid = userowneruuid;
    }
}
