// file: Friend.java
package com.example.spring_security.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    @EmbeddedId
    private FriendId id;

    @Column(name = "made_friend_at")
    private LocalDateTime madeFriendAt;

    public Friend(FriendId id, LocalDateTime madeFriendAt) {
        this.id = id;
        this.madeFriendAt = madeFriendAt;
    }

    @ManyToOne
    @JoinColumn(name = "user_id1", insertable = false, updatable = false)
    private User user1Entity;

    @ManyToOne
    @JoinColumn(name = "user_id2", insertable = false, updatable = false)
    private User user2Entity;
}




