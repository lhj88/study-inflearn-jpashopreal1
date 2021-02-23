package jpabook.jpashopreal1.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Book")
@Getter @Setter
public class Book extends Item{

    private String author;
    private String isbn;
}
