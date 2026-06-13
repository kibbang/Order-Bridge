package hello.orderbridge.channel.repository;

import hello.orderbridge.channel.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByIsActiveTrue();
}
