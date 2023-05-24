package ru.otus.crm.model;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.util.Objects.nonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq",
            initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = ALL, fetch = EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(cascade = ALL, fetch = EAGER)
    @JoinColumn(name = "client_id", nullable = false, updatable = false)
    private List<Phone> phoneList;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phoneList) {
        this.id = id;
        this.name = name;
        if (nonNull(address)) {
            this.address = address.clone();
            this.address.setClient(this);
        }
        if (nonNull(address)) {
            this.phoneList = clonePhoneList(phoneList);
        }
    }

    @Override
    public Client clone() {
        Client client = new Client(this.id, this.name);
        if (nonNull(this.address)) {
            Address address = this.address.clone();
            address.setClient(client);
            client.setAddress(address);
        }
        if (nonNull(phoneList)) {
            List<Phone> phones = clonePhoneList(this.phoneList);
            client.setPhoneList(phones);
        }
        return client;
    }

    private List<Phone> clonePhoneList(List<Phone> phoneList) {
        return phoneList.stream()
            .map(Phone::clone)
            .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equals(id, client.id) && Objects.equals(address,
            client.address) && Objects.equals(phoneList, client.phoneList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, phoneList);
    }

    @Override
    public String toString() {
        return "Client{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address=" + address +
            ", phoneList=" + phoneList +
            '}';
    }
}
