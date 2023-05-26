package ru.otus.crm.model;

import jakarta.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table(name = "client")
public class Client {

    @Id
    private final Long id;

    @Nonnull
    private final String name;

    @Nonnull
    private final Long addressId;

    @MappedCollection(idColumn = "client_id")
    private final Set<Phone> phoneSet;

    public Client(String name, Long addressId, Set<Phone> phoneSet) {
        this.id = null;
        this.name = name;
        this.addressId = addressId;
        this.phoneSet = phoneSet;
    }

    @PersistenceCreator
    public Client(Long id, String name, Long addressId,
        Set<Phone> phoneSet) {
        this.id = id;
        this.name = name;
        this.addressId = addressId;
        this.phoneSet = phoneSet;
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
        return Objects.equals(id, client.id) && Objects.equals(addressId,
            client.addressId) && Objects.equals(phoneSet, client.phoneSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressId, phoneSet);
    }

    @Override
    public String toString() {
        return "Client{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", addressId=" + addressId +
            ", phoneSet=" + phoneSet +
            '}';
    }
}
