package kg.home.demo.entity;

import jakarta.persistence.*;
import kg.home.demo.enums.MultipleThreadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MULTIPLE_thread")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleThread {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "multiple_thread_seq")
    @SequenceGenerator(name = "multiple_thread_seq", sequenceName = "multiple_thread_seq",allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MultipleThreadStatus status;
}
