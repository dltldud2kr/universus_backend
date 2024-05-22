package com.example.gazamung.dto;


import com.example.gazamung._enum.MsgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class notifyAndFcmSendDto {
    private String token;
    private String title;
    private String body;
    private Long relatedItemId;
    private Long receiver;
    private MsgType type ;
    private boolean isRead;

}
