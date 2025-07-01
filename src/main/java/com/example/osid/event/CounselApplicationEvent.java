package com.example.osid.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CounselApplicationEvent {
    private Long counselId;
    private Long dealerId;
    private String userName;
    private String dealerName;
    private String dealerEmail;
    private String counselTitle;
    private String counselContent;
    private String applicationTime;
}