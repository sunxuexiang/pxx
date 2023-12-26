package com.wanmi.sbc.order.trade.model.newPileTrade;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Entity
@Table(name = "new_pile_send_data")
@EqualsAndHashCode()
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewPileSendData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "account")
    private String account;

    @Column(name = "confirm_all_pile_new")
    private String confirmAllPileNew;

    @Column(name = "pile_new_commit_all")
    private String pileNewCommitAll;

    @Column(name = "newbatch_add")
    private String newbatchAdd;



}
