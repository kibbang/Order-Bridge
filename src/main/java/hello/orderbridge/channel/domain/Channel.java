package hello.orderbridge.channel.domain;

import hello.orderbridge.common.domain.BaseEntity;
import hello.orderbridge.enums.channel.ChannelType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "channels")
public class Channel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @Column(nullable = false)
    private String apiKey;

    @Column(nullable = false)
    private boolean isActive;

    public static Channel of(String name, ChannelType type, String apiKey) {
        Channel channel = new Channel();
        channel.name = name;
        channel.type = type;
        channel.apiKey = apiKey;
        channel.isActive = true;
        return channel;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
