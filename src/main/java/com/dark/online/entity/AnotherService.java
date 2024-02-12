package com.dark.online.entity;

import com.dark.online.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "another_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnotherService {
    @Id
    @GeneratedValue(generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.dark.online.util.CustomLongIdGenerator")
    private Long id;

    private ServiceStatus status;
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    List<String> links;

}
