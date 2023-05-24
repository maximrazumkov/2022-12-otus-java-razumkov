package ru.otus.crm.model;

import static jakarta.persistence.GenerationType.SEQUENCE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "phone")
public class Phone implements Cloneable {

    @Id
    @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq",
        initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "phone_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    public Phone(Long id, String street) {
        this.id = id;
        this.number = street;
    }

    public Phone(String number) {
        this.number = number;
    }

    @Override
    public Phone clone() {
        return new Phone(this.id, this.number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone phone = (Phone) o;
        return Objects.equals(id, phone.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Phone{" +
            "id=" + id +
            ", number='" + number + '\'' +
            '}';
    }
}
